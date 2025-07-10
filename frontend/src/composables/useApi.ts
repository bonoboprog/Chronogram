// Importazione delle dipendenze necessarie
import axios from 'axios'; // Libreria per effettuare chiamate HTTP
import { useAuthStore } from '@/store/auth'; // Store Pinia per la gestione dell'autenticazione
import { useRouter } from 'vue-router'; // Router di Vue per la navigazione
import { toastController } from '@ionic/vue'; // Controller per mostrare notifiche toast

/**
 * Creazione di un'istanza axios preconfigurata per l'API
 *
 * L'istanza viene configurata con:
 * - baseURL: preso dalle variabili d'ambiente (VITE_API_BASE_URL)
 * - headers: impostati per accettare/restituire JSON
 *
 * Questa istanza sarà utilizzata in tutta l'applicazione per le chiamate API
 */
export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // URL base dell'API da variabile d'ambiente
    headers: {
        'Content-Type': 'application/json', // Specifica che le richieste sono in JSON
    },
});

/**
 * Funzione per inizializzare gli intercettori di risposta axios
 *
 * Gli intercettori permettono di:
 * 1. Intercettare tutte le risposte dall'API
 * 2. Gestire gli errori in modo centralizzato
 * 3. Eseguire azioni specifiche per determinati codici di stato (es. 401)
 *
 * Nota: Deve essere chiamata durante l'inizializzazione dell'app (tipicamente in main.js/ts)
 */
export function initApiInterceptors() {


    api.interceptors.request.use(
        config => {
            const authStore = useAuthStore();
            const token = authStore.token || localStorage.getItem('jwtToken'); // Verifica entrambi
            console.log('JWT Token:', token); // Aggiungi un log per verificare il token

            // Rotte pubbliche da escludere dall'aggiunta del token
            const publicPaths = [
                '/api/auth/login',
                '/api/auth/register',
                '/api/auth/request-reset',
                '/api/auth/reset-password',
                '/api/llm/prompt'
            ];

            // Verifica se l'URL corrente è una di quelle pubbliche
            const isPublic = publicPaths.some(publicPath =>
                config.url?.includes(publicPath)
            );

            // Aggiungi il token solo se la rotta non è pubblica e se il token è presente
            if (!isPublic && token) {
                config.headers.Authorization = `Bearer ${token}`;
            }

            return config;
        },
        error => Promise.reject(error)
    );


    /**
     * Intercettore per le risposte
     *
     * La struttura è response.use(successHandler, errorHandler)
     * In questo caso gestiamo solo gli errori (secondo parametro)
     */
    api.interceptors.response.use(
        // Handler per risposte con successo - semplicemente le passa attraverso
        response => response,

        // Handler per errori - contiene la logica principale
        async error => {
            // Otteniamo lo store di autenticazione e il router
            // Nota: Questi devono essere chiamati dentro la funzione perché usano la Composition API
            const authStore = useAuthStore();
            const router = useRouter();

            // Log dell'errore completo in console per debug
            console.error('[Axios Error]', error);

            /**
             * Gestione specifica per errori 401 Unauthorized
             *
             * Quando riceviamo un 401:
             * 1. Disconnetto l'utente (logout)
             * 2. Mostro un toast di notifica
             * 3. Reindirizzo alla pagina di login (se non ci siamo già)
             */
            if (error.response && error.response.status === 401) {
                console.warn('Authentication error: 401 Unauthorized. Logging out user.');

                // Effettua il logout pulendo lo stato di autenticazione
                await authStore.logout();

                // Mostra una notifica all'utente
                const toast = await toastController.create({
                    message: 'Sessione scaduta o non autorizzata. Effettua nuovamente il login.',
                    duration: 3000, // Durata di 3 secondi
                    color: 'warning' // Stile di avvertimento
                });
                toast.present();

                // Reindirizza alla login solo se non siamo già sulla pagina di login
                if (router.currentRoute.value.name !== 'login') {
                    router.push({ name: 'login' });
                }
            }

            // Propaga l'errore per permettere una gestione specifica nei componenti
            return Promise.reject(error);
        }
    );
}