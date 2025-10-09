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

    <section v-if="ticket" class="detail-grid">
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
              <div><dt>Statut</dt><dd><span class="badge" :class="statusBadge(ticket.status)">{{ statusLabel(ticket.status) }}</span></dd></div>
              <div><dt>Priorité</dt><dd><span class="badge" :class="priorityBadge(ticket.priority)">{{ priorityLabel(ticket.priority) }}</span></dd></div>
              <div><dt>Assigné à</dt><dd>{{ ticket.assigneeUserId ? `Utilisateur #${ticket.assigneeUserId}` : '—' }}</dd></div>
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
          <h2 class="section-kicker">Historique</h2>
          <ul class="timeline">
            <li class="timeline__item timeline__initial">
              <div class="timeline__title">
                {{ ticket.title }}
              </div>
              <div class="timeline__meta">
                Créé {{ ticket.createdAt ? formatDate(ticket.createdAt) : '—' }}
              </div>
              <div class="timeline__body">
                <pre v-if="ticket.description" class="preserve">{{ ticket.description }}</pre>
                <span v-else class="text-muted">Aucune description.</span>
              </div>
            </li>

            <li
              v-for="event in thread"
              :key="`${event.kind}-${event.id}`"
              class="timeline__item"
              :class="commentItemClasses(event)"
            >
              <template v-if="event.kind !== 'COMMENT'">
                <div class="timeline__head-row">
                  <span class="t-meta">{{ eventDate(event) }}</span>
                  <span class="t-sep">•</span>
                  <span class="t-author">{{ eventAuthor(event) }}</span>
                  <span class="t-type-badge">{{ eventTypeLabel(event) }}</span>
                </div>

                <div v-if="event.kind === 'ATTACHMENT'" class="timeline__body attachment-block">
                  <strong>{{ (event as any).originalName }}</strong>
                  <span class="t-file-size">({{ ((event as any).size || 0) }} o)</span><br />
                  <a
                    v-if="(event as any).downloadUrl"
                    :href="(event as any).downloadUrl"
                    class="timeline__link"
                    target="_blank"
                    rel="noopener"
                  >Télécharger</a>
                </div>

                <div v-else-if="event.kind === 'INTERVENTION'" class="timeline__body">
                  {{ (event as any).title || 'Intervention' }}
                  <div class="t-sub">
                    {{ [(event as any).interventionType, (event as any).interventionStatus].filter(Boolean).join(' • ') }}
                  </div>
                </div>
              </template>

              <template v-else>
                <div
                  class="thread-comment"
                  :class="{
                    'thread-comment--client': isClientResponse(event),
                    'thread-comment--staff': !isClientResponse(event),
                    'thread-comment--internal': !!(event as any).internalOnly
                  }"
                >
                  <div class="thread-comment__meta">
                    <span class="thread-comment__author">{{ eventAuthor(event) }}</span>
                    <span class="thread-comment__time">{{ eventDate(event) }}</span>
                    <span v-if="(event as any).internalOnly" class="thread-comment__tag" title="Interne">INT</span>
                  </div>
                  <div class="thread-comment__body">
                    {{ (event as any).body }}
                  </div>
                </div>
              </template>
            </li>

            <li v-if="!thread.length" class="timeline__empty">
              Aucun événement pour ce ticket.
            </li>
          </ul>
        </div>
      </article>

      <article class="data-card" v-if="ticket">
        <div class="p-5">
          <TicketCommentsSection :ticket-id="ticket.id" @comment-posted="reloadThread" />
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchTicketDetails, fetchTicketThread } from '@/api/tickets'
import { fetchClientDetails } from '@/api/clients'
import { fetchSiteDetails } from '@/api/sites'
import TicketCommentsSection from '@/components/tickets/TicketCommentsSection.vue'
import type {
  Ticket,
  TicketThreadEvent,
  CommentEvent,
  AttachmentEvent,
  InterventionEvent
} from '@/types/tickets'
import type { Client } from '@/types/clients'
import type { Site } from '@/types/sites'

const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)

const route = useRoute()
const router = useRouter()
const ticket = ref<Ticket | null>(null)
const client = ref<Client | null>(null)
const site = ref<Site | null>(null)
const thread = ref<TicketThreadEvent[]>([])
const showDetails = ref(false)

const clientNameFallback = computed(() => (ticket.value?.clientId ? `Client #${ticket.value.clientId}` : '—'))
const siteDisplay = computed(() => site.value?.url || (site.value as any)?.prodUrl || null)

const formatter = new Intl.DateTimeFormat('fr-FR', { dateStyle: 'short', timeStyle: 'short' })

function toggleDetails() { showDetails.value = !showDetails.value }

async function load() {
  const { data } = await fetchTicketDetails(route.params.id as string)
  ticket.value = data
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

function isComment(e: TicketThreadEvent): e is CommentEvent { return e.kind === 'COMMENT' }
function isAttachment(e: TicketThreadEvent): e is AttachmentEvent { return e.kind === 'ATTACHMENT' }
function isIntervention(e: TicketThreadEvent): e is InterventionEvent { return e.kind === 'INTERVENTION' }

function eventDate(e: TicketThreadEvent) { return e.at ? formatter.format(new Date(e.at)) : '—' }
function eventAuthor(e: TicketThreadEvent) {
  if ((e as any).authorName) return (e as any).authorName
  if ((e as any).authorUserId) return `Utilisateur #${(e as any).authorUserId}`
  return 'Système'
}

function eventTypeLabel(e: TicketThreadEvent) {
  if (isIntervention(e)) return 'Intervention'
  if (isAttachment(e)) return 'Pièce jointe'
  return e.kind
}

function isClientResponse(e: TicketThreadEvent): boolean {
  if (!isComment(e)) return false
  return !!(e as CommentEvent).authorIsClient
}

function commentItemClasses(e: TicketThreadEvent) {
  if (e.kind !== 'COMMENT') return null
  return 'timeline__item--comment'
}

onMounted(load)
</script>