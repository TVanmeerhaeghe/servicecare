<template>
  <ul class="timeline">
    <li class="timeline__item timeline__initial timeline__item--comment">
      <div class="thread-comment thread-comment--client">
        <div class="thread-comment__title">
          {{ ticket.title }}
        </div>
        <div class="thread-comment__meta">
          <span class="thread-comment__time">
            Créé {{ ticket.createdAt ? fmt(ticket.createdAt) : '—' }}
          </span>
        </div>
        <div class="thread-comment__body">
          <pre v-if="ticket.description" class="preserve">{{ ticket.description }}</pre>
          <span v-else class="text-muted">Aucune description.</span>
        </div>
      </div>
    </li>

    <li
      v-for="event in thread"
      :key="`${event.kind}-${event.id}`"
      class="timeline__item"
      :class="event.kind === 'COMMENT' ? 'timeline__item--comment' : null"
    >
      <template v-if="event.kind !== 'COMMENT'">
        <div class="timeline__head-row">
          <span class="t-meta">{{ event.at ? fmt(event.at) : '—' }}</span>
          <span class="t-sep">•</span>
          <span class="t-author">{{ author(event) }}</span>
          <span class="t-type-badge">{{ typeLabel(event) }}</span>
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
            'thread-comment--client': isClient(event),
            'thread-comment--staff': !isClient(event),
            'thread-comment--internal': !!(event as any).internalOnly
          }"
        >
          <div class="thread-comment__meta">
            <span class="thread-comment__author">{{ author(event) }}</span>
            <span class="thread-comment__time">{{ event.at ? fmt(event.at) : '—' }}</span>
            <span v-if="(event as any).internalOnly" class="thread-comment__tag" title="Interne">Interne</span>
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
</template>

<script setup lang="ts">
import type { Ticket, TicketThreadEvent, AttachmentEvent, InterventionEvent, CommentEvent } from '@/types/tickets'

interface Props {
  ticket: Ticket
  thread: TicketThreadEvent[]
}
const props = defineProps<Props>()

const formatter = new Intl.DateTimeFormat('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
const fmt = (v: string) => formatter.format(new Date(v))

function isComment(e: TicketThreadEvent): e is CommentEvent { return e.kind === 'COMMENT' }
function isAttachment(e: TicketThreadEvent): e is AttachmentEvent { return e.kind === 'ATTACHMENT' }
function isIntervention(e: TicketThreadEvent): e is InterventionEvent { return e.kind === 'INTERVENTION' }

function author(e: TicketThreadEvent) {
  const any = e as any
  if (any.authorName) return any.authorName
  if (any.authorUserId) return `Utilisateur #${any.authorUserId}`
  return 'Système'
}

function typeLabel(e: TicketThreadEvent) {
  if (isIntervention(e)) return 'Intervention'
  if (isAttachment(e)) return 'Pièce jointe'
  return e.kind
}

function isClient(e: TicketThreadEvent) {
  return isComment(e) && (e.authorIsClient === true)
}
</script>