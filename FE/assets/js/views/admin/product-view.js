const ProductView = {

    modal: new bootstrap.Modal(
        document.getElementById("productModal")
    ),

    deletedImageIds: [],

    // =====================================================
    // FORMAT PRICE
    // =====================================================

    formatPrice: (value) => {

        if (value == null || value === "") {
            return "0 ₫";
        }

        return Number(value).toLocaleString(
            "vi-VN",
            {
                style: "currency",
                currency: "VND",
                maximumFractionDigits: 0
            }
        );
    },

    // =====================================================
    // TABLE
    // =====================================================

    renderTable: (products) => {

        const tbody =
            document.getElementById("productTable");

        tbody.innerHTML = "";

        products.forEach(p => {

            tbody.innerHTML += `
                <tr>

                    <td>${p.id}</td>

                    <td>
                        <img
                            src="${p.thumbnail || '/assets/no-image.png'}"
                            width="60"
                            height="60"
                            class="rounded border object-fit-cover"
                            style="object-fit:cover"
                        >
                    </td>

                    <td>
                        <div class="fw-semibold">
                            ${p.name || ""}
                        </div>
                    </td>

                    <td>${p.brandName || ""}</td>

                    <td>${p.categoryName || ""}</td>

                    <td>

                        ${
                p.discountId
                    ? `
                                    <div class="text-muted small text-decoration-line-through">
                                        ${ProductView.formatPrice(p.price)}
                                    </div>

                                    <div class="fw-bold text-danger">
                                        ${ProductView.formatPrice(p.finalPrice)}
                                    </div>
                                `
                    : `
                                    <div class="fw-bold text-danger">
                                        ${ProductView.formatPrice(p.price)}
                                    </div>
                                `
            }

                    </td>

                    <td>
                        <span class="fw-semibold">
                            ${p.stock || 0}
                        </span>
                    </td>

                    <td>
                        ${p.sku || ""}
                    </td>

                    <td>
                        <span class="badge ${
                p.status === "ACTIVE"
                    ? "bg-success"
                    : "bg-danger"
            }">
                            ${p.status}
                        </span>
                    </td>

                    <td class="text-nowrap">

                        <button
                            class="btn btn-sm"
                            onclick="ProductController.detail(${p.id})"
                        >
                            <i class="fa fa-eye"></i>
                        </button>

                        <button
                            class="btn btn-sm"
                            onclick="ProductController.openEdit(${p.id})"
                        >
                            <i class="fa fa-edit"></i>
                        </button>

                        <button
                            class="btn btn-sm"
                            onclick="ProductController.delete(${p.id})"
                        >
                            <i class="fa fa-trash"></i>
                        </button>

                    </td>

                </tr>
            `;
        });
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {
        const container = document.getElementById("pagination");

        let html = `
        <nav>
            <ul class="pagination">
    `;

        // ===== PREVIOUS =====
        html += `
        <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
            <a class="page-link" href="#" data-page="${currentPage - 1}">
                Previous
            </a>
        </li>
    `;

        // ===== PAGE NUMBERS =====
        for (let i = 0; i < totalPages; i++) {
            html += `
            <li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="#" data-page="${i}">
                    ${i + 1}
                </a>
            </li>
        `;
        }

        // ===== NEXT =====
        html += `
        <li class="page-item ${currentPage === totalPages - 1 ? "disabled" : ""}">
            <a class="page-link" href="#" data-page="${currentPage + 1}">
                Next
            </a>
        </li>
    `;

        html += `
            </ul>
        </nav>
    `;

        container.innerHTML = html;
    },

    // =====================================================
    // BRAND
    // =====================================================

    renderBrandOptions: (brands) => {

        const selects = [
            document.getElementById("brand"),
            document.getElementById("brandFilter")
        ];

        selects.forEach(sel => {

            if (!sel) return;

            sel.innerHTML =
                `<option value="">All Brand</option>`;

            brands.forEach(b => {

                sel.innerHTML += `
                    <option value="${b.id}">
                        ${b.name}
                    </option>
                `;
            });
        });
    },

    // =====================================================
    // CATEGORY
    // =====================================================

    renderCategoryOptions: (categories) => {

        const selects = [
            document.getElementById("category"),
            document.getElementById("categoryFilter")
        ];

        selects.forEach(sel => {

            if (!sel) return;

            sel.innerHTML =
                `<option value="">All Category</option>`;

            categories.forEach(c => {

                sel.innerHTML += `
                    <option value="${c.id}">
                        ${c.name}
                    </option>
                `;
            });
        });
    },

    // =====================================================
    // DISCOUNT
    // =====================================================

    renderDiscountOptions: (discounts) => {

        const select =
            document.getElementById("discount");

        if (!select) return;

        select.innerHTML =
            `<option value="">No Discount</option>`;

        discounts.forEach(d => {

            select.innerHTML += `
                <option value="${d.id}">
                    ${d.code}
                </option>
            `;
        });
    },

    // =====================================================
    // OPEN / CLOSE MODAL
    // =====================================================

    openModal: (title) => {

        document.getElementById("modalTitle")
            .innerText = title;

        ProductView.modal.show();
    },

    closeModal: () => {
        ProductView.modal.hide();
    },

    // =====================================================
    // RESET FORM
    // =====================================================

    resetForm: () => {

        document.getElementById("name").value = "";
        document.getElementById("brand").value = "";
        document.getElementById("category").value = "";
        document.getElementById("discount").value = "";
        document.getElementById("price").value = "";
        document.getElementById("stock").value = "";
        document.getElementById("sku").value = "";
        document.getElementById("color").value = "";
        document.getElementById("description").value = "";
        document.getElementById("status").value = "ACTIVE";

        document.getElementById("thumbnail").value = "";
        document.getElementById("images").value = "";

        // CLEAR PREVIEW
        document.getElementById("thumbnailPreview").innerHTML = "";
        document.getElementById("imagePreview").innerHTML = "";

        // RESET DELETE IDS
        ProductView.deletedImageIds = [];
    },

    resetFilter: () => {

        document.getElementById("searchInput").value = "";
        document.getElementById("brandFilter").value = "";
        document.getElementById("categoryFilter").value = "";
        document.getElementById("sortFilter").value = "newest";

        ProductController.load();
    },

    // =====================================================
    // FILL FORM
    // =====================================================

    fillForm: (p) => {

        ProductView.deletedImageIds = [];

        document.getElementById("name").value =
            p.name || "";

        document.getElementById("brand").value =
            p.brandId || "";

        document.getElementById("category").value =
            p.categoryId || "";

        document.getElementById("discount").value =
            p.discountId || "";

        document.getElementById("price").value =
            p.price
                ? Number(p.price).toLocaleString("vi-VN")
                : "";

        document.getElementById("stock").value =
            p.stock || "";

        document.getElementById("sku").value =
            p.sku || "";

        document.getElementById("color").value =
            p.color || "";

        document.getElementById("description").value =
            p.description || "";

        document.getElementById("status").value =
            p.status || "ACTIVE";

        // thumbnail
        if (p.thumbnail) {

            ProductView.renderThumbnailPreview(
                p.thumbnail,
                true
            );

        } else {

            document.getElementById("thumbnailPreview")
                .innerHTML = "";
        }

        // images
        ProductView.renderImages(
            p.images || []
        );
    },

    // =====================================================
    // THUMBNAIL PREVIEW
    // =====================================================

    renderThumbnailPreview: (url) => {

        const box =
            document.getElementById("thumbnailPreview");

        box.innerHTML = `
        <div class="position-relative d-inline-block">

            <img src="${url}"
                 class="img-fluid rounded border"
                 style="height:200px;object-fit:cover">

            <button type="button"
                    class="btn btn-danger btn-sm position-absolute top-0 end-0"
                    onclick="ProductView.removeThumbnail()">

                ✕
            </button>

        </div>
    `;
    },

    removeThumbnail: () => {

        document.getElementById("thumbnail").value = "";

        document.getElementById("thumbnailPreview")
            .innerHTML = "";
    },

    // =====================================================
    // RENDER OLD IMAGES
    // =====================================================

    renderImages: (images = []) => {

        const container =
            document.getElementById("imagePreview");

        container.innerHTML = "";

        images.forEach(img => {

            container.innerHTML += `
                <div
                    class="col-md-3 mb-3"
                    id="img-${img.id}"
                >

                    <div class="position-relative border rounded overflow-hidden">

                        <img
                            src="${img.imgUrl}"
                            class="w-100"
                            style="
                                height:150px;
                                object-fit:cover;
                            "
                        >

                        <button
                            type="button"
                            class="btn btn-danger btn-sm position-absolute top-0 end-0"

                            onclick="ProductView.deleteOldImage(${img.id})"
                        >
                            ✕
                        </button>

                    </div>

                </div>
            `;
        });
    },

    // =====================================================
    // DELETE OLD IMAGE
    // =====================================================

    deleteOldImage: (id) => {

        ProductView.deletedImageIds.push(id);

        document.getElementById(`img-${id}`)?.remove();
    },

    // =====================================================
    // INIT EVENTS
    // =====================================================

    init: () => {

        // thumbnail preview
        document.getElementById("thumbnail")
            ?.addEventListener("change", (e) => {

                const file = e.target.files[0];

                if (!file) return;

                const url =
                    URL.createObjectURL(file);

                ProductView.renderThumbnailPreview(url);
            });

        const priceInput = document.getElementById("price");

// =========================
// FOCUS
// bỏ format để nhập
// =========================
        priceInput?.addEventListener("focus", (e) => {

            let raw = e.target.value.replace(/\./g, "");

            e.target.value = raw;
        });

// =========================
// BLUR
// format lại tiền VN
// =========================
        priceInput?.addEventListener("blur", (e) => {

            let value = e.target.value.replace(/[^\d]/g, "");

            if (!value) {
                e.target.value = "";
                return;
            }

            e.target.value =
                Number(value).toLocaleString("vi-VN");
        });

        // multiple image preview
        document.getElementById("images")
            ?.addEventListener("change", (e) => {

                const files = [...e.target.files];

                const container =
                    document.getElementById("imagePreview");

                files.forEach((file, index) => {

                    const url =
                        URL.createObjectURL(file);

                    const randomId =
                        "new-" + Date.now() + index;

                    container.innerHTML += `
                        <div
                            class="col-md-3 mb-3"
                            id="${randomId}"
                        >

                            <div class="position-relative border rounded overflow-hidden">

                                <img
                                    src="${url}"
                                    class="w-100"
                                    style="
                                        height:150px;
                                        object-fit:cover;
                                    "
                                >

                                <button
                                    type="button"
                                    class="btn btn-danger btn-sm position-absolute top-0 end-0"

                                    onclick="
                                        document
                                            .getElementById('${randomId}')
                                            .remove()
                                    "
                                >
                                    ✕
                                </button>

                            </div>

                        </div>
                    `;
                });
            });
    }
};

// =====================================================
// DETAIL MODAL
// =====================================================

ProductView.detailModal =
    new bootstrap.Modal(
        document.getElementById("productDetailModal")
    );

// =====================================================
// SHOW DETAIL
// =====================================================

ProductView.showDetail = (p) => {

    // ===============================
    // BASIC
    // ===============================

    document.getElementById("detailName")
        .innerText = p.name || "";

    document.getElementById("detailBrand")
        .innerText = p.brandName || "";

    document.getElementById("detailCategory")
        .innerText = p.categoryName || "";

    document.getElementById("detailSku")
        .innerText = p.sku || "";

    document.getElementById("detailStock")
        .innerText = p.stock || 0;

    document.getElementById("detailSold")
        .innerText = p.soldQuantity || 0;

    document.getElementById("detailView")
        .innerText = p.viewCount || 0;

    document.getElementById("detailRating")
        .innerText = p.rating || 0;

    document.getElementById("detailDescription")
        .innerText = p.description || "Không có mô tả";

    // ===============================
    // STATUS
    // ===============================

    document.getElementById("detailStatus")
        .innerHTML = `
            <span class="badge ${
        p.status === "ACTIVE"
            ? "bg-success"
            : "bg-danger"
    }">
                ${p.status}
            </span>
        `;

    document.getElementById("detailColor")
        .innerText = p.color || "";

    // ===============================
    // PRICE
    // ===============================

    if (p.discountId) {

        document.getElementById("detailOldPrice")
            .innerText =
            ProductView.formatPrice(p.price);

        document.getElementById("detailPrice")
            .innerText =
            ProductView.formatPrice(p.finalPrice);

    } else {

        document.getElementById("detailOldPrice")
            .innerText = "";

        document.getElementById("detailPrice")
            .innerText =
            ProductView.formatPrice(p.price);
    }

    // ===============================
    // IMAGES
    // ===============================

    const carousel =
        document.getElementById("productCarouselInner");

    const thumbs =
        document.getElementById("productThumbs");

    carousel.innerHTML = "";
    thumbs.innerHTML = "";

    // thumbnail đầu tiên
    const allImages = [];

    if (p.thumbnail) {

        allImages.push({
            imgUrl: p.thumbnail
        });
    }

    if (p.images?.length) {

        p.images.forEach(img => {

            allImages.push(img);
        });
    }

    // fallback
    if (allImages.length === 0) {

        allImages.push({
            imgUrl: "/assets/no-image.png"
        });
    }

    // carousel
    allImages.forEach((img, index) => {

        carousel.innerHTML += `
            <div class="carousel-item ${
            index === 0 ? "active" : ""
        }">

                <img src="${img.imgUrl}"
                     class="d-block w-100 rounded"
                     style="
                        height:450px;
                        object-fit:cover;
                     ">

            </div>
        `;

        // thumbnail nhỏ
        thumbs.innerHTML += `
            <div class="col-3">

                <img src="${img.imgUrl}"
                     class="w-100 rounded border cursor-pointer"
                     style="
                        height:90px;
                        object-fit:cover;
                        cursor:pointer;
                     "

                     data-bs-target="#productCarousel"
                     data-bs-slide-to="${index}">
            </div>
        `;
    });

    ProductView.detailModal.show();
};

// =====================================================
// INIT
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {
        ProductView.init();
    }
);


