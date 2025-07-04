// src/views/ActivityFormPage.vue

<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start"><ion-back-button default-href="/home" /></ion-buttons>
        <ion-title class="title-centered">
          <ion-icon :icon="sparkles" class="title-icon" /> {{ isEditMode ? 'Edit Activity' : 'New Activity' }}
        </ion-title>
        <ion-note slot="end" class="date-note">{{ currentDate }}</ion-note>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding content-safe">

      <ion-item>
        <ion-input v-model="activity.name"
                   label="Name of Activity" label-placement="stacked" />
      </ion-item>

      <ion-item>
        <ion-input v-model.number="activity.duration" type="number"
                   label="Duration (min)" label-placement="stacked" />
      </ion-item>

      <ion-card class="details-card">
        <ion-card-content>
          <ion-textarea v-model="activity.details" :auto-grow="true"
                        :maxlength="400" counter
                        label="Details" label-placement="stacked"
                        placeholder="Add extra notes…" />
        </ion-card-content>
      </ion-card>

      <ion-item>
        <ion-input v-model="activity.category"
                   label="Category" label-placement="stacked" />
      </ion-item>

      <ion-item class="enjoyment-item" lines="none">
        <ion-label>Enjoyment</ion-label>
        <div class="stepper" slot="end">
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(-1)">−</ion-button>
          <span class="stepper-value">{{ activity.enjoyment }}</span>
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(1)">+</ion-button>
        </div>
      </ion-item>

      <ion-item>
        <ion-select v-model="activity.type" placeholder="Select type"
                    label="Type of activity" label-placement="stacked">
          <ion-select-option value="instrumental">Instrumental</ion-select-option>
          <ion-select-option value="final">Final</ion-select-option>
        </ion-select>
      </ion-item>

      <ion-item>
        <ion-select v-model="activity.recurrence" placeholder="Select recurrence"
                    label="Recurrence" label-placement="stacked">
          <ion-select-option value="R">Routinary (R)</ion-select-option>
          <ion-select-option value="E">Exceptional (E)</ion-select-option>
        </ion-select>
      </ion-item>

      <ion-item>
        <ion-input v-model.number="activity.cost" type="number" inputmode="decimal"
                   label="Cost (€)" label-placement="stacked" />
      </ion-item>

      <ion-item>
        <ion-input v-model="activity.location"
                   label="Location" label-placement="stacked" />
      </ion-item>

      <ion-button expand="block" size="large" class="save-btn" @click="saveActivity" :disabled="activityStore.isLoading">
        {{ isEditMode ? 'Update Activity' : 'Save Activity' }}
        <ion-icon slot="end" :icon="checkmarkCircleOutline" />
        <ion-spinner v-if="activityStore.isLoading" name="crescent" />
      </ion-button>

      <ion-fab vertical="bottom" horizontal="end" slot="fixed" class="ai-fab">
        <ion-fab-button @click="openAIModal"><ion-icon :icon="sparkles" /></ion-fab-button>
      </ion-fab>

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
            <ion-button :disabled="isLoadingAI" @click="handleMagicInput">
              <ion-icon v-if="!isLoadingAI" :icon="send" slot="start" />
              <ion-spinner v-else name="crescent" />
              Send
            </ion-button>
          </div>
          <ion-note v-if="errorMessageAI" color="danger">{{ errorMessageAI }}</ion-note>
        </ion-content>
      </ion-modal>

      <ion-toast
          :is-open="showToast"
          :message="toastMessage"
          :color="toastColor"
          :duration="3000"
          @didDismiss="showToast = false"
      ></ion-toast>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // Importa useRoute per i parametri
import { useActivityStore } from '@/stores/activityStore'; // Il tuo nuovo store per le attività
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonButton, IonIcon, IonItem, IonLabel, IonInput, IonTextarea, IonSelect,
  IonSelectOption, IonNote, IonFab, IonFabButton, IonModal, IonCard, IonCardContent,
  IonSpinner, IonToast, toastController // Importa toastController per i toast
} from '@ionic/vue';
import { sparkles, send, checkmarkCircleOutline } from 'ionicons/icons';

/* ---------- Stores and Router ---------- */
const router = useRouter();
const route = useRoute(); // Per accedere ai parametri della rotta (es. ID)
const activityStore = useActivityStore();

/* ---------- State ---------- */
// Tipo per l'attività (assicurati che sia lo stesso definito in activityStore.ts se non è globale)
interface ActivityForm {
  name: string; duration: number | null; details: string; enjoyment: number;
  category: string; type: 'instrumental' | 'final' | ''; recurrence: 'R' | 'E' | '';
  cost: number | null; location: string;
}

const activity = reactive<ActivityForm>({
  name: '', duration: null, details: '', enjoyment: 0,
  category: '', type: '', recurrence: '', cost: null, location: ''
});

