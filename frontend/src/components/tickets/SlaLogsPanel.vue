<template>
  <aside class="sla-panel card">
    <div class="sla-header">
      <h3 class="sla-title">Logs SLA</h3>
      <button @click="load" class="btn btn-ghost text-xs">Rafraîchir</button>
    </div>
    <div class="sla-scroll">
        <ul v-if="mappedLogs.length" class="sla-timeline">
        <li v-for="e in mappedLogs" :key="e.id" class="sla-item">
            <div class="sla-dot" :class="e.type.toLowerCase()" aria-hidden="true">{{ e.icon }}</div>
            <div class="sla-content">
            <div class="sla-title-row">
                <span class="sla-badge" :class="e.type.toLowerCase()">{{ label(e.type) }}</span>
                <span class="sla-title-text">{{ e.title }}</span>
            </div>
            <div v-if="e.subtitle" class="sla-subtitle">{{ e.subtitle }}</div>
            <div class="sla-meta">
                <span class="sla-when">
                <span class="rel">{{ e.rel }}</span>
                <span class="sep">•</span>
                <span class="abs">{{ e.abs }}</span>
                </span>
                <span v-if="e.displayActor" class="sla-actor">par {{ e.displayActor }}</span>
            </div>
            </div>
        </li>
        </ul>

        <div v-else class="sla-empty text-text-muted">Aucun log SLA.</div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import api from '@/api/http'

type SlaEventType = 'WAIT_START' | 'WAIT_END' | 'STATUS_CHANGE' | 'PRIORITY_CHANGE'
interface SlaEvent {
  id: number
  type: SlaEventType
  happenedAt: string
  actorUserId?: number | null
  actorUserName?: string | null
  note?: string | null
  payloadJson?: string | null
}

const props = defineProps<{ ticketId: number | null | undefined }>()
const logs = ref<SlaEvent[]>([])

async function load() {
  if (!props.ticketId) return
  try {
    const { data } = await api.get<SlaEvent[]>(`/tickets/${props.ticketId}/sla-events`)
    logs.value = data ?? []
  } catch (e) {
    console.error('SLA logs fetch failed', e)
  }
}

const mappedLogs = computed(() => {
  return logs.value.map(e => {
    const payload = parsePayload(e.payloadJson)
    return {
      ...e,
      displayActor: e.actorUserName || (e.actorUserId ? `#${e.actorUserId}` : null),
      icon: typeIcon(e.type),
      title: makeTitle(e, payload),
      subtitle: e.note || makeSubtitle(e, payload),
      abs: formatAbs(e.happenedAt),
      rel: timeAgo(new Date(e.happenedAt)),
    }
  })
})

function parsePayload(p?: string | null): any | null {
  if (!p) return null
  try { return JSON.parse(p) } catch { return null }
}

function makeTitle(e: SlaEvent, payload: any) {
  if (e.type === 'STATUS_CHANGE' && payload?.from && payload?.to) {
    return `Statut: ${payload.from} → ${payload.to}`
  }
  return label(e.type)
}

function makeSubtitle(e: SlaEvent, payload: any) {
  if (e.type === 'PRIORITY_CHANGE' && payload?.from && payload?.to) {
    return `Priorité: ${payload.from} → ${payload.to}`
  }
  return null
}

function typeIcon(t: SlaEventType) {
  switch (t) {
    case 'WAIT_START': return '⏸️'
    case 'WAIT_END': return '▶️'
    case 'STATUS_CHANGE': return '🔁'
    case 'PRIORITY_CHANGE': return '⚡'
    default: return 'ℹ️'
  }
}

function label(t: SlaEventType) {
  switch (t) {
    case 'WAIT_START': return 'Mise en attente'
    case 'WAIT_END': return 'Reprise'
    case 'STATUS_CHANGE': return 'Changement de statut'
    case 'PRIORITY_CHANGE': return 'Changement de priorité'
  }
}

function formatAbs(iso: string) {
  const d = new Date(iso)
  return d.toLocaleString(undefined, {
    year: '2-digit', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function timeAgo(date: Date) {
  const seconds = Math.floor((Date.now() - date.getTime()) / 1000)
  const rtf = new Intl.RelativeTimeFormat('fr', { numeric: 'auto' })
  const steps = [
    { s: 60, unit: 'second' as const },
    { s: 60, unit: 'minute' as const },
    { s: 24, unit: 'hour' as const },
    { s: 7, unit: 'day' as const },
    { s: 4.34524, unit: 'week' as const },
    { s: 12, unit: 'month' as const },
    { s: Infinity, unit: 'year' as const },
  ]
  let count = seconds
  let i = 0
  for (; i < steps.length && Math.abs(count) >= steps[i].s; i++) count = count / steps[i].s
  const unit = steps[i]?.unit || 'second'
  return rtf.format(-Math.round(count), unit as Intl.RelativeTimeFormatUnit)
}

onMounted(load)
watch(() => props.ticketId, load)
</script>