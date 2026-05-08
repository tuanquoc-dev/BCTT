const CategoryController = {

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
            const res = await AdminModel.getCategories({
                keyword: document.getElementById("searchInput")?.value || "",
                status: document.getElementById("statusFilter")?.value || "",
                page: this.currentPage,
                size: 10
            });

            const pageData = res.data?.data;

            CategoryView.renderTable(pageData.content || []);

            CategoryView.renderPagination(
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

    async create() {
        const data = CategoryView.getFormData();
        const file = CategoryView.getFile();

        if (!data.name) {
            showToast("Tên không được trống", "danger");
            return;
        }

        showLoading();
        try {
            await AdminModel.createCategory(data, file);
            showToast("Tạo thành công");
            CategoryView.closeModal();

            this.currentPage = 0;
            this.load();

        } finally {
            hideLoading();
        }
    },

    openCreate() {
        this.currentEditId = null;
        CategoryView.resetForm();
        CategoryView.openModal("Tạo Category");
    },

    openEdit(id, name, status, description, image) {
        this.currentEditId = id;

        CategoryView.fillForm({ name, status, description, image });

        const fileInput = document.getElementById("categoryFile");
        if (fileInput) fileInput.value = "";

        CategoryView.openModal("Cập nhật Category");
    },

    async update() {
        const data = CategoryView.getFormData();
        const file = CategoryView.getFile();

        showLoading();
        try {
            await AdminModel.updateCategory(this.currentEditId, data, file);
            showToast("Cập nhật thành công");
            CategoryView.closeModal();
            this.load();
        } finally {
            hideLoading();
        }
    },

    async delete(id) {
        if (!confirm("Xóa?")) return;

        showLoading();
        try {
            await AdminModel.deleteCategory(id);
            showToast("Đã xóa");
            this.load();
        } finally {
            hideLoading();
        }
    },

    save() {
        this.currentEditId ? this.update() : this.create();
    }
};

document.addEventListener("DOMContentLoaded", () => {
    CategoryController.init();
});