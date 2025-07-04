// src/stores/activityStore.ts

import { defineStore } from 'pinia';
import { reactive, ref } from 'vue';
import { api } from '@/composables/useApi'; // Assicurati che il tuo useApi.ts abbia l'interceptor!

// Definisci i tipi per le attività e per le risposte API
interface Activity {
    id?: string; // L'ID è opzionale perché non esiste quando creiamo una nuova attività
    name: string;
    duration: number | null;
    details: string;
    enjoyment: number;
    category: string;
    type: 'instrumental' | 'final' | '';
    recurrence: 'R' | 'E' | '';
    cost: number | null;
    location: string;
}

// Tipo per i dati parsati dall'LLM, che potrebbero essere parziali
interface ParsedActivityData {
    name?: string;
    duration?: number;
    details?: string;
    enjoyment?: number;
    category?: string;
    type?: 'instrumental' | 'final';
    recurrence?: 'R' | 'E';
    cost?: number;
    location?: string;
}

export const useActivityStore = defineStore('activity', () => {
    const activities = ref<Activity[]>([]);
    const isLoading = ref(false);
    const error = ref<string | null>(null);

    // Azioni
    async function fetchActivities() {
        isLoading.value = true;
        error.value = null;
        try {
            const response = await api.get<Activity[]>('/api/activities');
            activities.value = response.data;
        } catch (e: any) {
            error.value = e.response?.data?.message || 'Failed to fetch activities';
            console.error('[ActivityStore] Fetch activities failed:', e);
            throw e; // Rilancia l'errore per gestirlo nel componente
        } finally {
            isLoading.value = false;
        }
    }

    async function fetchActivityById(id: string): Promise<Activity> {
        isLoading.value = true;
        error.value = null;
        try {
            const response = await api.get<Activity>(`/api/activities/${id}`);
            return response.data;
        } catch (e: any) {
            error.value = e.response?.data?.message || `Failed to fetch activity with ID: ${id}`;
            console.error(`[ActivityStore] Fetch activity ${id} failed:`, e);
            throw e;
        } finally {
            isLoading.value = false;
        }
    }

    async function addActivity(newActivity: Activity): Promise<Activity> {
        isLoading.value = true;
        error.value = null;
        try {
            const response = await api.post<Activity>('/api/activities', newActivity);
            activities.value.push(response.data); // Aggiungi la nuova attività allo stato locale
            return response.data;
        } catch (e: any) {
            error.value = e.response?.data?.message || 'Failed to add activity';
            console.error('[ActivityStore] Add activity failed:', e);
            throw e;
        } finally {
            isLoading.value = false;
        }
    }

    async function updateActivity(id: string, updatedActivity: Activity): Promise<Activity> {
        isLoading.value = true;
        error.value = null;
        try {
            const response = await api.put<Activity>(`/api/activities/${id}`, updatedActivity);
            // Aggiorna l'attività nello stato locale
            const index = activities.value.findIndex(a => a.id === id);
            if (index !== -1) {
                activities.value[index] = response.data;
            }
            return response.data;
        } catch (e: any) {
            error.value = e.response?.data?.message || `Failed to update activity with ID: ${id}`;
            console.error(`[ActivityStore] Update activity ${id} failed:`, e);
            throw e;
        } finally {
            isLoading.value = false;
        }
    }

    async function removeActivity(id: string) {
        isLoading.value = true;
        error.value = null;
        try {
            await api.delete(`/api/activities/${id}`);
            // Rimuovi l'attività dallo stato locale
            activities.value = activities.value.filter(a => a.id !== id);
        } catch (e: any) {
            error.value = e.response?.data?.message || `Failed to remove activity with ID: ${id}`;
            console.error(`[ActivityStore] Remove activity ${id} failed:`, e);
            throw e;
        } finally {
            isLoading.value = false;
        }
    }

    // Azione per processare l'input naturale tramite LLM
    async function processNaturalInput(prompt: string): Promise<ParsedActivityData> {
        isLoading.value = true;
        error.value = null;
        try {
            const response = await api.post<ParsedActivityData>(
                '/api/llm/prompt',
                { prompt: prompt },
                // L'header Content-Type è già impostato in useApi.ts, ma puoi aggiungerlo qui se specifico
                // { headers: { 'Content-Type': 'application/json' } }
            );
            return response.data;
        } catch (e: any) {
            error.value = e.response?.data?.error ?? 'Error processing natural input via AI';
            console.error('[ActivityStore] LLM prompt failed:', e);
            throw e;
        } finally {
            isLoading.value = false;
        }
    }

    return {
        activities,
        isLoading,
        error,
        fetchActivities,
        fetchActivityById,
        addActivity,
        updateActivity,
        removeActivity,
        processNaturalInput,
    };
});