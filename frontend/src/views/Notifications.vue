<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Notifications</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-item>
        <ion-label>Email Notifications</ion-label>
        <ion-toggle v-model="emailNotifs"></ion-toggle>
      </ion-item>

      <ion-item>
        <ion-label>Push Notifications</ion-label>
        <ion-toggle v-model="pushNotifs"></ion-toggle>
      </ion-item>

      <ion-item>
        <ion-label>SMS Notifications</ion-label>
        <ion-toggle v-model="smsNotifs"></ion-toggle>
      </ion-item>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref } from 'vue';
const emailNotifs = ref(true);
const pushNotifs = ref(false);
const smsNotifs = ref(false);
</script>

