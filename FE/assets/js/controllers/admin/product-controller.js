const ProductController = {

    currentPage: 0,
    currentId: null,

    // =====================================================
    // LOAD
    // =====================================================
    load: async (page = 0) => {

        try {

            showLoading();

            ProductController.currentPage = page;

            const params = {
                keyword: document.getElementById("searchInput").value,
                brandId: document.getElementById("brandFilter").value || null,
                categoryId: document.getElementById("categoryFilter").value || null,
                sort: document.getElementById("sortFilter").value,
                page,
                size: 10
            };

            const res =
                await AdminModel.getProducts(params);

            const pageData = res.data.data;

            ProductView.renderTable(pageData.content);
            ProductView.renderPagination(
                pageData.totalPages,
                pageData.number
            );

        } catch (e) {

        } finally {

            hideLoading();
        }
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
            this.load(page);
        });
    },

    // =====================================================
    // INIT
    // =====================================================
    init: async function () {

        this.bindPagination();

        try {

            showLoading();

            const [
                brands,
                categories,
                discounts
            ] = await Promise.all([
                AdminModel.getBrands(),
                AdminModel.getCategories(),
                AdminModel.getDiscounts({
                    page: 0,
                    size: 100
                })
            ]);

            ProductView.renderBrandOptions(
                brands.data.data.content || []
            );

            ProductView.renderCategoryOptions(
                categories.data.data.content || []
            );

            ProductView.renderDiscountOptions(
                discounts.data.data.content || []
            );

            await this.load(0);

        } catch (e) {

            console.error(e);

        } finally {

            hideLoading();
        }
    },

    // =========================================
    // DETAIL
    // =========================================
    detail: async (id) => {

        try {

            showLoading();

            const res = await AdminModel.getProductById(id);

            const p = res.data.data;

            ProductView.showDetail(p);

        } catch (e) {

        } finally {

            hideLoading();
        }
    },

    // =====================================================
    // OPEN CREATE
    // =====================================================
    openCreate: () => {

        ProductController.currentId = null;

        ProductView.resetForm();

        ProductView.openModal("Create Product");
    },

    // =====================================================
    // OPEN EDIT
    // =====================================================
    openEdit: async (id) => {

        try {

            showLoading();

            ProductController.currentId = id;

            ProductView.resetForm();

            const res = await AdminModel.getProductById(id);

            const product = res.data.data;

            ProductView.fillForm(product);

            ProductView.openModal("Update Product");

        } catch (e) {

            // console.error(e);
            // showToast("Không tìm thấy sản phẩm", "error");

        } finally {

            hideLoading();
        }
    },

    // =====================================================
    // SAVE
    // =====================================================
    save: async () => {

        try {

            showLoading();

            const data = {

                name:
                document.getElementById("name").value,

                brandId:
                    Number(document.getElementById("brand").value),

                categoryId:
                    Number(document.getElementById("category").value),

                discountId:
                    document.getElementById("discount").value || null,

                price: Number(
                    document.getElementById("price")
                        .value.replace(/\./g, "")
                ),

                stock:
                    Number(document.getElementById("stock").value),

                sku:
                    document.getElementById("sku").value.trim() || null,

                color:
                document.getElementById("color").value,

                description:
                document.getElementById("description").value,

                status:
                document.getElementById("status").value
            };

            const thumbnail =
                document.getElementById("thumbnail").files[0];

            const images =
                [...document.getElementById("images").files];

            // ================= CREATE =================

            if (!ProductController.currentId) {

                await AdminModel.createProduct(
                    data,
                    thumbnail,
                    images
                );

                // showToast("Tạo sản phẩm thành công");

            }

            // ================= UPDATE =================

            else {

                await AdminModel.updateProduct(
                    ProductController.currentId,
                    data,
                    thumbnail,
                    images,
                    ProductView.deletedImageIds
                );

                showToast("Cập nhật sản phẩm thành công");
            }

            ProductView.closeModal();

            await ProductController.load(
                ProductController.currentPage
            );

        } catch (e) {

        } finally {

            hideLoading();
        }
    },

    // =====================================================
    // DELETE
    // =====================================================
    delete: async (id) => {

        const ok =
            confirm("Bạn có chắc muốn xóa?");

        if (!ok) return;

        try {

            showLoading();

            await AdminModel.deleteProduct(id);

            showToast("Xóa thành công");

            await ProductController.load(
                ProductController.currentPage
            );

        } catch (e) {
            //
            // console.error(e);
            //
            // showToast(
            //     "Xóa thất bại",
            //     "error"
            // );

        } finally {

            hideLoading();
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {

    ProductController.init();

    document
        .getElementById("saveBtn")
        .addEventListener(
            "click",
            ProductController.save
        );
});