import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { Preferences } from '@capacitor/preferences';
import { SecureStoragePlugin } from 'capacitor-secure-storage-plugin';
import { api } from '@/composables/useApi';
import { useRouter } from 'vue-router';
import { User, LoginResponse } from '@/types/auth';

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null);
    const token = ref<string | null>(null);
    const router = useRouter();

    // --- GETTERS ---
    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const email = computed(() => user.value?.email);
    const userId = computed(() => user.value?.id);
    const userName = computed(() => user.value?.name);

    // --- ACTIONS ---

    async function login(credentials: { email: string; password: string }) {
        try {
            console.log('[AuthStore] Attempting login with email:', credentials.email);
            const response = await api.post<LoginResponse>('/api/auth/login', credentials);
            const data = response.data;

            console.log('[AuthStore] Login successful. Token received:', data.token.substring(0, 10) + '...');
            token.value = data.token;
            user.value = data.user;

            await SecureStoragePlugin.set({ key: 'authToken', value: data.token });
            await Preferences.set({ key: 'userData', value: JSON.stringify(data.user) });
            console.log('[AuthStore] Token and user data stored');

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
            await SecureStoragePlugin.remove({ key: 'authToken' });
            await Preferences.remove({ key: 'userData' });
            console.log('[AuthStore] Token and user data removed from storage');
        } catch (storageError) {
            console.error('[AuthStore] Error removing auth data:', storageError);
        }

        //delete api.defaults.headers.common['Authorization'];
        router.push('/login');
        console.log('[AuthStore] User redirected to login');
    }

    async function checkAuthStatus() {
        console.log('[AuthStore] Checking authentication status');
        try {
            const { value } = await SecureStoragePlugin.get({ key: 'authToken' });
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
            console.error('[AuthStore] SecureStoragePlugin error:', error);
        }
    }

    return {
        user,
        token,
        isAuthenticated,
        email,
        userId,
        userName,
        login,
        logout,
        checkAuthStatus
    };
});