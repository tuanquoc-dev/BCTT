const BannerController = {

    currentPage: 0,

    currentId: null,

    modal: null,

    // ================= LOAD =================
    load: async (page = 0) => {

        try {

            showLoading();

            BannerController.currentPage = page;

            const params = {

                keyword:
                document.getElementById("searchInput").value,

                status:
                document.getElementById("statusFilter").value,

                page: page,

                size: 10

            };

            const res = await BannerModel.getAll(params);

            const data = res.data.data;

            BannerView.renderTable(data.content);

            BannerView.renderPagination(
                data.totalPages,
                data.number
            );

        } catch (err) {

        } finally {

            hideLoading();

        }

    },

    // ================= CREATE =================
    openCreateModal: () => {

        BannerController.currentId = null;

        BannerView.resetForm();

        document.getElementById("bannerModalTitle").innerText =
            "Create Banner";

        BannerController.modal.show();

    },

    // ================= UPDATE =================
    openUpdateModal: async (id) => {

        try {

            showLoading();

            BannerController.currentId = id;

            const res = await BannerModel.getById(id);

            BannerView.fillForm(res.data.data);

            document.getElementById("bannerModalTitle").innerText =
                "Update Banner";

            BannerController.modal.show();

        } catch (err) {

        } finally {

            hideLoading();

        }

    },

    // ================= SUBMIT =================
    submit: async () => {

        try {

            showLoading();

            const { data, file } =
                BannerView.getFormData();

            if (BannerController.currentId) {

                await BannerModel.update(
                    BannerController.currentId,
                    data,
                    file
                );

                showToast("Update banner success");

            } else {

                await BannerModel.create(data, file);

                showToast("Create banner success");

            }

            BannerController.modal.hide();

            await BannerController.load(
                BannerController.currentPage
            );

        } catch (err) {

        } finally {

            hideLoading();

        }

    },

    // ================= DELETE =================
    delete: async (id) => {

        const confirmDelete = confirm(
            "Delete this banner?"
        );

        if (!confirmDelete) return;

        try {

            showLoading();

            await BannerModel.delete(id);

            showToast("Delete banner success");

            await BannerController.load(
                BannerController.currentPage
            );

        } catch (err) {

        } finally {

            hideLoading();

        }

    },

    // ================= RESET =================
    resetFilter: () => {

        document.getElementById("searchInput").value = "";

        document.getElementById("statusFilter").value = "";

        BannerController.load();

    }

};

// ================= INIT =================

document.addEventListener("DOMContentLoaded", async () => {

    BannerController.modal = new bootstrap.Modal(
        document.getElementById("bannerModal")
    );

    // SEARCH
    document
        .getElementById("searchInput")
        .addEventListener("keyup", () => {

            BannerController.load();

        });

    // STATUS
    document
        .getElementById("statusFilter")
        .addEventListener("change", () => {

            BannerController.load();

        });

    // PREVIEW IMAGE
    document
        .getElementById("thumbnail")
        .addEventListener("change", (e) => {

            const file = e.target.files[0];

            if (!file) return;

            document.getElementById(
                "thumbnailPreview"
            ).src = URL.createObjectURL(file);

        });

    await BannerController.load();

});