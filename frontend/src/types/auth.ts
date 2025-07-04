// src/types/auth.ts

export interface User {
    id: string;
    email: string;
    name?: string;
}

export interface LoginResponse {
    token: string;
    user: User;
}