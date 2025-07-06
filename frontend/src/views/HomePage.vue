<template>
  <ion-page>
    <ion-header></ion-header>

    <ion-content class="ion-padding">
      <div class="user-info gradient-text">
        <ion-icon :icon="personCircleOutline" class="user-icon" />
        <h2>User name</h2>
      </div>

      <div class="time-diary-page">
        <div class="date-toolbar" color="dark">
          <div class="date-toolbar-inner">
            <ion-buttons slot="start">
              <ion-button fill="clear" @click="goPrevDay">
                <ion-icon :icon="chevronBackOutline" />
              </ion-button>
            </ion-buttons>

            <div class="date-pager-wrapper">
              <ion-segment v-model="selectedDateIndex" class="date-pager" :scrollable="false">
                <ion-segment-button
                    v-for="(d, i) in displayDates"
                    :key="i"
                    :value="i"
                >
                  <div class="date-number">{{ d.day }}</div>
                  <div class="date-month">{{ d.month }}</div>
                </ion-segment-button>
              </ion-segment>
            </div>

            <ion-buttons slot="end">
              <ion-button fill="clear" @click="goNextDay">
                <ion-icon :icon="chevronForwardOutline" />
              </ion-button>
            </ion-buttons>
          </div>
        </div>

        <div class="timeline-container">
          <transition-group name="activity" tag="div">
            <div
                v-for="a in activities"
                :key="a.activityId"
                class="activity-row"
                :style="{ '--stripe-color': categoryColors |

| 'var(--surface2)' }"
            >
              <div class="time-label">{{ formatTime(a.createdAt) }}</div>
              <div
                  class="timeline-dot"
                  :style="{ background: categoryColors }"
              />
              <div class="timeline-line" />
              <ion-card
                  class="activity-bubble"
                  :style="{ borderLeftColor: categoryColors }"
              >
                <ion-card-content>
                  <strong>{{ a.activityTypeName }}</strong> {{ a.activityTypeDescription }}
                  <p v-if="a.details">{{ a.details }}</p>
                  <p v-if="a.location">Location: {{ a.location }}</p>
                  <p v-if="a.costEuro">Cost: €{{ a.costEuro }}</p>
                </ion-card-content>
              </ion-card>
            </div>
          </transition-group>
          <div v-if="activities.length === 0 &&!isLoading" class="ion-text-center ion-padding">
            <ion-text color="medium">No activities for this date.</ion-text>
          </div>
        </div>

        <ion-fab vertical="bottom" horizontal="center" slot="fixed" class="add-fab">
          <ion-fab-button class="add-fab-btn" @click="addActivity">
            <ion-icon :icon="addOutline" />
          </ion-fab-button>
        </ion-fab>

        <ion-button
            fill="clear"
            class="bottom-icon left"
            @click="navigateTab({ detail: { value: 'home' } })"
        >
          <ion-icon :icon="homeOutline" />
        </ion-button>

        <ion-button
            fill="clear"
            class="bottom-icon right"
            @click="navigateTab({ detail: { value: 'settings' } })"
        >
          <ion-icon :icon="settingsOutline" />
        </ion-button>
      </div>
      <ion-loading :is-open="isLoading" message="Loading activities..." />
      <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open=false" />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  IonPage, IonHeader, IonContent, IonIcon, IonButton, IonButtons,
  IonCard, IonCardContent, IonFab, IonFabButton, IonSegment, IonSegmentButton,
  IonLoading, IonToast, IonText
} from '@ionic/vue';
import {
  homeOutline, settingsOutline, personCircleOutline,
  chevronBackOutline, chevronForwardOutline, addOutline
} from 'ionicons/icons';
import { api } from '@/composables/useApi'; // Assuming useApi handles JWT
import dayjs from 'dayjs'; // For date formatting

/* ---------- State ---------- */
const router = useRouter();
const route = useRoute();
const selectedTab = ref(route.name?.toString().toLowerCase() |

    | 'home');

// Use a reactive variable for the current date being displayed
const currentDate = ref(dayjs()); // Initialize with today's date

const selectedDateIndex = ref(1); // 0: prev, 1: current, 2: next
const displayDates = ref();

interface Activity {
  activityId: number;
  activityDate: string; // ISO date string
  durationMins: number;
  pleasantness: number;
  location: string;
  costEuro: string; // Backend sends as string
  userId: number;
  activityTypeId: number;
  createdAt: string; // ISO timestamp string
  updatedAt: string; // ISO timestamp string
  activityTypeName: string;
  activityTypeDescription: string;
  isInstrumental: boolean;
  isRoutinary: boolean;
  details?: string; // Optional, as it might not always be present or retrieved
}

const activities = ref<Activity>();
const isLoading = ref(false);
const toast = reactive({ open: false, message: '', color: 'danger' as const });

/* ---------- Methods ---------- */
const navigateTab = (event: CustomEvent) => {
  const tab = event.detail.value;
  if (tab === 'home') router.push({ name: 'Home' });
  else if (tab === 'settings') router.push({ name: 'Settings' });
};

