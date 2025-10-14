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
          <span class="t-sep"> • </span>
          <span v-if="author(event)" class="t-author">{{ author(event) }}</span>
          <span class="t-type-badge"> {{ typeLabel(event) }}</span>
        </div>

        <div v-if="isAttachment(event)" class="timeline__body attachment-block">
          <template v-if="isImageAttachment(event)">
            <div style="display:inline-block; border:1px solid var(--color-border); border-radius:6px; overflow:hidden; max-width:480px;">
              <img
                :src="imageSrc[event.id]"
                :alt="event.originalName || 'Image'"
                style="display:block; max-width:100%; height:auto;"
              />
            </div>
            <div class="text-muted text-xs" style="margin-top:4px;">
              {{ event.originalName }}
            </div>
          </template>

          <template v-else>
            <button class="btn btn-ghost btn-xs" @click="downloadAttachment(event.id, event.originalName || 'fichier')">
              Télécharger {{ event.originalName }}
            </button>
          </template>
        </div>

        <div v-else-if="isIntervention(event)" class="timeline__body">
          {{ event.title || 'Intervention' }}
          <div class="t-sub">
            {{ [event.interventionType, event.interventionStatus].filter(Boolean).join(' • ') }}
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
          <div class="thread-comment__meta"
               style="display:flex; align-items:center; justify-content:space-between; gap:8px;">
            <div style="display:flex; align-items:center; gap:8px;">
              <span v-if="author(event)" class="thread-comment__author">{{ author(event) }}</span>
              <span class="thread-comment__time">{{ event.at ? fmt(event.at) : '—' }}</span>
            </div>
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
import { fetchAttachmentBlob } from '@/api/tickets'
import { ref, watch, onBeforeUnmount, toRefs } from 'vue'

interface Props {
  ticket: Ticket
  thread: TicketThreadEvent[]
}
const props = defineProps<Props>()
const { thread } = toRefs(props) 

const formatter = new Intl.DateTimeFormat('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
const fmt = (v: string) => formatter.format(new Date(v))

function isComment(e: TicketThreadEvent): e is CommentEvent { return e.kind === 'COMMENT' }
function isAttachment(e: TicketThreadEvent): e is AttachmentEvent { return e.kind === 'ATTACHMENT' }
function isIntervention(e: TicketThreadEvent): e is InterventionEvent { return e.kind === 'INTERVENTION' }

function isImageAttachment(e: TicketThreadEvent) {
  if (!isAttachment(e)) return false
  const ct = (e as AttachmentEvent).contentType || ''
  const name = (e as AttachmentEvent).originalName || ''
  return ct.startsWith('image/') || /\.(png|jpe?g|gif|webp|bmp|svg)$/i.test(name)
}

function author(e: TicketThreadEvent) {
  const any = e as any
  if (any.authorName) return any.authorName
  if (any.authorUserId) return `Utilisateur #${any.authorUserId}`
  return ''
}

function typeLabel(e: TicketThreadEvent) {
  if (isIntervention(e)) return 'Intervention'
  if (isAttachment(e)) return 'Pièce jointe'
  return e.kind
}

function isClient(e: TicketThreadEvent) {
  return isComment(e) && (e.authorIsClient === true)
}

const imageSrc = ref<Record<number, string>>({})

async function loadImageBlob(id: number) {
  if (imageSrc.value[id]) return
  try {
    const res = await fetchAttachmentBlob(id)
    const url = URL.createObjectURL(res.data)
    imageSrc.value[id] = url
  } catch {
  }
}

watch(
  thread,
  (list) => {
    const items = (list || []).filter(isAttachment).filter(isImageAttachment)
    for (const e of items) {
      loadImageBlob(e.id)
    }
  },
  { immediate: true, deep: true }
)

async function downloadAttachment(id: number, name: string) {
  try {
    const res = await fetchAttachmentBlob(id)
    const blobUrl = URL.createObjectURL(res.data)
    const a = document.createElement('a')
    a.href = blobUrl
    a.download = name
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(blobUrl)
  } catch (e) {
    alert('Téléchargement impossible')
  }
}

onBeforeUnmount(() => {
  Object.values(imageSrc.value).forEach((u) => u && URL.revokeObjectURL(u))
})
</script>