<template>
  <div class="ticket-comments">
    <h2 class="section-kicker">Ajouter une réponse</h2>
    <form class="comment-form" @submit.prevent="submit">
      <textarea
        v-model.trim="message"
        class="input"
        rows="3"
        placeholder="Votre réponse…"
      ></textarea>

      <div class="comment-form__actions" :class="{ 'only-action': isClientRole }">
        <div style="display:flex; align-items:center; gap:8px; flex-wrap:wrap;">
          <button type="button" class="btn btn-ghost btn-sm" :disabled="submitting" @click="pickFiles">
            {{ files.length ? `Ajouter (+${files.length})` : 'Ajouter des fichiers' }}
          </button>
          <input
            ref="fileInput"
            type="file"
            multiple
            accept="image/*,application/pdf,.zip,.7z,.rar,.txt,.log"
            @change="onFiles"
            style="display:none"
          />

          <label v-if="!isClientRole" class="inline-flex items-center gap-2 text-xs">
            <input type="checkbox" v-model="internalOnly" />
            Interne
          </label>
        </div>

        <button class="btn btn-primary btn-sm" :disabled="!canSubmit || submitting">
          {{ submitting ? submitLabelBusy : submitLabel }}
        </button>
      </div>

      <div v-if="files.length" style="margin-top:8px;">
        <div style="display:grid; grid-template-columns:repeat(auto-fill, minmax(120px,1fr)); gap:8px;">
          <div v-for="(f, i) in files" :key="i" style="border:1px solid var(--color-border); border-radius:6px; padding:6px;">
            <div v-if="isImage(f)" style="width:100%; aspect-ratio: 4/3; overflow:hidden; border-radius:4px; background:var(--color-surface-alt); display:flex; align-items:center; justify-content:center;">
              <img :src="previews[i]" :alt="f.name" style="max-width:100%; max-height:100%; object-fit:cover;" />
            </div>
            <div v-else class="text-sm" style="word-break:break-all;">
              {{ f.name }} <span class="text-muted">({{ formatSize(f.size) }})</span>
            </div>
          </div>
        </div>
        <div style="margin-top:6px; display:flex; gap:8px;">
          <button type="button" class="btn btn-ghost btn-xs" :disabled="submitting" @click="clearFiles">Vider les fichiers</button>
        </div>
      </div>

      <div v-if="error" class="text-error text-sm" style="margin-top:6px;">{{ error }}</div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { createTicketComment } from '@/api/ticketComments'
import { uploadTicketAttachment } from '@/api/tickets'

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

const fileInput = ref<HTMLInputElement | null>(null)
const files = ref<File[]>([])
const previews = ref<string[]>([])
const error = ref<string | null>(null)

const submitLabel = computed(() => (files.value.length && !message.value ? 'Publier les fichiers' : 'Publier'))
const submitLabelBusy = computed(() => (files.value.length && !message.value ? 'Upload…' : 'Envoi…'))
const canSubmit = computed(() => !!message.value || files.value.length > 0)

function pickFiles() {
  fileInput.value?.click()
}
function onFiles(e: Event) {
  const input = e.target as HTMLInputElement
  const list = Array.from(input.files || [])
  files.value = list
  previews.value.forEach((u) => URL.revokeObjectURL(u))
  previews.value = list.map((f) => (isImage(f) ? URL.createObjectURL(f) : ''))
  error.value = null
}
function clearFiles() {
  files.value = []
  previews.value.forEach((u) => u && URL.revokeObjectURL(u))
  previews.value = []
  if (fileInput.value) fileInput.value.value = ''
}
function isImage(f: File) {
  return f.type?.startsWith('image/')
}
function formatSize(n: number) {
  if (n < 1024) return `${n} B`
  if (n < 1024 * 1024) return `${(n / 1024).toFixed(1)} KB`
  return `${(n / (1024 * 1024)).toFixed(1)} MB`
}

onBeforeUnmount(() => {
  previews.value.forEach((u) => u && URL.revokeObjectURL(u))
})

async function submit() {
  if (!canSubmit.value) return
  submitting.value = true
  error.value = null
  try {
    if (message.value) {
      const internal = !isClientRole.value ? (internalOnly.value || undefined) : undefined
      await createTicketComment({
        ticketId: props.ticketId,
        body: message.value,
        internalOnly: internal,
      })
      message.value = ''
      internalOnly.value = false
    }

    if (files.value.length) {
      for (const f of files.value) {
        await uploadTicketAttachment(props.ticketId, f)
      }
      clearFiles()
    }

    emit('comment-posted')
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Échec de l’envoi'
  } finally {
    submitting.value = false
  }
}
</script>
