<template>
  <ion-app>
    <ion-router-outlet />
  </ion-app>
</template>

<script setup lang="ts">
import { IonApp, IonRouterOutlet } from '@ionic/vue';
import { useAuthStore } from '@/store/auth';
// Chiama l'azione per controllare lo stato all'avvio dell'app
const authStore = useAuthStore();
authStore.checkAuthStatus();
</script>
