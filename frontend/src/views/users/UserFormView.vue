<template>
  <div class="page-stack">
    <header class="page-header">
      <h1>{{ isEdit ? "Modifier l’utilisateur" : "Nouvel utilisateur" }}</h1>
      <div class="filters-controls">
        <button class="btn btn-ghost" @click="goBack">Annuler</button>
        <button class="btn btn-primary" :disabled="submitting" @click="save">
          {{
            submitting ? "Enregistrement…" : isEdit ? "Enregistrer" : "Créer"
          }}
        </button>
      </div>
    </header>

    <section class="data-card">
      <form class="grid gap-5 p-6" @submit.prevent="save">
        <div class="form-section">
          <h2 class="form-section-title">Informations</h2>
          <div class="user-form-grid">
            <label class="field col-6">
              <span>Prénom</span>
              <input class="input" v-model.trim="form.firstName" required />
            </label>
            <label class="field col-6">
              <span>Nom</span>
              <input class="input" v-model.trim="form.lastName" required />
            </label>

            <label class="field col-6">
              <span>Téléphone</span>
              <input class="input" v-model.trim="form.phone" />
            </label>
            <label class="field col-6">
              <span>Email</span>
              <input
                class="input"
                type="email"
                v-model.trim="form.email"
                required
              />
            </label>

            <label class="field col-4">
              <span>Client (optionnel)</span>
              <select class="input" v-model="clientIdStr">
                <option value="">—</option>
                <option v-for="c in clients" :key="c.id" :value="String(c.id)">
                  {{ c.name || "#" + c.id }}
                </option>
              </select>
            </label>
            <label class="field col-4">
              <span>Rôle</span>
              <select class="input" v-model="form.role">
                <option value="ADMIN">Admin</option>
                <option value="AGENT">Agent</option>
                <option value="TECHNICIAN">Technicien</option>
                <option value="CLIENT">Client</option>
              </select>
            </label>
            <label class="field col-4">
              <span>Statut</span>
              <select class="input" v-model="form.status">
                <option value="ACTIVE">Actif</option>
                <option value="DISABLED">Désactivé</option>
                <option value="INVITED">Invité</option>
              </select>
            </label>

            <label class="field col-6" v-if="!isEdit">
              <span>Mot de passe</span>
              <input
                class="input"
                type="password"
                v-model.trim="password"
                minlength="8"
                required
              />
            </label>
          </div>
        </div>

        <div v-if="isEdit" class="form-section">
          <h2 class="form-section-title">Réinitialiser le mot de passe</h2>
          <div class="reset-block">
            <label class="field">
              <span>Nouveau mot de passe</span>
              <input
                class="input"
                type="password"
                v-model.trim="newPassword"
                minlength="8"
              />
            </label>
            <div class="reset-actions">
              <button
                type="button"
                class="btn btn-ghost btn-sm"
                @click="newPassword = ''"
              >
                Effacer
              </button>
              <button
                type="button"
                class="btn btn-primary btn-sm"
                :disabled="!newPassword"
                @click="doResetPassword"
              >
                Réinitialiser
              </button>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="goBack">
            Annuler
          </button>
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            {{
              submitting ? "Enregistrement…" : isEdit ? "Enregistrer" : "Créer"
            }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  fetchUserDetails,
  createUser,
  updateUser,
  resetUserPassword,
} from "@/api/users";
import { fetchClients } from "@/api/clients";
import type { User, UserCreatePayload, UserUpdatePayload } from "@/types/users";
import type { Client } from "@/types/clients";

const route = useRoute();
const router = useRouter();
const isEdit = computed(() => !!route.params.id);

const submitting = ref(false);
const clients = ref<Client[]>([]);

const form = ref<
  UserUpdatePayload & {
    id?: number;
    email: string;
    firstName: string;
    lastName: string;
    role: "ADMIN" | "AGENT" | "TECHNICIAN" | "CLIENT";
    status: "ACTIVE" | "DISABLED" | "INVITED";
  }
>({
  email: "",
  firstName: "",
  lastName: "",
  phone: "",
  role: "CLIENT",
  status: "ACTIVE",
  clientId: null,
});
const clientIdStr = ref<string>("");
const password = ref<string>("");
const newPassword = ref<string>("");

async function load() {
  try {
    const { data } = await fetchClients({ page: 0, size: 1000 });
    clients.value = data.content || [];
  } catch {}

  if (!isEdit.value) return;
  const { data } = await fetchUserDetails(route.params.id as string);
  const u: User = data;
  form.value = {
    id: u.id,
    email: u.email,
    firstName: u.firstName,
    lastName: u.lastName,
    phone: u.phone || "",
    role: u.role,
    status: u.status,
    clientId: u.clientId ?? null,
  };
  clientIdStr.value = u.clientId ? String(u.clientId) : "";
}

async function save() {
  submitting.value = true;
  try {
    form.value.clientId = clientIdStr.value ? Number(clientIdStr.value) : null;

    if (isEdit.value) {
      const id = route.params.id as string;
      const { id: _omit, ...payload } = form.value;
      await updateUser(id, payload as UserUpdatePayload);
      router.push({ name: "user-details", params: { id } });
    } else {
      const payload: UserCreatePayload = {
        email: form.value.email,
        password: password.value,
        firstName: form.value.firstName,
        lastName: form.value.lastName,
        phone: form.value.phone || undefined,
        clientId: clientIdStr.value ? Number(clientIdStr.value) : undefined,
      };
      await createUser(payload);
      router.push({ name: "users-list" });
    }
  } finally {
    submitting.value = false;
  }
}

async function doResetPassword() {
  if (!isEdit.value || !newPassword.value) return;
  const ok = confirm("Confirmer la réinitialisation du mot de passe ?");
  if (!ok) return;
  await resetUserPassword(route.params.id as string, {
    newPassword: newPassword.value,
  });
  newPassword.value = "";
  alert("Mot de passe réinitialisé.");
}

function goBack() {
  if (isEdit.value)
    router.push({ name: "user-details", params: { id: route.params.id } });
  else router.push({ name: "users-list" });
}

onMounted(load);
</script>
