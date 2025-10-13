<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <h1 class="text-lg">
          Ticket #{{ ticket?.id }} - {{ ticket?.title || 'Ticket' }}
        </h1>
        <p class="text-sm text-muted">
          {{ ticket?.description || 'Aucun détail fourni.' }}
        </p>

        <div
          v-if="isAdmin"
          style="display:flex; align-items:center; gap:8px; flex-wrap:wrap; margin-top:10px;"
        >
          <select v-model="selectedAssigneeId" class="select select-sm" style="min-width:320px;">
            <option :value="null">– Sélectionner un assigné –</option>
            <option v-for="u in assignees" :key="u.id" :value="u.id">
              {{ displayAssignee(u) }}
            </option>
          </select>
          <button class="btn btn-primary" :disabled="!selectedAssigneeId || assigning" @click="doAssign">
            {{ assigning ? 'Assignation…' : 'Assigner' }}
          </button>
        </div>
      </div>

      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Retour</button>
        <button
          v-if="!isClientRole"
          class="btn btn-primary text-sm"
          @click="goEdit"
        >
          Modifier
        </button>
      </div>
    </header>

    <div v-if="ticket" class="details-layout">
      <section class="detail-grid">
        <article class="data-card ticket-summary-card">
          <div class="ticket-summary-grid">
            <div class="ts-item">
              <span class="ts-label">Statut</span>
              <span class="badge" :class="statusBadge(ticket.status)">{{ statusLabel(ticket.status) }}</span>
            </div>
            <div class="ts-item">
              <span class="ts-label">Priorité</span>
              <span class="badge" :class="priorityBadge(ticket.priority)">{{ priorityLabel(ticket.priority) }}</span>
            </div>
            <div class="ts-item">
              <span class="ts-label">Répondre avant</span>
              <span class="ts-value">{{ formatDate(ticket.respondBy) }}</span>
            </div>
            <div class="ts-item">
              <span class="ts-label">Résoudre avant</span>
              <span class="ts-value">{{ formatDate(ticket.resolveBy) }}</span>
            </div>
            <div class="ts-item" v-if="!isClientRole">
              <span class="ts-label">Client</span>
              <span class="ts-value">{{ client?.name || clientNameFallback }}</span>
            </div>
            <div class="ts-item">
              <span class="ts-label">Site</span>
              <span class="ts-value">{{ siteDisplay || '—' }}</span>
            </div>
          </div>
          <div class="ticket-summary-actions">
            <button class="btn btn-ghost btn-sm" @click="toggleDetails">
              {{ showDetails ? 'Moins de détails' : 'Plus de détails' }}
            </button>
          </div>
        </article>

        <template v-if="showDetails">
          <article class="data-card">
            <div class="p-5">
              <h2 class="section-kicker">Résumé détaillé</h2>
              <dl class="info-grid">
                <div><dt>Assigné à</dt><dd>{{ displayAssignee(assigneeUser) || '—' }}</dd></div>
                <div><dt>Raison attente</dt><dd>{{ ticket.waitingReason || '—' }}</dd></div>
                <div><dt>Temps en pause</dt><dd>{{ formatDuration(ticket.pausedSeconds) }}</dd></div>
                <div><dt>SLA dépassé</dt><dd>{{ ticket.slaBreached ? 'Oui' : 'Non' }}</dd></div>
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
                <div><dt>Créé le</dt><dd>{{ formatDate(ticket.createdAt || null) }}</dd></div>
                <div><dt>Màj le</dt><dd>{{ formatDate(ticket.updatedAt || null) }}</dd></div>
              </dl>
            </div>
          </article>

          <article v-if="!isClientRole && client" class="data-card">
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

          <article v-if="!isClientRole" class="data-card">
            <div class="p-5">
              <h2 class="section-kicker">Références</h2>
              <dl class="info-grid">
                <div><dt>Client</dt><dd>{{ client?.name || clientNameFallback }}</dd></div>
                <div><dt>Site</dt><dd>{{ siteDisplay || '—' }}</dd></div>
                <div><dt>Contrat</dt><dd>{{ ticket.contractId || '—' }}</dd></div>
              </dl>
            </div>
          </article>
        </template>

        <article class="data-card">
          <div class="p-5">
            <div style="display:flex; align-items:center; justify-content:space-between; gap:8px; margin-bottom:8px;">
              <h2 class="section-kicker" style="margin:0;">Discussion</h2>
              <button
                v-if="!isClientRole"
                class="btn btn-danger btn-sm"
                :disabled="resolving || isClosed || (!isAdmin && !canCloseNormally)"
                @click="doResolve"
              >
                {{ resolving ? 'Résolution…' : 'Marqué comme résolue' }}
              </button>
            </div>

            <TicketThread :ticket="ticket" :thread="thread" />
          </div>
        </article>

        <article class="data-card" v-if="ticket">
          <div class="p-5">
            <TicketCommentsSection :ticket-id="ticket.id" @comment-posted="reloadThread" />
          </div>
        </article>
      </section>

      <SlaLogsPanel v-if="!isClientRole" :ticket-id="Number(route.params.id)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchTicketDetails, fetchTicketThread, assignTicket, transitionTicket } from '@/api/tickets'
