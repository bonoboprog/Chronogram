import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { Preferences } from '@capacitor/preferences'; // Per lo storage sicuro
import { api } from '@/composables/useApi';
import { useRouter } from 'vue-router';
// Definiamo il tipo per i dati dell'utente che vogliamo salvare
interface User {
    username: string;
}

export const useAuthStore = defineStore('auth', () => {
    // --- STATE ---
    const user = ref<User | null>(null);
    const token = ref<string | null>(null);
    const router = useRouter();

    // --- GETTERS ---
    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const username = computed(() => user.value?.username);

    // --- ACTIONS ---

    /**
     * Esegue il login, salva il token e lo stato dell'utente.
     */
    async function login(credentials: { email: string, password: string }) {
        const { data } = await api.post('/api/auth/login', credentials);

        if (!data.success || !data.token) {
            throw new Error(data.message || 'Login failed');
        }

        // Aggiorna lo stato dello store
        token.value = data.token;
        user.value = { username: data.username };

        // Salva il token in modo persistente e sicuro sul dispositivo
        await Preferences.set({ key: 'authToken', value: data.token });
        await Preferences.set({ key: 'userData', value: JSON.stringify(user.value) });

        // Configura l'header di default per tutte le future chiamate API
        api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    }

    /**
     * Esegue il logout, pulisce lo stato e lo storage.
     */
    async function logout() {
        token.value = null;
        user.value = null;

        // Rimuovi i dati persistenti
        await Preferences.remove({ key: 'authToken' });
        await Preferences.remove({ key: 'userData' });

        // Rimuovi l'header di default
        delete api.defaults.headers.common['Authorization'];

        // Reindirizza alla pagina di login
        router.push('/login');
    }

    /**
     * Controlla se esiste un token salvato all'avvio dell'app per ripristinare la sessione.
     */
    async function checkAuthStatus() {
        const storedToken = await Preferences.get({ key: 'authToken' });
        const storedUser = await Preferences.get({ key: 'userData' });

        if (storedToken.value && storedUser.value) {
            // Ri-popola lo stato dello store
            token.value = storedToken.value;
            user.value = JSON.parse(storedUser.value);

            // Ri-configura l'header di default per le chiamate API
            api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`;
        }
    }

    return {
        user,
        token,
        isAuthenticated,
        username,
        login,
        logout,
        checkAuthStatus
    };
});