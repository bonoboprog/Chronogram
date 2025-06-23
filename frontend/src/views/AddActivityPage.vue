<template>
  <ion-page>
    <!-- ▸ HEADER -->
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start"><ion-back-button default-href="/home" /></ion-buttons>
        <ion-title class="title-centered">
          <ion-icon :icon="sparkles" class="title-icon" /> New Activity
        </ion-title>
        <ion-note slot="end" class="date-note">{{ currentDate }}</ion-note>
      </ion-toolbar>
    </ion-header>

    <!-- ▸ CONTENT -->
    <ion-content :fullscreen="true" class="ion-padding content-safe">

      <!-- Name -->
      <ion-item>
        <ion-input v-model="activity.name"
                   label="Name of Activity" label-placement="stacked" />
      </ion-item>

      <!-- Duration -->
      <ion-item>
        <ion-input v-model.number="activity.duration" type="number"
                   label="Duration (min)" label-placement="stacked" />
      </ion-item>

      <!-- Details -->
      <ion-card class="details-card">
        <ion-card-content>
          <ion-textarea v-model="activity.details" :auto-grow="true"
                        :maxlength="400" counter
                        label="Details" label-placement="stacked"
                        placeholder="Add extra notes…" />
        </ion-card-content>
      </ion-card>

      <!-- Category -->
      <ion-item>
        <ion-input v-model="activity.category"
                   label="Category" label-placement="stacked" />
      </ion-item>

      <!-- Enjoyment (moved below Category) -->
      <ion-item class="enjoyment-item" lines="none">
        <ion-label>Enjoyment</ion-label>
        <div class="stepper" slot="end">
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(-1)">−</ion-button>
          <span class="stepper-value">{{ activity.enjoyment }}</span>
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(1)">+</ion-button>
        </div>
      </ion-item>

      <!-- Type -->
      <ion-item>
        <ion-select v-model="activity.type" placeholder="Select type"
                    label="Type of activity" label-placement="stacked">
          <ion-select-option value="instrumental">Instrumental</ion-select-option>
          <ion-select-option value="final">Final</ion-select-option>
        </ion-select>
      </ion-item>

      <!-- Recurrence -->
      <ion-item>
        <ion-select v-model="activity.recurrence" placeholder="Select recurrence"
                    label="Recurrence" label-placement="stacked">
          <ion-select-option value="R">Routinary (R)</ion-select-option>
          <ion-select-option value="E">Exceptional (E)</ion-select-option>
        </ion-select>
      </ion-item>

      <!-- Cost -->
      <ion-item>
        <ion-input v-model.number="activity.cost" type="number" inputmode="decimal"
                   label="Cost (€)" label-placement="stacked" />
      </ion-item>

      <!-- Location -->
      <ion-item>
        <ion-input v-model="activity.location"
                   label="Location" label-placement="stacked" />
      </ion-item>

      <!-- Save -->
      <ion-button expand="block" size="large" class="save-btn" @click="saveActivity">
        Save Activity <ion-icon slot="end" :icon="checkmarkCircleOutline" />
      </ion-button>

      <!-- FAB -->
      <ion-fab vertical="bottom" horizontal="end" slot="fixed" class="ai-fab">
        <ion-fab-button @click="openAIModal"><ion-icon :icon="sparkles" /></ion-fab-button>
      </ion-fab>

      <!-- MODAL -->
      <ion-modal :is-open="isModalOpen" @did-dismiss="closeAIModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>Describe your activity</ion-title>
            <ion-buttons slot="end"><ion-button @click="closeAIModal">Close</ion-button></ion-buttons>
          </ion-toolbar>
        </ion-header>

        <ion-content class="ion-padding">
          <ion-textarea v-model="naturalInput" :auto-grow="true" :rows="3"
                        label="Description" label-placement="stacked"
                        placeholder="e.g. One-hour gym session…" />
          <ion-note color="medium" class="ion-margin-vertical">
            Tip: include duration, cost and how you felt for better suggestions.
          </ion-note>
          <div class="ion-text-end">
            <ion-button :disabled="isLoading" @click="handleMagicInput">
              <ion-icon v-if="!isLoading" :icon="send" slot="start" />
              <ion-spinner v-else name="crescent" />
              Send
            </ion-button>
          </div>
          <ion-note v-if="errorMessage" color="danger">{{ errorMessage }}</ion-note>
        </ion-content>
      </ion-modal>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonButton, IonIcon, IonItem, IonLabel, IonInput, IonTextarea, IonSelect,
  IonSelectOption, IonNote, IonFab, IonFabButton, IonModal, IonCard, IonCardContent,
  IonSpinner
} from '@ionic/vue'
import { sparkles, send, checkmarkCircleOutline } from 'ionicons/icons'
import axios from 'axios'

