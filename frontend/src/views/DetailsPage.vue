<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button defaultHref="/home" />
        </ion-buttons>
        <ion-title>Activity Details</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list class="glass-card">
        <ion-item>
          <ion-label>Name of Activity</ion-label>
          <ion-input v-model="activity.name" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Duration (min)</ion-label>
          <ion-input type="number" v-model="activity.duration" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Details</ion-label>
          <ion-textarea v-model="activity.details" :readonly="!isEditing" auto-grow />
        </ion-item>

        <ion-item-divider>Enjoyment</ion-item-divider>

        <ion-item>
          <ion-label>Category</ion-label>
          <ion-input v-model="activity.category" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Type of Activity</ion-label>
          <ion-input v-model="activity.type" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Recurrence</ion-label>
          <ion-input v-model="activity.recurrence" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Cost (€)</ion-label>
          <ion-input type="number" v-model="activity.cost" :readonly="!isEditing" />
        </ion-item>

        <ion-item>
          <ion-label>Location</ion-label>
          <ion-input v-model="activity.location" :readonly="!isEditing" />
        </ion-item>
      </ion-list>

      <div class="button-group">
        <ion-button expand="block" color="medium" v-if="!isEditing" @click="isEditing = true">Edit</ion-button>
        <ion-button expand="block" color="primary" v-if="isEditing" @click="saveChanges">Save Changes</ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// Simulamos los datos pasados (más adelante los recogerás del store o props)
const route = useRoute();
const router = useRouter();

const activity = ref({
  name: 'University',
  duration: 60,
  details: 'Attended lecture and worked on a project.',
  category: 'Education',
  type: 'Study',
  recurrence: 'Daily',
  cost: 0,
  location: 'University of Cassino'
});

const isEditing = ref(false);

const saveChanges = () => {
  isEditing.value = false;
  // Aquí puedes añadir tu lógica para guardar en el backend
  console.log('Changes saved:', activity.value);
};
</script>

<style scoped>
.glass-card {
  background-color: var(--surface0);
  border-radius: 16px;
  padding: 1rem;
  margin-bottom: 2rem;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.1);
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 1rem;
}
</style>
