<template>
  <ion-page>
    <ion-header></ion-header>

    <ion-content class="ion-padding">
      <div class="user-info gradient-text">
        <ion-icon :icon="personCircleOutline" class="user-icon" />
        <h2>User name</h2>
      </div>

      <div class="time-diary-page">
        <div class="current-date-display">
          <h3>{{ formattedCurrentDate }}</h3>
        </div>

        <div class="timeline-container">
          <transition-group name="activity" tag="div">
            <div
                v-for="a in activities"
                :key="a.activityId"
                class="activity-row"
                :style="{ '--stripe-color': categoryColors[a.activityTypeName] || 'var(--surface2)' }"
            >
              <div class="time-label">{{ formatTime(a.createdAt) }}</div>
              <div
                  class="timeline-dot"
                  :style="{ background: categoryColors[a.activityTypeName] || 'var(--surface2)' }"
              />
              <div class="timeline-line" />
              <ion-card
                  class="activity-bubble"
                  :style="{ borderLeftColor: categoryColors[a.activityTypeName] || 'var(--surface2)' }"
                  @click="editActivity(a)"
              >
                <ion-card-content>
                  <div class="action-buttons">
                    <ion-button fill="clear" size="small" @click.stop="confirmDelete(a.activityId)">
                      <ion-icon :icon="trashBinOutline" color="danger" />
                    </ion-button>
                  </div>

                  <strong>{{ a.activityTypeName }}</strong> {{ a.activityTypeDescription }}
                  <p v-if="a.details">{{ a.details }}</p>
                  <p v-if="a.location">Location: {{ a.location }}</p>
                  <p v-if="a.costEuro">Cost: €{{ a.costEuro }}</p>
                </ion-card-content>
              </ion-card>
            </div>
          </transition-group>
          <div v-if="activities.length === 0 && !isLoading" class="ion-text-center ion-padding">
            <ion-text color="medium">No activities for today.</ion-text>
          </div>
        </div>

        <div class="bottom-icons-container">
          <!-- Home icon (non-clickable) -->
          <div class="bottom-icon left home-icon">
            <ion-icon :icon="homeOutline" />
          </div>

          <!-- Plus button (fixed position) -->
          <div class="bottom-icon center">
            <ion-fab-button class="add-fab-btn" @click="addActivity">
              <ion-icon :icon="addOutline" />
            </ion-fab-button>
          </div>

          <!-- Settings icon (clickable) -->
          <div class="bottom-icon settings-icon" @click="navigateTab({ detail: { value: 'settings' } })">
            <ion-icon :icon="settingsOutline" />
          </div>
        </div>
      </div>
      <ion-loading :is-open="isLoading" message="Loading activities..." />
      <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open=false" />
    </ion-content>
    <ion-alert
        :is-open="showDeleteConfirm"
        header="Confirm Delete"
        :message="`Delete activity from ${deleteTime}?`"
        :buttons="alertButtons"
        @didDismiss="handleAlertDismiss"
    />
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage, IonHeader, IonContent, IonIcon, IonButton,
  IonCard, IonCardContent, IonFabButton,
  IonLoading, IonToast, IonText, IonAlert
} from '@ionic/vue';
import {
  homeOutline, settingsOutline, personCircleOutline, addOutline, trashBinOutline
} from 'ionicons/icons';
import dayjs from 'dayjs';
import { api } from '@/composables/useApi';

import { watch } from 'vue';
import { useActivityStore } from '@/store/activityStore';

/* ---------- State ---------- */
const router = useRouter();
const showDeleteConfirm = ref(false);
const pendingDeleteId = ref<number | null>(null);
const deleteTime = ref('');

const alertButtons = ref([
  { text: 'Cancel', role: 'cancel' },
  { text: 'Delete', handler: () => {
      if (pendingDeleteId.value !== null) {
        deleteActivity(pendingDeleteId.value);
      }
    }
  }
]);

