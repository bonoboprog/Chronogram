// INTEGRARE QUI SOLO LA PARTE GRAFICA NON LA LOGICA DELLO SCRIPT TS DELLLA CORRISPONDENTE  PAGINA FRONTEND DI VIOLETA


<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Set New Password</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding">
      <div class="form-container">
        <p>Enter your new password below. Make sure it's a strong one!</p>
        
        <ion-item>
          <ion-input
            v-model="newPassword"
            label="New Password"
            label-placement="floating"
            :type="showPassword ? 'text' : 'password'"
          ></ion-input>
          <ion-icon slot="end" :icon="showPassword ? eyeOffOutline : eyeOutline" @click="showPassword = !showPassword"></ion-icon>
        </ion-item>

        <ion-item>
          <ion-input
            v-model="confirmPassword"
            label="Confirm New Password"
            label-placement="floating"
            type="password"
          ></ion-input>
        </ion-item>

        <div v-if="!passwordsMatch && confirmPassword" class="error-message">
          Passwords do not match.
        </div>
        <div v-if="!isPasswordStrong && newPassword" class="error-message">
          Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a symbol.
        </div>

        <ion-button 
          expand="block" 
          @click="handleResetPassword" 
          class="ion-margin-top" 
          :disabled="isLoading || !isFormValid"
        >
          <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
          <span v-else>Save New Password</span>
        </ion-button>
      </div>

      <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open = false"></ion-toast>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonItem, IonInput, IonButton, IonIcon, IonSpinner, IonToast } from '@ionic/vue';
import { eyeOutline, eyeOffOutline } from 'ionicons/icons';
import { api } from '@/composables/useApi';

// --- State ---
const route = useRoute();
const router = useRouter();
const token = ref(route.query.token as string || '');

const newPassword = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const isLoading = ref(false);
const toast = reactive({ open: false, message: '', color: 'success' as const });

// --- Validation ---
const passwordsMatch = computed(() => newPassword.value === confirmPassword.value);
const isPasswordStrong = computed(() => /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/.test(newPassword.value));
const isFormValid = computed(() => token.value && passwordsMatch.value && isPasswordStrong.value);

// --- Methods ---
const showToast = (msg:string, col:'success'|'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

const handleResetPassword = async () => {
  if (!isFormValid.value) {
    showToast('Please correct the form errors.', 'danger');
    return;
  }

  isLoading.value = true;
  try {
    const { data } = await api.post('/api/auth/reset-password', {
      token: token.value,
      newPassword: newPassword.value
    });

    if (data.success) {
      showToast(data.message, 'success');
      // Redirect to login page after successful reset
      router.push('/login');
    } else {
      showToast(data.message, 'danger');
    }
  } catch (error: any) {
    console.error('Error resetting password:', error);
    showToast(error.message || 'An error occurred.', 'danger');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.form-container {
  max-width: 500px;
  margin: 2rem auto;
}
.error-message {
  color: var(--ion-color-danger);
  font-size: 0.8rem;
  padding-left: 16px;
  padding-top: 4px;
}
</style>
