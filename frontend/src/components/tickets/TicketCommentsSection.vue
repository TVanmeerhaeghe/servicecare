<template>
  <div class="ticket-comments">
    <h2 class="section-kicker">Ajouter une réponse</h2>
    <form class="comment-form" @submit.prevent="submit">
      <textarea
        v-model.trim="message"
        class="input"
        rows="3"
        placeholder="Votre réponse…"
        required
      ></textarea>
      <div class="comment-form__actions" :class="{ 'only-action': isClientRole }">
        <label v-if="!isClientRole" class="inline-flex items-center gap-2 text-xs">
          <input type="checkbox" v-model="internalOnly" />
          Interne
        </label>
        <button class="btn btn-primary btn-sm" :disabled="submitting">
          {{ submitting ? 'Envoi…' : 'Publier' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { createTicketComment } from '@/api/ticketComments'

interface Props {
  ticketId: number | string
}
const props = defineProps<Props>()
const emit = defineEmits<{ (e: 'comment-posted'): void }>()

const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)

const message = ref('')
const internalOnly = ref(false)
const submitting = ref(false)

async function submit() {
  if (!message.value) return
  submitting.value = true
  try {
    const internal = !isClientRole.value ? (internalOnly.value || undefined) : undefined
    await createTicketComment({
      ticketId: props.ticketId,
      body: message.value,
      internalOnly: internal,
    })
    message.value = ''
    internalOnly.value = false
    emit('comment-posted')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.comment-form__actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>