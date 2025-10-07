<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <h1 class="text-lg">{{ ticket?.title || 'Ticket' }}</h1>
        <p class="text-sm text-muted">
          {{ ticket?.description || 'Aucun détail fourni.' }}
        </p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Retour</button>
        <button class="btn btn-primary text-sm" @click="goEdit">Modifier</button>
      </div>
    </header>

    <section v-if="ticket" class="detail-grid">
      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Résumé</h2>
          <dl class="info-grid">
            <div><dt>Statut</dt><dd><span class="badge" :class="statusBadge(ticket.status)">{{ statusLabel(ticket.status) }}</span></dd></div>
            <div><dt>Priorité</dt><dd><span class="badge" :class="priorityBadge(ticket.priority)">{{ priorityLabel(ticket.priority) }}</span></dd></div>
            <div><dt>Assigné à</dt><dd>{{ ticket.assigneeUserId ? `Utilisateur #${ticket.assigneeUserId}` : '—' }}</dd></div>
            <div><dt>Raison attente</dt><dd>{{ ticket.waitingReason || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Échéances</h2>
          <dl class="info-grid">
            <div><dt>Répondre avant</dt><dd>{{ formatDate(ticket.respondBy) }}</dd></div>
            <div><dt>Résoudre avant</dt><dd>{{ formatDate(ticket.resolveBy) }}</dd></div>
            <div><dt>Répondu le</dt><dd>{{ formatDate(ticket.respondedAt) }}</dd></div>
            <div><dt>Résolu le</dt><dd>{{ formatDate(ticket.resolvedAt) }}</dd></div>
            <div><dt>SLA dépassé</dt><dd>{{ ticket.slaBreached ? 'Oui' : 'Non' }}</dd></div>
            <div><dt>Temps en pause</dt><dd>{{ formatDuration(ticket.pausedSeconds) }}</dd></div>
          </dl>
        </div>
      </article>

      <article v-if="client" class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Client</h2>
          <dl class="info-grid">
            <div><dt>Nom</dt><dd>{{ client.name || '—' }}</dd></div>
            <div><dt>Email</dt><dd>{{ client.contactEmail || '—' }}</dd></div>
            <div><dt>Téléphone</dt><dd>{{ client.contactPhone || '—' }}</dd></div>
            <div><dt>Statut</dt><dd>{{ client.status || '—' }}</dd></div>
          </dl>
          <button class="btn btn-primary text-sm mt-4" @click="goToClientDetails">
            Voir le client
          </button>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Références</h2>
          <dl class="info-grid">
            <div><dt>Client ID</dt><dd>{{ ticket.clientId }}</dd></div>
            <div><dt>Site ID</dt><dd>{{ ticket.siteId || '—' }}</dd></div>
            <div><dt>Contrat ID</dt><dd>{{ ticket.contractId || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Historique</h2>
          <ul class="timeline">
            <li v-for="event in thread" :key="`${event.kind}-${event.id}`">
              <div class="timeline__meta">{{ eventDate(event) }}</div>
              <div class="timeline__title">{{ eventTitle(event) }}</div>
              <div class="timeline__body" v-if="eventDetails(event)">{{ eventDetails(event) }}</div>
              <a
                v-if="event.kind === 'ATTACHMENT' && event.downloadUrl"
                :href="event.downloadUrl"
                class="timeline__link"
                target="_blank"
                rel="noopener"
              >
                Télécharger
              </a>
            </li>
            <li v-if="!thread.length" class="timeline__empty">Aucun événement pour ce ticket.</li>
          </ul>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <TicketCommentsSection
            v-if="ticket"
            :ticket-id="ticket.id"
            @comment-posted="reloadThread"
          />
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchTicketDetails } from '@/api/tickets'
import { fetchClientDetails } from '@/api/clients'
import TicketCommentsSection from '@/components/tickets/TicketCommentsSection.vue'
import { fetchTicketThread } from '@/api/tickets'
import type { Ticket } from '@/types/tickets'
import type { Client } from '@/types/clients'
import type { TicketThreadEvent } from '@/types/tickets'

const route = useRoute()
const router = useRouter()
const ticket = ref<Ticket | null>(null)
const client = ref<Client | null>(null)
const thread = ref<TicketThreadEvent[]>([])

const formatter = new Intl.DateTimeFormat('fr-FR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

async function load() {
  const { data } = await fetchTicketDetails(route.params.id as string)
  ticket.value = data
  if (data.clientId) {
    const clientRes = await fetchClientDetails(data.clientId)
    client.value = clientRes.data
  }
  await loadThread()
}
async function loadThread() {
  const threadRes = await fetchTicketThread(route.params.id as string)
  thread.value = threadRes.data
}
function reloadThread() {
  loadThread()
}

function goBack() {
  router.push({ name: 'tickets-list' })
}

function goEdit() {
  router.push({ name: 'ticket-edit', params: { id: route.params.id } })
}

function goToClientDetails() {
  if (!client.value?.id) return
  router.push({ name: 'client-details', params: { id: client.value.id } })
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

function formatDuration(seconds: number) {
  if (!seconds) return '—'
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  return `${hours}h${mins.toString().padStart(2, '0')}`
}

function eventDate(e: TicketThreadEvent) {
  return e.at ? formatter.format(new Date(e.at)) : '—'
}

function eventTitle(e: TicketThreadEvent) {
  if (e.kind === 'COMMENT') return e.authorName || 'Commentaire'
  if (e.kind === 'INTERVENTION') return e.title || 'Intervention'
  return e.originalName || 'Fichier'
}

function eventDetails(e: TicketThreadEvent) {
  if (e.kind === 'COMMENT') return e.body || ''
  if (e.kind === 'INTERVENTION') {
    return [
      e.interventionType,
      e.interventionStatus,
      e.technicianUserId ? `Technicien #${e.technicianUserId}` : null,
    ].filter(Boolean).join(' • ')
  }
  return `${e.size ?? 0} octets`
}

onMounted(load)
</script>