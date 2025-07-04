<template>
  <ion-page>
    <ion-header></ion-header>

    <ion-content class="ion-padding">
      <div class="user-info gradient-text">
        <ion-icon :icon="personCircleOutline" class="user-icon" />
        <h2>User name</h2>
      </div>

      <div class="time-diary-page">
        <!-- DATE PAGER -->
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

        <!-- TIMELINE -->
        <div class="timeline-container">
          <transition-group name="activity" tag="div">
            <div
                v-for="a in activities"
                :key="a.id"
                class="activity-row"
                :style="{ '--stripe-color': categoryColors[a.category] || 'var(--surface2)' }"
            >
              <div class="time-label">{{ a.time }}</div>
              <div
                  class="timeline-dot"
                  :style="{ background: categoryColors[a.category] }"
              />
              <div class="timeline-line" />
              <ion-card
                  class="activity-bubble"
                  :style="{ borderLeftColor: categoryColors[a.category] }"
              >
                <ion-card-content>
                  <strong>{{ a.code }}</strong> {{ a.text }}
                </ion-card-content>
              </ion-card>
            </div>
          </transition-group>
        </div>

        <!-- FAB central "+" -->
        <ion-fab vertical="bottom" horizontal="center" slot="fixed" class="add-fab">
          <ion-fab-button class="add-fab-btn" @click="addActivity">
            <ion-icon :icon="addOutline" />
          </ion-fab-button>
        </ion-fab>

        <!-- Botón Home (izquierda) -->
        <ion-button
            fill="clear"
            class="bottom-icon left"
            @click="navigateTab('home')"
        >
          <ion-icon :icon="homeOutline" />
        </ion-button>

        <!-- Botón Settings (derecha) -->
        <ion-button
            fill="clear"
            class="bottom-icon right"
            @click="navigateTab('settings')"
        >
          <ion-icon :icon="settingsOutline" />
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  IonPage, IonHeader, IonContent, IonIcon, IonButton, IonButtons,
  IonCard, IonCardContent, IonFab, IonFabButton, IonSegment, IonSegmentButton
} from '@ionic/vue';
import {
  homeOutline, settingsOutline, personCircleOutline,
  chevronBackOutline, chevronForwardOutline, addOutline
} from 'ionicons/icons';

const router = useRouter();
const route = useRoute();
const selectedTab = ref(route.name?.toString().toLowerCase() || 'home');

const navigateTab = (tabName: string) => {
  router.push({ name: tabName.charAt(0).toUpperCase() + tabName.slice(1) });
  if (tabName === 'home') router.push({ name: 'Home' });
  else if (tabName === 'settings') router.push({ name: 'Settings' });
};

interface Activity {
  id: number
  time: string
  code: string
  text: string
  category: string
}

const today = new Date()
const selectedDateIndex = ref(1)
const displayDates = ref([
  offsetDate(-1),
  offsetDate(0),
  offsetDate(1)
])

function offsetDate(offset: number) {
  const d = new Date(today)
  d.setDate(today.getDate() + offset)
  return {
    day: d.getDate().toString().padStart(2, '0'),
    month: d.toLocaleString('default', { month: 'short' })
  }
}
function rotateDates(dir: number) {
  displayDates.value = displayDates.value.map((_, i) => offsetDate(i - 1 + dir))
}
function goPrevDay() {
  selectedDateIndex.value = 0
  rotateDates(-1)
}
function goNextDay() {
  selectedDateIndex.value = 2
  rotateDates(1)
}

const activities = ref<Activity[]>([
  { id: 1, time: '06:30', code: '403', text: 'Getting up, getting out of bed, going to bed', category: 'morning' },
  { id: 2, time: '06:45', code: '401', text: 'Washing, personal care (brush teeth, shave, make-up…)', category: 'hygiene' },
  { id: 3, time: '07:15', code: '100', text: 'Serving food, set the table', category: 'food' },
  { id: 4, time: '07:25', code: '421', text: 'Sandwiches, breakfast, cold food…', category: 'food' },
  { id: 5, time: '07:55', code: '900', text: 'Travelling to/from work', category: 'commute' },
  { id: 6, time: '08:20', code: '000', text: 'Regular paid work (workplace or elsewhere)', category: 'work' },
  { id: 7, time: '10:00', code: '010', text: 'Break: coffee/work break', category: 'break' }
])

function addActivity() {
  router.push({ name: 'AddActivity' }); // Navigate to the AddActivity route
}

const categoryColors: Record<string, string> = {
  morning: 'var(--sky)',
  hygiene: 'var(--teal)',
  food: 'var(--peach)',
  commute: 'var(--blue)',
  work: 'var(--mauve)',
  break: 'var(--green)'
}

onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
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
.date-month { font-size: .7rem; opacity: .7; }

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
  font-size: .75rem;
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
  --background: transparent !important;
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