<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-button @click="goBack">
            <ion-icon slot="icon-only" :icon="arrowBackOutline" />
          </ion-button>
        </ion-buttons>
        <ion-title>Edit Profile</ion-title>
        <ion-buttons slot="end">
          <ion-icon :icon="personCircleOutline" class="profile-icon" />
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-list lines="none">
        <ion-item class="glass-input">
          <ion-input label="Name" label-placement="floating" autocomplete="given-name" type="text" v-model="name" />
        </ion-item>
        <ion-item class="glass-input">
          <ion-input label="Surname" label-placement="floating" autocomplete="family-name" type="text" v-model="surname" />
        </ion-item>
        <ion-item class="glass-input">
          <ion-input label="Phone" label-placement="floating" autocomplete="tel" type="tel" v-model="phone" />
        </ion-item>
        <ion-item class="glass-input">
          <ion-input label="Email" label-placement="floating" autocomplete="email" type="email" v-model="email" />
        </ion-item>
        <ion-item class="glass-input">
          <ion-input label="Password" label-placement="floating" type="password" v-model="password" />
        </ion-item>
        <ion-item class="glass-input" :class="{ 'item-has-value': !!selectedBirthday }" @click="openBirthdayModal" :detail="false" button>
          <ion-label position="floating">Birthday</ion-label>
          <div class="custom-input-value">{{ formattedBirthday }}</div>
        </ion-item>
        <ion-item class="glass-input">
          <ion-select
              label="Gender"
              label-placement="floating"
              interface="popover"
              v-model="gender"
              :interface-options="{ cssClass: 'ion-dark catppuccin-select-overlay' }"
          >
            <ion-select-option value="male">Male</ion-select-option>
            <ion-select-option value="female">Female</ion-select-option>
            <ion-select-option value="other">Other</ion-select-option>
            <ion-select-option value="not_specified">Prefer not to say</ion-select-option>
          </ion-select>
        </ion-item>
      </ion-list>

      <ion-button expand="block" class="ion-margin-top">Save Changes</ion-button>

      <ion-modal ref="birthdayModal" :keep-contents-mounted="true">
        <ion-datetime
            id="birthday-datetime"
            presentation="date"
            v-model="selectedBirthday"
            @ionChange="birthdayModal?.dismiss()"
            class="ion-dark"
            :interface-options="{ cssClass: 'catppuccin-datetime-overlay' }"
        />
      </ion-modal>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonButton, IonIcon,
  IonItem, IonLabel, IonInput, IonSelect, IonSelectOption, IonDatetime, IonModal
} from '@ionic/vue';
import { useRouter } from 'vue-router';
import { personCircleOutline, arrowBackOutline } from 'ionicons/icons';

const router = useRouter();

const name = ref('');
const surname = ref('');
const phone = ref('');
const email = ref('');
const password = ref('');
const gender = ref('');
const birthdayModal = ref<InstanceType<typeof IonModal>>();
const selectedBirthday = ref<string>();

const openBirthdayModal = () => {
  birthdayModal.value?.$el.present();
};

const formattedBirthday = computed(() => {
  if (!selectedBirthday.value) return '';
  return new Date(selectedBirthday.value).toLocaleDateString('en-GB', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
});

const goBack = () => {
  router.push({ name: 'Settings' });
};
</script>

<style scoped>
.profile-icon {
  font-size: 1.8rem;
  color: var(--mauve);
}
ion-item.glass-input {
  --inner-padding-top: 8px;
  --inner-padding-bottom: 8px;
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
