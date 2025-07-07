import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import { IonicVue } from '@ionic/vue';
import { createPinia } from 'pinia';
import { initApiInterceptors } from '@/composables/useApi';

/* Core CSS required for Ionic components to work properly */
import '@ionic/vue/css/core.css';
/* Basic CSS for apps built with Ionic */
import '@ionic/vue/css/normalize.css';
import '@ionic/vue/css/structure.css';
import '@ionic/vue/css/typography.css';
/* Optional CSS utils that can be commented out */
import '@ionic/vue/css/padding.css';
import '@ionic/vue/css/float-elements.css';
import '@ionic/vue/css/text-alignment.css';
import '@ionic/vue/css/text-transformation.css';
import '@ionic/vue/css/flex-utils.css';
import '@ionic/vue/css/display.css';

/**
 * Ionic Dark Mode
 * -----------------------------------------------------
 * For more info, please see:
 * https://ionicframework.com/docs/theming/dark-mode
 */

/* @import '@ionic/vue/css/palettes/dark.always.css'; */
/* @import '@ionic/vue/css/palettes/dark.class.css'; */
/* import '@ionic/vue/css/palettes/dark.system.css'; */

/* Theme variables */
import './theme/variables.css';
/* Catppuccin theme */
import '@/theme/catppuccin.scss';


if (import.meta.env.DEV) {
  import('@vue/devtools').then((devtools) => {
    if (typeof devtools.connect === 'function') {
      devtools.connect('http://192.168.59.1:8098', 8098);
    } else {
      console.warn('connect() not available from @vue/devtools');
    }
  }).catch((err) => {
    console.error('Failed to load Vue Devtools:', err);
  });
}


const app = createApp(App)
    .use(IonicVue)
    .use(router)
    .use(createPinia());

/* Set the active theme on root element  */
document.documentElement.setAttribute('data-theme', 'mocha');

router.isReady().then(() => {
  app.mount('#app');
  // Initialize API interceptors after app is mounted and Pinia/Router are ready
  initApiInterceptors();
});