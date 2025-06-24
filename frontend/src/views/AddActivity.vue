<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-button @click="goTo('Home')">
            <ion-icon :icon="homeOutline" :color="selectedTab === 'Home' ? 'peach' : ''" slot="icon-only" />
          </ion-button>
          <ion-button @click="goTo('Calendar')">
            <ion-icon :icon="calendarOutline" :color="selectedTab === 'Calendar' ? 'peach' : ''" slot="icon-only" />
          </ion-button>
          <ion-button @click="goTo('Settings')">
            <ion-icon :icon="settingsOutline" :color="selectedTab === 'Settings' ? 'peach' : ''" slot="icon-only" />
          </ion-button>
        </ion-buttons>
        <ion-title class="ion-text-right">{{ currentDate }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">New Activity</ion-title>
        </ion-toolbar>
      </ion-header>

      <!-- AI Input Section -->
      <ion-card class="input-card">
        <ion-card-header>
          <ion-card-title class="section-title">
            <ion-icon :icon="sparkles" color="primary" class="magic-icon"></ion-icon>
            Describe with AI
          </ion-card-title>
        </ion-card-header>

        <ion-card-content>
          <ion-item>
            <ion-textarea
                label="Describe with AI"
                label-placement="stacked"
                v-model="naturalInput"
                placeholder="e.g., Gym for an hour, it was tiring but enjoyable"
                :auto-grow="true"
                class="magic-textarea"
                :rows="2" />
          </ion-item>

          <ion-button expand="block" @click="handleMagicInput" :disabled="isLoading">
            <ion-icon v-if="!isLoading" slot="start" :icon="send"></ion-icon>
            <ion-spinner v-else name="crescent" slot="start"></ion-spinner>
            Use AI
          </ion-button>

          <ion-note class="helper-note">
            Tip: Include duration, cost, and feelings for better analysis.
          </ion-note>
          <ion-note color="danger" v-if="errorMessage">{{ errorMessage }}</ion-note>
        </ion-card-content>
      </ion-card>

      <!-- Manual Input Section -->
      <ion-card class="input-card">
        <ion-card-header>
          <ion-card-title class="section-title">
            <ion-icon :icon="pencilOutline" color="medium"></ion-icon>
            Or fill details manually
          </ion-card-title>
        </ion-card-header>

        <ion-card-content>
          <ion-item>
            <ion-input label="Name of New Activity" label-placement="stacked" v-model="activity.name"></ion-input>
          </ion-item>

          <ion-item>
            <ion-input label="Duration (min)" label-placement="stacked" type="number" v-model="activity.duration"></ion-input>
          </ion-item>

          <ion-item>
            <ion-textarea label="Details" label-placement="stacked" :auto-grow="true" v-model="activity.details"></ion-textarea>
          </ion-item>

          <ion-item>
            <ion-label>Enjoyment</ion-label>
            <div class="stepper" slot="end">
              <ion-button @click="adjustEnjoyment(-1)" fill="clear" size="small">-</ion-button>
              <span class="stepper-value">{{ activity.enjoyment }}</span>
              <ion-button @click="adjustEnjoyment(1)" fill="clear" size="small">+</ion-button>
            </div>
          </ion-item>

          <ion-item>
            <ion-input label="Category" label-placement="stacked" v-model="activity.category"></ion-input>
          </ion-item>

          <ion-item>
            <ion-select label="Type of activity" label-placement="stacked" interface="popover" v-model="activity.type">
              <ion-select-option value="instrumental">Instrumental</ion-select-option>
              <ion-select-option value="final">Final</ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-select label="Recurrence" label-placement="stacked" interface="popover" v-model="activity.recurrence">
              <ion-select-option value="R">Routinary (R)</ion-select-option>
              <ion-select-option value="E">Exceptional (E)</ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-input label="Cost (€)" label-placement="stacked" type="number" inputmode="decimal" v-model="activity.cost"></ion-input>
          </ion-item>

          <ion-item>
            <ion-input label="Location" label-placement="stacked" v-model="activity.location"></ion-input>
          </ion-item>
        </ion-card-content>
      </ion-card>

      <ion-button expand="block" @click="saveActivity" class="ion-margin-top" size="large">
        Save Activity
        <ion-icon slot="end" :icon="checkmarkCircleOutline"></ion-icon>
      </ion-button>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonButton, IonIcon,
  IonItem, IonLabel, IonInput, IonTextarea, IonSelect, IonSelectOption, IonSpinner,
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonNote, toastController
} from '@ionic/vue';

