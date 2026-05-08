const DiscountController = {

    currentPage: 0,
    currentEditId: null,

    init() {
        this.bindPagination();
        this.load();
        this.bindFilter();
    },

    // ================= LOAD =================
    load: async () => {
        showLoading();
        try {
            const res = await AdminModel.getDiscounts({
                keyword: document.getElementById("searchInput")?.value || "",
                status: document.getElementById("statusFilter")?.value || "",
                page: this.currentPage,
                size: 10
            });

            const pageData = res.data?.data;

            DiscountView.renderTable(pageData.content || []);
            DiscountView.renderPagination(
                pageData.totalPages,
                pageData.number
            );

        } finally {
            hideLoading();
        }
    },

    bindFilter() {

        // SEARCH REALTIME
        document
            .getElementById("searchInput")
            ?.addEventListener("keyup", () => {

                this.currentPage = 0;
                this.load();
            });

        // STATUS REALTIME
        document
            .getElementById("statusFilter")
            ?.addEventListener("change", () => {

                this.currentPage = 0;
                this.load();
            });
    },

    bindPagination() {
        const container = document.getElementById("pagination");
        if (!container) return;

        container.addEventListener("click", (e) => {
            e.preventDefault();

            const target = e.target.closest("[data-page]");
            if (!target) return;

            const page = parseInt(target.dataset.page);
            if (isNaN(page)) return;

            this.currentPage = page;
            this.load();
        });
    },

    // ================= OPEN CREATE =================
    openCreate: () => {
        DiscountController.currentId = null;
        DiscountView.resetForm();
        DiscountView.openModal("Create Discount");
    },

    // ================= OPEN EDIT =================
    openEdit: async (id) => {
        showLoading();
        try {
            const res = await AdminModel.getDiscountById(id);
            DiscountController.currentId = id;
            DiscountView.fillForm(res.data.data);
            DiscountView.openModal("Update Discount");
        } finally {
            hideLoading();
        }
    },

    // ================= SAVE =================
    save: async () => {

        const payload = {
            code: document.getElementById("code").value,
            discountType: document.getElementById("discountType").value,
            discountValue: Number(document.getElementById("discountValue").value),
            limitNumber: Number(document.getElementById("limitNumber").value),
            minOrderValue: Number(document.getElementById("minOrderValue").value),
            maxDiscount: Number(document.getElementById("maxDiscount").value),
            limitPerUser: Number(document.getElementById("limitPerUser").value),
            status: document.getElementById("status").value,
            startDate: document.getElementById("startDate").value,
            endDate: document.getElementById("endDate").value,
            description: document.getElementById("description").value
        };

        showLoading();

        try {
            if (DiscountController.currentId == null) {
                await AdminModel.createDiscount(payload);
                showToast("Create success");
            } else {
                await AdminModel.updateDiscount(DiscountController.currentId, payload);
                showToast("Update success");
            }

            DiscountView.closeModal();
            DiscountController.load();

        } finally {
            hideLoading();
        }
    },

    // ================= DELETE =================
    delete: async (id) => {

        if (!confirm("Delete discount?")) return;

        showLoading();

        try {
            await AdminModel.deleteDiscount(id);
            showToast("Delete success");
            DiscountController.load();
        } finally {
            hideLoading();
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    DiscountController.init();
});