/* ---------- State ---------- */
const API = import.meta.env.VITE_API_BASE_URL

interface ActivityForm {
  name: string; duration: number | null; details: string; enjoyment: number;
  category: string; type: 'instrumental' | 'final' | ''; recurrence: 'R' | 'E' | '';
  cost: number | null; location: string;
}
const activity = reactive<ActivityForm>({
  name: '', duration: null, details: '', enjoyment: 0,
  category: '', type: '', recurrence: '', cost: null, location: ''
})

const naturalInput = ref('')
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isModalOpen = ref(false)
const currentDate = new Intl.DateTimeFormat('en-GB').format(new Date())

/* ---------- Methods ---------- */
const adjustEnjoyment = (d: number) => {
  const v = activity.enjoyment + d
  if (v >= -3 && v <= 3) activity.enjoyment = v
}

async function handleMagicInput () {
  if (!naturalInput.value.trim()) return
  isLoading.value = true; errorMessage.value = null
  try {
    const { data } = await axios.post<Partial<ActivityForm>>(
        `${API}/api/llm/prompt`, { prompt: naturalInput.value },
        { headers: { 'Content-Type': 'application/json' } }
    )
    for (const [k, v] of Object.entries(data)) {
      if (k in activity && v != null) (activity as any)[k] = v
    }
    naturalInput.value = ''
    closeAIModal()
  } catch (e: any) {
    errorMessage.value = e?.response?.data?.error ?? 'Unknown error'
  } finally {
    isLoading.value = false
  }
}

const openAIModal  = () => (isModalOpen.value = true)
const closeAIModal = () => (isModalOpen.value = false)
const saveActivity = () => { console.log(JSON.stringify(activity, null, 2)); alert('✅ Activity saved!') }
</script>

<style scoped>
/* General tokens */
:root{ --separator-color:color-mix(in srgb,var(--mauve)30%,transparent) }

/* Header */
.title-centered{display:flex;align-items:center;justify-content:center;gap:.35rem}
.title-icon{font-size:1.1rem;color:var(--peach)}
.date-note{font-size:.8rem;color:var(--ion-color-medium);padding-inline-end:8px}

/* Inputs / selects */
ion-input,ion-textarea,ion-select{--highlight-color-focused:var(--peach);--border-color:var(--surface2);--border-radius:12px}
ion-item.item-has-focus{--border-color:var(--peach)}
ion-select::part(placeholder){color:var(--overlay1)}

/* Separators */
ion-item{--inner-border-width:0;position:relative;padding-bottom:6px}
ion-item::after{content:'';position:absolute;left:0;right:0;bottom:0;height:1px;background:var(--separator-color);opacity:.9}
ion-item.item-has-focus::after{background:var(--peach);opacity:1}

/* Enjoyment row */
.enjoyment-item ion-label{font-size:.82rem;color:var(--ion-color-medium)}
.stepper{display:flex;align-items:center}
.stepper-value{font-weight:600;font-size:1rem;width:32px;text-align:center}

/* Details card */
.details-card{background:rgba(255,255,255,.02);border:1px solid var(--separator-color);border-radius:12px;margin:6px 0}

/* Save button */
.save-btn{--background:var(--gradient-peach-mauve);--border-radius:999px;font-size:1rem;height:48px;margin:12px 72px 12px 0}

/* FAB */
.ai-fab{--bottom:calc(112px + env(safe-area-inset-bottom))}
.ai-fab ion-fab-button{
  width:56px;height:56px;border-radius:50%;
  --background:var(--gradient-peach-mauve);--color:var(--crust);
  box-shadow:0 4px 14px -4px var(--peach)
}

/* Safe scroll padding */
.content-safe{padding-bottom:96px}

/* Modal */
ion-modal{--border-radius:16px}
</style>
