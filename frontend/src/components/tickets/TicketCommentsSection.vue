<template>
  <div class="ticket-comments">
    <h2 class="section-kicker">Commentaires</h2>

    <form class="comment-form" @submit.prevent="submit">
      <textarea
        v-model.trim="message"
        class="input"
        rows="3"
        placeholder="Ajouter un commentaire…"
        required
      ></textarea>
      <div class="comment-form__actions">
        <label class="inline-flex items-center gap-2 text-xs">
          <input type="checkbox" v-model="internalOnly" />
          Interne
        </label>
        <button class="btn btn-primary btn-sm" :disabled="submitting">
          {{ submitting ? 'Envoi…' : 'Publier' }}
        </button>
      </div>
    </form>

    <ul class="comment-list" v-if="comments.length">
      <li v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-item__meta">
          <span class="comment-item__author">{{ c.authorName || 'Utilisateur' }}</span>
          <span class="comment-item__date">{{ formatDate(c.createdAt) }}</span>
          <span v-if="c.internalOnly" class="badge badge--status-inactive badge--thin">Interne</span>
        </div>
        <div class="comment-item__body">{{ c.body }}</div>
        <button
          v-if="allowDelete(c)"
          class="comment-item__delete"
          @click="remove(c)"
          :disabled="deletingId === c.id"
        >
          ✕
        </button>
      </li>
    </ul>

    <div v-else class="text-sm text-muted mt-2">Aucun commentaire.</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  fetchTicketComments,
  createTicketComment,
  deleteTicketComment,
} from '@/api/ticketComments'
import type { TicketComment } from '@/types/ticketComments'

interface Props {
  ticketId: number | string
}
const props = defineProps<Props>()
const emit = defineEmits<{ (e: 'comment-posted'): void }>()

const comments = ref<TicketComment[]>([])
const message = ref('')
const internalOnly = ref(false)
const submitting = ref(false)
const deletingId = ref<number | null>(null)

const formatter = new Intl.DateTimeFormat('fr-FR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

function formatDate(v: string) {
  return formatter.format(new Date(v))
}

function allowDelete(_c: TicketComment) {
  return true
}

async function load() {
  const { data } = await fetchTicketComments(props.ticketId)
  comments.value = data.content
}

async function submit() {
  if (!message.value) return
  submitting.value = true
  try {
    await createTicketComment({
      ticketId: props.ticketId,
      body: message.value,
      internalOnly: internalOnly.value || undefined,
    })
    message.value = ''
    internalOnly.value = false
    await load()
    emit('comment-posted')
  } finally {
    submitting.value = false
  }
}

async function remove(c: TicketComment) {
  deletingId.value = c.id
  try {
    await deleteTicketComment(c.id)
    comments.value = comments.value.filter((x) => x.id !== c.id)
  } finally {
    deletingId.value = null
  }
}

onMounted(load)
</script>