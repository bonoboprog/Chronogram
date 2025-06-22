import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';

// NOTA: Gli import delle pagine non sono più necessari qui sopra,
// perché li carichiamo dinamicamente ("lazy loading") qui sotto.

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login' // Imposta la pagina di login come predefinita
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/AddActivity.vue')
  },
  // --- ROTTA AGGIUNTA ---
  {
    path: '/register',
    name: 'Register', // Questo è il nome che abbiamo usato in LoginPage.vue
    component: () => import('@/views/RegistrationPage.vue')
  },
  {
    path: '/ForgotPassword',
    name: 'ForgotPasswordPage', // Add "Page" to match your component
    component: () => import('@/views/ForgotPasswordPage.vue')
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

export default router;