<!-- File: src/views/NewActivity.vue -->
<template>
  <ion-page>
    <!-- HEADER -->
    <ion-header>
      <ion-toolbar>
        <ion-title class="title-centered">
          <ion-icon :icon="sparkles" class="title-icon" /> New Activity
        </ion-title>
        <ion-note slot="end" class="date-note">{{ currentDate }}</ion-note>
      </ion-toolbar>
    </ion-header>

    <!-- CONTENT -->
    <ion-content :fullscreen="true" class="ion-padding content-safe">
      <div class="activity-container">

        <div class="form-wrapper">
          <ion-list lines="none">
            <!-- Name -->
            <ion-item :class="errorClass('name')" class="glass-input">
              <ion-icon slot="start" :icon="clipboardOutline" class="input-icon" />
              <ion-input
                  v-model="activity.name"
                  label="Name of Activity"
                  label-placement="floating"
                  :aria-label="'Activity Name'"
              />
            </ion-item>

            <!-- Duration -->
            <ion-item :class="errorClass('duration')" class="glass-input">
              <ion-icon slot="start" :icon="timeOutline" class="input-icon" />
              <ion-input
                  v-model.number="activity.duration"
                  type="number"
                  label="Duration (minutes)"
                  label-placement="floating"
                  :aria-label="'Duration'"
              />
            </ion-item>

            <!-- Details -->
            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="documentTextOutline" class="input-icon" />
              <ion-textarea
                  v-model="activity.details"
                  :auto-grow="true"
                  :maxlength="400"
                  counter
                  label="Details"
                  label-placement="floating"
                  placeholder="Add extra notes..."
              />
            </ion-item>

            <!-- Type -->
            <ion-item :class="errorClass('activityTypeId')" class="glass-input">
              <ion-icon slot="start" :icon="pricetagsOutline" class="input-icon" />
              <ion-select
                  v-model="activity.activityTypeId"
                  label="Type of activity"
                  label-placement="floating"
                  interface="popover"
                  :interface-options="{ cssClass: 'ion-dark catppuccin-select-overlay' }"
              >
                <ion-select-option
                    v-for="type in activityTypes"
                    :key="type.activityTypeId"
                    :value="type.activityTypeId"
                >
                  {{ type.name }}
                </ion-select-option>
              </ion-select>

            </ion-item>

            <!-- Pleasantness -->
            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="happyOutline" class="input-icon" />
              <div class="pleasantness-container">
                <ion-label>Pleasantness</ion-label>
                <div class="stepper-wrapper">
                  <ion-button fill="clear" size="small" @click="adjustPleasantness(-1)">−</ion-button>
                  <span class="stepper-value" :class="{
          'positive': activity.pleasantness > 0,
          'negative': activity.pleasantness < 0
        }">
        {{ activity.pleasantness }}
      </span>
                  <ion-button fill="clear" size="small" @click="adjustPleasantness(1)">+</ion-button>
                </div>
              </div>
            </ion-item>

            <!-- Recurrence -->
            <ion-item :class="errorClass('recurrence')" class="glass-input">
              <ion-icon slot="start" :icon="repeatOutline" class="input-icon" />
              <ion-select
                  v-model="activity.recurrence"
                  label="Recurrence"
                  label-placement="floating"
                  interface="popover"
                  :interface-options="{ cssClass: 'ion-dark catppuccin-select-overlay' }"
              >
                <ion-select-option value="R">Routinary (R)</ion-select-option>
                <ion-select-option value="E">Exceptional (E)</ion-select-option>
              </ion-select>
            </ion-item>

            <!-- Cost -->
            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="cashOutline" class="input-icon" />
              <ion-input
                  v-model="activity.costEuro"
                  type="text"
                  inputmode="decimal"
                  label="Cost (€)"
                  label-placement="floating"
                  :aria-label="'Cost'"
              />
            </ion-item>

            <!-- Location -->
            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="locationOutline" class="input-icon" />
              <ion-input
                  v-model="activity.location"
                  label="Location"
                  label-placement="floating"
                  :aria-label="'Location'"
              />
            </ion-item>
          </ion-list>

          <ion-grid class="ion-margin-top">
            <ion-row class="ion-justify-content-around">
              <ion-col size="5">
                <ion-button expand="block" class="pill-button gradient-outline" @click="router.back()">Cancel</ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button
                    expand="block"
                    :disabled="isLoading || hasErrors"
                    class="pill-button gradient-outline"
                    @click="saveActivity"
                >
                  Save Activity
                </ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

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
          <ion-textarea
              v-model="naturalInput"
              :auto-grow="true"
              :rows="3"
              label="Description"
              label-placement="floating"
              placeholder="e.g. One-hour gym session…"
              class="glass-input"
          />
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

      <ion-loading :is-open="isLoading" message="Saving activity..." />
      <ion-toast
          :is-open="toast.open"
          :message="toast.message"
          :color="toast.color"
          :duration="2500"
          @didDismiss="toast.open = false"
      />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton,
  IonButton, IonIcon, IonItem, IonLabel, IonInput, IonTextarea, IonSelect,
  IonSelectOption, IonNote, IonFab, IonFabButton, IonModal, IonSpinner,
  IonLoading, IonToast, IonGrid, IonRow, IonCol, IonList
} from '@ionic/vue';

