<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Suivi des demandes et incidents.</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-primary text-sm" @click="goToCreate">
          Nouveau ticket
        </button>
      </div>
    </header>

    <section class="filters-bar">
      <div class="filters-controls">
        <label class="field">
          <span>Recherche</span>
          <input
            v-model.trim="filters.query"
            class="input"
            type="search"
            placeholder="Titre, description…"
            @keyup.enter="reload"
          />
        </label>
        <label class="field">
          <span>Statut</span>
          <select v-model="filters.status" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="OPEN">Ouverts</option>
            <option value="ASSIGNED">Assignés</option>
            <option value="IN_PROGRESS">En cours</option>
            <option value="WAITING">En attente</option>
            <option value="CLOSED">Fermés</option>
            <option value="CANCELED">Annulés</option>
          </select>
        </label>
        <label class="field">
          <span>Priorité</span>
          <select v-model="filters.priority" class="input" @change="reload">
            <option value="">Toutes</option>
            <option value="CRITICAL">Critique</option>
            <option value="HIGH">Haute</option>
            <option value="MEDIUM">Moyenne</option>
            <option value="LOW">Basse</option>
          </select>
        </label>
        <label class="field">
          <span>SLA dépassé</span>
          <select v-model="filters.sla" class="input" @change="reload">
            <option value="">Tous</option>
            <option value="true">Oui</option>
            <option value="false">Non</option>
          </select>
        </label>
      </div>
      <div class="text-sm text-muted">
        {{ pagination.total }} résultat(s)
      </div>
    </section>

    <section class="data-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>Titre</th>
            <th>Client</th>
            <th>Priorité</th>
            <th>Statut</th>
            <th>Échéances</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center text-muted py-6">Chargement…</td>
          </tr>
          <tr v-else-if="!tickets.length">
            <td colspan="6" class="text-center text-muted py-6">Aucun ticket trouvé.</td>
          </tr>
          <tr v-for="ticket in tickets" :key="ticket.id">
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">{{ ticket.title }}</span>
                <span class="data-table__cell--sub">{{ ticket.description || '—' }}</span>
              </div>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">
                  {{ clientName(ticket.clientId) }}
                </span>
              </div>
            </td>
            <td>
              <span class="badge" :class="priorityBadge(ticket.priority)">
                {{ priorityLabel(ticket.priority) }}
              </span>
            </td>
            <td>
              <span class="badge" :class="statusBadge(ticket.status)">
                {{ statusLabel(ticket.status) }}
              </span>
            </td>
            <td>
              <div class="data-table__cell">
                <span class="data-table__cell--main">
                  Réponse&nbsp;: {{ formatDate(ticket.respondBy) }}
                </span>
                <span class="data-table__cell--sub">
                  Résolution&nbsp;: {{ formatDate(ticket.resolveBy) }}
                </span>
              </div>
            </td>
            <td class="data-table__actions">
              <div class="btn-group">
                <button class="btn btn-ghost text-sm" @click="goToEdit(ticket.id)">Modifier</button>
                <button class="btn btn-ghost text-sm" @click="goToDetails(ticket.id)">Détails</button>
                <button class="btn btn-ghost text-sm text-danger" @click="askDelete(ticket)">
                  Supprimer
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <footer class="pagination">
      <div>Page {{ pagination.page }} / {{ totalPages }}</div>
      <div class="pagination-controls">
        <button
          class="btn btn-ghost"
          :disabled="pagination.page === 1"
          @click="changePage(pagination.page - 1)"
        >
          Précédent
        </button>
        <button
          class="btn btn-ghost"
          :disabled="pagination.page === totalPages"
          @click="changePage(pagination.page + 1)"
        >
          Suivant
        </button>
      </div>
    </footer>
  </div>

  <ConfirmDialog
    v-if="confirmVisible"
    @cancel="confirmVisible = false"
    @confirm="handleDelete"
  >
    <template #title>Supprimer le ticket</template>
    Confirmer la suppression de <strong>{{ targetTitle }}</strong> ?
  </ConfirmDialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchTickets, deleteTicket } from '@/api/tickets'