const currentDate = ref(dayjs());

interface Activity {
  activityId: number;
  activityDate: string;
  durationMins: number;
  pleasantness: number;
  location: string;
  costEuro: string;
  userId: number;
  activityTypeId: number;
  createdAt: string;
  updatedAt: string;
  activityTypeName: string;
  activityTypeDescription: string;
  isInstrumental: boolean;
  isRoutinary: boolean;
  details?: string;
}

const activities = ref<Activity[]>([]);
const isLoading = ref(false);
const toast = ref({ open: false, message: '', color: 'danger' });

const activityStore = useActivityStore();

/* ---------- Computed ---------- */
const formattedCurrentDate = computed(() => {
  return currentDate.value.format('MMMM D, YYYY');
});

/* ---------- Methods ---------- */
const navigateTab = (event: { detail: { value: string } }) => {
  const tab = event.detail.value;
  if (tab === 'home') router.push({ name: 'Home' });
  else if (tab === 'settings') router.push({ name: 'Settings' });
};

function formatTime(isoTimestamp: string): string {
  return dayjs(isoTimestamp).format('HH:mm');
}

const categoryColors: Record<string, string> = {
  'Work': 'var(--mauve)',
  'Study': 'var(--blue)',
  'Leisure': 'var(--green)',
  'Exercise': 'var(--sky)',
  'Food': 'var(--peach)',
  'Hygiene': 'var(--teal)',
  'Commute': 'var(--mauve)',
  'Default': 'var(--surface2)'
};

watch(() => activityStore.needsRefresh, (shouldRefresh) => {
  if (shouldRefresh) {
    fetchActivities();                 // 🔁 richiama la lista aggiornata
    activityStore.needsRefresh = false;
  }
});

function editActivity(activity: Activity) {
  router.push({
    name: 'AddActivity',
    query: {
      id: activity.activityId.toString(),
      name: activity.activityTypeName,
      durationMins: activity.durationMins?.toString() || '',
      details: activity.details || '',
      pleasantness: activity.pleasantness.toString(),
      activityTypeId: activity.activityTypeId.toString(),
      recurrence: activity.isRoutinary ? 'R' : 'E',
      costEuro: activity.costEuro || '',
      location: activity.location || ''
    }
  });
}

function handleAlertDismiss() {
  showDeleteConfirm.value = false;
  pendingDeleteId.value = null;
}

async function deleteActivity(activityId: number) {
  isLoading.value = true;
  try {
    const { data } = await api.post(`/api/activities/delete`, {
      activityId: activityId
    });

    if (data?.success) {
      showToast('Activity deleted', 'success');
      activities.value = activities.value.filter(a => a.activityId !== activityId);

      // Focus management after successful deletion
      setTimeout(() => {
        const safeElement = document.querySelector('.add-fab-btn') as HTMLElement;
        if (safeElement) safeElement.focus();
      }, 100);
    } else {
      throw new Error(data?.message || 'Failed to delete activity');
    }
  } catch (err: any) {
    console.error('Delete error:', err);
    showToast(err.message || 'Delete failed', 'danger');
  } finally {
    isLoading.value = false;
    pendingDeleteId.value = null;
    showDeleteConfirm.value = false;
  }
}

function confirmDelete(activityId: number) {
  const activity = activities.value.find(a => a.activityId === activityId);
  if (!activity) return;

  pendingDeleteId.value = activityId;
  deleteTime.value = formatTime(activity.createdAt);
  showDeleteConfirm.value = true;

  // Immediately move focus to the alert's first button
  setTimeout(() => {
    const firstAlertButton = document.querySelector('.alert-button:first-child') as HTMLElement;
    firstAlertButton?.focus();
  }, 50);
}

