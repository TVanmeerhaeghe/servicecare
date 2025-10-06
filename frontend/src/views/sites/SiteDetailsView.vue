<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <h1 class="text-lg">{{ site?.name || 'Site' }}</h1>
        <p class="text-sm text-muted">{{ site?.url || '—' }}</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Retour</button>
        <button class="btn btn-primary text-sm" @click="goEdit">Modifier</button>
      </div>
    </header>

    <section v-if="site" class="detail-grid">
      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Informations</h2>
          <dl class="info-grid">
            <div><dt>URL</dt><dd>{{ site.url || '—' }}</dd></div>
            <div><dt>Statut</dt><dd><span class="badge" :class="badgeClass(site.status)">{{ site.status || '—' }}</span></dd></div>
            <div><dt>Environnement</dt><dd>{{ site.environment || '—' }}</dd></div>
            <div><dt>Type</dt><dd>{{ site.type || '—' }}</dd></div>
            <div><dt>CMS</dt><dd>{{ site.cms || '—' }}</dd></div>
            <div><dt>Hébergeur</dt><dd>{{ site.hostingProvider || '—' }}</dd></div>
            <div><dt>Client ID</dt><dd>{{ site.clientId || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Environnements</h2>
          <dl class="info-grid">
            <div><dt>URL production</dt><dd>{{ site.prodUrl || '—' }}</dd></div>
            <div><dt>URL staging</dt><dd>{{ site.stagingUrl || '—' }}</dd></div>
            <div><dt>Dépôt</dt><dd>{{ site.repoUrl || '—' }}</dd></div>
            <div><dt>Hébergeur</dt><dd>{{ site.hostingProvider || '—' }}</dd></div>
            <div><dt>Plan d’hébergement</dt><dd>{{ site.hostingPlan || '—' }}</dd></div>
            <div><dt>IP serveur</dt><dd>{{ site.serverIp || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Versions</h2>
          <dl class="info-grid">
            <div><dt>PHP</dt><dd>{{ site.phpVersion || '—' }}</dd></div>
            <div><dt>Node</dt><dd>{{ site.nodeVersion || '—' }}</dd></div>
            <div><dt>MySQL</dt><dd>{{ site.mysqlVersion || '—' }}</dd></div>
            <div><dt>SSL</dt><dd><span class="badge" :class="sslBadge(site.sslStatus)">{{ site.sslStatus || '—' }}</span></dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Monitoring</h2>
          <dl class="info-grid">
            <div><dt>Analytics ID</dt><dd>{{ site.analyticsId || '—' }}</dd></div>
            <div><dt>Google Tag</dt><dd>{{ site.gtId || '—' }}</dd></div>
            <div><dt>Sentry DSN</dt><dd>{{ site.sentryDsn || '—' }}</dd></div>
            <div><dt>Notes</dt><dd>{{ site.notes || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Maintenance</h2>
          <dl class="info-grid">
            <div><dt>Activée</dt><dd>{{ site.maintenanceEnabled ? 'Oui' : 'Non' }}</dd></div>
            <div><dt>Email maintenance</dt><dd>{{ site.maintenanceEmail || '—' }}</dd></div>
            <div><dt>Dernière maintenance</dt><dd>{{ site.lastMaintenanceAt || '—' }}</dd></div>
            <div><dt>Prochaine maintenance</dt><dd>{{ site.nextMaintenanceAt || '—' }}</dd></div>
            <div><dt>Dernière sauvegarde</dt><dd>{{ site.lastBackupAt || '—' }}</dd></div>
          </dl>
        </div>
      </article>

      <article v-if="client" class="data-card">
        <div class="p-5">
          <h2 class="section-kicker">Client associé</h2>
          <dl class="info-grid">
            <div><dt>Nom</dt><dd>{{ client.name || '—' }}</dd></div>
            <div><dt>Statut</dt><dd>{{ client.status || '—' }}</dd></div>
            <div><dt>Email</dt><dd>{{ client.contactEmail || '—' }}</dd></div>
            <div><dt>Téléphone</dt><dd>{{ client.contactPhone || '—' }}</dd></div>
          </dl>
          <button class="btn btn-primary text-sm mt-4" @click="goToClientDetails">
            Voir le client
          </button>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchSiteDetails } from '@/api/sites'
import { fetchClientDetails } from '@/api/clients'
import type { Site } from '@/types/sites'
import type { Client } from '@/types/clients'

const route = useRoute()
const router = useRouter()
const site = ref<Site | null>(null)
const client = ref<Client | null>(null)

const badgeClass = (status: string | null) => ({
  'badge--status-active': status === 'ACTIVE',
  'badge--status-inactive': status === 'INACTIVE',
})

const sslBadge = (status: string | null) => ({
  'badge--status-active': status === 'VALID',
  'badge--status-inactive': status === 'EXPIRED' || status === 'NOT_INSTALLED',
})

async function load() {
  const { data } = await fetchSiteDetails(route.params.id as string)
  site.value = data
  if (data.clientId) {
    await loadClientInfo(data.clientId)
  }
}

async function loadClientInfo(clientId: number) {
  const { data } = await fetchClientDetails(clientId)
  client.value = data
}

function goBack() {
  router.push({ name: 'sites-list' })
}

function goEdit() {
  router.push({ name: 'site-edit', params: { id: route.params.id } })
}

function goToClientDetails() {
  if (!client.value?.id) return
  router.push({ name: 'client-details', params: { id: client.value.id } })
}

onMounted(load)
</script>