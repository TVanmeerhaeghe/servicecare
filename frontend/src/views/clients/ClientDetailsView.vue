<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <h1 class="text-lg">{{ client?.name || 'Client' }}</h1>
        <p class="text-sm text-muted">{{ client?.legalName || '—' }}</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Retour</button>
        <button class="btn btn-primary text-sm" @click="goEdit">Modifier</button>
      </div>
    </header>

    <section class="detail-grid" v-if="client">
      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Coordonnées</h2>
          <dl class="info-grid">
            <div>
              <dt>Statut</dt>
              <dd>
                <span class="badge" :class="client.status === 'ACTIF' ? 'badge--primary' : ''">
                  {{ client.status || '—' }}
                </span>
              </dd>
            </div>
            <div>
              <dt>Contact</dt>
              <dd>{{ contactFullName || '—' }}</dd>
            </div>
            <div>
              <dt>Email contact</dt>
              <dd>{{ client.contactEmail || '—' }}</dd>
            </div>
            <div>
              <dt>Téléphone</dt>
              <dd>{{ client.contactPhone || '—' }}</dd>
            </div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Informations légales</h2>
          <dl class="info-grid">
            <div><dt>Dénomination légale</dt><dd>{{ client.legalName || '—' }}</dd></div>
            <div><dt>SIRET</dt><dd>{{ client.siret || '—' }}</dd></div>
            <div><dt>TVA</dt><dd>{{ client.vatNumber || '—' }}</dd></div>
            <div><dt>Devise</dt><dd>{{ client.currencyCode || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Communication</h2>
          <dl class="info-grid">
            <div><dt>Email facturation</dt><dd>{{ client.billingEmail || '—' }}</dd></div>
            <div><dt>Email technique</dt><dd>{{ client.technicalEmail || '—' }}</dd></div>
            <div><dt>Site web</dt><dd>{{ client.websiteUrl || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Adresse</h2>
          <dl class="info-grid">
            <div><dt>Adresse</dt><dd>{{ client.addressLine1 || '—' }}</dd></div>
            <div><dt>Ville</dt><dd>{{ client.city || '—' }}</dd></div>
            <div><dt>Code postal</dt><dd>{{ client.postalCode || '—' }}</dd></div>
            <div><dt>Pays</dt><dd>{{ client.countryCode || '—' }}</dd></div>
          </dl>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchClientDetails } from '@/api/clients'
import type { Client } from '@/types/clients'

const route = useRoute()
const router = useRouter()
const client = ref<Client | null>(null)

const contactFullName = computed(() => {
  if (!client.value) return ''
  return [client.value.contactFirstName, client.value.contactLastName].filter(Boolean).join(' ').trim()
})

async function load() {
  const { data } = await fetchClientDetails(route.params.id as string)
  client.value = data
}

const goBack = () => router.push({ name: 'clients-list' })
const goEdit = () => router.push({ name: 'client-edit', params: { id: route.params.id } })

onMounted(load)
</script>