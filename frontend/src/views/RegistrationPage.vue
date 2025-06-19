<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <!-- Glassmorphism wrapper -->
      <div class="registration-container">
        <div class="registration-header">
          <ion-icon :icon="personAddOutline" class="header-icon" />
          <h1>Registration</h1>
        </div>

        <div class="form-wrapper">
          <ion-list lines="none">
            <!-- NAME -->
            <ion-item :class="errorClass('name')" class="glass-input">
              <ion-input
                v-model="form.name"
                label="Name"
                label-placement="floating"
                type="text"
                :aria-label="'Name'"
                autocomplete="given-name"
              />
            </ion-item>

            <!-- SURNAME -->
            <ion-item :class="errorClass('surname')" class="glass-input">
              <ion-input
                v-model="form.surname"
                label="Surname"
                label-placement="floating"
                type="text"
                :aria-label="'Surname'"
                autocomplete="family-name"
              />
            </ion-item>

            <!-- PHONE -->
            <ion-item class="glass-input">
              <ion-input
                v-model="form.phone"
                label="Phone"
                label-placement="floating"
                type="tel"
                :aria-label="'Phone'"
                autocomplete="tel"
              />
            </ion-item>

            <!-- EMAIL -->
            <ion-item :class="errorClass('email')" class="glass-input">
              <ion-input
                v-model="form.email"
                label="Email"
                label-placement="floating"
                type="email"
                :aria-label="'Email'"
                autocomplete="username"
              />
            </ion-item>

            <!-- PASSWORD -->
            <ion-item :class="errorClass('password')" class="glass-input password-item">
              <ion-input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                label="Password"
                label-placement="floating"
                :aria-label="'Password'"
                autocomplete="new-password"
              />
              <ion-icon
                slot="end"
                :icon="showPassword ? eyeOffOutline : eyeOutline"
                @click="showPassword = !showPassword"
                class="toggle-eye"
              />
            </ion-item>

            <!-- BIRTHDAY -->
            <ion-item
              class="glass-input"
              :class="{ 'item-has-value': !!form.birthday }"
              @click="openBirthdayModal"
              :detail="false"
              button
            >
              <ion-label position="floating">Birthday</ion-label>
              <div class="custom-input-value">{{ formattedBirthday }}</div>
            </ion-item>

            <!-- GENDER -->
            <ion-item class="glass-input">
              <ion-select
                v-model="form.gender"
                label="Gender"
                label-placement="floating"
                interface="popover"
                :interface-options="{ cssClass: 'ion-dark catppuccin-select-overlay' }"
              >
                <ion-select-option value="male">Male</ion-select-option>
                <ion-select-option value="female">Female</ion-select-option>
                <ion-select-option value="other">Other</ion-select-option>
                <ion-select-option value="not_specified">Prefer not to say</ion-select-option>
              </ion-select>
            </ion-item>
          </ion-list>

          <!-- BUTTONS -->
          <ion-grid class="ion-margin-top">
            <ion-row class="ion-justify-content-around">
              <ion-col size="5">
                <ion-button expand="block" class="pill-button gradient-outline" @click="router.back()">Cancel</ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button
                  expand="block"
                  :disabled="isLoading || hasErrors"
                  class="pill-button gradient-outline"
                  @click="handleRegister"
                >
                  Register
                </ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

      <!-- BIRTHDAY MODAL -->
      <ion-modal ref="birthdayModal" :keep-contents-mounted="true">
        <ion-datetime
          presentation="date"
          v-model="dateIso"
          @ionChange="onBirthdaySelected"
          class="ion-dark"
          :interface-options="{ cssClass: 'catppuccin-datetime-overlay' }"
        />
      </ion-modal>

      <!-- LOADING -->
      <ion-loading :is-open="isLoading" message="Registering..." />

      <!-- TOAST -->
      <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open=false" />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import {
  IonPage,
  IonContent,
  IonList,
  IonItem,
  IonInput,
  IonIcon,
  IonButton,
  IonGrid,
  IonRow,
  IonCol,
  IonSelect,
  IonSelectOption,
  IonLabel,
  IonDatetime,
  IonModal,
  IonLoading,
  IonToast,
  useIonToast,
} from '@ionic/vue';
import { useRouter } from 'vue-router';
import { personAddOutline, eyeOutline, eyeOffOutline } from 'ionicons/icons';
import dayjs from 'dayjs';
import { api } from '@/composables/useApi';

// ---------- reactive state ----------
const router = useRouter();
const birthdayModal = ref<InstanceType<typeof IonModal>>();
const isLoading = ref(false);
const showPassword = ref(false);
const dateIso = ref<string>(); // bound to ion-datetime (ISO string)

// unified form state
const form = reactive({
  name: '',
  surname: '',
  phone: '',
  email: '',
  password: '',
  birthday: '', // dd-MM-yyyy
  gender: '',
});

// toast helper
const toast = reactive({ open: false, message: '', color: 'danger' as const });

// ---------- computed ----------
const formattedBirthday = computed(() => form.birthday);

const hasErrors = computed(() => {
  return (
    !form.name.trim() ||
    !form.surname.trim() ||
    !isValidEmail(form.email) ||
    !isStrongPassword(form.password)
  );
});

// ---------- methods ----------
const openBirthdayModal = () => birthdayModal.value?.$el.present();

const onBirthdaySelected = () => {
  if (dateIso.value) {
    form.birthday = dayjs(dateIso.value).format('DD-MM-YYYY');
  }
  birthdayModal.value?.$el.dismiss();
};

function errorClass(field: keyof typeof form) {
  if (field === 'email') return { 'ion-invalid': form.email && !isValidEmail(form.email) };
  if (field === 'password') return { 'ion-invalid': form.password && !isStrongPassword(form.password) };
  return { 'ion-invalid': !(form[field] as string).trim() };
}

function isValidEmail(email: string) {
  return /^[\w-.]+@([\w-]+\.)+[\w-]{2,}$/.test(email);
}

function isStrongPassword(pwd: string) {
  return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/.test(pwd);
}

async function handleRegister() {
  if (hasErrors.value) {
    showToast('Please correct the highlighted fields.', 'danger');
    return;
  }

  isLoading.value = true;
  try {
    const response = await api.post('/register', { ...form });
    console.log('🟢 Response:', response);

    const data = response.data;

    if (!data || typeof data !== 'object') {
      throw new Error('Invalid server response');
    }

    if (!data.success) throw new Error(data.message);

    showToast('Registered successfully!', 'success');
    router.push({ name: 'Login' });
  } catch (err: any) {
    showToast(err.message || 'Unexpected error', 'danger');
  } finally {
    isLoading.value = false;
  }
}


function showToast(message: string, color: 'danger' | 'success') {
  toast.message = message;
  toast.color = color;
  toast.open = true;
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
ion-item.glass-input {
  --inner-padding-top: 8px;
  --inner-padding-bottom: 8px;
}
ion-item.ion-invalid {
  --highlight-color-focused: var(--ion-color-danger);
  --background: rgba(var(--ion-color-danger-rgb), 0.1);
}
.password-item ion-icon.toggle-eye {
  cursor: pointer;
  font-size: 1.2rem;
}
.custom-input-value {
  width: 100%;
  text-align: start;
  font-size: inherit;
  color: var(--ion-text-color);
  padding: 8px 0;
  min-height: calc(1em + 16px);
}
</style>
