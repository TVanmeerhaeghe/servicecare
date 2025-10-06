<template>
  <div class="app-main p-6 flex flex-col gap-6">
      <div class="flex flex-wrap justify-center gap-6">
        <div class="flex-1 min-w-[250px] max-w-sm card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Tickets ouverts</p>
          <p class="text-2xl font-bold">{{ overview.openCount }}</p>
        </div>

        <div class="flex-1 min-w-[250px] max-w-sm card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Tickets en retard</p>
          <p class="text-2xl font-bold text-red-500">{{ overview.breachedOpenCount }}</p>
        </div>

        <div class="flex-1 min-w-[250px] max-w-sm card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Tickets assignés à moi</p>
          <p class="text-2xl font-bold text-blue-500">{{ overview.myAssignedOpen }}</p>
        </div>
      </div>

      <div class="flex flex-wrap justify-center gap-6">
        <div class="flex-1 min-w-[250px] max-w-md card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Temps moyen réponse (h)</p>
          <p class="text-2xl font-bold">{{ overview.avgResponseHours }}</p>
        </div>

        <div class="flex-1 min-w-[250px] max-w-md card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Temps moyen résolution (h)</p>
          <p class="text-2xl font-bold">{{ overview.avgResolveHours }}</p>
        </div>
      </div>

      <div class="flex justify-center">
        <div class="w-full max-w-md card card-hover p-6 text-center">
          <p class="text-sm text-text-muted uppercase mb-2">Nouveaux tickets aujourd'hui</p>
          <p class="text-2xl font-bold">{{ overview.todayNewTickets }}</p>
        </div>
      </div>
  </div>
</template>


<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import api from '@/api/http'

interface DashboardOverview {
  openCount: number
  breachedOpenCount: number
  avgResponseHours: number
  avgResolveHours: number
  todayNewTickets: number
  myAssignedOpen: number
}

const overview = reactive<DashboardOverview>({
  openCount: 0,
  breachedOpenCount: 0,
  avgResponseHours: 0,
  avgResolveHours: 0,
  todayNewTickets: 0,
  myAssignedOpen: 0
})

async function loadOverview() {
  try {
    const res = await api.get<DashboardOverview>('/dashboard/overview')
    const data = res.data

    overview.openCount = data.openCount
    overview.breachedOpenCount = data.breachedOpenCount
    overview.avgResponseHours = data.avgResponseHours
    overview.avgResolveHours = data.avgResolveHours
    overview.todayNewTickets = data.todayNewTickets
    overview.myAssignedOpen = data.myAssignedOpen
  } catch (err: any) {
    console.error('Erreur lors du chargement du dashboard', err)
  }
}

onMounted(() => {
  loadOverview()
})
</script>
