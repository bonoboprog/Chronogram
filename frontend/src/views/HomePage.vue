<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-title>AI Assistant 🤖</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-card>
        <ion-card-header>
          <ion-card-title>Ask something</ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <ion-textarea
              v-model="prompt"
              label="Prompt"
              placeholder="e.g. What can I do today to be more productive?"
              :rows="4"
              auto-grow
          ></ion-textarea>

          <ion-button
              expand="block"
              class="ion-margin-top"
              :disabled="loading || !prompt.trim()"
              @click="sendPrompt"
          >
            <ion-spinner v-if="loading" name="dots" slot="start" />
            Send
          </ion-button>
        </ion-card-content>
      </ion-card>

      <ion-card v-if="response">
        <ion-card-header>
          <ion-card-subtitle>LLM Response</ion-card-subtitle>
        </ion-card-header>
        <ion-card-content>
          <pre class="response-box">{{ response }}</pre>
        </ion-card-content>
      </ion-card>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonButton,
  IonTextarea,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonCardSubtitle,
  IonSpinner,
} from '@ionic/vue'

import { ref } from 'vue'
import axios from 'axios'

const API = import.meta.env.VITE_API_BASE_URL
const prompt = ref('')
const response = ref('')
const loading = ref(false)

const sendPrompt = async () => {
  response.value = ''
  loading.value = true

  const payload = new URLSearchParams({ prompt: prompt.value })

  console.log('[🔎 DEBUG] API URL:', `${API}/llmPrompt`)
  console.log('[🟨 DEBUG] Prompt sent:', prompt.value)

  try {
    const res = await axios.post(`${API}/llmPrompt`, payload, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })

    console.log('[✅ DEBUG] Raw response:', res.data)

    

    response.value = res.data.llmResponse || 'No content from model'

  } catch (err: any) {
    console.error('[❌ ERROR] Axios failed:', err)
    response.value = `Error: ${err.message}`
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.response-box {
  background: #f6f6f6;
  padding: 12px;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #333;
  white-space: pre-wrap;
}
</style>
