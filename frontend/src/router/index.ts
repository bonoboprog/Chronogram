import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login' // Puoi reindirizzare a home o login a seconda della tua logica iniziale
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegistrationPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPasswordPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/ResetPasswordPage.vue'),
    meta: { requiresAuth: false }
  },
  // 👇 Rotte protette (richiedono autenticazione)
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomePageVio.vue'),
    meta: { requiresAuth: true }
  },
  {
    // Modifica: Rotta unica per aggiungere e modificare attività
    // L'ID è opzionale: /activity-form per aggiungere, /activity-form/123 per modificare
    path: '/activity-form/:id?', // <-- ID opzionale
    name: 'ActivityForm', // <-- Nome significativo per la navigazione
    component: () => import('@/views/ActivityFormPage.vue'), // <-- Nome del componente unificato
    meta: { requiresAuth: true },
    props: true // Permette di passare l'ID come prop al componente
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/calendar',
    name: 'Calendar',
    component: () => import('@/views/CalendarPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support',
    name: 'Support',
    component: () => import('@/views/SupportPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: () => import('@/views/ChangePassword.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: () => import('@/views/EditProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/delete-account',
    name: 'DeleteAccount',
    component: () => import('@/views/DeleteAccount.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/delete-reasons',
    name: 'DeleteReasons',
    component: () => import('@/views/DeleteReasons.vue'),
    meta: { requiresAuth: true }
  },
  {
    // Modifica: Rotta per i dettagli dell'attività con ID dinamico
    path: '/details/:id', // <-- ID obbligatorio per i dettagli
    name: 'Details',
    component: () => import('@/views/DetailsPage.vue'),
    meta: { requiresAuth: true },
    props: true // Permette di passare l'ID come prop al componente
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach(async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const authStore = useAuthStore();

  // ***** PUNTO CRUCIALE *****
  // Assicurati che lo stato di autenticazione sia caricato da SecureStorage
  // prima di prendere decisioni sulla navigazione.
  // Questo è asincrono e deve essere atteso.
  if (!authStore.token && !authStore.user) { // Controllo più preciso per evitare chiamate inutili
    console.log('[Router] Token or user not in store. Attempting to check status from storage...');
    await authStore.checkAuthStatus(); // <-- CHIAMATA FONDAMENTALE QUI
  }
  // ********************************************

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const isAuthenticated = authStore.isAuthenticated; // Questo ora riflette lo stato reale dopo checkAuthStatus()

  console.log(`[Router] Navigating to: ${to.path}`);
  console.log(`[Router] Requires auth: ${requiresAuth}`);
  console.log(`[Router] User authenticated (from store): ${isAuthenticated}`);

  // Se la rotta richiede autenticazione ma l'utente NON è loggato
  if (requiresAuth && !isAuthenticated) {
    console.log('[Router] Access denied. Redirecting to login');
    return next({
      name: 'Login',
      query: { redirect: to.fullPath }
    });
  }

  // Se l'utente è loggato ma prova ad accedere a login/register/forgot-password/reset-password
  // (rotte che non richiedono autenticazione ma sono tipicamente per non autenticati)
  if (!requiresAuth && isAuthenticated && (to.name === 'Login' || to.name === 'Register' || to.name === 'ForgotPassword' || to.name === 'ResetPassword')) {
    console.log('[Router] User already authenticated. Redirecting to home');
    return next({ name: 'Home' });
  }

  console.log('[Router] Access granted');
  next();
});

export default router;