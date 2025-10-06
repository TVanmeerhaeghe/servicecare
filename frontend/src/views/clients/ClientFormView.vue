<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">
          Renseignez les informations principales et le contact.
        </p>
      </div>
      <button class="btn btn-ghost text-sm" @click="goBack">Annuler</button>
    </header>

    <section class="data-card">
      <form class="grid gap-5 p-6" @submit.prevent="submit">
        <div class="form-section">
          <h2 class="form-section-title">Général</h2>
          <div class="form-grid">
            <label class="field"><span>Nom *</span><input v-model="form.name" class="input" required /></label>
            <label class="field"><span>Dénomination légale</span><input v-model="form.legalName" class="input" /></label>
            <label class="field"><span>SIRET</span><input v-model="form.siret" class="input" /></label>
            <label class="field"><span>Numéro TVA</span><input v-model="form.vatNumber" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Contact</h2>
          <div class="form-grid">
            <label class="field"><span>Prénom</span><input v-model="form.contactFirstName" class="input" /></label>
            <label class="field"><span>Nom</span><input v-model="form.contactLastName" class="input" /></label>
            <label class="field"><span>Email</span><input v-model="form.contactEmail" type="email" class="input" /></label>
            <label class="field"><span>Téléphone</span><input v-model="form.contactPhone" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Communication</h2>
          <div class="form-grid">
            <label class="field"><span>Email facturation</span><input v-model="form.billingEmail" type="email" class="input" /></label>
            <label class="field"><span>Email technique</span><input v-model="form.technicalEmail" type="email" class="input" /></label>
            <label class="field"><span>Site web</span><input v-model="form.websiteUrl" class="input" /></label>
            <label class="field"><span>Devise</span><input v-model="form.currencyCode" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Adresse</h2>
          <div class="form-grid">
            <label class="field"><span>Adresse</span><input v-model="form.addressLine1" class="input" /></label>
            <label class="field"><span>Ville</span><input v-model="form.city" class="input" /></label>
            <label class="field"><span>Code postal</span><input v-model="form.postalCode" class="input" /></label>
            <label class="field"><span>Pays</span><input v-model="form.countryCode" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <label class="field max-w-60">
            <span>Statut</span>
            <select v-model="form.status" class="input">
              <option value="ACTIF">Actif</option>
              <option value="INACTIF">Inactif</option>
              <option value="LEAD">Lead</option>
            </select>
          </label>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="goBack">
            Annuler
          </button>
          <button type="submit" class="btn btn-primary">
            {{ isEdit ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createClient, updateClient, fetchClientDetails } from '@/api/clients'
import type { ClientPayload, Client } from '@/types/clients'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const form = reactive<ClientPayload>({
  name: null,
  legalName: null,
  siret: null,
  vatNumber: null,
  contactFirstName: null,
  contactLastName: null,
  contactEmail: null,
  contactPhone: null,
  billingEmail: null,
  technicalEmail: null,
  websiteUrl: null,
  addressLine1: null,
  postalCode: null,
  city: null,
  countryCode: null,
  currencyCode: null,
  status: 'ACTIF',
})

async function preload() {
  if (!isEdit.value) return
  const data: Client = (await fetchClientDetails(route.params.id as string)).data
  Object.assign(form, data)
}

async function submit() {
  if (isEdit.value) {
    await updateClient(route.params.id as string, form)
  } else {
    await createClient(form)
  }
  router.push({ name: 'clients-list' })
}

const goBack = () => router.push({ name: 'clients-list' })

onMounted(preload)
</script>