import { fetchClients } from '@/api/clients'
import type { Ticket } from '@/types/tickets'
import type { Client } from '@/types/clients'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const router = useRouter()
const tickets = ref<Ticket[]>([])
const loading = ref(false)
const confirmVisible = ref(false)
const targetId = ref<number | null>(null)
const targetTitle = ref('')
const clients = ref<Client[]>([])

const filters = reactive({
  query: '',
  status: '',
  priority: '',
  sla: '',
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

const formatter = new Intl.DateTimeFormat('fr-FR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(pagination.total / pagination.size))
)

const slaFilterValue = computed(() => {
  if (filters.sla === '') return undefined
  return filters.sla === 'true'
})

function clientName(id: number | null) {
  if (!id) return '—'
  const match = clients.value.find((c) => c.id === id)
  return match?.name || `Client #${id}`
}

function statusBadge(status: Ticket['status']) {
  return {
    'badge--ticket-status-open': status === 'OPEN',
    'badge--ticket-status-assigned': status === 'ASSIGNED',
    'badge--ticket-status-in-progress': status === 'IN_PROGRESS',
    'badge--ticket-status-waiting': status === 'WAITING',
    'badge--ticket-status-closed': status === 'CLOSED',
    'badge--ticket-status-canceled': status === 'CANCELED',
  }
}

function priorityBadge(priority: Ticket['priority']) {
  return {
    'badge--ticket-priority-critical': priority === 'CRITICAL',
    'badge--ticket-priority-high': priority === 'HIGH',
    'badge--ticket-priority-medium': priority === 'MEDIUM',
    'badge--ticket-priority-low': priority === 'LOW',
  }
}

function statusLabel(status: Ticket['status']) {
  return {
    OPEN: 'Ouvert',
    ASSIGNED: 'Assigné',
    IN_PROGRESS: 'En cours',
    WAITING: 'En attente',
    CLOSED: 'Fermé',
    CANCELED: 'Annulé',
  }[status]
}

function priorityLabel(priority: Ticket['priority']) {
  return {
    CRITICAL: 'Critique',
    HIGH: 'Haute',
    MEDIUM: 'Moyenne',
    LOW: 'Basse',
  }[priority]
}

function formatDate(value: string | null) {
  if (!value) return '—'
  return formatter.format(new Date(value))
}

async function loadClients() {
  const { data } = await fetchClients({ page: 0, size: 100 })
  clients.value = data.content
}

async function loadTickets() {
  loading.value = true
  try {
    const { data } = await fetchTickets({
      page: pagination.page - 1,
      size: pagination.size,
      search: filters.query || undefined,
      status: filters.status || undefined,
      priority: filters.priority || undefined,
      slaBreached: slaFilterValue.value,
    })
    tickets.value = data.content
    pagination.total = data.totalElements
  } finally {
    loading.value = false
  }
}

function reload() {
  pagination.page = 1
  loadTickets()
}

function changePage(next: number) {
  if (next < 1 || next > totalPages.value) return
  pagination.page = next
  loadTickets()
}

function goToCreate() {
  router.push({ name: 'ticket-create' })
}

function goToEdit(id: number) {
  router.push({ name: 'ticket-edit', params: { id } })
}

function goToDetails(id: number) {
  router.push({ name: 'ticket-details', params: { id } })
}

function askDelete(ticket: Ticket) {
  targetId.value = ticket.id
  targetTitle.value = ticket.title
  confirmVisible.value = true
}

async function handleDelete() {
  if (targetId.value == null) return
  await deleteTicket(targetId.value)
  confirmVisible.value = false
  await loadTickets()
}

onMounted(async () => {
  await Promise.all([loadClients(), loadTickets()])
})
</script>