<template>
  <ion-page>
    <ion-content class="ion-padding ion-text-center">
      <div class="login-container">
        <div class="logo-wrapper">
          <img src="/logo.png" alt="Chronogram Logo" class="login-logo" />
        </div>

        <div class="form-wrapper">
          <ion-list lines="none">
            <ion-item class="glass-input">
              <ion-icon :icon="personOutline" class="input-icon" />
              <ion-input
                  label="Username or Email"
                  label-placement="floating"
                  placeholder="Insert your username"
                  type="email"
                  autocomplete="username"
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
            <ion-button @click="handleForgotPassword" fill="clear" class="forgot-password-btn">Forgot Password?</ion-button>
          </div>

          <ion-grid class="ion-margin-top">
            <ion-row class="ion-justify-content-around">
              <ion-col size="5">
                <ion-button @click="handleLogin" expand="block" class="pill-button gradient-outline">Login</ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button @click="goToRegistration" expand="block" class="pill-button gradient-outline">Sign Up</ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'; // Import ref
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol,
} from '@ionic/vue';
import { personOutline, keyOutline } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import axios from 'axios'; // Import axios

const router = useRouter();

// Define reactive variables for form inputs
const email = ref('');
const password = ref('');

const goToRegistration = () => {
  router.push({ name: 'Register' });
};

const handleLogin = async () => {
  console.log('Attempting login...');
  const payload = {
    email: email.value,
    password: password.value
  };

  console.log('Sending login payload:', payload);

  try {
    const API = import.meta.env.VITE_API_BASE_URL; // Ensure this is correctly configured
    const response = await axios.post(`${API}/login`, payload, {
      headers: { 'Content-Type': 'application/json' }
    });

    console.log('Backend login response:', response.data);
    if (response.data.success) {
      alert(`Login successful! Welcome, ${response.data.username}`);
      router.push({ name: 'Home' }); // Redirect to home on success
    } else {
      alert(`Login failed: ${response.data.message}`);
    }
  } catch (error) {
    console.error('Error during login:', error);
    alert('An error occurred during login. Please try again.');
  }
};

const handleForgotPassword = () => {
  console.log('Forgot password clicked...');
};
</script>

<style scoped>

/* Il tuo stile rimane invariato */
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