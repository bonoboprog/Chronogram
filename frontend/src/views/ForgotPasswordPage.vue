<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <div class="registration-container">
        <div class="registration-header">
          <ion-icon :icon="keyOutline" class="header-icon" />
          <h1 class="title-peach">Recover Access</h1>
        </div>

        <div class="form-wrapper">
          <p class="reset-subtitle">
            Enter your email to receive reset instructions.
          </p>

          <ion-list lines="none">
            <ion-item :class="errorClass('email')" class="glass-input">
              <ion-icon slot="start" :icon="mailOutline" class="input-icon" />
              <ion-input
                  v-model="email"
                  label="Email"
                  label-placement="floating"
                  type="email"
                  :aria-label="'Email'"
                  autocomplete="email"
              />
            </ion-item>
          </ion-list>

          <ion-grid class="ion-margin-top">
            <ion-row class="ion-justify-content-around">
              <ion-col size="5">
                <ion-button expand="block" class="pill-button gradient-outline" @click="router.push('/login')">
                  Cancel
                </ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button
                    expand="block"
                    :disabled="isLoading || !isValidEmail(email)"
                    class="pill-button gradient-outline"
                    @click="handleSendResetLink"
                >
                  Send Link
                </ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

      <ion-loading :is-open="isLoading" message="Sending reset link..." />
      <ion-toast
          :is-open="toast.open"
          :message="toast.message"
          :color="toast.color"
          :duration="2500"
          @didDismiss="toast.open = false"
      />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'; // Added reactive import
import { useRouter } from 'vue-router';
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol, IonLoading, IonToast
} from '@ionic/vue';
import { mailOutline, keyOutline } from 'ionicons/icons';
import { api } from '@/composables/useApi';

/* ---------- state ---------- */
const router = useRouter();
const isLoading = ref(false);
const email = ref('');

const toast = reactive({ open: false, message: '', color: 'danger' as string });

/* ---------- computed ---------- */
const isValidEmail = (e: string) =>
    /^[\w-.]+@([\w-]+\.)+[\w-]{2,}$/.test(e);

const errorClass = (f: string) => ({
  'ion-invalid': f === 'email' && email.value && !isValidEmail(email.value)
});

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

/* ---------- password reset ---------- */
async function handleSendResetLink() {
  if (!isValidEmail(email.value)) {
    showToast('Please enter a valid email address', 'danger');
    return;
  }

  isLoading.value = true;
  try {
    const { data } = await api.post('/api/auth/request-reset', { email: email.value });

    if (!data?.success) throw new Error(data?.message ?? 'Unknown error');

    showToast(data.message || 'Reset link sent! Check your inbox.', 'success');
    router.push({ name: 'Login' });
  } catch (err: any) {
    console.error('Password reset error:', err);
    const message = err.response?.data?.message || err.message || 'Failed to send reset link';
    showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
.registration-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem 0;
}

.registration-header {
  text-align: center;
  margin-bottom: 2rem;
}

.header-icon {
  font-size: 4rem;
  color: var(--mauve);
  margin-bottom: 0.5rem;
}

.form-wrapper {
  max-width: 450px;
  width: 100%;
}

.reset-subtitle {
  font-size: 0.95rem;
  text-align: center;
  margin: 0 0 1.5rem;
  color: var(--subtitle1);
}

ion-item.glass-input {
  --inner-padding-top: 4px;
  --inner-padding-bottom: 4px;
  --min-height: 48px;
  font-size: 0.95rem;
}

ion-input {
  font-size: 0.95rem;
  --padding-start: 0;
  --padding-end: 0;
}

ion-item.ion-invalid {
  --highlight-color-focused: var(--ion-color-danger);
  --background: rgba(var(--ion-color-danger-rgb), 0.1);
}

.title-peach {
  color: var(--peach);
}

ion-icon.input-icon {
  color: var(--peach);
  font-size: 1.2rem;
  margin-right: 8px;
}

.pill-button {
  --border-radius: 20px;
}
</style>