import { fetchClientDetails } from '@/api/clients'
import { fetchSiteDetails } from '@/api/sites'
import { fetchAssignees } from '@/api/users'
import TicketCommentsSection from '@/components/tickets/TicketCommentsSection.vue'
import TicketThread from '@/components/tickets/TicketThread.vue'
import SlaLogsPanel from '@/components/tickets/SlaLogsPanel.vue'
import type {
  Ticket,
  TicketThreadEvent,
  CommentEvent,
  AttachmentEvent,
  InterventionEvent
} from '@/types/tickets'
import type { Client } from '@/types/clients'
import type { Site } from '@/types/sites'
import type { Assignee } from '@/types/users'

const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)
const isAdmin = computed(() => auth.user?.role === 'ADMIN')
const isClosed = computed(() => ticket.value?.status === 'CLOSED' || ticket.value?.status === 'CANCELED')

const route = useRoute()
const router = useRouter()
const ticketId = computed(() => Number(route.params.id))
const ticket = ref<Ticket | null>(null)
const client = ref<Client | null>(null)
const site = ref<Site | null>(null)
const thread = ref<TicketThreadEvent[]>([])
const showDetails = ref(false)

const assignees = ref<Assignee[]>([])
const selectedAssigneeId = ref<number | null>(null)
const assigning = ref(false)
const resolving = ref(false)

const clientNameFallback = computed(() => (ticket.value?.clientId ? `Client #${ticket.value.clientId}` : '—'))
const siteDisplay = computed(() => site.value?.url || (site.value as any)?.prodUrl || null)

const formatter = new Intl.DateTimeFormat('fr-FR', { dateStyle: 'short', timeStyle: 'short' })

function toggleDetails() { showDetails.value = !showDetails.value }

function displayAssignee(u: Assignee) {
  const name = u.displayName || [u.firstName, u.lastName].filter(Boolean).join(' ').trim()
  return `${name || u.email} · ${u.role}`
}

const assigneeUser = computed(() => {
  const id = ticket.value?.assigneeUserId
  return id ? (assignees.value.find(a => a.id === id) || null) : null
})

async function load() {
  const { data } = await fetchTicketDetails(route.params.id as string)
  ticket.value = data

  selectedAssigneeId.value = (data.assigneeUserId ?? null) as number | null

  if (!isClientRole.value && data.clientId) {
    const clientRes = await fetchClientDetails(data.clientId)
    client.value = clientRes.data
  }
  if (data.siteId) {
    try {
      const siteRes = await fetchSiteDetails(data.siteId)
      site.value = siteRes.data
    } catch {}
  }
  await loadThread()
}

async function reloadTicket() {
  await load()
}

