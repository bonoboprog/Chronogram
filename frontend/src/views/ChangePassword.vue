<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Change Password</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-item>
        <ion-label position="stacked">Current Password</ion-label>
        <ion-input type="password" v-model="currentPassword"></ion-input>
      </ion-item>

      <ion-item>
        <ion-label position="stacked">New Password</ion-label>
        <ion-input type="password" v-model="newPassword"></ion-input>
      </ion-item>

      <ion-item>
        <ion-label position="stacked">Confirm New Password</ion-label>
        <ion-input type="password" v-model="confirmPassword"></ion-input>
      </ion-item>

      <ion-button expand="block" class="ion-margin-top">Update Password</ion-button>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref } from 'vue';
const currentPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
</script>