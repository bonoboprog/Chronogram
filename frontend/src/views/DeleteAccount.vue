<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="light">
        <ion-buttons slot="start">
          <ion-back-button default-href="/settings" />
        </ion-buttons>
        <ion-title>Delete account</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="delete-wrapper">
        <h2 class="title">Are you sure you want to delete your account?</h2>
        <p class="description">
          Once you delete your account, it cannot be undone. All your data will be permanently erased from this app: your profile information, preferences, saved content, and any activity history.
        </p>
        <p class="description">
          We're sad to see you go, but we understand that sometimes it’s necessary. Please take a moment to consider the consequences before proceeding.
        </p>

        <ion-button expand="block" color="danger" class="delete-button" @click="goToFeedback">
          Delete account
        </ion-button>

        <ion-button expand="block" fill="outline" class="cancel-button" router-link="/settings">
          Go back
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToFeedback = () => {
  router.push({ name: 'DeleteReason' })
}
</script>

<style scoped>
.delete-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  margin-top: 2rem;
}

.title {
  font-size: 1.2rem;
  font-weight: bold;
}

.description {
  font-size: 0.95rem;
  color: var(--ion-color-medium);
}

.delete-button {
  margin-top: 1rem;
}

.cancel-button {
  margin-top: 0.5rem;
}
</style>
