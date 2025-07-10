<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="light">
        <ion-title>Request received</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="feedback-wrapper">
        <p class="description">
          We're sending an email to <strong>your.email@email.com</strong> confirming your account is deleted.
        </p>
        <h3 class="reason-title">Why did you decide to leave this app?</h3>

        <ion-list>
          <ion-item v-for="(reason, index) in reasons" :key="index" lines="none">
            <ion-checkbox v-model="reason.selected" slot="start"></ion-checkbox>
            <ion-label>{{ reason.text }}</ion-label>
          </ion-item>
        </ion-list>

        <ion-button expand="block" class="done-button" @click="goToLogin">
          Done
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const reasons = ref([
  { text: "I'm not using the app.", selected: false },
  { text: "I found a better alternative.", selected: false },
  { text: "The app contains too many ads.", selected: false },
  { text: "The app didn’t have the features or functionality I was looking for.", selected: false },
  { text: "I’m not satisfied with the quality of content.", selected: false },
  { text: "The app was difficult to navigate.", selected: false },
  { text: "Other.", selected: false },
])

const goToLogin = () => {
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.feedback-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.description {
  font-size: 0.95rem;
  color: var(--ion-color-medium);
}

.reason-title {
  font-weight: bold;
  margin-top: 1rem;
  font-size: 1rem;
}

.done-button {
  margin-top: 1.5rem;
}
</style>
