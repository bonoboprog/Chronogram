<template>
  <ion-page>
    <ion-header translucent>
      <ion-toolbar color="dark">
        <ion-title class="ion-text-center title-peach">Settings</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding content-safe-area">
      <div class="settings-container">
        <!-- Profile Header -->
        <div class="profile-header glass-input">
          <ion-icon :icon="personOutline" class="avatar-icon input-icon" aria-hidden="true" />
          <div class="name-container">
            <p class="hello">Hello</p>
            <h2 class="name">User Name</h2>
          </div>
          <ion-button fill="clear" class="edit-button" @click="editProfile" aria-label="Edit profile">
            <ion-icon :icon="createOutline" class="input-icon" />
          </ion-button>
        </div>

        <!-- Settings Options -->
        <ion-list lines="none" class="form-wrapper">
          <ion-item button @click="goToEditProfile">
            <ion-icon slot="start" :icon="personOutline" class="input-icon" />
            <ion-label>Edit Profile</ion-label>
          </ion-item>

          <ion-item button @click="goToChangePassword">
            <ion-icon slot="start" :icon="lockClosedOutline" class="input-icon" />
            <ion-label>Change Password</ion-label>
          </ion-item>

          <ion-item button @click="goToNotifications">
            <ion-icon slot="start" :icon="notificationsOutline" class="input-icon" />
            <ion-label>Notifications</ion-label>
          </ion-item>

          <ion-item button @click="goToSupport">
            <ion-icon slot="start" :icon="helpCircleOutline" class="input-icon" />
            <ion-label>Support</ion-label>
          </ion-item>

          <ion-item button @click="goToDeleteAccount">
            <ion-icon slot="start" :icon="trashOutline" class="input-icon danger-text" />
            <ion-label class="danger-text">Delete Account</ion-label>
          </ion-item>
        </ion-list>
      </div>
    </ion-content>
    <!-- Bottom Navigation -->
    <!-- Bottom Navigation - Updated to match style -->
    <div class="bottom-icons-container">
      <!-- Home icon (clickable) -->
      <div class="bottom-icon home-icon" @click="goToHome" aria-label="Home">
        <ion-icon :icon="homeOutline" />
      </div>

      <!-- Logout button (center) -->
      <div class="bottom-icon center">
        <div class="logout-btn" @click="confirmLogout" aria-label="Sign Out">
          <ion-icon :icon="exitOutline" color="danger" /> <!-- Added color="danger" here -->
        </div>
      </div>

      <!-- Settings icon (highlighted) -->
      <div class="bottom-icon settings-icon">
        <ion-icon :icon="settingsOutline" />
      </div>
    </div>
  </ion-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonIcon,
  IonList, IonItem, IonLabel, IonButton, alertController
} from '@ionic/vue';

import {
  personOutline, lockClosedOutline, notificationsOutline, helpCircleOutline, exitOutline,
  homeOutline, createOutline, trashOutline, settingsOutline
} from 'ionicons/icons';

const router = useRouter();

const goToHome = () => {
  router.push({ name: 'Home' });
};

const goToEditProfile = () => {
  router.push({ name: 'EditProfile' });
};

const goToChangePassword = () => {
  router.push({ name: 'ChangePassword' });
};

const goToNotifications = () => {
  router.push({ name: 'Notifications' });
};

const goToSupport = () => {
  router.push({ name: 'Support' });
};

const goToDeleteAccount = () => {
  router.push({ name: 'DeleteAccount' });
};


const confirmLogout = async () => {
  const alert = await alertController.create({
    header: 'Are you sure?',
    message: 'Do you want to log out and leave the app?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      { text: 'Yes, log out', handler: () => router.push({ name: 'Login' }) }
    ]
  });
  await alert.present();
};

const editProfile = () => {
  console.log('Edit profile clicked');
};

onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
});
</script>

<style scoped>
/* Safe padding for FAB and bottom nav */
.content-safe-area {
  --padding-bottom: 100px;
}

/* Centered container for content */
.settings-container {
  max-width: 450px;
  width: 100%;
  margin: 1.5rem auto 0;
  padding: 0 1rem;
  box-sizing: border-box;
}

/* Title color */
.title-peach {
  color: var(--peach, #fca17d);
}

/* Profile Header */
.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 16px;
  color: var(--text, #fff);
  margin-bottom: 1rem;
  width: 100%;
  box-sizing: border-box;
}

.name-container {
  flex: 1;
  min-width: 0;
}

.hello {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.7;
}

.name {
  margin: 0;
  font-size: clamp(1.1rem, 2.5vw, 1.4rem);
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.avatar-icon {
  font-size: 48px;
  background-color: var(--surface0, rgba(255, 255, 255, 0.08));
  border-radius: 50%;
  padding: 8px;
}

.edit-button {
  margin-left: auto;
  padding: 0;
  min-width: unset;
  height: auto;
}

/* Glass effect reused */
.glass-input {
  background-color: var(--surface0, rgba(255, 255, 255, 0.06));
  border-radius: 16px;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.input-icon {
  color: var(--peach, #fca17d);
  font-size: 1.2rem;
}

.danger-text {
  color: var(--red, #f45c5c) !important;
}

.form-wrapper {
  width: 100%;
}




/* Bottom Navigation */
.bottom-icons-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px 25px;
  z-index: 100;
}

.bottom-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.bottom-icon ion-icon {
  font-size: 28px;
  color: var(--overlay1); /* Default color for inactive icons */
}

/* Home icon */
.home-icon {
  pointer-events: auto;
}

/* Settings icon - highlighted */
.settings-icon ion-icon {
  color: var(--peach); /* Peach color for active tab */
}

/* Center logout button */
.logout-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--surface0, rgba(255, 255, 255, 0.08));
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--red, #f45c5c);
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.12);
  transform: scale(1.05);
}

.logout-btn ion-icon {
  font-size: 28px;
}

/* Optional hover effect for items */
ion-item.button::part(native):hover {
  background-color: rgba(255, 255, 255, 0.05);
}

ion-item {
  --inner-padding-top: 12px;
  --inner-padding-bottom: 12px;
  --min-height: 56px;
}
</style>
