<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>

        <!-- Botón de retroceso (con color peach y funcional) -->
        <ion-buttons slot="start">
          <ion-button @click="goToHome">
            <ion-icon slot="icon-only" :icon="arrowBackOutline" class="back-icon" />
          </ion-button>
        </ion-buttons>

        <!-- Icono calendario a la derecha -->
        <ion-buttons slot="end">
          <ion-icon :icon="calendarOutline" class="header-icon" />
        </ion-buttons>

        <ion-title></ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <!-- Calendario centrado -->
      <div class="calendar-wrapper">
        <ion-datetime
            presentation="date"
            :value="selectedDate"
            @ionChange="onDateChange"
            locale="en-GB"
            class="calendar"
        ></ion-datetime>
      </div>

      <!-- Fecha seleccionada -->
      <div class="date-label gradient-text">
        <ion-icon :icon="calendarOutline" class="date-icon" />
        <h2>{{ formattedDate }}</h2>
      </div>

      <!-- Lista de actividades -->
      <div v-if="filteredEvents.length > 0">
        <div
            v-for="(event, index) in filteredEvents"
            :key="index"
            class="event-box"
        >
          <div class="event-time">{{ event.time }}</div>
          <div class="event-info">
            <div class="event-title">{{ event.title }}</div>
            <div class="event-dot" :style="{ backgroundColor: event.color }"></div>
          </div>
        </div>
      </div>
      <div v-else class="no-events">
        No activities for this day.
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  IonPage, IonHeader, IonToolbar, IonTitle,
  IonContent, IonDatetime, IonIcon, IonButtons, IonButton
} from '@ionic/vue'
import { calendarOutline, arrowBackOutline } from 'ionicons/icons'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHome = () => {
  router.push({ name: 'Home' })
}

const selectedDate = ref(new Date().toISOString().substring(0, 10))

const events = [
  { date: '2025-06-20', time: '08:00', title: 'Morning Activity', color: '#f9a28f' },
  { date: '2025-06-20', time: '09:00', title: 'University', color: '#d9a4f5' },
  { date: '2025-06-21', time: '11:00', title: 'Gym Session', color: '#b4e1ff' },
]

const onDateChange = (e: CustomEvent) => {
  selectedDate.value = e.detail.value
}

const formattedDate = computed(() => {
  const date = new Date(selectedDate.value)
  return date.toLocaleDateString('en-GB', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: '2-digit'
  })
})

const filteredEvents = computed(() =>
    events.filter(e => e.date === selectedDate.value)
)
</script>

<style scoped>
.calendar-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.calendar {
  max-width: 320px;
  border-radius: 16px;
  background-color: var(--base);
}

.date-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  justify-content: center;
  margin-bottom: 1.2rem;
}

.date-icon {
  font-size: 1.4rem;
}

.back-icon {
  font-size: 1.4rem;
  color: var(--peach);
}

.header-icon {
  font-size: 1.4rem;
  color: var(--peach);
}

.event-box {
  background-color: var(--surface1);
  border: 1px solid var(--overlay1);
  border-radius: 16px;
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.event-time {
  font-weight: bold;
  color: var(--subtext0);
  margin-bottom: 4px;
}

.event-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-title {
  font-size: 1rem;
  color: var(--text);
}

.event-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.no-events {
  text-align: center;
  margin-top: 2rem;
  font-size: 0.95rem;
  color: var(--subtext0);
}
</style>
