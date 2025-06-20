<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-segment :value="selectedTab" @ionChange="navigateTab">
          <ion-segment-button value="home">
            <ion-icon :icon="homeOutline" />
          </ion-segment-button>
          <ion-segment-button value="calendar">
            <ion-icon :icon="calendarOutline" />
          </ion-segment-button>
          <ion-segment-button value="settings">
            <ion-icon :icon="settingsOutline" />
          </ion-segment-button>
        </ion-segment>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="user-info gradient-text">
        <ion-icon :icon="personCircleOutline" class="user-icon" />
        <h2>User name</h2>
      </div>

      <h1 class="current-date gradient-text">{{ currentDate }}</h1>

      <div class="activity-card" v-for="(activity, index) in activities" :key="index">
        <p><strong>{{ activity.time }} - {{ activity.title }}</strong></p>
        <div class="button-group">
          <ion-button class="gradient-outline pill-button" size="small">Details</ion-button>
          <ion-button class="peach-gradient pill-button" size="small">Add new activity</ion-button>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {
  IonPage, IonHeader, IonToolbar, IonSegment, IonSegmentButton,
  IonContent, IonIcon, IonButton
} from '@ionic/vue';
import {
  homeOutline, calendarOutline, settingsOutline, personCircleOutline
} from 'ionicons/icons';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const selectedTab = ref(route.name?.toString().toLowerCase() || 'home');

const navigateTab = (event: CustomEvent) => {
  const tab = event.detail.value;
  if (tab === 'home') router.push({ name: 'Home' });
  else if (tab === 'calendar') router.push({ name: 'Calendar' });
  else if (tab === 'settings') router.push({ name: 'Settings' });
};

const activities = [
  { time: '8:00', title: 'Morning Activity' },
  { time: '9:00', title: 'University' }
];

const now = new Date();
const currentDate = now.toLocaleDateString('en-GB');

// ✅ Imposta il tema mocha all’avvio
onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
});
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.user-icon {
  font-size: 2rem;
  color: var(--ion-color-primary);
}

.current-date {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 2rem;
}

.activity-card {
  background-color: var(--surface1);
  border: 1px solid var(--overlay1);
  border-radius: 16px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  color: var(--text);
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 0.5rem;
}

ion-segment-button {
  padding: 0.5rem;
  border-radius: 9999px;
  backdrop-filter: blur(6px);
  --color-checked: var(--peach);
  --indicator-color: transparent;
}
</style>