const BrandView = {

    removeImage: false,

    // ================= TABLE =================
    renderTable: (brands) => {

        const tbody =
            document.getElementById("brandTable");

        tbody.innerHTML = brands.map(b => `

            <tr>

                <td>${b.id}</td>

                <td class="fw-semibold">
                    ${b.name}
                </td>

                <td>

                    <img
                        src="${b.logo || ''}"
                        width="50"
                        height="50"
                        class="rounded border object-fit-cover"
                    >

                </td>

                <td>

                    <span class="badge bg-${b.status === 'ACTIVE'
            ? 'success'
            : 'secondary'}">

                        ${b.status === 'ACTIVE'
            ? 'Hoạt động'
            : 'Ngừng hoạt động'}

                    </span>

                </td>

                <td>

                    <div class="d-flex gap-2 justify-content-center">

                        <!-- EDIT -->
                        <button
                            class="btn btn-sm btn-light border"
                            title="Chỉnh sửa"
                            onclick="BrandController.openEdit(
                                ${b.id},
                                '${b.name}',
                                '${b.status}',
                                '${b.logo || ''}'
                            )"
                        >

                            <i class="fa fa-edit text-dark"></i>

                        </button>

                        <!-- DELETE -->
                        <button
                            class="btn btn-sm btn-light border"
                            title="Xóa"
                            onclick="BrandController.delete(${b.id})"
                        >

                            <i class="fa fa-trash text-dark"></i>

                        </button>

                    </div>

                </td>

            </tr>

        `).join("");
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {

        const container =
            document.getElementById("pagination");

        if (!container) return;

        let html =
            `<nav><ul class="pagination">`;

        html += `
            <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
                <a class="page-link"
                   href="#"
                   data-page="${currentPage - 1}">
                    Trước
                </a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {

            html += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <a class="page-link"
                       href="#"
                       data-page="${i}">
                        ${i + 1}
                    </a>
                </li>
            `;
        }

        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? "disabled" : ""}">
                <a class="page-link"
                   href="#"
                   data-page="${currentPage + 1}">
                    Sau
                </a>
            </li>
        `;

        html += `</ul></nav>`;

        container.innerHTML = html;
    },

    // ================= MODAL =================
    openModal: (title) => {

        document.getElementById("modalTitle").innerText =
            title;

        new bootstrap.Modal(
            document.getElementById("brandModal")
        ).show();
    },

    closeModal: () => {

        bootstrap.Modal
            .getInstance(
                document.getElementById("brandModal")
            )
            .hide();
    },

    // ================= IMAGE =================
    showImage: (url) => {

        const img =
            document.getElementById("previewImage");

        const btn =
            document.getElementById("btnRemoveImage");

        if (url) {

            img.src = url;

            img.classList.remove("d-none");

            btn.classList.remove("d-none");
        }
    },

    removeCurrentImage: () => {

        document.getElementById("previewImage")
            .classList.add("d-none");

        document.getElementById("btnRemoveImage")
            .classList.add("d-none");

        BrandView.removeImage = true;
    },

    // ================= FORM =================
    fillForm: (data) => {

        document.getElementById("name").value =
            data.name || "";

        document.getElementById("status").value =
            data.status || "ACTIVE";

        BrandView.removeImage = false;

        if (data.logo) {

            BrandView.showImage(data.logo);
        }
    },

    resetForm: () => {

        document.getElementById("name").value = "";

        document.getElementById("file").value = "";

        document.getElementById("status").value =
            "ACTIVE";

        document.getElementById("previewImage")
            .classList.add("d-none");

        document.getElementById("btnRemoveImage")
            .classList.add("d-none");

        BrandView.removeImage = false;
    },

    resetFilter: () => {

        document.getElementById("searchInput").value = "";

        document.getElementById("statusFilter").value = "";

        BrandController.currentPage = 0;

        BrandController.load();
    },

    getFormData: () => ({

        name:
        document.getElementById("name").value,

        status:
        document.getElementById("status").value,

        removeImage:
        BrandView.removeImage
    }),

    getFile: () =>
        document.getElementById("file").files[0]
};