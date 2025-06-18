<template>
  <ion-page>
    <ion-content :fullscreen="true" class="ion-padding">
      <div class="registration-container">
        <div class="registration-header">
          <ion-icon :icon="personAddOutline" class="header-icon"></ion-icon>
          <h1>Registration</h1>
        </div>

        <div class="form-wrapper">
          <ion-list lines="none">

            <ion-item class="glass-input">
              <ion-input label="Name" label-placement="floating" autocomplete="given-name" type="text" v-model="name"></ion-input>
            </ion-item>
            <ion-item class="glass-input">
              <ion-input label="Surname" label-placement="floating" autocomplete="family-name" type="text" v-model="surname"></ion-input>
            </ion-item>
            <ion-item class="glass-input">
              <ion-input label="Phone" label-placement="floating" autocomplete="tel" type="tel" v-model="phone"></ion-input>
            </ion-item>
            <ion-item class="glass-input">
              <ion-input label="Email" label-placement="floating" autocomplete="email" type="email" v-model="email"></ion-input>
            </ion-item>
            <ion-item class="glass-input">
              <ion-input label="Password" label-placement="floating" type="password" v-model="password"></ion-input>
            </ion-item>
            <ion-item
                class="glass-input"
                :class="{ 'item-has-value':!!selectedBirthday }"
                @click="openBirthdayModal"
                :detail="false"
                button
            >
              <ion-label position="floating">Birthday</ion-label>
              <div class="custom-input-value">{{ formattedBirthday }}</div>
            </ion-item>

            <ion-item class="glass-input">
              <ion-select
                  label="Gender"
                  label-placement="floating"
                  interface="popover"
                  :interface-options="{ cssClass: 'ion-dark catppuccin-select-overlay' }"
                  v-model="gender"
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
                <ion-button @click="goBackToLogin" expand="block" class="pill-button gradient-outline">Cancel</ion-button>
              </ion-col>
              <ion-col size="5">
                <ion-button @click="handleRegister" expand="block" class="pill-button gradient-outline">Register</ion-button>
              </ion-col>
            </ion-row>
          </ion-grid>
        </div>
      </div>

      <ion-modal ref="birthdayModal" :keep-contents-mounted="true">
        <ion-datetime
            id="birthday-datetime"
            presentation="date"
            v-model="selectedBirthday"
            @ionChange="birthdayModal.value?.$el.dismiss()"
            class="ion-dark"
            :interface-options="{
              cssClass: 'catppuccin-datetime-overlay'
            }"
        ></ion-datetime>
      </ion-modal>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  IonPage, IonContent, IonList, IonItem, IonInput, IonIcon,
  IonButton, IonGrid, IonRow, IonCol, IonSelect, IonSelectOption,
  IonLabel, IonDatetime, IonModal
} from '@ionic/vue';
import { useRouter } from 'vue-router';
import { personAddOutline } from 'ionicons/icons';
import axios from 'axios'; // Import axios

const router = useRouter();
const birthdayModal = ref<InstanceType<typeof IonModal>>();
const selectedBirthday = ref<string>();

// Define reactive variables for form inputs
const name = ref('');
const surname = ref('');
const phone = ref('');
const email = ref('');
const password = ref('');
const gender = ref('');

const openBirthdayModal = () => {
  birthdayModal.value?.$el.present();
};

const formattedBirthday = computed(() => {
  if (!selectedBirthday.value) return '';

  // Extract ISO date parts directly (YYYY-MM-DD format)
  const datePart = selectedBirthday.value.split('T')[0];
  const [year, month, day] = datePart.split('-');

  // Return in dd-MM-yyyy format
  return `${day}-${month}-${year}`;
});

const goBackToLogin = () => {
  router.back();
};

const handleRegister = async () => {
  console.log('Attempting registration...');






  // Construct the payload to send to the backend
  const payload = {
    name: name.value,
    surname: surname.value,
    phone: phone.value,
    email: email.value,
    password: password.value,
    birthday: formattedBirthday.value, // Send the formatted string
    gender: gender.value
  };

  console.log('Sending payload:', payload);

  try {
    const API = import.meta.env.VITE_API_BASE_URL; // Ensure this is correctly configured
    const response = await axios.post(`${API}/register`, payload, {
      headers: { 'Content-Type': 'application/json' }
    });

    console.log('Backend response:', response.data);
    if (response.data.success) {
      alert(response.data.message);
      router.push({ name: 'Login' }); // Redirect to login on success
    } else {
      alert(`Registration failed: ${response.data.message}`);
    }
  } catch (error) {
    console.error('Error during registration:', error);
    alert('An error occurred during registration. Please try again.');
  }
};
</script>

<style scoped>
/* I tuoi stili scoped rimangono invariati */
.registration-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 2rem;
  padding-bottom: 2rem;
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
h1 {
  font-weight: 600;
  font-size: 1.8rem;
}
.form-wrapper {
  max-width: 450px;
  width: 100%;
}
ion-item.glass-input {
  --inner-padding-top: 8px;
  --inner-padding-bottom: 8px;
}
ion-select::part(text) {
  color: var(--ion-text-color);
}
.custom-input-value {
  width: 100%;
  text-align: start;
  font-size: inherit;
  color: var(--ion-text-color);
  padding-top: 8px;
  padding-bottom: 8px;
  min-height: calc(1em + 16px);
}
</style>