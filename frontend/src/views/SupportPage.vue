<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Support</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-item>
        <ion-label position="stacked">Subject</ion-label>
        <ion-input v-model="subject" placeholder="Enter subject"></ion-input>
      </ion-item>

      <ion-item>
        <ion-label position="stacked">Message</ion-label>
        <ion-textarea v-model="message" placeholder="Describe your issue" auto-grow></ion-textarea>
      </ion-item>

      <ion-button expand="block" class="ion-margin-top">Send Message</ion-button>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref } from 'vue';
const subject = ref('');
const message = ref('');
</script>
