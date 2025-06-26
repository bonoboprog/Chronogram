<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <div class="registration-container">
        <div class="registration-header">
          <ion-icon :icon="keyOutline" class="header-icon" />
          <h1 class="title-peach">Set New Password</h1>
        </div>

        <div class="form-wrapper">
          <p class="reset-subtitle">
            Enter your new password below. Make sure it's a strong one!
          </p>

          <ion-list lines="none">
            <!-- New Password Field -->
            <ion-item :class="errorClass('password')" class="glass-input password-item">
              <ion-icon slot="start" :icon="lockClosedOutline" class="input-icon" />
              <ion-input
                  v-model="form.newPassword"
                  label="New Password"
                  label-placement="floating"
                  :type="showPassword ? 'text' : 'password'"
                  :aria-label="'New Password'"
                  autocomplete="new-password"
                  @ionInput="validatePassword"
              />
              <ion-icon
                  slot="end"
                  :icon="showPassword ? eyeOffOutline : eyeOutline"
                  @click="showPassword = !showPassword"
                  class="toggle-eye"
              />
            </ion-item>

            <!-- Confirm Password Field -->
            <ion-item :class="errorClass('confirmPassword')" class="glass-input">
              <ion-icon slot="start" :icon="lockClosedOutline" class="input-icon" />
              <ion-input
                  v-model="form.confirmPassword"
                  label="Confirm Password"
                  label-placement="floating"
                  type="password"
                  :aria-label="'Confirm Password'"
                  autocomplete="new-password"
                  @ionInput="validatePassword"
              />
            </ion-item>
          </ion-list>

          <!-- Validation Messages -->
          <div v-if="errors.password" class="error-message">
            {{ errors.password }}
          </div>
          <div v-if="errors.confirmPassword" class="error-message">
            {{ errors.confirmPassword }}
          </div>

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
                    :disabled="isLoading || hasErrors"
                    class="pill-button gradient-outline"
                    @click="handleResetPassword"
                >
                  <ion-spinner v-if="isLoading" name="crescent"></ion-spinner>
                  <span v-else>Save Password</span>
                </ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

      <ion-loading :is-open="isLoading" message="Updating password..." />
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
import { ref, reactive, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol, IonSpinner, IonToast,
  IonLoading
} from '@ionic/vue';
import { keyOutline, lockClosedOutline, eyeOutline, eyeOffOutline } from 'ionicons/icons';
import { api } from '@/composables/useApi';

/* ---------- state ---------- */
const route = useRoute();
const router = useRouter();
const token = ref(route.query.token as string || '');
const showPassword = ref(false);
const isLoading = ref(false);

const form = reactive({
  newPassword: '',
  confirmPassword: ''
});

const errors = reactive({
  password: '',
  confirmPassword: ''
});

const toast = reactive({
  open: false,
  message: '',
  color: 'danger' as const
});

/* ---------- computed ---------- */
const hasErrors = computed(() =>
    !form.newPassword ||
    !form.confirmPassword ||
    !!errors.password ||
    !!errors.confirmPassword ||
    form.newPassword !== form.confirmPassword
);

/* ---------- methods ---------- */
const isStrongPassword = (p: string) =>
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/.test(p);

const errorClass = (field: keyof typeof errors) => ({
  'ion-invalid': !!errors[field]
});

const validatePassword = () => {
  errors.password = '';
  errors.confirmPassword = '';

  if (form.newPassword && !isStrongPassword(form.newPassword)) {
    errors.password = 'Must include uppercase, lowercase, number, and symbol';
  }

  if (form.confirmPassword && form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }
};

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

/* ---------- password reset ---------- */
async function handleResetPassword() {
  try {
    validatePassword();

    if (hasErrors.value) {
      await showToast('Please correct the form errors', 'danger');
      return;
    }

    isLoading.value = true;
    const { data } = await api.post('/api/auth/reset-password', {
      token: token.value,
      newPassword: form.newPassword
    });

    if (!data?.success) throw new Error(data?.message ?? 'Unknown error');

    await showToast(data.message || 'Password updated successfully!', 'success');
    await router.push({ name: 'Login' });
  } catch (err: any) {
    console.error('Password reset error:', err);
    const message = err.response?.data?.message || err.message || 'Failed to update password';
    await showToast(message, 'danger');
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

.password-item ion-icon.toggle-eye {
  cursor: pointer;
  font-size: 1.2rem;
}

.error-message {
  color: var(--ion-color-danger);
  font-size: 0.8rem;
  padding-left: 16px;
  padding-top: 4px;
  margin-bottom: 1rem;
}

.title-peach {
  color: var(--peach);
}

ion-icon.input-icon {
  color: var(--peach);
  font-size: 1.2rem;
  margin-right: 8px;
}

ion-icon.toggle-eye {
  color: var(--peach);
}

.pill-button {
  --border-radius: 20px;
}
</style>