const UserModel = {
    getMe: () => api.get("/users/me"),

    updateProfile: (formData) =>
        api.put("/users/profile", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        }),

    changePassword: (data) =>
        api.put("/users/change-password", data),
};