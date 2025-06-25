<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <!-- Gear icon to go back to Settings -->
        <ion-buttons slot="start">
          <ion-button @click="goToSettings">
            <ion-icon :icon="settingsOutline" slot="icon-only" />
          </ion-button>
        </ion-buttons>
        <ion-title class="reset-header-title">Recover Access</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="reset-container">

        <p class="reset-subtitle">
          Enter your email to receive reset instructions.
        </p>

        <!-- Email field -->
        <ion-item class="reset-input" lines="inset">
          <ion-icon slot="start" :icon="mailOutline" color="#f9c7a1;"></ion-icon>
          <ion-input
              v-model="email"
              type="email"
              placeholder="Your email"
              fill="outline"
          ></ion-input>
        </ion-item>

        <!-- Submit button -->
        <ion-button expand="block" class="reset-button">
          Send Reset Link
        </ion-button>

        <!-- Back to login -->
        <ion-text class="back-login">
          <router-link to="/login">Back to login</router-link>
        </ion-text>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { mailOutline, settingsOutline } from 'ionicons/icons'
import { useRouter } from 'vue-router'

const email = ref('')
const router = useRouter()

// Navigate back to Settings page
function goToSettings() {
  router.push({ name: 'Settings' })
}
</script>

<style scoped>
.reset-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 3rem;
  text-align: center;
}

.reset-header-title {
  text-align: center;
  font-size: 1.2rem;
  color: #e0aaff;
}

.reset-subtitle {
  font-size: 0.95rem;
  margin: 0 2rem 1.5rem;
  color: #aaa;
}

.reset-input {
  width: 90%;
  max-width: 400px;
  margin-bottom: 1rem;
  border-radius: 8px;
}

.reset-button {
  width: 90%;
  max-width: 400px;
  font-weight: 500;
  margin-bottom: 1rem;
  --background: #c18aff;
  --background-hover: #d7aaff;
  --color: white;
  border-radius: 12px;
}

.back-login {
  font-size: 0.85rem;
  margin-top: 1rem;
  color: #aaa;
}
</style>