const activityId = ref<string | null>(null); // Per tenere traccia dell'ID in modalità modifica
const isEditMode = computed(() => !!activityId.value); // Computed per sapere se siamo in modalità modifica

const naturalInput = ref('');
// Caricamento ed errore per la parte AI, separati dallo store loading generale per chiarezza UI
const isLoadingAI = ref(false);
const errorMessageAI = ref<string | null>(null);

const isModalOpen = ref(false);
const currentDate = new Intl.DateTimeFormat('en-GB').format(new Date());

// Toast state (copiato da LoginPage.vue per consistenza)
const showToast = ref(false);
const toastMessage = ref('');
const toastColor = ref('danger');

const presentToast = async (message: string, color: string = 'danger') => {
  toastMessage.value = message;
  toastColor.value = color;
  showToast.value = true;
  // Puoi anche usare direttamente toastController per più controllo
  // const toast = await toastController.create({
  //   message,
  //   duration: 3000,
  //   color,
  // });
  // toast.present();
};

/* ---------- Lifecycle Hooks ---------- */
onMounted(async () => {
  if (route.params.id) { // Se c'è un ID nei parametri della rotta, siamo in modalità modifica
    activityId.value = route.params.id as string;
    try {
      presentToast('Loading activity for editing...', 'tertiary'); // Toast informativo
      // Recupera l'attività dal backend tramite lo store
      const fetchedActivity = await activityStore.fetchActivityById(activityId.value);
      // Assegna i dati recuperati all'oggetto reattivo del form
      Object.assign(activity, fetchedActivity);
      presentToast('Activity loaded!', 'success');
    } catch (error) {
      presentToast('Failed to load activity for editing. Please try again.', 'danger');
      console.error('Error loading activity for edit:', error);
      router.replace({ name: 'Home' }); // Reindirizza se l'attività non esiste o non può essere caricata
    }
  }
});


/* ---------- Methods ---------- */
const adjustEnjoyment = (d: number) => {
  const v = activity.enjoyment + d
  if (v >= -3 && v <= 3) activity.enjoyment = v
}

async function handleMagicInput() {
  if (!naturalInput.value.trim()) {
    presentToast('Please provide a description for the AI.', 'warning');
    return;
  }
  isLoadingAI.value = true;
  errorMessageAI.value = null;
  try {
    // Chiama l'azione dello store per l'elaborazione LLM
    const parsedData = await activityStore.processNaturalInput(naturalInput.value);

    // Applica i dati parsati all'oggetto reattivo del form
    for (const [k, v] of Object.entries(parsedData)) {
      if (k in activity && v != null) {
        // Conversione esplicita per i tipi se necessario, es. 'type' o 'recurrence'
        // Questo è un esempio, potresti voler aggiungere più controlli di tipo
        if (k === 'type' && (v === 'instrumental' || v === 'final')) {
          (activity as any)[k] = v;
        } else if (k === 'recurrence' && (v === 'R' || v === 'E')) {
          (activity as any)[k] = v;
        } else if (k === 'duration' || k === 'cost' || k === 'enjoyment') {
          (activity as any)[k] = Number(v); // Assicurati che i numeri siano numeri
        } else {
          (activity as any)[k] = v;
        }
      }
    }
    naturalInput.value = '';
    presentToast('AI processed input successfully!', 'success');
    closeAIModal();
  } catch (e: any) {
    errorMessageAI.value = (e?.message || e?.response?.data?.error) ?? 'Unknown error from AI';
    presentToast(errorMessageAI.value, 'danger');
  } finally {
    isLoadingAI.value = false;
  }
}

const openAIModal  = () => (isModalOpen.value = true);
const closeAIModal = () => (isModalOpen.value = false);

// Funzione unificata per salvare/aggiornare l'attività
const saveActivity = async () => {
  // Aggiungi qui la logica di validazione del form prima di inviare
  if (!activity.name.trim()) {
    presentToast('Activity name is required!', 'danger');
    return;
  }
  if (activity.duration === null || activity.duration < 0) {
    presentToast('Duration must be a positive number!', 'danger');
    return;
  }
  // ... altre validazioni necessarie ...

  try {
    if (isEditMode.value) {
      // Chiama l'azione updateActivity dello store
      await activityStore.updateActivity(activityId.value!, activity);
      presentToast('Activity updated successfully!', 'success');
    } else {
      // Chiama l'azione addActivity dello store
      await activityStore.addActivity(activity);
      presentToast('Activity added successfully!', 'success');
    }
    router.replace({ name: 'Home' }); // Reindirizza dopo il successo
  } catch (e: any) {
    const msg = e?.response?.data?.message || 'Failed to save activity. Please try again.';
    presentToast(msg, 'danger');
    console.error('Error saving activity:', e);
  }
};
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