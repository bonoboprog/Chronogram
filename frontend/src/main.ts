import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { IonicVue } from '@ionic/vue';
import { createPinia } from 'pinia';
import { initApiInterceptors } from '@/composables/useApi';
import { useAuthStore } from '@/store/auth';

/* Ionic CSS */
import '@ionic/vue/css/core.css';
import '@ionic/vue/css/normalize.css';
import '@ionic/vue/css/structure.css';
import '@ionic/vue/css/typography.css';
import '@ionic/vue/css/padding.css';
import '@ionic/vue/css/float-elements.css';
import '@ionic/vue/css/text-alignment.css';
import '@ionic/vue/css/text-transformation.css';
import '@ionic/vue/css/flex-utils.css';
import '@ionic/vue/css/display.css';

/* Custom themes */
import './theme/variables.css';
import '@/theme/catppuccin.scss';

// ⿡ Crea l'app
const app = createApp(App);

// ⿢ Crea Pinia separatamente
const pinia = createPinia();

// ⿣ Esporta Pinia per debug in dev

  console.log("💡 DEV mode: exposing pinia");

  (window as any).__PINIA__ = pinia;

  import('@vue/devtools').then((devtools) => {
    if (typeof devtools.connect === 'function') {
      devtools.connect('http://192.168.59.1:8098', 8098); // 🔧 IP VM
    } else {
      console.warn('connect() not available from @vue/devtools');
    }
  }).catch((err) => {
    console.error('Failed to load Vue Devtools:', err);
  });


// ⿤ Monta tutti i plugin
app.use(IonicVue);
app.use(router);
app.use(pinia);

// ⿥ Imposta tema
document.documentElement.setAttribute('data-theme', 'mocha');

// ⿦ Esegui il mount dopo auth + interceptor
router.isReady().then(async () => {
  const authStore = useAuthStore();
  await authStore.checkAuthStatus();

  app.mount('#app');
  initApiInterceptors();
});