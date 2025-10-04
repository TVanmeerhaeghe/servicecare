<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRoute, useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref<string | null>(null)

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

async function submit() {
  errorMsg.value = null
  loading.value = true
  try {
    const response = await auth.login({ email: email.value.trim(), password: password.value })
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.replace(redirect)
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.error?.message || 'Échec de connexion'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="card" style="max-width:420px;margin:3rem auto;">
    <h2>Connexion</h2>
    <p style="opacity:.7;">Entrez votre e-mail et votre mot de passe</p>

    <form @submit.prevent="submit" style="display:flex;flex-direction:column;gap:.75rem;margin-top:1rem;">
      <label>
        <div>Email</div>
        <input v-model="email" type="email" required autocomplete="username" />
      </label>

      <label>
        <div>Mot de passe</div>
        <input v-model="password" type="password" required autocomplete="current-password" />
      </label>

      <button :disabled="loading" type="submit">
        {{ loading ? 'Connexion…' : 'Se connecter' }}
      </button>

      <p v-if="errorMsg" style="color:#c00;margin:.5rem 0 0;">{{ errorMsg }}</p>
    </form>
  </div>
</template>