async function loadThread() {
  const threadRes = await fetchTicketThread(route.params.id as string)

  type ThreadApiResponse = TicketThreadEvent[] | { content?: any[] }
  const raw = threadRes.data as ThreadApiResponse
  const items: any[] = Array.isArray(raw) ? raw : (raw?.content ?? [])

  const coerceBool = (v: any) => v === true || v === 'true' || v === 1 || v === '1'

  const normalized = items.map((c: any) => {
    const rawAuthorIsClient = c.authorIsClient ?? c.author_is_client ?? null

    const common = {
      kind: c.kind ?? 'COMMENT',
      id: Number(c.id),
      at: c.createdAt ?? c.at ?? null,
    }

    if ((common.kind as string).toUpperCase() === 'COMMENT') {
      const comment: CommentEvent = {
        kind: 'COMMENT',
        id: common.id,
        at: common.at,
        authorName: c.authorName ?? null,
        authorUserId: c.authorUserId ?? null,
        body: c.body ?? '',
        internalOnly: !!c.internalOnly,
        authorIsClient: coerceBool(rawAuthorIsClient)
      }
      return comment as TicketThreadEvent
    }

    if ((common.kind as string).toUpperCase() === 'ATTACHMENT') {
      const a: AttachmentEvent = {
        kind: 'ATTACHMENT',
        id: common.id,
        at: common.at,
        authorName: c.authorName ?? null,
        authorUserId: c.authorUserId ?? null,
        originalName: c.originalName ?? null,
        size: c.size ?? null,
        downloadUrl: c.downloadUrl ?? null,
        contentType: c.contentType ?? null
      }
      return a as TicketThreadEvent
    }

    const i: InterventionEvent = {
      kind: 'INTERVENTION',
      id: common.id,
      at: common.at,
      authorName: c.authorName ?? null,
      authorUserId: c.authorUserId ?? null,
      title: c.title ?? null,
      interventionType: c.interventionType ?? null,
      interventionStatus: c.interventionStatus ?? null,
      technicianUserId: c.technicianUserId ?? null,
      scheduledStart: c.scheduledStart ?? null,
      scheduledEnd: c.scheduledEnd ?? null,
      actualStart: c.actualStart ?? null,
      actualEnd: c.actualEnd ?? null
    }
    return i as TicketThreadEvent
  })

  thread.value = normalized
}

function reloadThread() { loadThread() }
function goBack() { router.push({ name: 'tickets-list' }) }
function goEdit() {
  if (isClientRole.value) return
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
  return `${hours}h${mins.toString().padStart(2,'0')}`
}

async function loadAssignees() {
  const { data } = await fetchAssignees({ page: 0, size: 20 })
  assignees.value = Array.isArray((data as any).content) ? (data as any).content : (data as any)
}

async function doAssign() {
  if (!selectedAssigneeId.value) return
  assigning.value = true
  try {
    await assignTicket(ticketId.value, Number(selectedAssigneeId.value))
    await reloadTicket()
  } finally {
    assigning.value = false
  }
}

function canTransition(status: Ticket['status'], action: string): boolean {
  const act = action.toUpperCase()
  const ALLOWED: Record<Ticket['status'], string[]> = {
    OPEN: ['ASSIGN','START','CANCEL'],
    ASSIGNED: ['START','WAIT','CANCEL'],
    IN_PROGRESS: ['WAIT','CLOSE','CANCEL'],
    WAITING: ['RESUME','CANCEL'],
    CLOSED: [],
    CANCELED: [],
  }
  return ALLOWED[status]?.includes(act) ?? false
}

const canCloseNormally = computed(() =>
  ticket.value ? canTransition(ticket.value.status, 'CLOSE') : false
)

async function doResolve() {
  if (!ticket.value) return
  const allow = canCloseNormally.value

  // Désactive pour non-admin si non autorisé
  if (!allow && !isAdmin.value) {
    alert('Transition non autorisée')
    return
  }

  resolving.value = true
  try {
    if (allow) {
      await transitionTicket(ticketId.value, 'CLOSE')
    } else {
      // ADMIN: forcer directement, pas d’appel qui échoue avant
      const ok = window.confirm('Transition non autorisée.\n\nForcer la fermeture ?')
      if (!ok) return
      await transitionTicket(ticketId.value, 'CLOSE', { force: true })
    }
    await reloadTicket()
  } catch (e: any) {
    alert(e?.response?.data?.message || e?.message || 'Erreur')
  } finally {
    resolving.value = false
  }
}

onMounted(async () => {
  await load()
  await loadAssignees()
})
</script>

<!-- Dans le template, désactiver pour non-admin si non autorisé -->