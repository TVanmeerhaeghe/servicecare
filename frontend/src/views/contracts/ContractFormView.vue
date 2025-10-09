<template>
  <div class="page-stack">
    <header class="page-header">
      <div>
        <p class="text-sm text-muted">Configurez le contrat (client, période, support, SLA).</p>
      </div>
      <div class="filters-controls">
        <button class="btn btn-ghost" @click="goBack">Annuler</button>
        <button class="btn btn-primary" :disabled="submitting" @click="save">
          {{ submitting ? 'Enregistrement…' : (isEdit ? 'Enregistrer' : 'Créer') }}
        </button>
      </div>
    </header>

    <section class="data-card">
      <form class="grid gap-5 p-6" @submit.prevent="save">
        <div class="form-section">
          <h2 class="form-section-title">Général</h2>
          <div class="form-grid">
            <label class="field">
              <span>Nom *</span>
              <input class="input" v-model.trim="form.name" required />
            </label>
            <label class="field" style="grid-column: 1 / -1">
              <span>Description</span>
              <textarea class="input" rows="3" v-model.trim="form.description"></textarea>
            </label>

            <label class="field" v-if="!isEdit">
              <span>Client *</span>
              <select class="input" v-model="clientIdStr" required>
                <option value="" disabled>Choisir un client…</option>
                <option v-for="c in clients" :key="c.id" :value="String(c.id)">
                  {{ c.name || ('#' + c.id) }}
                </option>
              </select>
            </label>

            <label class="field" style="grid-column: 1 / -1">
              <span>Sites</span>
              <select class="input" multiple size="5" v-model="siteIdsStr">
                <option v-for="s in sites" :key="s.id" :value="String(s.id)">
                  {{ s.name || ('Site #' + s.id) }}
                </option>
              </select>
              <small class="text-muted">Maintenez Ctrl/Cmd pour sélectionner plusieurs sites.</small>
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Période & règles</h2>
          <div class="form-grid">
            <label class="field">
              <span>Début *</span>
              <input class="input" type="date" v-model="form.startDate" required />
            </label>
            <label class="field">
              <span>Fin</span>
              <input class="input" type="date" v-model="form.endDate" />
            </label>
            <label class="field">
              <span>Renouvellement auto</span>
              <div class="inline-flex items-center gap-2">
                <input type="checkbox" v-model="form.autoRenew" /> Oui
              </div>
            </label>
            <label class="field">
              <span>Délai de préavis (jours)</span>
              <input class="input" type="number" min="0" v-model.number="form.noticeDays" />
            </label>

            <label class="field">
              <span>Fuseau horaire</span>
              <select class="input" v-model="form.timezone">
                <option value="Europe/Paris">Europe/Paris</option>
                <option value="Europe/Brussels">Europe/Brussels</option>
                <option value="Europe/London">Europe/London</option>
                <option value="UTC">UTC</option>
              </select>
            </label>

            <label class="field">
              <span>Jours de support</span>
              <select class="input" v-model="form.supportDays">
                <option v-for="opt in supportDaysOptions" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </option>
              </select>
            </label>
            <label class="field">
              <span>Heure début</span>
              <input class="input" type="time" v-model="form.supportHoursStart" />
            </label>
            <label class="field">
              <span>Heure fin</span>
              <input class="input" type="time" v-model="form.supportHoursEnd" />
            </label>

            <label class="field">
              <span>Fenêtre de mesure SLA</span>
              <select class="input" v-model="form.measureWindow">
                <option v-for="opt in measureWindowOptions" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </option>
              </select>
            </label>
            <label class="field">
              <span>Pause si ticket en attente</span>
              <div class="inline-flex items-center gap-2">
                <input type="checkbox" v-model="form.pauseOnWaiting" /> Oui
              </div>
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">SLA (délais en heures)</h2>
          <div class="form-grid">
            <label class="field">
              <span>Réponse Critique</span>
              <select class="input" v-model.number="form.respCritHours">
                <option v-for="h in hourOptions" :key="'rc'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Réponse Haute</span>
              <select class="input" v-model.number="form.respHighHours">
                <option v-for="h in hourOptions" :key="'rh'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Réponse Moyenne</span>
              <select class="input" v-model.number="form.respMediumHours">
                <option v-for="h in hourOptions" :key="'rm'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Réponse Basse</span>
              <select class="input" v-model.number="form.respLowHours">
                <option v-for="h in hourOptions" :key="'rl'+h" :value="h">{{ h }} h</option>
              </select>
            </label>

            <label class="field">
              <span>Résolution Critique</span>
              <select class="input" v-model.number="form.resoCritHours">
                <option v-for="h in hourOptions" :key="'sc'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Résolution Haute</span>
              <select class="input" v-model.number="form.resoHighHours">
                <option v-for="h in hourOptions" :key="'sh'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Résolution Moyenne</span>
              <select class="input" v-model.number="form.resoMediumHours">
                <option v-for="h in hourOptions" :key="'sm'+h" :value="h">{{ h }} h</option>
              </select>
            </label>
            <label class="field">
              <span>Résolution Basse</span>
              <select class="input" v-model.number="form.resoLowHours">
                <option v-for="h in hourOptions" :key="'sl'+h" :value="h">{{ h }} h</option>
              </select>
            </label>

            <label class="field">
              <span>Heures incluses / mois</span>
              <input class="input" type="number" min="0" v-model.number="form.includedHoursMonth" />
            </label>
            <label class="field">
              <span>Tickets max / mois</span>
              <input class="input" type="number" min="0" v-model.number="form.maxTicketsMonth" />
            </label>
          </div>
        </div>

        <div class="form-section">
          <h2 class="form-section-title">Tarifs & statut</h2>
          <div class="form-grid">
            <label class="field">
              <span>Taux heures supp. (€ / h)</span>
              <input class="input" type="number" step="0.01" min="0" v-model.number="form.overtimeRate" />
            </label>
            <label class="field">
              <span>Taux urgence (€ / h)</span>
              <input class="input" type="number" step="0.01" min="0" v-model.number="form.emergencyRate" />
            </label>
            <label class="field max-w-60">
              <span>Statut</span>
              <select class="input" v-model="form.status">
                <option value="ACTIVE">Actif</option>
                <option value="INACTIVE">Inactif</option>
                <option value="EXPIRED">Expiré</option>
              </select>
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="goBack">Annuler</button>
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            {{ submitting ? 'Enregistrement…' : (isEdit ? 'Enregistrer' : 'Créer') }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { fetchContractDetails, createContract, updateContract } from '@/api/contracts'
import { fetchClients } from '@/api/clients'
import { fetchSites } from '@/api/sites'
import type { ContractCreatePayload, ContractUpdatePayload, Contract } from '@/types/contracts'
import type { Client } from '@/types/clients'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const isClientRole = computed(() => auth.isClientRole)
const isEdit = computed(() => !!route.params.id)

const submitting = ref(false)

const form = ref<ContractUpdatePayload & { id?: number }>({
  name: '',
  description: '',
  clientId: undefined as any,
  siteIds: [],
  startDate: new Date().toISOString().slice(0,10),
  endDate: '',
  autoRenew: false,
  noticeDays: 30,
  timezone: 'Europe/Paris',
  supportDays: 'MON_FRI' as any,
  supportHoursStart: '09:00',
  supportHoursEnd: '18:00',
  measureWindow: 'BUSINESS_HOURS' as any,
  pauseOnWaiting: true,
  respCritHours: 1,
  respHighHours: 4,
  respMediumHours: 8,
  respLowHours: 24,
  resoCritHours: 4,
  resoHighHours: 16,
  resoMediumHours: 40,
  resoLowHours: 120,
  includedHoursMonth: 0,
  maxTicketsMonth: 0,
  overtimeRate: undefined,
  emergencyRate: undefined,
  status: 'ACTIVE',
})

const supportDaysOptions = [
  { value: 'MON_FRI', label: 'Lun - Ven' },
  { value: 'SEVEN_DAYS', label: '7j/7' },
]
const measureWindowOptions = [
  { value: 'BUSINESS_HOURS', label: 'Heures ouvrées' },
  { value: 'CALENDAR', label: 'Calendaires' },
]
const hourOptions = [1, 2, 4, 8, 12, 16, 24, 40, 72, 120]

const clients = ref<Client[]>([])
const sites = ref<{ id: number; name?: string | null }[]>([])

const clientIdStr = ref<string>('')
const siteIdsStr = ref<string[]>([])

async function preloadSelectors() {
  try {
    const { data } = await fetchClients({ page: 0, size: 1000, status: 'ACTIVE' as any })
    clients.value = data.content || []
  } catch {}

  try {
    const { data } = await fetchSites({ page: 0, size: 1000 })
    sites.value = (data.content || data || []) as any
  } catch {}
}

async function load() {
  await preloadSelectors()

  if (!isEdit.value) return
  const { data } = await fetchContractDetails(route.params.id as string)
  const c: Contract = data
  form.value = {
    id: c.id,
    name: c.name,
    description: c.description || '',
    clientId: c.clientId as number,
    siteIds: c.siteIds || [],
    startDate: c.startDate,
    endDate: c.endDate || '',
    autoRenew: c.autoRenew,
    noticeDays: c.noticeDays,
    timezone: c.timezone,
    supportDays: c.supportDays as any,
    supportHoursStart: (c.supportHoursStart as any)?.toString().slice(0,5),
    supportHoursEnd: (c.supportHoursEnd as any)?.toString().slice(0,5),
    measureWindow: c.measureWindow as any,
    pauseOnWaiting: c.pauseOnWaiting,
    respCritHours: c.respCritHours,
    respHighHours: c.respHighHours,
    respMediumHours: c.respMediumHours,
    respLowHours: c.respLowHours,
    resoCritHours: c.resoCritHours,
    resoHighHours: c.resoHighHours,
    resoMediumHours: c.resoMediumHours,
    resoLowHours: c.resoLowHours,
    includedHoursMonth: c.includedHoursMonth,
    maxTicketsMonth: c.maxTicketsMonth,
    overtimeRate: (c.overtimeRate as any) ?? undefined,
    emergencyRate: (c.emergencyRate as any) ?? undefined,
    status: c.status,
  }

  clientIdStr.value = String(form.value.clientId ?? '')
  siteIdsStr.value = (form.value.siteIds || []).map(id => String(id))
}

async function save() {
  if (isClientRole.value) return 
  submitting.value = true
  try {
    if (!isEdit.value) {
      form.value.clientId = clientIdStr.value ? Number(clientIdStr.value) : (undefined as any)
    }
    form.value.siteIds = (siteIdsStr.value || []).map(v => Number(v))

    const payloadBase = {
      ...form.value,
      endDate: form.value.endDate || null,
      overtimeRate: form.value.overtimeRate ?? null,
      emergencyRate: form.value.emergencyRate ?? null,
    }

    if (isEdit.value) {
      const id = route.params.id as string
      const { id: _omit, ...payload } = payloadBase
      await updateContract(id, payload as ContractUpdatePayload)
      router.push({ name: 'contract-details', params: { id } })
    } else {
      await createContract(payloadBase as ContractCreatePayload)
      router.push({ name: 'contracts-list' })
    }
  } finally {
    submitting.value = false
  }
}

function goBack() {
  if (isEdit.value) router.push({ name: 'contract-details', params: { id: route.params.id } })
  else router.push({ name: 'contracts-list' })
}

onMounted(load)
</script>