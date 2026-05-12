const BrandController = {

    currentPage: 0,
    currentEditId: null,

    init() {
        this.bindPagination();
        this.load();
        this.bindFilter();
    },

    async load() {
        showLoading();
        try {
            const res = await AdminModel.getBrands({
                keyword: document.getElementById("searchInput")?.value || "",
                status: document.getElementById("statusFilter")?.value || "",
                page: this.currentPage,
                size: 10
            });

            const pageData = res.data?.data;

            BrandView.renderTable(pageData.content || []);

            // 🔥 FIX THỨ TỰ
            BrandView.renderPagination(
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

    // ================= PAGINATION =================
    bindPagination() {
        const container = document.getElementById("pagination");
        if (!container) return;

        container.addEventListener("click", (e) => {
            e.preventDefault();

            // 🔥 FIX: đảm bảo lấy đúng thẻ có data-page
            const target = e.target.closest("[data-page]");
            if (!target) return;

            const page = parseInt(target.dataset.page);
            if (isNaN(page)) return;

            this.currentPage = page;
            this.load();
        });
    },

    // ================= CREATE =================
    async create() {
        const data = BrandView.getFormData();
        const file = BrandView.getFile();

        if (!data.name) {
            showToast("Tên không được trống", "danger");
            return;
        }

        showLoading();
        try {
            await AdminModel.createBrand(data, file);
            showToast("Tạo thành công");
            BrandView.closeModal();

            this.currentPage = 0;
            this.load();

        } finally {
            hideLoading();
        }
    },

    // ================= EDIT =================
    openCreate() {
        this.currentEditId = null;
        BrandView.resetForm();
        BrandView.openModal("Tạo Brand");
    },

    openEdit(id, name, status, logo) {
        this.currentEditId = id;

        BrandView.fillForm({ name, status, logo });

        const fileInput = document.getElementById("file");
        if (fileInput) fileInput.value = "";

        BrandView.openModal("Cập nhật Brand");
    },

    async update() {
        const data = BrandView.getFormData();
        const file = BrandView.getFile();

        showLoading();
        try {
            await AdminModel.updateBrand(this.currentEditId, data, file);
            showToast("Cập nhật thành công");
            BrandView.closeModal();
            this.load();
        } finally {
            hideLoading();
        }
    },

    // ================= DELETE =================
    async delete(id) {
        if (!confirm("Xóa?")) return;

        showLoading();
        try {
            await AdminModel.deleteBrand(id);
            showToast("Đã xóa");
            this.load();
        } finally {
            hideLoading();
        }
    },

    // ================= SAVE =================
    save() {
        this.currentEditId ? this.update() : this.create();
    }
};

document.addEventListener("DOMContentLoaded", () => {
    BrandController.init();
});