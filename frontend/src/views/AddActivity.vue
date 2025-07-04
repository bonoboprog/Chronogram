<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-button>Home</ion-button>
          <ion-button>Calendar</ion-button>
          <ion-button>Settings</ion-button>
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

      <!-- Input Magico -->
      <div class="magic-input-section">
        <ion-item-divider color="light">
          <ion-label>
            <ion-icon :icon="sparkles" color="primary" class="magic-icon"></ion-icon>
            Describe with AI
          </ion-label>
        </ion-item-divider>

        <ion-item>
          <ion-textarea
              label="Descrizione con AI"
              label-placement="stacked"
              v-model="naturalInput"
              placeholder="Es: Palestra per un'ora, è stato faticoso ma bello"
              :auto-grow="true"
              class="magic-textarea"
              :rows="2" ></ion-textarea>

          <ion-button @click="handleMagicInput" fill="clear" slot="end" :disabled="isLoading" style="align-self: flex-start;">
            <ion-icon v-if="!isLoading" slot="icon-only" :icon="send" color="primary"></ion-icon>
            <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
          </ion-button>

          <ion-note slot="helper">
            Suggerimento: Includi durata, costo e sensazioni per un'analisi più precisa.
          </ion-note>
          <ion-note slot="error" v-if="errorMessage">{{ errorMessage }}</ion-note>
        </ion-item>
      </div>

      <!-- Inserimento Manuale -->
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
  sparkles, send, pencilOutline, checkmarkCircleOutline
} from 'ionicons/icons';
import axios from 'axios';

// ✅ API base
const API = import.meta.env.VITE_API_BASE_URL;

// ✅ Interfaccia del form
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

// ✅ Stato reattivo
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
const currentDate = new Intl.DateTimeFormat('it-IT').format(new Date());

/**
 * 🎯 Funzione che invia il prompt al backend e aggiorna il form
 */
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
    console.log('📦 Risposta LLM:', data);

    if (data && typeof data === 'object') {
      let almenoUnoAggiornato = false;

      for (const [key, value] of Object.entries(data)) {
        if (key in activity && value !== undefined && value !== null) {
          (activity as any)[key] = value;
          almenoUnoAggiornato = true;
        }
      }

      if (!almenoUnoAggiornato) {
        console.warn('⚠️ Nessun dato valido ricevuto dal backend:', data);
        errorMessage.value = '⚠️ Nessun dato utile trovato nella risposta AI.';
      }

      naturalInput.value = '';
    } else {
      errorMessage.value = '❌ Risposta malformata dal backend.';
    }

  } catch (e: unknown) {
    console.error('❌ Errore durante la chiamata LLM:', e);

    if (axios.isAxiosError<ErrorResponse>(e)) {
      errorMessage.value = e.response?.data?.error || `Errore di rete: ${e.message}`;
    } else {
      errorMessage.value = '❌ Errore sconosciuto.';
    }

  } finally {
    isLoading.value = false;
  }
}

/**
 * ➕ Enjoyment stepper (da -3 a +3)
 */
function adjustEnjoyment(adjustment: number) {
  const newValue = activity.enjoyment + adjustment;
  if (newValue >= -3 && newValue <= 3) {
    activity.enjoyment = newValue;
  }
}

/**
 * 💾 Simulazione salvataggio
 */
function saveActivity() {
  console.log('✅ Attività salvata:', JSON.stringify(activity, null, 2));
  alert('✅ Attività salvata! Controlla la console.');
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
