<template>
  <div class="flex items-center justify-center min-h-screen bg-bg">
    <div class="bg-surface rounded-xl shadow-soft1 p-10 w-1/2 max-w-lg">
      <h2 class="text-2xl font-semibold mb-4 text-text text-center">Connexion</h2>
      <p class="text-text-muted mb-8 text-center">Entrez votre e-mail et mot de passe</p>

      <form @submit.prevent="submit" class="flex flex-col">
        <label class="flex flex-col">
          <span class="text-xs text-text-muted mb-2">Email</span>
          <input
            v-model="email"
            type="email"
            required
            class="input"
            placeholder="ex: vous@exemple.com"
            autocomplete="username"
          />
        </label>

        <label class="flex flex-col">
          <span class="text-xs text-text-muted mb-2">Mot de passe</span>
          <input
            v-model="password"
            type="password"
            required
            class="input"
            placeholder="••••••••"
            autocomplete="current-password"
          />
        </label>

        <button
          type="submit"
          class="btn btn-primary w-full mt-4 text-center"
        >
          {{ loading ? 'Connexion…' : 'Se connecter' }}
        </button>

        <p v-if="errorMsg" class="text-danger text-sm mt-4 text-center">{{ errorMsg }}</p>
      </form>
    </div>
  </div>
</template>


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
    await auth.login({ email: email.value.trim(), password: password.value })
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.replace(redirect)
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.error?.message || 'Échec de connexion'
  } finally {
    loading.value = false
  }
}
</script>
