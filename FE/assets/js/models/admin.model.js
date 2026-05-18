function buildFormData(data, file) {
    const formData = new FormData();

    formData.append(
        "data",
        new Blob([JSON.stringify(data)], {
            type: "application/json"
        })
    );

    if (file) {
        formData.append("file", file);
    }

    return formData;
}

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
            params: {status}
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
    },

    // =============================
    // 🏷 BRAND
    // =============================
    getBrands: (params = {}) => {
        return api.get("/admin/brands", {
            params: {
                keyword: params.keyword || "",
                status: params.status || null,
                page: params.page ?? 0,
                size: params.size ?? 10
            }
        });
    },

    getBrandById: (id) =>
        api.get(`/admin/brands/${id}`),

    createBrand: (data, file) => {
        return api.post(
            "/admin/brands",
            buildFormData(data, file)
        );
    },

    updateBrand: (id, data, file) => {
        return api.put(
            `/admin/brands/${id}`,
            buildFormData(data, file)
        );
    },

    deleteBrand: (id) =>
        api.delete(`/admin/brands/${id}`),

    // =============================
    // 📂 CATEGORY
    // =============================
    getCategories: (params = {}) => {
        return api.get("/admin/categories", {
            params: {
                keyword: params.keyword || "",
                status: params.status || null,
                page: params.page ?? 0,
                size: params.size ?? 10
            }
        });
    },

    getCategoryById: (id) =>
        api.get(`/admin/categories/${id}`),

    createCategory: (data, file) => {
        return api.post(
            "/admin/categories",
            buildFormData(data, file),
            {
                headers: {"Content-Type": "multipart/form-data"}
            }
        );
    },

    updateCategory: (id, data, file) => {
        return api.put(
            `/admin/categories/${id}`,
            buildFormData(data, file),
            {
                headers: {"Content-Type": "multipart/form-data"}
            }
        );
    },

    deleteCategory: (id) =>
        api.delete(`/admin/categories/${id}`),

    // =============================
// 🛒 PRODUCT
// =============================

// 🔍 SEARCH PRODUCT
    getProducts: (params = {}) => {
        return api.get("/admin/products", {
            params: {
                keyword: params.keyword || "",
                minPrice: params.minPrice || null,
                maxPrice: params.maxPrice || null,
                brandId: params.brandId || null,
                categoryId: params.categoryId || null,
                sort: params.sort || "newest",
                page: params.page ?? 0,
                size: params.size ?? 10
            }
        });
    },

// 🔍 GET BY ID
    getProductById: (id) =>
        api.get(`/admin/products/${id}`),

// ➕ CREATE PRODUCT (thumbnail + images)
    createProduct: (data, thumbnail, images = []) => {
        const formData = new FormData();

        formData.append(
            "data",
            new Blob([JSON.stringify(data)], {type: "application/json"})
        );

        if (thumbnail) {
            formData.append("thumbnail", thumbnail);
        }

        images.forEach(img => {
            formData.append("images", img);
        });

        return api.post("/admin/products", formData);
    },

// ✏️ UPDATE PRODUCT
    updateProduct: (id, data, thumbnail, newImages = [], deleteImageIds = []) => {
        const formData = new FormData();

        formData.append(
            "data",
            new Blob([JSON.stringify(data)], {type: "application/json"})
        );

        if (thumbnail) {
            formData.append("thumbnail", thumbnail);
        }

        newImages.forEach(img => {
            formData.append("newImages", img);
        });

        deleteImageIds.forEach(id => {
            formData.append("deleteImageIds", id);
        });

        return api.put(`/admin/products/${id}`, formData);
    },

// ❌ DELETE (soft delete -> INACTIVE)
    deleteProduct: (id) =>
        api.delete(`/admin/products/${id}`),

// 🎟 DISCOUNT
// =============================

// 🔍 GET ALL
    getDiscounts: (params) => {
        return api.get("/admin/discounts", {params});
    },

// 🔍 GET BY ID
    getDiscountById: (id) => {
        return api.get(`/admin/discounts/${id}`);
    },

// ➕ CREATE
    createDiscount: (data) => {
        return api.post("/admin/discounts", data);
    },

// ✏️ UPDATE
    updateDiscount: (id, data) => {
        return api.put(`/admin/discounts/${id}`, data);
    },

// ❌ DELETE
    deleteDiscount: (id) => {
        return api.delete(`/admin/discounts/${id}`);
    },

    // ORDERS
    getOrders: (params) => {

        return api.get(
            "/admin/orders",
            {
                params
            }
        );
    },

    getOrderDetail: (id) =>
        api.get(`/admin/orders/${id}`),

    confirmOrder: (id) =>
        api.put(`/admin/orders/${id}/confirm`),

    rejectOrder: (id) =>
        api.put(`/admin/orders/${id}/reject`),

    cancelOrder: (id) =>
        api.put(`/admin/orders/${id}/cancel`),

    shippingOrder: (id) =>
        api.put(`/admin/orders/${id}/shipping`),

    completeOrder: (id) =>
        api.put(`/admin/orders/${id}/complete`),

    // DASHBOARD
    dashboard: () =>
        api.get("/admin/orders/dashboard")
};

// ================= BANNER =================

const BannerModel = {

    getAll: (params) =>
        api.get("/admin/banners", { params }),

    getById: (id) =>
        api.get(`/admin/banners/${id}`),

    create: (data, file) => {

        return api.post(
            "/admin/banners",
            buildFormData(data, file)
        );

    },


    update: (id, data, file) => {

        return api.put(
            `/admin/banners/${id}`,
            buildFormData(data, file)
        );

    },

    delete: (id) =>
        api.delete(`/admin/banners/${id}`),

};

const NotificationAdminModel = {

    read: (id) =>
        api.put(`admin/${id}/read`),

    getAdminNotifications: () =>
        api.get("admin/notifications")
};

const AdminCommentModel = {

    // =============================
    // SEARCH COMMENTS
    // =============================

    getComments(params) {

        return api.get(
            "/admin/comments",
            { params }
        );
    },

    // =============================
    // UPDATE STATUS
    // =============================

    updateStatus(id, status) {

        return api.put(
            `/admin/comments/${id}/status`,
            null,
            {
                params: { status }
            }
        );
    },

    // =============================
    // DELETE
    // =============================

    delete(id) {

        return api.delete(
            `/admin/comments/${id}`
        );
    },
    

    // =============================
    // REPLY COMMENT
    // =============================

    reply(data) {

        return api.post(
            "/admin/comments/reply",
            data
        );
    }
};