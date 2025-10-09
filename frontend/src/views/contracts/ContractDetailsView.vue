<template>
  <div class="page-stack" v-if="contract">
    <header class="page-header">
      <h1>{{ contract.name }}</h1>
      <div class="filters-controls">
        <button class="btn btn-ghost" @click="goBack">Retour</button>
        <button v-if="!isClientRole" class="btn btn-primary" @click="goEdit">Modifier</button>
      </div>
    </header>

    <article class="data-card">
      <div class="p-5">
        <h2 class="section-kicker">Résumé</h2>
        <dl class="info-grid">
          <div><dt>Client</dt><dd>{{ contract.clientName || ('#' + contract.clientId) }}</dd></div>
          <div><dt>Période</dt><dd>{{ fmtDate(contract.startDate) }} → {{ fmtDate(contract.endDate) }}</dd></div>
          <div><dt>Statut</dt><dd><span class="badge">{{ statusLabel(contract.status) }}</span></dd></div>
          <div><dt>Sites</dt><dd>{{ contract.siteIds?.length || 0 }}</dd></div>
        </dl>
      </div>
    </article>

    <article class="data-card">
      <div class="p-5">
        <h2 class="section-kicker">Paramètres</h2>
        <dl class="info-grid">
          <div><dt>Fuseau</dt><dd>{{ contract.timezone }}</dd></div>
          <div><dt>Jours support</dt><dd>{{ supportDaysLabel(contract.supportDays) }}</dd></div>
          <div><dt>Heures</dt><dd>{{ fmtTime(contract.supportHoursStart) }} - {{ fmtTime(contract.supportHoursEnd) }}</dd></div>
          <div><dt>Fenêtre mesure</dt><dd>{{ measureWindowLabel(contract.measureWindow) }}</dd></div>
          <div><dt>Pause si attente</dt><dd>{{ contract.pauseOnWaiting ? 'Oui' : 'Non' }}</dd></div>
        </dl>
      </div>
    </article>

    <article class="data-card">
      <div class="p-5">
        <h2 class="section-kicker">SLA</h2>
        <dl class="info-grid">
          <div><dt>Resp (Crit/High/Med/Low)</dt><dd>{{ contract.respCritHours }}/{{ contract.respHighHours }}/{{ contract.respMediumHours }}/{{ contract.respLowHours }} h</dd></div>
          <div><dt>Reso (Crit/High/Med/Low)</dt><dd>{{ contract.resoCritHours }}/{{ contract.resoHighHours }}/{{ contract.resoMediumHours }}/{{ contract.resoLowHours }} h</dd></div>
          <div><dt>Inclus/mois</dt><dd>{{ contract.includedHoursMonth }} h</dd></div>
          <div><dt>Tickets max/mois</dt><dd>{{ contract.maxTicketsMonth }}</dd></div>
        </dl>
      </div>
    </article>

    <article class="data-card" v-if="contract.description">
      <div class="p-5">
        <h2 class="section-kicker">Description</h2>
        <pre class="preserve">{{ contract.description }}</pre>
      </div>
    </article>

    <article class="data-card">
      <div class="p-5">
        <h2 class="section-kicker">Sites liés</h2>

        <div v-if="!contract.siteIds?.length" class="text-muted">
          Aucun site lié à ce contrat.
        </div>

        <div v-else>
          <table class="data-table">
            <thead>
              <tr>
                <th>Site</th>
                <th class="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="sitesLoading">
                <td colspan="2" class="text-center text-muted py-6">Chargement…</td>
              </tr>
              <tr v-else v-for="s in sites" :key="s.id" class="data-table__row">
                <td>
                  <div class="data-table__cell">
                    <span class="data-table__cell--main">{{ s.name || ('Site #' + s.id) }}</span>
                  </div>
                </td>
                <td class="data-table__actions">
                  <div class="btn-group">
                    <button class="btn btn-ghost text-sm" @click="goToSiteDetails(s.id)">Voir le site</button>
                  </div>
                </td>
              </tr>
              <tr v-if="!sitesLoading && !sites.length">
                <td colspan="2" class="text-center text-muted py-6">
                  Impossible de charger les détails des sites.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchContractDetails } from '@/api/contracts'
import { fetchSiteDetails } from '@/api/sites'
import type { Contract } from '@/types/contracts'
import type { Site } from '@/types/sites'

const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)

const route = useRoute()
const router = useRouter()
const contract = ref<Contract | null>(null)

// Sites liés
const sites = ref<Site[]>([])
const sitesLoading = ref(false)

async function loadSitesForContract() {
  sites.value = []
  if (!contract.value?.siteIds?.length) return
  sitesLoading.value = true
  try {
    const results = await Promise.all(
      contract.value.siteIds.map((id) =>
        fetchSiteDetails(id).then((r) => r.data).catch(() => null)
      )
    )
    sites.value = results.filter(Boolean) as Site[]
  } finally {
    sitesLoading.value = false
  }
}

const fmt = new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium' })
function fmtDate(v?: string | null) { return v ? fmt.format(new Date(v)) : '—' }

const SUPPORT_DAYS_LABELS: Record<string, string> = {
  MON_FRI: 'Lun - Ven',
  SEVEN_DAYS: '7j/7',
}
const MEASURE_WINDOW_LABELS: Record<string, string> = {
  BUSINESS_HOURS: 'Heures ouvrées',
  CALENDAR: 'Calendaires',
}
const STATUS_LABELS: Record<string, string> = {
  ACTIVE: 'Actif',
  INACTIVE: 'Inactif',
  EXPIRED: 'Expiré',
}

function supportDaysLabel(v?: string | null) {
  return v ? (SUPPORT_DAYS_LABELS[v] || v) : '—'
}
function measureWindowLabel(v?: string | null) {
  return v ? (MEASURE_WINDOW_LABELS[v] || v) : '—'
}
function statusLabel(v?: string | null) {
  return v ? (STATUS_LABELS[v] || v) : '—'
}
function fmtTime(v?: string | null) {
  if (!v) return '—'
  return v.length >= 5 ? v.slice(0, 5) : v
}

async function load() {
  const { data } = await fetchContractDetails(route.params.id as string)
  contract.value = data
  await loadSitesForContract()
}
function goBack() { router.push({ name: 'contracts-list' }) }
function goEdit() { router.push({ name: 'contract-edit', params: { id: route.params.id } }) }
function goToSiteDetails(id: number) {
  router.push({ name: 'site-details', params: { id } })
}

onMounted(load)
</script>