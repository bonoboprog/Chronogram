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
        <div class="stepper" slot="end" role="group" aria-label="Enjoyment level">
          <ion-button aria-label="Decrease enjoyment" fill="clear" size="small" @click="adjustEnjoyment(-1)">−</ion-button>
          <span class="stepper-value" aria-live="polite">{{ activity.enjoyment }}</span>
          <ion-button aria-label="Increase enjoyment" fill="clear" size="small" @click="adjustEnjoyment(1)">+</ion-button>
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
            <ion-button :disabled="isLoadingAI || !naturalInput.trim()" @click="handleMagicInput">
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
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useActivityStore } from '@/store/activityStore';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonButton, IonIcon, IonItem, IonLabel, IonInput, IonTextarea, IonSelect,
  IonSelectOption, IonNote, IonFab, IonFabButton, IonModal, IonCard, IonCardContent,
  IonSpinner, IonToast
} from '@ionic/vue';
import { sparkles, send, checkmarkCircleOutline } from 'ionicons/icons';

/* ---------- Stores and Router ---------- */
const router = useRouter();
const route = useRoute();
const activityStore = useActivityStore();

/* ---------- State ---------- */
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

const activityId = ref<string | null>(null);
const isEditMode = computed(() => !!activityId.value);

const naturalInput = ref('');
const isLoadingAI = ref(false);
const errorMessageAI = ref<string | null>(null);
const isModalOpen = ref(false);
const currentDate = new Intl.DateTimeFormat('en-GB').format(new Date());

const showToast = ref(false);
const toastMessage = ref('');
const toastColor = ref('danger');

const presentToast = async (message: string, color: string = 'danger') => {
  toastMessage.value = message;
  toastColor.value = color;
  showToast.value = true;
};

/* ---------- Lifecycle Hooks ---------- */
onMounted(async () => {
  if (route.params.id && typeof route.params.id === 'string') {
    activityId.value = route.params.id;
    try {
      presentToast('Loading activity for editing...', 'tertiary');
      const fetchedActivity = await activityStore.fetchActivityById(activityId.value);
      Object.assign(activity, fetchedActivity);
      presentToast('Activity loaded!', 'success');
    } catch (error) {
      presentToast('Failed to load activity for editing. Please try again.', 'danger');
      console.error('Error loading activity for edit:', error);
      router.replace({ name: 'Home' });
    }
  }
});

onBeforeUnmount(() => {
  isModalOpen.value = false;
});

/* ---------- Methods ---------- */
const adjustEnjoyment = (d: number) => {
  const v = activity.enjoyment + d;
  if (v >= -3 && v <= 3) activity.enjoyment = v;
};

async function handleMagicInput() {
  if (!naturalInput.value.trim()) {
    presentToast('Please provide a description for the AI.', 'warning');
    return;
  }

  isLoadingAI.value = true;
  errorMessageAI.value = null;

  try {
    const parsedData = await activityStore.processNaturalInput(naturalInput.value);

    for (const [k, v] of Object.entries(parsedData)) {
      if (k in activity && v != null) {
        if (k === 'type' && (v === 'instrumental' || v === 'final')) {
          activity.type = v;
        } else if (k === 'recurrence' && (v === 'R' || v === 'E')) {
          activity.recurrence = v;
        } else if (k === 'duration' || k === 'cost' || k === 'enjoyment') {
          const numValue = Number(v);
          if (!isNaN(numValue)) {
            activity[k] = numValue;
          }
        } else if (k === 'name' || k === 'details' || k === 'category' || k === 'location') {
          activity[k] = String(v);
        }
      }
    }

    naturalInput.value = '';
    presentToast('AI processed input successfully!', 'success');
    closeAIModal();
  } catch (e: any) {
    errorMessageAI.value = 'Failed to process your description. ';
    if (e?.response?.status === 429) {
      errorMessageAI.value += 'Too many requests. Please try again later.';
    } else {
      errorMessageAI.value += 'Please try a different description.';
    }
    presentToast(errorMessageAI.value, 'danger');
  } finally {
    isLoadingAI.value = false;
  }
}

const openAIModal = () => (isModalOpen.value = true);
const closeAIModal = () => (isModalOpen.value = false);

const saveActivity = async () => {
  if (!activity.name.trim()) {
    presentToast('Activity name is required!', 'danger');
    return;
  }

  if (activity.duration === null || isNaN(activity.duration) || activity.duration < 0) {
    presentToast('Duration must be a positive number!', 'danger');
    return;
  }

  if (activity.cost !== null && (isNaN(activity.cost) || activity.cost < 0)) {
    presentToast('Cost must be a positive number!', 'danger');
    return;
  }

  try {
    if (isEditMode.value) {
      await activityStore.updateActivity(activityId.value!, activity);
      presentToast('Activity updated successfully!', 'success');
    } else {
      await activityStore.addActivity(activity);
      presentToast('Activity added successfully!', 'success');
      // Reset form after successful creation
      Object.assign(activity, {
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
    }
    router.replace({ name: 'Home' });
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