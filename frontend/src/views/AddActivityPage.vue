<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start"><ion-back-button default-href="/home" /></ion-buttons>
        <ion-title class="title-centered">
          <ion-icon :icon="sparkles" class="title-icon" /> New Activity
        </ion-title>
        <ion-note slot="end" class="date-note">{{ currentDateDisplay }}</ion-note>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding content-safe">

      <ion-item>
        <ion-select v-model="activity.activityTypeId" placeholder="Select activity type"
                    label="Activity Type" label-placement="stacked">
          <ion-select-option v-for="type in activityTypes" :key="type.activityTypeId" :value="type.activityTypeId">
            {{ type.name }}
          </ion-select-option>
        </ion-select>
      </ion-item>

      <ion-item>
        <ion-input v-model.number="activity.durationMins" type="number"
                   label="Duration (min)" label-placement="stacked" />
      </ion-item>

      <ion-item class="enjoyment-item" lines="none">
        <ion-label>Enjoyment</ion-label>
        <div class="stepper" slot="end">
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(-1)">−</ion-button>
          <span class="stepper-value">{{ activity.pleasantness }}</span>
          <ion-button fill="clear" size="small" @click="adjustEnjoyment(1)">+</ion-button>
        </div>
      </ion-item>

      <ion-item>
        <ion-input v-model="activity.costEuro" type="number" inputmode="decimal"
                   label="Cost (€)" label-placement="stacked" />
      </ion-item>

      <ion-item>
        <ion-input v-model="activity.location"
                   label="Location" label-placement="stacked" />
      </ion-item>

      <ion-card class="details-card">
        <ion-card-content>
          <ion-textarea v-model="activity.details" :auto-grow="true"
                        :maxlength="400" counter
                        label="Details" label-placement="stacked"
                        placeholder="Add extra notes…" />
        </ion-card-content>
      </ion-card>

      <ion-button expand="block" size="large" class="save-btn" @click="saveActivity" :disabled="isLoading">
        Save Activity <ion-icon slot="end" :icon="checkmarkCircleOutline" />
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

    </ion-content>
    <ion-loading :is-open="isLoading" message="Saving activity..." />
    <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open=false" />
  </ion-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonButton, IonIcon, IonItem, IonLabel, IonInput, IonTextarea, IonSelect,
  IonSelectOption, IonNote, IonFab, IonFabButton, IonModal, IonCard, IonCardContent,
  IonSpinner, IonLoading, IonToast
} from '@ionic/vue'
import { sparkles, send, checkmarkCircleOutline } from 'ionicons/icons'
import { api } from '@/composables/useApi' // Assuming useApi handles JWT
import dayjs from 'dayjs' // For date formatting

/* ---------- State ---------- */
const router = useRouter()
const API = import.meta.env.VITE_API_BASE_URL

interface ActivityForm {
  activityTypeId: number | null;
  durationMins: number | null;
  pleasantness: number; // Renamed from enjoyment
  location: string;
  costEuro: string; // Changed to string for backend compatibility
  details: string; // Mapped from original 'name' and 'details'
}

const activity = reactive<ActivityForm>({
  activityTypeId: null,
  durationMins: null,
  pleasantness: 0,
  location: '',
  costEuro: '',
  details: ''
})

interface ActivityType {
  activityTypeId: number;
  name: string;
  description: string;
  isInstrumental: boolean;
  isRoutinary: boolean;
}
const activityTypes = ref<ActivityType>();

const naturalInput = ref('')
const isLoading = ref(false) // For main save activity button
const isLoadingAI = ref(false) // For AI modal button
const errorMessageAI = ref<string | null>(null)
const isModalOpen = ref(false)
const currentDateDisplay = new Intl.DateTimeFormat('en-GB').format(new Date())
const toast = reactive({ open: false, message: '', color: 'danger' as const });

/* ---------- Methods ---------- */
const adjustEnjoyment = (d: number) => {
  const v = activity.pleasantness + d
  if (v >= -3 && v <= 3) activity.pleasantness = v
}

async function fetchActivityTypes() {
  try {
    const { data } = await api.get(`${API}/api/activities/types`);
    if (data.success) {
      activityTypes.value = data.data ||;
    } else {
      showToast(data.message |

          | 'Failed to load activity types.', 'danger');
    }
  } catch (error: any) {
    console.error('Error fetching activity types:', error);
    const message = error.response?.data?.message |

        | error.message |
        | 'An unexpected error occurred while fetching activity types.';
    showToast(message, 'danger');
  }
}

async function handleMagicInput () {
  if (!naturalInput.value.trim()) return
  isLoadingAI.value = true;
  errorMessageAI.value = null
  try {
    const { data } = await api.post<Partial<ActivityForm>>(
        `${API}/api/llm/prompt`, { prompt: naturalInput.value },
        { headers: { 'Content-Type': 'application/json' } }
    )
    // Map LLM output to form fields
    for (const [k, v] of Object.entries(data)) {
      if (k === 'enjoyment' && v!= null) {
        activity.pleasantness = v as number; // Map enjoyment to pleasantness
      } else if (k === 'cost' && v!= null) {
        activity.costEuro = String(v); // Map cost to costEuro and convert to string
      } else if (k === 'duration' && v!= null) {
        activity.durationMins = v as number; // Map duration to durationMins
      } else if (k === 'name' && v!= null) {
        // LLM 'name' might be a general activity name, map to details for now
        activity.details = `${activity.details? activity.details + '\n' : ''}Activity: ${v}`;
      } else if (k === 'category' && v!= null) {
        // Try to find matching activityTypeId based on category name
        const matchingType = activityTypes.value.find(type => type.name.toLowerCase() === String(v).toLowerCase());
        if (matchingType) {
          activity.activityTypeId = matchingType.activityTypeId;
        } else {
          console.warn(`No matching activity type found for category: ${v}`);
        }
      } else if (k in activity && v!= null) {
        (activity as any)[k] = v;
      }
    }
    naturalInput.value = ''
    closeAIModal()
  } catch (e: any) {
    errorMessageAI.value = e?.response?.data?.message |

        | e?.response?.data?.error |
        | 'Unknown error'
  } finally {
    isLoadingAI.value = false
  }
}

const openAIModal  = () => (isModalOpen.value = true)
const closeAIModal = () => (isModalOpen.value = false)

async function saveActivity() {
  isLoading.value = true;
  try {
    const userId = localStorage.getItem('userId'); // Assuming userId is stored after login
    if (!userId) {
      showToast('User not logged in. Please log in again.', 'danger');
      router.push({ name: 'Login' });
      return;
    }

    // Prepare data for backend
    const activityData = {
      userId: parseInt(userId),
      activityDate: dayjs().format('YYYY-MM-DD'), // Set current date
      activityTypeId: activity.activityTypeId,
      durationMins: activity.durationMins,
      pleasantness: activity.pleasantness,
      location: activity.location,
      costEuro: activity.costEuro,
      details: activity.details // Use details for the activity name/description
    };

    const { data } = await api.post(`${API}/api/activities/create`, activityData);

    if (data.success) {
      showToast(data.message |

          | 'Activity saved successfully!', 'success');
      router.push({ name: 'Home' }); // Navigate back to home on success
    } else {
      showToast(data.message |

          | 'Failed to save activity.', 'danger');
    }
  } catch (error: any) {
    console.error('Error saving activity:', error);
    const message = error.response?.data?.message |

        | error.message |
        | 'An unexpected error occurred.';
    showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

/* ---------- Lifecycle Hooks ---------- */
onMounted(() => {
  fetchActivityTypes(); // Fetch activity types when component mounts
});
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