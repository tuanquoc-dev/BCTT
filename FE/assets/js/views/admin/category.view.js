const CategoryView = {

    removeImage: false,

    renderTable: (categories) => {
        const tbody = document.getElementById("categoryTable");

        tbody.innerHTML = categories.map(c => `
            <tr>
                <td>${c.id}</td>
                <td>${c.name}</td>
                <td>${c.description || ''}</td>
                <td><img src="${c.image || ''}" width="50"/></td>
                <td>
                    <span class="badge bg-${c.status === 'ACTIVE' ? 'success' : 'secondary'}">
                        ${c.status}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm"
                        onclick="CategoryController.openEdit(${c.id}, '${c.name}', '${c.status}', \`${c.description || ''}\`, '${c.image || ''}')">
                        <i class="fa fa-edit"></i>
                    </button>

                    <button class="btn btn-sm"
                        onclick="CategoryController.delete(${c.id})">
                        <i class="fa fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join("");
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {
        const container = document.getElementById("pagination");
        if (!container) return;

        let html = `<nav><ul class="pagination">`;

        html += `
            <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {
            html += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? "disabled" : ""}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
            </li>
        `;

        html += `</ul></nav>`;

        container.innerHTML = html;
    },

    // ===== IMAGE =====
    showImage: (url) => {
        const img = document.getElementById("categoryPreview");
        const btn = document.getElementById("categoryRemoveBtn");

        if (url) {
            img.src = url;
            img.classList.remove("d-none");
            btn.classList.remove("d-none");
        }
    },

    removeCurrentImage: () => {
        document.getElementById("categoryPreview").classList.add("d-none");
        document.getElementById("categoryRemoveBtn").classList.add("d-none");

        CategoryView.removeImage = true;
    },

    // ===== FORM =====
    getFormData: () => ({
        name: document.getElementById("categoryName")?.value,
        status: document.getElementById("categoryStatus")?.value,
        description: document.getElementById("categoryDescription")?.value,
        removeImage: CategoryView.removeImage
    }),

    getFile: () =>
        document.getElementById("categoryFile")?.files[0],

    resetForm: () => {
        document.getElementById("categoryName").value = "";
        document.getElementById("categoryFile").value = "";
        document.getElementById("categoryStatus").value = "ACTIVE";
        document.getElementById("categoryDescription").value = "";

        document.getElementById("categoryPreview").classList.add("d-none");
        document.getElementById("categoryRemoveBtn").classList.add("d-none");

        CategoryView.removeImage = false;
    },

    resetFilter: () => {

        document.getElementById("searchInput").value = "";
        document.getElementById("statusFilter").value = "";

        CategoryController.currentPage = 0;
        CategoryController.load();
    },

    fillForm: (data) => {
        document.getElementById("categoryName").value = data.name || "";
        document.getElementById("categoryStatus").value = data.status || "ACTIVE";
        document.getElementById("categoryDescription").value = data.description || "";

        CategoryView.removeImage = false;

        if (data.image) {
            CategoryView.showImage(data.image);
        }
    },

    openModal: (title) => {
        document.getElementById("modalTitle").innerText = title;
        new bootstrap.Modal(document.getElementById("categoryModal")).show();
    },

    closeModal: () => {
        bootstrap.Modal.getInstance(document.getElementById("categoryModal")).hide();
    }
};