import {
  sparkles, send, clipboardOutline, timeOutline, documentTextOutline,
  pricetagsOutline, happyOutline, repeatOutline, cashOutline, locationOutline
} from 'ionicons/icons';

import { api } from '@/composables/useApi';

const router = useRouter();
const isLoading = ref(false);
const isModalOpen = ref(false);
const naturalInput = ref('');
const errorMessage = ref<string | null>(null);
const activityTypes = ref<any[]>([]);
const currentDate = new Intl.DateTimeFormat('en-GB').format(new Date());

const activity = reactive({
  name: '',
  duration: null as number | null,
  details: '',
  pleasantness: 0,
  activityTypeId: '',
  recurrence: '' as 'R' | 'E' | '',
  costEuro: '',
  location: ''
});

const toast = reactive({ open: false, message: '', color: 'danger' as const });

const hasErrors = computed(() =>
    !activity.name.trim() ||
    !activity.duration ||
    !activity.activityTypeId ||
    !activity.recurrence
);

const errorClass = (field: keyof typeof activity) => ({
  'ion-invalid':
      (field === 'name' && !activity.name.trim()) ||
      (field === 'duration' && !activity.duration) ||
      (field === 'activityTypeId' && !activity.activityTypeId) ||
      (field === 'recurrence' && !activity.recurrence)
});

const adjustPleasantness = (d: number) => {
  const v = activity.pleasantness + d;
  if (v >= -3 && v <= 3) activity.pleasantness = v;
};

const openAIModal = () => (isModalOpen.value = true);
const closeAIModal = () => (isModalOpen.value = false);

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

function normalizeActivity(data: any) {
  return {
    name: data.name || '',
    duration: data.duration || null,
    details: data.details || '',
    pleasantness: data.pleasantness ?? 0,
    activityTypeId: data.activityTypeId || '',
    recurrence: data.recurrence || '',
    costEuro: data.costEuro || '',
    location: data.location || ''
  };
}

async function handleMagicInput() {
  if (!naturalInput.value.trim()) return;
  isLoading.value = true;
  errorMessage.value = null;

  try {
    const { data } = await api.post('/api/llm/prompt', { prompt: naturalInput.value });
    Object.assign(activity, normalizeActivity(data));
    naturalInput.value = '';
    closeAIModal();
    showToast('Activity fields updated!', 'success');
  } catch (err: any) {
    errorMessage.value = err.response?.data?.error || 'Failed to process request';
    showToast('AI processing failed', 'danger');
  } finally {
    isLoading.value = false;
  }
}

async function saveActivity() {
  if (hasErrors.value) {
    showToast('Please fill required fields', 'danger');
    return;
  }

  isLoading.value = true;

  try {
    const payload = {
      ...activity,
      costEuro: activity.costEuro ? parseFloat(activity.costEuro) : 0,
      details: activity.details.trim(),
      location: activity.location.trim()
    };

    const { data } = await api.post('/api/activities/create', payload);
    if (!data.success) throw new Error(data.message || 'Failed to save');

    showToast('Activity saved successfully!', 'success');
    setTimeout(() => router.push('/home'), 1500);
  } catch (err: any) {
    console.error('Save error:', err);
    const message = err.response?.data?.message || err.message || 'Save failed';
    showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}

onMounted(async () => {
  try {
    const res = await api.post('/api/activities/types');
    console.log('Raw activity types response:', res); // 👈 ADD THIS
    const data = res.data;
    activityTypes.value = Array.isArray(data) ? data : data.data || [];
  } catch (err) {
    console.error('Failed to fetch activity types:', err);
    showToast('Failed to load activity types', 'danger');
  }
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

/* Pleasantness Container */
.pleasantness-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 8px 0;
}

.stepper-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stepper-value {
  min-width: 28px;
  text-align: center;
  font-weight: bold;
  font-size: 1.1rem;
}

.positive {
  color: var(--ion-color-success);
}

.negative {
  color: var(--ion-color-danger);
}

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