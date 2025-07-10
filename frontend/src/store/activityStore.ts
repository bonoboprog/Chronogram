// src/store/activityStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useActivityStore = defineStore('activity', () => {
    const needsRefresh = ref(false); // 🔄 segnala il bisogno di aggiornare
    return { needsRefresh };
});
