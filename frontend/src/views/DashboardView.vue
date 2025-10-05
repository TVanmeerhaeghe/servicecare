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

const overview = reactive({
  openCount: 24,
  breachedOpenCount: 3,
  avgResponseHours: 1.2,
  avgResolveHours: 3.8,
  todayNewTickets: 5,
  myAssignedOpen: 7
})

</script>
