const AdminModel = {

    // =============================
    // 🔍 GET USERS (SEARCH + PAGINATION)
    // =============================
    getUsers: (params = {}) => {
        return api.get("/admin/users", {
            params: {
                keyword: params.keyword || "",
                page: params.page || 0,
                size: params.size || 10
            }
        });
    },

    // =============================
    // ➕ CREATE STAFF
    // =============================
    createStaff: (data) => {
        return api.post("/admin/staff", data);
    },

    // =============================
    // 🔄 UPDATE STATUS
    // =============================
    updateStatus: (id, status) => {
        return api.put(`/admin/users/${id}/status`, null, {
            params: { status }
        });
    },

    // =============================
    // ✏️ UPDATE USER
    // =============================
    updateUser: (id, data) => {
        return api.put(`/admin/users/${id}`, data);
    },

    getCurentUser: (id, data) => {
        return api.get(`/admin/me/${id}`);
    },

    updateProfile: (data) => {
        return api.put("/admin/me", data);
    }
};