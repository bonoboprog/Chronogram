<template>
  <IonPage class="time-diary-page">
    <!-- ───────────── HEADER : DATE PAGER ───────────── -->
    <IonHeader>
      <IonToolbar class="date-toolbar" color="dark">
        <!-- Prev Day -->
        <IonButtons slot="start">
          <IonButton fill="clear" @click="goPrevDay">
            <IonIcon :icon="chevronBackOutline" />
          </IonButton>
        </IonButtons>

        <!-- Centered date segment (modern pill style) -->
        <div class="date-pager-wrapper">
          <IonSegment v-model="selectedDateIndex" class="date-pager" :scrollable="false">
            <IonSegmentButton
                v-for="(d, i) in displayDates"
                :key="i"
                :value="i"
            >
              <div class="date-number">{{ d.day }}</div>
              <div class="date-month">{{ d.month }}</div>
            </IonSegmentButton>
          </IonSegment>
        </div>

        <!-- Next Day -->
        <IonButtons slot="end">
          <IonButton fill="clear" @click="goNextDay">
            <IonIcon :icon="chevronForwardOutline" />
          </IonButton>
        </IonButtons>
      </IonToolbar>
    </IonHeader>

    <!-- ───────────── CONTENT : TIMELINE ───────────── -->
    <IonContent :scroll-y="true">
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
            <IonCard
                class="activity-bubble"
                :style="{ borderLeftColor: categoryColors[a.category] }"
            >
              <IonCardContent>
                <strong>{{ a.code }}</strong> {{ a.text }}
              </IonCardContent>
            </IonCard>
          </div>
        </transition-group>
      </div>
    </IonContent>

    <!-- ───────────── FABs ───────────── -->
    <!-- Home -->
    <IonFab vertical="bottom" horizontal="start" slot="fixed" class="corner-fab home-fab">
      <IonFabButton size="small">
        <IonIcon :icon="homeOutline" />
      </IonFabButton>
    </IonFab>

    <!-- Settings -->
    <IonFab vertical="bottom" horizontal="end" slot="fixed" class="corner-fab settings-fab">
      <IonFabButton size="small">
        <IonIcon :icon="settingsOutline" />
      </IonFabButton>
    </IonFab>

    <!-- Add Activity (primary) -->
    <IonFab vertical="bottom" horizontal="center" slot="fixed" class="add-fab">
      <IonFabButton class="add-fab-btn" @click="addActivity">
        <IonIcon :icon="addOutline" />
      </IonFabButton>
    </IonFab>
  </IonPage>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonButtons,
  IonButton,
  IonIcon,
  IonSegment,
  IonSegmentButton,
  IonContent,
  IonCard,
  IonCardContent,
  IonFab,
  IonFabButton
} from '@ionic/vue'
import {
  chevronBackOutline,
  chevronForwardOutline,
  addOutline,
  homeOutline,
  settingsOutline
} from 'ionicons/icons'

interface Activity {
  id: number
  time: string
  code: string
  text: string
  category: string
}

/* ---------- Dates ---------- */
const today = new Date()
const selectedDateIndex = ref(1) // 0 = prev, 1 = today, 2 = next
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

/* ---------- Activities ---------- */
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
  // placeholder action
  console.log('add new activity')
}

/* ---------- Category Colors ---------- */
const categoryColors: Record<string, string> = {
  morning: 'var(--sky)',
  hygiene:  'var(--teal)',
  food:     'var(--peach)',
  commute:  'var(--blue)',
  work:     'var(--mauve)',
  break:    'var(--green)'
}
</script>

<style scoped>
/****************** DATE PAGER ************************/
.date-toolbar { --min-height: 44px; }
.date-pager-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
}
.date-pager {
  --background: transparent;
  border-radius: 999px;
  padding: 0 4px;
  overflow: hidden;
}
.date-pager ion-segment-button {
  --background: transparent;
  --color: var(--text);
  min-width: 80px;
  flex-direction: column;
  text-transform: uppercase;
}
.date-pager ion-segment-button::part(indicator) { display: none; }
.date-pager ion-segment-button.ion-activated {
  background: var(--surface2);
  border-radius: 12px;
}
.date-number { font-size: 1rem;  font-weight: 700; line-height: 1; }
.date-month  { font-size: .7rem; line-height: 1; opacity: .7; }

/****************** TIMELINE ************************/
.timeline-container { padding: 0 0 64px 0; }
.activity-row {
  position: relative;
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;          /* gap tra le attività */
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
  left: 50px;                  /* dopo la colonna orari */
  top: 6px;
  width: 8px; height: 8px;
  border-radius: 50%;
  background: var(--stripe-color);
}
.timeline-line {
  position: absolute;
  left: 53.5px;                /* centro del dot */
  top: 18px; bottom: -18px;
  width: 1px;
  background: var(--surface2);
}

/****************** ACTIVITY BUBBLE *****************/
.activity-bubble {
  flex: 1;
  margin-left: 64px;           /* 50 (orari) + 14 (spazio/dot) */
  --background: rgba(255,255,255,.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,.12);
  border-left: 4px solid var(--stripe-color);
  border-radius: 18px 18px 6px 18px;
  transition: background .15s ease, box-shadow .15s ease;
}
.activity-bubble:hover {
  background: linear-gradient(
      120deg,
      color-mix(in srgb, var(--peach) 15%, transparent),
      color-mix(in srgb, var(--mauve) 15%, transparent)
  );
  box-shadow: 0 0 12px -2px var(--mauve);
}
.activity-bubble::after {
  content: '';
  position: absolute;
  left: -6px; bottom: 0;
  width: 12px; height: 12px;
  border-radius: 0 0 0 12px;
  background: inherit;
  border-left: inherit;
  border-bottom: inherit;
}

/****************** CORNER FABs *********************/
.corner-fab { --bottom: calc(20px + env(safe-area-inset-bottom)); }
.corner-fab ion-fab-button {
  --background: var(--surface1);
  --color: var(--text);
  --size: 42px;
}

/****************** FAB principale “+” **************/
.add-fab {
  /* niente sfondo quadrato */
  --background: transparent !important;
  /* rendo il contenitore circolare (failsafe) */
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
  transition: transform .15s ease;
}
.add-fab-btn:hover  { transform: scale(1.05); }
.add-fab-btn:active { transform: scale(.95); }

/****************** ENTRANCE ANIMATION **************/
.activity-enter-active,
.activity-leave-active {
  transition: all .25s cubic-bezier(.2,.8,.4,1);
}
.activity-enter-from {
  opacity: 0;
  transform: translateY(12px) scale(.98);
}
.activity-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}
</style>