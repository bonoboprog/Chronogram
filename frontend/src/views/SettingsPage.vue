<template>
  <ion-page>
    <ion-header translucent>
      <ion-toolbar color="dark">
        <ion-title class="ion-text-center gradient-text">Settings</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding">
      <!-- Profile Header -->
      <div class="profile-header glass-card">
        <ion-icon :icon="personOutline" class="avatar-icon" />
        <div>
          <p class="hello">Hello</p>
          <h2 class="name">User Name</h2>
        </div>
        <ion-icon :icon="createOutline" class="edit-icon" />
      </div>

      <!-- Quick Actions -->
      <ion-grid class="quick-actions">
        <ion-row>
          <ion-col size="6" class="centered-col">
            <ion-button class="gradient-outline pill-button" shape="round" size="small" @click="confirmLogout">
              <ion-icon :icon="logOutOutline" slot="icon-only" />
            </ion-button>
            <p>Left Home</p>
          </ion-col>
          <ion-col size="6" class="centered-col">
            <ion-button class="peach-gradient pill-button" shape="round" size="small" @click="goToHome">
              <ion-icon :icon="homeOutline" slot="icon-only" />
            </ion-button>
            <p>Came Home</p>
          </ion-col>
        </ion-row>
      </ion-grid>

      <!-- Settings Options -->
      <ion-list lines="full" class="glass-card">
        <ion-item button>
          <ion-label>Edit Profile</ion-label>
          <ion-icon :icon="personOutline" slot="end" />
        </ion-item>

        <ion-item button>
          <ion-label>Change Password</ion-label>
          <ion-icon :icon="lockClosedOutline" slot="end" />
        </ion-item>

        <ion-item button>
          <ion-label>Notifications</ion-label>
          <ion-icon :icon="notificationsOutline" slot="end" />
        </ion-item>

        <ion-item button>
          <ion-label>Support</ion-label>
          <ion-icon :icon="helpCircleOutline" slot="end" />
        </ion-item>

        <ion-item button @click="goToLogin">
          <ion-label class="danger-text">Sign Out</ion-label>
          <ion-icon :icon="exitOutline" slot="end" class="danger-text" />
        </ion-item>

        <!-- ✅ New Row: Delete Account -->
        <ion-item button>
          <ion-label class="danger-text">Delete Account</ion-label>
          <ion-icon :icon="trashOutline" slot="end" class="danger-text" />
        </ion-item>
      </ion-list>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonIcon, IonButton,
  IonGrid, IonRow, IonCol, IonItem, IonLabel, IonList, alertController
} from '@ionic/vue';
import {
  personOutline, lockClosedOutline, notificationsOutline, helpCircleOutline, exitOutline,
  homeOutline, logOutOutline, createOutline, trashOutline // 🔧 Importado nuevo icono
} from 'ionicons/icons';

const router = useRouter();

const goToLogin = () => {
  router.push({ name: 'Login' });
};

const goToHome = () => {
  router.push({ name: 'Home' });
};

const confirmLogout = async () => {
  const alert = await alertController.create({
    header: 'Are you sure?',
    message: 'Do you want to log out and leave the app?',
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel'
      },
      {
        text: 'Yes, log out',
        handler: () => {
          router.push({ name: 'Login' });
        }
      }
    ]
  });

  await alert.present();
};

// ✅ Applica il tema mocha
onMounted(() => {
  document.documentElement.setAttribute('data-theme', 'mocha');
});
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 16px;
  background-color: var(--surface1);
  color: var(--text);
}

.avatar-icon {
  font-size: 60px;
  color: var(--text);
  background-color: var(--ion-color-medium);
  border-radius: 50%;
  padding: 0.5rem;
}

.hello {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.7;
}

.name {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
}

.edit-icon {
  margin-left: auto;
  font-size: 1.2rem;
  opacity: 0.8;
  color: var(--text);
}

.quick-actions {
  margin: 1.5rem 0;
}

.centered-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  color: var(--text);
}

.danger-text {
  color: var(--ion-color-danger);
}

.glass-card {
  background-color: var(--surface0);
  border-radius: 16px;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-top: 1rem;
}
</style>
