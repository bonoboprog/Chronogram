<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="dark">
        <ion-buttons slot="start">
          <ion-back-button default-href="/settings" />
        </ion-buttons>
        <ion-title>Notifications</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list class="glass-card">
        <ion-item>
          <ion-icon :icon="mailOutline" slot="start" class="purple-icon" />
          <ion-label>Email Notifications</ion-label>
          <ion-toggle v-model="emailNotifs" color="success"></ion-toggle>
        </ion-item>

        <ion-item>
          <ion-icon :icon="notificationsOutline" slot="start" class="purple-icon" />
          <ion-label>Push Notifications</ion-label>
          <ion-toggle v-model="pushNotifs" color="primary"></ion-toggle>
        </ion-item>

        <ion-item>
          <ion-icon :icon="chatbubbleOutline" slot="start" class="purple-icon" />
          <ion-label>SMS Notifications</ion-label>
          <ion-toggle v-model="smsNotifs" color="tertiary"></ion-toggle>
        </ion-item>
      </ion-list>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  mailOutline,
  notificationsOutline,
  chatbubbleOutline
} from 'ionicons/icons'

const emailNotifs = ref(true)
const pushNotifs = ref(false)
const smsNotifs = ref(false)
</script>

<style scoped>
.glass-card {
  background-color: var(--surface0);
  border-radius: 16px;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.purple-icon {
  color: #c39bd3; /* tono morado suave tipo 'Chronogram' */
  font-size: 1.5rem;
}
</style>
