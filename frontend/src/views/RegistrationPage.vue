<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <div class="registration-container">
        <div class="registration-header">
          <ion-icon :icon="personAddOutline" class="header-icon" />
          <h1 class="title-peach">Registration</h1>
        </div>

        <div class="form-wrapper">
          <ion-list lines="none">
            <ion-item :class="errorClass('name')" class="glass-input">
              <ion-icon slot="start" :icon="personOutline" class="input-icon" />
              <ion-input
                  v-model="form.name"
                  label="Name"
                  label-placement="floating"
                  type="text"
                  :aria-label="'Name'"
                  autocomplete="given-name"
              />
            </ion-item>

            <ion-item :class="errorClass('surname')" class="glass-input">
              <ion-icon slot="start" :icon="personOutline" class="input-icon" />
              <ion-input
                  v-model="form.surname"
                  label="Surname"
                  label-placement="floating"
                  type="text"
                  :aria-label="'Surname'"
                  autocomplete="family-name"
              />
            </ion-item>

            <ion-item :class="errorClass('username')" class="glass-input">
              <ion-icon slot="start" :icon="atOutline" class="input-icon" />
              <ion-input
                  v-model="form.username"
                  label="Username"
                  label-placement="floating"
                  type="text"
                  :aria-label="'Username'"
                  autocomplete="username"
              />
            </ion-item>

            <ion-item :class="errorClass('address')" class="glass-input">
              <ion-icon slot="start" :icon="locationOutline" class="input-icon" />
              <ion-input
                  v-model="form.address"
                  label="Address"
                  label-placement="floating"
                  type="text"
                  :aria-label="'Address'"
                  autocomplete="street-address"
              />
            </ion-item>

            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="callOutline" class="input-icon" />
              <ion-input
                  v-model="form.phone"
                  label="Phone"
                  label-placement="floating"
                  type="tel"
                  :aria-label="'Phone'"
                  autocomplete="tel"
              />
            </ion-item>

            <ion-item :class="errorClass('email')" class="glass-input">
              <ion-icon slot="start" :icon="mailOutline" class="input-icon" />
              <ion-input
                  v-model="form.email"
                  label="Email"
                  label-placement="floating"
                  type="email"
                  :aria-label="'Email'"
                  autocomplete="email"
              />
            </ion-item>

            <ion-item :class="errorClass('password')" class="glass-input password-item">
              <ion-icon slot="start" :icon="keyOutline" class="input-icon" />
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

            <ion-item
                class="glass-input"
                :class="{ 'item-has-value': !!form.birthday }"
                @click="openBirthdayModal"
                :detail="false"
                button
            >
              <ion-icon slot="start" :icon="calendarOutline" class="input-icon" />
              <ion-label position="floating">Birthday</ion-label>
              <div class="custom-input-value">{{ formattedBirthday }}</div>
            </ion-item>

            <ion-item class="glass-input">
              <ion-icon slot="start" :icon="transgenderOutline" class="input-icon" />
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

      <ion-modal ref="birthdayModal" :keep-contents-mounted="true">
        <ion-datetime
            presentation="date"
            v-model="dateIso"
            @ionChange="onBirthdaySelected"
            class="ion-dark"
            :interface-options="{ cssClass: 'catppuccin-datetime-overlay' }"
        />
      </ion-modal>
      <ion-loading :is-open="isLoading" message="Registering..." />
      <ion-toast :is-open="toast.open" :message="toast.message" :color="toast.color" :duration="2500" @didDismiss="toast.open=false" />
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol, IonSelect, IonSelectOption,
  IonLabel, IonDatetime, IonModal, IonLoading, IonToast
} from '@ionic/vue';
import { useRouter } from 'vue-router';
import {
  personAddOutline, eyeOutline, eyeOffOutline,
  callOutline, mailOutline, personOutline,
  keyOutline, calendarOutline, transgenderOutline,
  locationOutline, atOutline // <-- IMPORTED ICON
} from 'ionicons/icons';
import dayjs from 'dayjs';
import { api } from '@/composables/useApi';

/* ---------- state ---------- */
const router        = useRouter();
const birthdayModal = ref<InstanceType<typeof IonModal>>();
const isLoading     = ref(false);
const showPassword  = ref(false);
const dateIso       = ref<string>();

const form = reactive({
  name: '', surname: '', username: '', address: '', phone: '', // <-- ADDED USERNAME
  email: '', password: '', birthday: '', gender: ''
});

const toast = reactive({ open: false, message: '', color: 'danger' as const });

/* ---------- computed ---------- */
const formattedBirthday = computed(() => form.birthday);
const hasErrors = computed(() =>
    !form.name.trim()       ||
    !form.surname.trim()    ||
    !form.username.trim()   || // <-- ADDED CHECK
    !form.address.trim()    ||
    !isValidEmail(form.email) ||
    !isStrongPassword(form.password)
);

/* ---------- helpers ---------- */
const isValidEmail = (e: string) =>
    /^[\w-.]+@([\w-]+\.)+[\w-]{2,}$/.test(e);

const isStrongPassword = (p: string) =>
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/.test(p);

const errorClass = (f: keyof typeof form) => ({
  'ion-invalid':
      (f === 'email'    && form.email    && !isValidEmail(form.email)) ||
      (f === 'password' && form.password && !isStrongPassword(form.password)) ||
      (!(form[f] as string).trim() && ['name', 'surname', 'address', 'username'].includes(f)) // <-- ADDED USERNAME
});

const openBirthdayModal = () => birthdayModal.value?.$el.present();

const onBirthdaySelected = () => {
  if (dateIso.value) {
    form.birthday = dayjs(dateIso.value).format('DD-MM-YYYY');
  }
  birthdayModal.value?.$el.dismiss();
};

const showToast = (msg: string, col: 'success' | 'danger') => {
  toast.message = msg;
  toast.color = col;
  toast.open = true;
};

/* ---------- registration ---------- */
async function handleRegister() {
  if (hasErrors.value) {
    showToast('Please correct the highlighted fields.', 'danger');
    return;
  }

  isLoading.value = true;
  try {
    const { data } = await api.post('/api/auth/register', { ...form });
    console.log('Register API response data:', data);  // <-- ADD THIS
    if (!data?.success) throw new Error(data?.message ?? 'Unknown error');

    showToast(data.message || 'Registered successfully!', 'success');
    await router.push({ name: 'Login' });
  } catch (err: any) {
    console.error('Registration error:', err);
    const message = err.response?.data?.message || err.message || 'Unexpected error';
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
ion-item.glass-input {
  --inner-padding-top: 4px;
  --inner-padding-bottom: 4px;
  --min-height: 48px;
  font-size: 0.95rem;
}
ion-input,
ion-select {
  font-size: 0.95rem;
  --padding-start: 0;
  --padding-end: 0;
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
ion-select::part(icon) {
  color: var(--peach);
}
</style>