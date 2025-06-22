<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title class="reset-header-title">Recover Access</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="reset-container">
        <p class="reset-subtitle">
          Enter your email to receive reset instructions.
        </p>

        <ion-item class="reset-input" lines="inset">
          <ion-icon slot="start" :icon="mailOutline"></ion-icon>
          <ion-input
              v-model="email"
              type="email"
              placeholder="Your email"
              fill="outline"
          ></ion-input>
        </ion-item>

        <ion-button expand="block" class="reset-button" @click="handleSendResetLink">
          Send Reset Link
        </ion-button>

        <ion-text class="back-login">
          <RouterLink to="/login">Back to login</RouterLink>
        </ion-text>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import axios from 'axios'
import { mailOutline } from 'ionicons/icons'
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonItem,
  IonIcon,
  IonInput,
  IonButton,
  IonText
} from '@ionic/vue'

const router = useRouter()
const email = ref('')

const handleSendResetLink = async () => {
  if (!email.value || !email.value.includes('@')) {
    alert('Please enter a valid email address.')
    return
  }

  console.log('Sending password reset link to:', email.value)

  try {
    const API = import.meta.env.VITE_API_BASE_URL
    const response = await axios.post(`${API}/forgot-password`, { email: email.value }, {
      headers: { 'Content-Type': 'application/json' }
    })

    if (response.data.success) {
      alert('Reset link sent! Check your inbox.')
      router.push('/login')
    } else {
      alert(`Failed to send link: ${response.data.message}`)
    }
  } catch (error) {
    console.error('Error sending reset link:', error)
    alert('An error occurred. Please try again later.')
  }
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
}

.reset-subtitle {
  font-size: 0.95rem;
  margin: 0 2rem 1.5rem;
  color: var(--text, #aaa);
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
}

.back-login {
  font-size: 0.85rem;
  margin-top: 1rem;
  color: var(--text, #aaa);
}
</style>