function offsetDate(baseDate: dayjs.Dayjs, offset: number) {
  const d = baseDate.add(offset, 'day');
  return {
    day: d.date().toString().padStart(2, '0'),
    month: d.format('MMM') // e.g., 'Jul'
  };
}

function rotateDates(dir: number) {
  currentDate.value = currentDate.value.add(dir, 'day');
  displayDates.value = displayDates.value.map((_, i) => offsetDate(currentDate.value, i - 1));
  fetchActivities(); // Re-fetch activities for the new date
}

function goPrevDay() {
  selectedDateIndex.value = 0;
  rotateDates(-1);
}

function goNextDay() {
  selectedDateIndex.value = 2;
  rotateDates(1);
}

function formatTime(isoTimestamp: string): string {
  return dayjs(isoTimestamp).format('HH:mm');
}

const categoryColors: Record<string, string> = {
  // Map your backend activity type names to colors
  // You'll need to define these based on your actual activity types
  'Work': 'var(--mauve)',
  'Study': 'var(--blue)',
  'Leisure': 'var(--green)',
  'Exercise': 'var(--sky)',
  'Food': 'var(--peach)',
  'Hygiene': 'var(--teal)',
  'Commute': 'var(--mauve)',
  'Default': 'var(--surface2)' // Fallback color
};

async function fetchActivities() {
  isLoading.value = true;
  activities.value =; // Clear current activities
  try {
    // Assuming userId is stored in localStorage after login
    const userId = localStorage.getItem('userId');
    if (!userId) {
      showToast('User not logged in. Please log in again.', 'danger');
      router.push({ name: 'Login' }); // Redirect to login if no user ID
      return;
    }

    const formattedDate = currentDate.value.format('YYYY-MM-DD'); // Format date for backend
    const { data } = await api.get(`/api/activities/list?userId=${userId}&activityDate=${formattedDate}`);

    if (data.success) {
      activities.value = data.data ||; // Ensure it's an array, even if empty
      showToast(data.message |

          | 'Activities loaded.', 'success');
    } else {
      showToast(data.message |

          | 'Failed to load activities.', 'danger');
    }
  } catch (error: any) {
    console.error('Error fetching activities:', error);
    const message = error.response?.data?.message |

        | error.message |
        | 'An unexpected error occurred while fetching activities.';
    showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}

function addActivity() {
  router.push({ name: 'AddActivity' });
}

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

/* ---------- Lifecycle Hooks ---------- */
onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
  fetchActivities(); // Fetch activities when component mounts
});

// Watch for changes in selectedDateIndex to re-fetch activities
// This is important if the user navigates dates using the pager
watch(selectedDateIndex, (newIndex, oldIndex) => {
  // Only re-fetch if the index actually changes and it's not part of a rotation
  // The rotateDates function already calls fetchActivities, so this might be redundant
  // if user only uses prev/next buttons. But if they click directly on a segment, it's needed.
  if (newIndex!== oldIndex && Math.abs(newIndex - oldIndex) === 1) {
    // This condition helps prevent double-fetching if rotateDates already triggered it
    // For direct segment clicks, we need to adjust currentDate and then fetch
    currentDate.value = currentDate.value.add(newIndex - oldIndex, 'day');
    displayDates.value = displayDates.value.map((_, i) => offsetDate(currentDate.value, i - 1));
    fetchActivities();
  }
});

// Also watch the currentDate itself, as rotateDates updates it
watch(currentDate, () => {
  // This watch ensures activities are fetched whenever currentDate changes,
  // regardless of how it changed (e.g., direct manipulation or via rotateDates)
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

/* Date Toolbar */
.date-toolbar { margin-bottom: 1rem; }
.date-toolbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
}
.date-pager-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
}
.date-pager {
  --background: transparent;
  border-radius: 999px;
  padding: 0 4px;
}
.date-pager ion-segment-button {
  --background: transparent;
  --color: var(--text);
  min-width: 80px;
  flex-direction: column;
  text-transform: uppercase;
}
.date-pager ion-segment-button.ion-activated {
  background: var(--surface2);
  border-radius: 12px;
}
.date-number { font-size: 1rem; font-weight: 700; }
.date-month { font-size:.7rem; opacity:.7; }

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

/* FAB central */
.add-fab {
  --background: transparent!important;
  width: 68px; height: 68px;
  border-radius: 50%;
  overflow: visible;
  --bottom: calc(28px + env(safe-area-inset-bottom));
}
.add-fab-btn {
  --background: var(--gradient-pink-mauve);
  --color: var(--crust);
  width: 68px; height: 68px;
  border-radius: 50%;
  box-shadow: 0 4px 14px -4px var(--mauve);
}

/* Botones Home y Settings */
.bottom-icon {
  position: fixed;
  bottom: calc(24px + env(safe-area-inset-bottom));
  z-index: 50;
  background: transparent;
  --color: #c7b8f5;
  font-size: 1.8rem;
  padding: 0;
  min-width: auto;
  height: auto;
}
.bottom-icon.left {
  left: 20px;
}
.bottom-icon.right {
  right: 20px;
}
</style>