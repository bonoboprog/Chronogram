<template>
  <ion-page>
    <ion-content class="ion-padding ion-text-center">
      <div class="login-container">
        <div class="logo-wrapper">
          <img src="/logo.png" alt="App Logo" class="login-logo" />
        </div>

        <div class="form-wrapper">
          <ion-list lines="none">
            <ion-item class="glass-input">
              <ion-icon :icon="personOutline" class="input-icon" />
              <ion-input
                  label="Email"
                  label-placement="floating"
                  placeholder="Insert your email"
                  type="email"
                  autocomplete="email"
                  v-model="email"
              ></ion-input>
            </ion-item>

            <ion-item class="glass-input">
              <ion-icon :icon="keyOutline" class="input-icon" />
              <ion-input
                  label="Password"
                  label-placement="floating"
                  type="password"
                  placeholder="Insert your password"
                  autocomplete="current-password"
                  v-model="password"
              ></ion-input>
            </ion-item>
          </ion-list>

          <div class="forgot-password-container">
            <ion-button @click="handleForgotPassword" fill="clear" class="forgot-password-btn">
              Forgot Password?
            </ion-button>
          </div>

          <ion-grid class="ion-margin-top">
            <ion-row class="ion-justify-content-around">
              <ion-col size="5">
                <ion-button @click="handleLogin" expand="block" class="pill-button gradient-outline" :disabled="isLoading">
                  <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
                  <span v-else>Login</span>
                </ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button @click="goToRegistration" expand="block" class="pill-button gradient-outline">
                  Sign Up
                </ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

      <ion-toast
          :is-open="showToast"
          :message="toastMessage"
          :color="toastColor"
          :duration="3000"
          @didDismiss="showToast = false"
      ></ion-toast>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol, IonToast, IonSpinner // MODIFIED: Imported IonSpinner
} from '@ionic/vue';
import { personOutline, keyOutline } from 'ionicons/icons';

const router = useRouter();
const auth = useAuthStore();

const isLoading = ref(false);

// Form data
const email = ref('');
const password = ref('');

// Toast state
const showToast = ref(false);
const toastMessage = ref('');
const toastColor = ref('danger');

const presentToast = (message: string, color: string = 'danger') => {
  toastMessage.value = message;
  toastColor.value = color;
  showToast.value = true;
};

const handleLogin = async () => {
  if (!email.value.trim()) {
    presentToast('Please enter your email');
    return;
  }
  if (!password.value.trim()) {
    presentToast('Please enter your password');
    return;
  }

  isLoading.value = true;

  try {
    await auth.login({ email: email.value, password: password.value });
    presentToast('Login successful!', 'success');
    router.push({ name: 'Home' });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'An unknown error occurred';
    presentToast(errorMessage, 'danger');
  } finally {
    isLoading.value = false;
  }
};
const goToRegistration = () => {
  router.push({ name: 'Register' });
};

const handleForgotPassword = () => {
  router.push({ name: 'ForgotPassword' });
};
</script>

<style scoped>

.pill-button ion-spinner {
  width: 1.5em;
  height: 1.5em;
}

.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.logo-wrapper {
  margin-bottom: 2.5rem;
}
.login-logo {
  width: 65%;
  max-width: 240px;
  height: auto;
}
.form-wrapper {
  max-width: 450px;
  width: 100%;
}
.forgot-password-container {
  display: flex;
  justify-content: flex-end;
  padding-right: 4px;
  margin-top: 4px;
  margin-bottom: 1.5rem;
}
.forgot-password-btn {
  --color: var(--subtext0);
  font-size: 0.8rem;
  text-transform: none;
  font-weight: 500;
  --padding-start: 0;
  --padding-end: 0;
  height: auto;
}
</style>