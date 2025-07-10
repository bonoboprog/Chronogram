<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <!-- Go back to Settings -->
        <ion-buttons slot="start">
          <ion-button @click="goTo('Settings')">
            <ion-icon :icon="settingsOutline" slot="icon-only" />
          </ion-button>
        </ion-buttons>
        <ion-title>Support</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">

      <!-- Intro with support explanation -->
      <ion-card class="support-intro">
        <ion-card-header>
          <ion-card-title class="section-title">
            We're here to help you with anything and everything on Chronogram
          </ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <p>
            At Chronogram, we want your experience to get better every day. You can share your concern or check the frequently asked questions below.
          </p>
        </ion-card-content>
      </ion-card>

      <!-- Search bar (non-functional for now) -->
      <ion-item class="search-item">
        <ion-icon :icon="searchOutline" slot="start" color="medium"></ion-icon>
        <ion-input placeholder="Search help" clear-input></ion-input>
      </ion-item>

      <!-- FAQ with accordion -->
      <ion-accordion-group>
        <ion-accordion v-for="(item, index) in faq" :key="index" :value="item.q">
          <ion-item slot="header" color="dark">
            <ion-label>{{ item.q }}</ion-label>
          </ion-item>
          <div class="ion-padding" slot="content">
            {{ item.a }}
          </div>
        </ion-accordion>
      </ion-accordion-group>

      <!-- Message submission area -->
      <ion-card class="message-box">
        <ion-card-header>
          <ion-card-title class="section-title">
            Still stuck? Help is a message away
          </ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <ion-item>
            <ion-label position="stacked">Subject</ion-label>
            <ion-input v-model="subject" placeholder="Enter subject" required></ion-input>
          </ion-item>
          <ion-item>
            <ion-label position="stacked">Message</ion-label>
            <ion-textarea v-model="message" placeholder="Describe your issue" auto-grow></ion-textarea>
          </ion-item>
          <ion-button expand="block" class="send-button" @click="sendMessage">
            Send Message
          </ion-button>
        </ion-card-content>
      </ion-card>

    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import {
  IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonItem,
  IonInput, IonTextarea, IonButton, IonLabel, IonCard, IonCardContent,
  IonCardHeader, IonCardTitle, IonIcon, IonAccordionGroup, IonAccordion,
  IonButtons, toastController
} from '@ionic/vue';
import { searchOutline, settingsOutline } from 'ionicons/icons';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Redirect to Settings
function goTo(name: string) {
  router.push({ name });
}

// Inputs for user message
const subject = ref('');
const message = ref('');

// FAQ sample questions and answers
const faq = [
  {
    q: 'How do I add a new activity?',
    a: 'You can add a new activity by going to the Home page and tapping the + button.'
  },
  {
    q: 'Can I edit a past activity?',
    a: 'Yes, open the Calendar, select the date, and tap the activity to edit it.'
  },
  {
    q: 'Is there a way to customize my categories?',
    a: 'Currently categories are predefined, but customization will be available soon.'
  }
];

// Handle message submission and show toast
async function sendMessage() {
  if (!subject.value.trim() || !message.value.trim()) {
    const toast = await toastController.create({
      message: 'Please fill in both subject and message.',
      duration: 2000,
      color: 'danger'
    });
    return toast.present();
  }

  console.log('Message sent:', { subject: subject.value, message: message.value });

  subject.value = '';
  message.value = '';

  const toast = await toastController.create({
    message: 'Your message has been sent!',
    duration: 2000,
    color: 'success'
  });
  toast.present();
}
</script>

<style scoped>
/* Card base styling */
.support-intro,
.message-box {
  background-color: #2b2b3a;
  border-radius: 12px;
  color: #e0aaff;
}

/* Title text (highlighted) */
.section-title {
  color: #e0aaff;
  font-size: 1.05rem;
  font-weight: bold;
}

/* Paragraph description */
.support-intro p {
  color: #ccc;
  font-size: 0.95em;
}

/* Search bar styling */
.search-item {
  background: #2b2b3a;
  margin-bottom: 1rem;
  border-radius: 8px;
}

/* FAQ section spacing */
ion-accordion-group {
  margin-top: 1rem;
  margin-bottom: 2rem;
}

/* Button styled like Chronogram */
.send-button {
  margin-top: 1rem;
  --background: #c18aff;
  --background-hover: #d7aaff;
  --color: white;
  font-weight: bold;
  border-radius: 12px;
}
</style>
