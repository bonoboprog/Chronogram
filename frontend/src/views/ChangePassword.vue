<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <!-- Back button to go Home -->
        <ion-buttons slot="start">
          <ion-button @click="goToHome">
            <ion-icon slot="icon-only" :icon="arrowBackOutline" class="back-icon" />
          </ion-button>
        </ion-buttons>
        <ion-title class="page-title">Change Password</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="form-wrapper">
        <!-- New Password with eye toggle -->
        <ion-item lines="inset">
          <ion-label position="stacked">New Password</ion-label>
          <ion-input
              :type="showNewPassword ? 'text' : 'password'"
              v-model="newPassword"
              placeholder="Enter new password"
          ></ion-input>
          <ion-icon
              :icon="eyeOutline"
              slot="end"
              class="eye-icon"
              @click="toggleNewPassword"
          />
        </ion-item>

        <!-- Confirm New Password with eye toggle -->
        <ion-item lines="inset">
          <ion-label position="stacked">Confirm New Password</ion-label>
          <ion-input
              :type="showConfirmPassword ? 'text' : 'password'"
              v-model="confirmPassword"
              placeholder="Confirm new password"
          ></ion-input>
          <ion-icon
              :icon="eyeOutline"
              slot="end"
              class="eye-icon"
              @click="toggleConfirmPassword"
          />
        </ion-item>

        <!-- Submit button -->
        <ion-button expand="block" class="submit-btn">
          Update Password
        </ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { eyeOutline, arrowBackOutline } from 'ionicons/icons'

// Navigation
const router = useRouter()
const goToHome = () => router.push({ name: 'Home' })

// Form fields
const newPassword = ref('')
const confirmPassword = ref('')

// Eye icon toggles
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const toggleNewPassword = () => {
  showNewPassword.value = !showNewPassword.value
}
const toggleConfirmPassword = () => {
  showConfirmPassword.value = !showConfirmPassword.value
}
</script>

<style scoped>
.page-title {
  text-align: center;
  font-size: 1.2rem;
  color: #e0aaff;
}

.form-wrapper {
  margin-top: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 0 1.5rem;
}

/* Eye icon color and size */
.eye-icon {
  color: #ca9e8a;
  font-size: 1.3rem;
  cursor: pointer;
}

/* Button styling */
.submit-btn {
  --background: #c18aff;
  --background-hover: #d7aaff;
  --color: white;
  font-weight: 600;
  border-radius: 20px;
  margin-top: 2rem;
}

/* Back button icon color */
.back-icon {
  font-size: 1.4rem;
  color: var(--peach, #fab387); /* fallback peach */
}
</style>