async function fetchActivities() {
  isLoading.value = true;
  activities.value = [];

  const userId = 1; // ← Temporary hardcoded user ID
  const activityDate = currentDate.value.format('YYYY-MM-DD');

  try {
    const { data } = await api.post('/api/activities/list', {
      userId,
      activityDate
    });

    if (!data?.success) {
      throw new Error(data?.message || 'Failed to fetch activities');
    }

    activities.value = data.data || [];
  } catch (err: any) {
    console.error('Activity fetch error:', err);
    const message = err.response?.data?.message || err.message || 'Unexpected error';
    showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}

function addActivity() {
  router.push({ name: 'AddActivity' });
}

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.value.message = msg;
  toast.value.color = col;
  toast.value.open = true;
};

/* ---------- Lifecycle Hooks ---------- */
onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
  fetchActivities();
});
</script>

<style scoped>
/* User Info */
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

/* Current Date Display */
.current-date-display {
  text-align: center;
  margin-bottom: 1.5rem;
}
.current-date-display h3 {
  margin: 0;
  font-size: 1.2rem;
  color: var(--text);
}

/* Timeline */
.timeline-container { padding: 0 0 64px 0; }
.activity-row {
  position: relative;
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
  --stripe-color: var(--surface2);
}
.time-label {
  width: 50px;
  text-align: right;
  font-size:.75rem;
  color: var(--overlay1);
  margin-right: 14px;
}
.timeline-dot {
  position: absolute;
  left: 50px;
  top: 6px;
  width: 8px; height: 8px;
  border-radius: 50%;
  background: var(--stripe-color);
}
.timeline-line {
  position: absolute;
  left: 53.5px;
  top: 18px; bottom: -18px;
  width: 1px;
  background: var(--surface2);
}
.activity-bubble {
  flex: 1;
  margin-left: 64px;
  --background: rgba(255,255,255,.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,.12);
  border-left: 4px solid var(--stripe-color);
  border-radius: 18px 18px 6px 18px;
}
.activity-bubble:hover {
  background: linear-gradient(
      120deg,
      color-mix(in srgb, var(--peach) 15%, transparent),
      color-mix(in srgb, var(--mauve) 15%, transparent)
  );
  box-shadow: 0 0 12px -2px var(--mauve);
}

/* Botones Home y Settings */
.bottom-icons-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px 25px; /* Increased side padding and bottom padding */
  z-index: 100;
}

.bottom-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.bottom-icon ion-icon {
  font-size: 28px;
}

/* Home icon - special color, no click */
.home-icon {
  pointer-events: none;
  color: var(--peach);
}

/* Settings icon - clickable, highlight */
.settings-icon ion-icon {
  color: #c7b8f5;
}

/* Settings button */
.bottom-icon.right ion-button {
  --padding-start: 0;
  --padding-end: 0;
  min-width: 44px;
  min-height: 44px;
}
.bottom-icon.right ion-icon {
  font-size: 28px; /* Increased size */
  color: #c7b8f5; /* Original color */
}

/* Central plus button */
.add-fab-btn {
  --background: var(--gradient-pink-mauve);
  --color: var(--crust);
  width: 60px;
  height: 60px;
  min-width: 60px;
  min-height: 60px;
  padding: 0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px -4px var(--mauve);
}

.add-fab-btn ion-icon {
  font-size: 28px;
}

/* Adjust timeline padding to avoid overlap */
.timeline-container {
  padding-bottom: 100px;
}

.action-buttons {
  position: absolute;
  top: 30px;
  right: 30px;
  display: flex;
  gap: 4px;
  z-index: 10;
}

.action-buttons ion-button {
  --padding-start: 4px;
  --padding-end: 4px;
  --padding-top: 4px;
  --padding-bottom: 4px;
  width: 28px;
  height: 28px;
}

.activity-bubble {
  position: relative; /* Needed for absolute positioning of buttons */
}
/* Adjust card content padding to make space for buttons */
ion-card-content {
  position: relative;
  padding-right: 40px !important;
}

</style>