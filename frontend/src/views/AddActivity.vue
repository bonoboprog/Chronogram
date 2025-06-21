<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <!-- Navigation icons -->
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
      <div class="magic-input-section">
        <ion-item-divider color="light">
          <ion-label>
            <ion-icon :icon="sparkles" color="primary" class="magic-icon"></ion-icon>
            Describe with AI
          </ion-label>
        </ion-item-divider>

        <ion-item>
          <ion-textarea
              label="Describe with AI"
              label-placement="stacked"
              v-model="naturalInput"
              placeholder="e.g., Gym for an hour, it was tiring but enjoyable"
              :auto-grow="true"
              class="magic-textarea"
              :rows="2" ></ion-textarea>

          <ion-button @click="handleMagicInput" fill="clear" slot="end" :disabled="isLoading" style="align-self: flex-start;">
            <ion-icon v-if="!isLoading" slot="icon-only" :icon="send" color="primary"></ion-icon>
            <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
          </ion-button>

          <ion-note slot="helper">
            Tip: Include duration, cost, and feelings for better analysis.
          </ion-note>
          <ion-note slot="error" v-if="errorMessage">{{ errorMessage }}</ion-note>
        </ion-item>
      </div>

      <!-- Manual Input Section -->
      <div class="manual-form-section">
        <ion-item-divider color="light">
          <ion-label>Or fill details manually</ion-label>
        </ion-item-divider>

        <ion-item>
          <ion-icon :icon="pencilOutline" slot="start" color="medium"></ion-icon>
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
      </div>

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
  IonItemDivider, IonNote
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

// Send input to backend and populate form fields
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
    console.log('LLM Response:', data);

    if (data && typeof data === 'object') {
      let updated = false;
      for (const [key, value] of Object.entries(data)) {
        if (key in activity && value !== undefined && value !== null) {
          (activity as any)[key] = value;
          updated = true;
        }
      }
      if (!updated) {
        console.warn('No valid data received from AI:', data);
        errorMessage.value = 'No useful data found in AI response.';
      }
      naturalInput.value = '';
    } else {
      errorMessage.value = 'Invalid response from backend.';
    }

  } catch (e: unknown) {
    console.error('Error calling LLM:', e);
    if (axios.isAxiosError<ErrorResponse>(e)) {
      errorMessage.value = e.response?.data?.error || `Network error: ${e.message}`;
    } else {
      errorMessage.value = 'Unknown error.';
    }
  } finally {
    isLoading.value = false;
  }
}

// Adjust enjoyment from -3 to +3
function adjustEnjoyment(adjustment: number) {
  const newValue = activity.enjoyment + adjustment;
  if (newValue >= -3 && newValue <= 3) {
    activity.enjoyment = newValue;
  }
}

// Simulate save
function saveActivity() {
  console.log('Activity saved:', JSON.stringify(activity, null, 2));
  alert('Activity saved! Check the console.');
}
</script>

<style scoped>
.magic-input-section {
  margin-bottom: 2rem;
  border: 1px solid var(--ion-color-light-shade);
  border-radius: 8px;
  background: var(--ion-color-light-tint);
}

.magic-icon {
  margin-right: 8px;
  font-size: 1.2em;
  vertical-align: middle;
}

.magic-textarea {
  font-style: italic;
}

.manual-form-section ion-item {
  --padding-start: 0;
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

ion-note[slot="helper"] {
  font-size: 0.8em;
}
</style>
