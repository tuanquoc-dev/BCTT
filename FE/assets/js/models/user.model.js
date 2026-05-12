const UserModel = {
    getMe: () => api.get("/users/me"),

    updateProfile: (formData) =>
        api.put("/users/profile", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        }),

    changePassword: (data) =>
        api.put("/users/change-password", data),

    // ======================================
    // PRODUCTS
    // ======================================

    searchProducts: (params) =>
        api.get("/products", { params }),

    getProductDetail: (slug) =>
        api.get(`/products/${slug}`),

    // ======================================
// BRANDS
// ======================================

    getBrands() {
        return api.get("/public/brands");
    },

// ======================================
// CATEGORIES
// ======================================

    getCategories() {

        return api.get(
            "/public/categories");
    },

    // ======================================
    // ORDERS
    // ======================================

    createOrder(data) {

        return api.post(
            "/users/orders",
            data
        );
    },

    getMyOrders() {

        return api.get(
            "/users/orders"
        );
    },

    getOrderDetail(code) {

        return api.get(
            `/users/orders/${code}`
        );
    },
};