<template>
  <ion-app>
    <ion-router-outlet />
  </ion-app>
</template>

<script setup lang="ts">
import { IonApp, IonRouterOutlet } from '@ionic/vue';
import { useAuthStore } from '@/store/auth';

console.log('[App] Initializing application');
const authStore = useAuthStore();

try {
  console.log('[App] Checking authentication status');
  await authStore.checkAuthStatus();
  console.log('[App] Auth check completed');
} catch (error) {
  console.error('[App] Error during auth check:', error);
}
</script>
