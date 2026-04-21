const AuthModel = {
    login: (data) => api.post("/auth/login", data),

    register: (data) => api.post("/auth/register", data),

    forgot: (email) => api.post("/auth/forgot-password", { email }),

    reset: (data) => api.post("/auth/reset-password", data),
};