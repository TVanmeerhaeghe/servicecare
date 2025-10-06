import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import DashboardView from "@/views/DashboardView.vue";
import ClientsLayout from "@/views/clients/ClientsLayout.vue";
import ClientsListView from "@/views/clients/ClientsListView.vue";
import ClientFormView from "@/views/clients/ClientFormView.vue";
import ClientDetailsView from "@/views/clients/ClientDetailsView.vue";
import { useAuthStore } from "@/stores/auth";

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: { public: true },
  },
  {
    path: "/",
    redirect: "/dashboard",
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    meta: { requiresAuth: true, title: "Dashboard" },
  },
  {
    path: "/clients",
    component: ClientsLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: "",
        name: "clients-list",
        component: ClientsListView,
        meta: { title: "Clients" },
      },
      {
        path: "create",
        name: "client-create",
        component: ClientFormView,
        meta: { title: "Nouveau client" },
      },
      {
        path: ":id",
        name: "client-details",
        component: ClientDetailsView,
        props: true,
        meta: { title: "Détails client" },
      },
      {
        path: ":id/edit",
        name: "client-edit",
        component: ClientFormView,
        props: true,
        meta: { title: "Modifier client" },
      },
    ],
  },
  { path: "/:pathMatch(.*)*", redirect: "/dashboard" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  if (!auth.hydrated) auth.hydrateFromStorage();

  if (to.meta.public) return true;
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: "login", query: { redirect: to.fullPath } };
  }
  if (to.name === "login" && auth.isAuthenticated) {
    return { name: "dashboard" };
  }
  return true;
});

export default router;
