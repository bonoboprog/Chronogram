import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';

// NOTA: Gli import delle pagine non sono più necessari qui sopra,
// perché li carichiamo dinamicamente ("lazy loading") qui sotto.

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'// Imposta la pagina di login come predefinita
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/activity',
    name: 'AddActivity',
    component: () => import('@/views/AddActivity.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue')
  },
  // --- ROTTA AGGIUNTA ---
  {
    path: '/register',
    name: 'Register', // Questo è il nome che abbiamo usato in LoginPage.vue
    component: () => import('@/views/RegistrationPage.vue')
  },

  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPasswordPage.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/ResetPasswordPage.vue')
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: () => import('@/views/CalendarPage.vue')
  },
  {
    path: '/support',
    name: 'Support',
    component: () => import('@/views/SupportPage.vue')
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: () => import('@/views/ChangePassword.vue')
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: () => import('@/views/EditProfile.vue')
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue')
  },
  {
    path: '/delete-account',
    name: 'DeleteAccount',
    component: () => import('@/views/DeleteAccount.vue')
  },
  {
    path: '/delete-reasons',
    name: 'DeleteReasons',
    component: () => import('@/views/DeleteReasons.vue')
  },
  {
    path: '/details',
    name: 'Details',
    component: () => import('@/views/DetailsPage.vue')
  }

];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

export default router;