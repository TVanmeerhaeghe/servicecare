<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Renseignez les informations techniques.</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost text-sm" @click="goBack">Annuler</button>
      </div>
    </header>

    <section class="data-card">
      <form class="grid gap-6 p-6" @submit.prevent>
        <div class="form-section">
          <h2 class="form-section-title">Général</h2>
          <div class="form-grid">
            <label class="field">
              <span>Nom *</span>
              <input v-model="form.name" class="input" required />
            </label>
            <label class="field">
              <span>URL *</span>
              <input v-model="form.url" type="url" class="input" required />
            </label>
            <label class="field">
              <span>Client ID *</span>
              <input v-model.number="form.clientId" type="number" min="1" class="input" required />
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Configuration</h2>
          <div class="form-grid">
            <label class="field">
              <span>Environnement</span>
              <select v-model="form.environment" class="input">
                <option value="PROD">Production</option>
                <option value="STAGING">Staging</option>
                <option value="DEV">Développement</option>
              </select>
            </label>
            <label class="field">
              <span>Type</span>
              <select v-model="form.type" class="input">
                <option value="WEBSITE">Site web</option>
                <option value="SHOP">E-commerce</option>
                <option value="API">API</option>
                <option value="APP">Application</option>
              </select>
            </label>
            <label class="field">
              <span>CMS</span>
              <select v-model="form.cms" class="input">
                <option value="CUSTOM">Custom</option>
                <option value="WORDPRESS">WordPress</option>
                <option value="SHOPIFY">Shopify</option>
                <option value="PRESTASHOP">PrestaShop</option>
                <option value="DRUPAL">Drupal</option>
                <option value="JOOMLA">Joomla</option>
                <option value="MAGENTO">Magento</option>
                <option value="WIX">Wix</option>
                <option value="OTHER">Autre</option>
              </select>
            </label>
            <label class="field">
              <span>Statut</span>
              <select v-model="form.status" class="input">
                <option value="ACTIVE">Actif</option>
                <option value="INACTIVE">Inactif</option>
              </select>
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Environnements</h2>
          <div class="form-grid">
            <label class="field"><span>URL production</span><input v-model="form.prodUrl" class="input" /></label>
            <label class="field"><span>URL staging</span><input v-model="form.stagingUrl" class="input" /></label>
            <label class="field"><span>Dépôt</span><input v-model="form.repoUrl" class="input" /></label>
            <label class="field"><span>Hébergeur</span><input v-model="form.hostingProvider" class="input" /></label>
            <label class="field"><span>Plan d’hébergement</span><input v-model="form.hostingPlan" class="input" /></label>
            <label class="field"><span>IP serveur</span><input v-model="form.serverIp" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Versions & monitoring</h2>
          <div class="form-grid">
            <label class="field"><span>Version PHP</span><input v-model="form.phpVersion" class="input" /></label>
            <label class="field"><span>Version Node</span><input v-model="form.nodeVersion" class="input" /></label>
            <label class="field"><span>Version MySQL</span><input v-model="form.mysqlVersion" class="input" /></label>
            <label class="field">
              <span>Statut SSL</span>
              <select v-model="form.sslStatus" class="input">
                <option value="UNKNOWN">Inconnu</option>
                <option value="VALID">Valide</option>
                <option value="EXPIRED">Expiré</option>
                <option value="NOT_INSTALLED">Non installé</option>
              </select>
            </label>
            <label class="field"><span>ID Analytics</span><input v-model="form.analyticsId" class="input" /></label>
            <label class="field"><span>Google Tag ID</span><input v-model="form.gtId" class="input" /></label>
            <label class="field"><span>Sentry DSN</span><input v-model="form.sentryDsn" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Maintenance</h2>
          <div class="form-grid">
            <label class="field">
              <span>Maintenance activée</span>
              <select v-model="maintenanceEnabledString" class="input">
                <option value="true">Oui</option>
                <option value="false">Non</option>
              </select>
            </label>
            <label class="field"><span>Email maintenance</span><input v-model="form.maintenanceEmail" class="input" /></label>
            <label class="field"><span>Dernière maintenance</span><input v-model="form.lastMaintenanceAt" type="date" class="input" /></label>
            <label class="field"><span>Prochaine maintenance</span><input v-model="form.nextMaintenanceAt" type="date" class="input" /></label>
            <label class="field"><span>Dernière sauvegarde</span><input v-model="form.lastBackupAt" type="date" class="input" /></label>
          </div>
        </div>

        <div class="form-section">
            <label class="field">
            <span>Notes</span>
            <textarea v-model="form.notes" class="input" rows="4"></textarea>
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
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createSite, updateSite, fetchSiteDetails } from '@/api/sites'
import type { SitePayload, Site } from '@/types/sites'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = reactive<SitePayload>({
  clientId: null,
  name: null,
  url: null,
  environment: 'PROD',
  type: 'WEBSITE',
  cms: 'CUSTOM',
  status: 'ACTIVE',
  repoUrl: null,
  prodUrl: null,
  stagingUrl: null,
  hostingProvider: null,
  hostingPlan: null,
  serverIp: null,
  phpVersion: null,
  nodeVersion: null,
  mysqlVersion: null,
  sslStatus: 'UNKNOWN',
  analyticsId: null,
  gtId: null,
  sentryDsn: null,
  maintenanceEnabled: true,
  maintenanceEmail: null,
  lastMaintenanceAt: null,
  nextMaintenanceAt: null,
  lastBackupAt: null,
  notes: null,
})

const maintenanceEnabledString = computed({
  get: () => String(form.maintenanceEnabled ?? true),
  set: (value: string) => {
    form.maintenanceEnabled = value === 'true'
  },
})

async function preload() {
  if (!isEdit.value) return
  const { data } = await fetchSiteDetails(route.params.id as string)
  Object.assign(form, data)
}

async function submit() {
  const payload = { ...form }
  if (isEdit.value) {
    await updateSite(route.params.id as string, payload)
  } else {
    await createSite(payload)
  }
  router.push({ name: 'sites-list' })
}

function goBack() {
  router.push({ name: 'sites-list' })
}

onMounted(preload)
</script>