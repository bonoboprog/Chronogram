import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { api } from '@/composables/useApi';
import { useRouter } from 'vue-router';

interface User {
    username: string;
}

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null);
    const token = ref<string | null>(null);
    const router = useRouter();

    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const username = computed(() => user.value?.username);

    async function login(credentials: { email: string; password: string }): Promise<LoginResponse> {
        const response = await api.post<LoginResponse>('/api/auth/login', credentials);
        const data = response.data;

        console.log('[login] Raw response:', response);
        console.log('[login] Parsed data:', data);

        if (!data.success || !data.token) {
            throw new Error(data.message || 'Login failed');
        }

        token.value = data.token;
        user.value = { username: data.username };

        // ✅ Salva solo in localStorage
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userData', JSON.stringify(user.value));

        // ✅ Imposta header di default per le chiamate future
        api.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;

        return data;
    }

    async function logout() {
        token.value = null;
        user.value = null;

        localStorage.removeItem('authToken');
        localStorage.removeItem('userData');

        delete api.defaults.headers.common['Authorization'];
        router.push('/login');
    }

    async function checkAuthStatus() {
        const storedToken = localStorage.getItem('authToken');
        const storedUser = localStorage.getItem('userData');

        if (storedToken && storedUser) {
            token.value = storedToken;
            user.value = JSON.parse(storedUser);

            api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`;
            console.log('[auth] Sessione ripristinata da localStorage');
        } else {
            console.log('[auth] Nessun token trovato in localStorage');
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
