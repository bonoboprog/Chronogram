import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { Preferences } from '@capacitor/preferences';
import { SecureStorage } from '@capacitor/secure-storage';
import { api } from '@/composables/useApi';
import { useRouter } from 'vue-router';

// Definiamo il tipo per i dati dell'utente che vogliamo salvare

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null);
    const token = ref<string | null>(null);
    const router = useRouter();

    // --- GETTERS ---
    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const username = computed(() => user.value?.username);

    // --- ACTIONS ---

    async function login(credentials: { email: string; password: string }) {
        try {
            console.log('[AuthStore] Attempting login with email:', credentials.email);
            const response = await api.post<LoginResponse>('/api/auth/login', credentials);
            const data = response.data;

            console.log('[AuthStore] Login successful. Token received:', data.token.substring(0, 10) + '...');
            token.value = data.token;
            user.value = { username: data.username };

            await SecureStorage.set({ key: 'authToken', value: data.token });
            await Preferences.set({ key: 'userData', value: JSON.stringify(user.value) });
            console.log('[AuthStore] Token securely stored');

            //api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
            return data;
        } catch (error) {
            console.error('[AuthStore] Login failed:', error);
            throw error;
        }
    }

    async function logout() {
        console.log('[AuthStore] Initiating logout');
        token.value = null;
        user.value = null;

        try {
            await SecureStorage.remove({ key: 'authToken' });
            await Preferences.remove({ key: 'userData' });
            console.log('[AuthStore] Token removed from storage');
        } catch (storageError) {
            console.error('[AuthStore] Error removing token:', storageError);
        }

        //delete api.defaults.headers.common['Authorization'];
        router.push('/login');
        console.log('[AuthStore] User redirected to login');
    }

    async function checkAuthStatus() {
        console.log('[AuthStore] Checking authentication status');
        try {
            const { value } = await SecureStorage.get({ key: 'authToken' });
            const storedUser = await Preferences.get({ key: 'userData' });

            if (value && storedUser.value) {
                console.log('[AuthStore] Found stored token. Length:', value.length);
                token.value = value;
                user.value = JSON.parse(storedUser.value);
                //api.defaults.headers.common['Authorization'] = `Bearer ${value}`;
                console.log('[AuthStore] User authenticated from storage');
            } else {
                console.log('[AuthStore] No stored token found');
            }
        } catch (error) {
            console.error('[AuthStore] SecureStorage error:', error);
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