import {
  sparkles, send, pencilOutline, checkmarkCircleOutline,
  homeOutline, calendarOutline, settingsOutline
} from 'ionicons/icons';

import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const selectedTab = ref(route.name?.toString() || '');

function goTo(name: string) {
  selectedTab.value = name;
  router.push({ name });
}

const API = import.meta.env.VITE_API_BASE_URL;

interface ActivityForm {
  name: string;
  duration: number | null;
  details: string;
  enjoyment: number;
  category: string;
  type: 'instrumental' | 'final' | '';
  recurrence: 'R' | 'E' | '';
  cost: number | null;
  location: string;
}

interface ErrorResponse {
  error: string;
}

const activity = reactive<ActivityForm>({
  name: '',
  duration: null,
  details: '',
  enjoyment: 0,
  category: '',
  type: '',
  recurrence: '',
  cost: null,
  location: ''
});

const naturalInput = ref('');
const isLoading = ref(false);
const errorMessage = ref<string | null>(null);
const currentDate = new Intl.DateTimeFormat('en-GB').format(new Date());

async function handleMagicInput() {
  if (!naturalInput.value.trim()) return;
  isLoading.value = true;
  errorMessage.value = null;

  try {
    const response = await axios.post<Partial<ActivityForm>>(
        `${API}/llmPrompt`,
        { prompt: naturalInput.value },
        { headers: { 'Content-Type': 'application/json' } }
    );

    const data = response.data;
    if (data && typeof data === 'object') {
      let updated = false;
      for (const [key, value] of Object.entries(data)) {
        if (key in activity && value !== undefined && value !== null) {
          (activity as any)[key] = value;
          updated = true;
        }
      }
      if (!updated) {
        errorMessage.value = 'No useful data found in AI response.';
      }
      naturalInput.value = '';
    } else {
      errorMessage.value = 'Invalid response from backend.';
    }
  } catch (e: unknown) {
    if (axios.isAxiosError<ErrorResponse>(e)) {
      errorMessage.value = e.response?.data?.error || `Network error: ${e.message}`;
    } else {
      errorMessage.value = 'Unknown error.';
    }
  } finally {
    isLoading.value = false;
  }
}

function adjustEnjoyment(adjustment: number) {
  const newValue = activity.enjoyment + adjustment;
  if (newValue >= -3 && newValue <= 3) {
    activity.enjoyment = newValue;
  }
}

async function saveActivity() {
  console.log('Activity saved:', JSON.stringify(activity, null, 2));
  const toast = await toastController.create({
    message: 'Activity saved successfully!',
    duration: 2000,
    color: 'success',
    position: 'bottom'
  });
  await toast.present();
}
</script>

<style scoped>
.input-card {
  margin-bottom: 2rem;
  border-radius: 12px;
  background-color: #2b2b3a; /* gris oscuro suave */
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.section-title {
  font-size: 1.1em;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e0aaff; /* rosita pastel */
}



.magic-icon {
  font-size: 1.2em;
}

.magic-textarea {
  font-style: italic;
}

.helper-note {
  margin-top: 0.5rem;
  display: block;
  color: #aaa;
}


.stepper {
  display: flex;
  align-items: center;
}

.stepper-value {
  font-size: 1.2em;
  font-weight: bold;
  padding: 0 16px;
  min-width: 40px;
  text-align: center;
}

ion-title.ion-text-right {
  padding-inline-end: 16px;
  font-size: 0.9em;
  color: var(--ion-color-medium-shade);
}
</style>
