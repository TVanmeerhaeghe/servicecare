<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <h1 class="text-lg">{{ isEdit ? 'Modifier le ticket' : 'Nouveau ticket' }}</h1>
        <p class="text-sm text-muted">Créez ou mettez à jour une demande.</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Annuler</button>
        <button class="btn btn-primary text-sm" @click="submit">
          {{ isEdit ? 'Enregistrer' : 'Créer' }}
        </button>
      </div>
    </header>

    <section class="data-card">
      <form class="grid gap-6 p-6" @submit.prevent="submit">
        <div class="form-section">
          <h2 class="form-section-title">Informations</h2>
          <div class="form-grid">
            <label class="field">
              <span>Titre *</span>
              <input v-model="form.title" class="input" required />
            </label>
            <label class="field">
              <span>Client *</span>
              <select v-model="selectedClientId" class="input" required>
                <option value="" disabled>Sélectionner un client</option>
                <option
                  v-for="client in clientOptions"
                  :key="client.id"
                  :value="client.id"
                >
                  {{ client.name || `Client #${client.id}` }}
                </option>
              </select>
            </label>
            <label class="field">
              <span>Site ID</span>
              <input v-model.number="form.siteId" type="number" min="1" class="input" />
            </label>
            <label class="field">
              <span>Contrat ID</span>
              <input v-model.number="form.contractId" type="number" min="1" class="input" />
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Détails</h2>
          <div class="form-grid">
            <label class="field">
              <span>Description</span>
              <textarea v-model="form.description" class="input" rows="4"></textarea>
            </label>
            <label class="field">
              <span>Priorité</span>
              <select v-model="form.priority" class="input">
                <option value="CRITICAL">Critique</option>
                <option value="HIGH">Haute</option>
                <option value="MEDIUM">Moyenne</option>
                <option value="LOW">Basse</option>
              </select>
            </label>
            <label class="field">
              <span>Statut</span>
              <select v-model="form.status" class="input">
                <option value="OPEN">Ouvert</option>
                <option value="ASSIGNED">Assigné</option>
                <option value="IN_PROGRESS">En cours</option>
                <option value="WAITING">En attente</option>
                <option value="CLOSED">Fermé</option>
                <option value="CANCELED">Annulé</option>
              </select>
            </label>
            <label class="field">
              <span>ID assigné</span>
              <input v-model.number="form.assigneeUserId" type="number" min="1" class="input" />
            </label>
            <label class="field">
              <span>Raison attente</span>
              <input v-model="form.waitingReason" class="input" />
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="goBack">Annuler</button>
          <button type="submit" class="btn btn-primary">
            {{ isEdit ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createTicket, updateTicket, fetchTicketDetails } from '@/api/tickets'
import { fetchClients } from '@/api/clients'
import type { TicketPayload, Ticket } from '@/types/tickets'
import type { Client } from '@/types/clients'

const route = useRoute()
const router = useRouter()
const clientOptions = ref<Client[]>([])
const isEdit = computed(() => Boolean(route.params.id))

const form = reactive<TicketPayload>({
  clientId: null,
  title: '',
  description: '',
  siteId: null,
  contractId: null,
  priority: 'MEDIUM',
  assigneeUserId: null,
  status: 'OPEN',
  waitingReason: '',
})

const selectedClientId = computed({
  get: () => form.clientId ?? '',
  set: (value: string | number) => {
    form.clientId = value === '' ? null : Number(value)
  },
})

async function loadClients() {
  const { data } = await fetchClients({ page: 0, size: 100 })
  clientOptions.value = data.content
}

async function preload() {
  if (!isEdit.value) return
  const { data } = await fetchTicketDetails(route.params.id as string)
  applyTicketToForm(data)
}

function applyTicketToForm(ticket: Ticket) {
  form.clientId = ticket.clientId ?? null
  form.title = ticket.title
  form.description = ticket.description ?? ''
  form.siteId = ticket.siteId ?? null
  form.contractId = ticket.contractId ?? null
  form.priority = ticket.priority
  form.assigneeUserId = ticket.assigneeUserId ?? null
  form.status = ticket.status
  form.waitingReason = ticket.waitingReason ?? ''
}

function buildUpdatePayload(): Partial<TicketPayload> {
  return {
    title: form.title,
    description: form.description || null,
    priority: form.priority,
    assigneeUserId: form.assigneeUserId ?? null,
    status: form.status,
    waitingReason: form.waitingReason || null,
    siteId: form.siteId ?? null,
    contractId: form.contractId ?? null,
    clientId: form.clientId,
  }
}

async function submit() {
  if (!form.title || !form.clientId) return
  if (isEdit.value) {
    await updateTicket(route.params.id as string, buildUpdatePayload())
  } else {
    await createTicket({
      clientId: form.clientId,
      title: form.title,
      description: form.description || null,
      siteId: form.siteId ?? null,
      contractId: form.contractId ?? null,
      priority: form.priority,
      assigneeUserId: form.assigneeUserId ?? null,
    })
  }
  router.push({ name: 'tickets-list' })
}

function goBack() {
  router.push({ name: 'tickets-list' })
}

onMounted(async () => {
  await Promise.all([loadClients(), preload()])
})
</script>