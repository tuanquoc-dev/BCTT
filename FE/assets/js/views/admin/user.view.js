const UserView = {

    renderTable: (users) => {
        const tbody = document.getElementById("userTableBody");

        if (!users.length) {
            tbody.innerHTML = `<tr><td colspan="10" class="text-center">Không có dữ liệu</td></tr>`;
            return;
        }

        tbody.innerHTML = users.map(u => `
        <tr>
            <td>${u.id}</td>
            <td>${u.username}</td>
            <td>${u.fullName || ""}</td>
            <td>${u.email || ""}</td>
            <td>${u.phone || ""}</td>
            <td>${u.age || ""}</td>
            <td>${u.address || ""}</td>
            <td>${u.role || "N/A"}</td>
            <td>
                                        <span class="badge ${
            u.status === "ACTIVE"
                ? "bg-success"
                : "bg-secondary"
        }">

                            ${
            u.status === "ACTIVE"
                ? "Hoạt động"
                : "Ngừng hoạt động"
        }

                        </span>
            </td>
            <td>
                <button class="btn btn-sm editBtn"
                        data-user='${JSON.stringify(u)}'>
                    <i class="fa fa-edit text-dark"></i>
                </button>
            </td>
        </tr>
        `).join("");
    },

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
                Trước
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
                Sau
            </a>
        </li>
    `;

        html += `
            </ul>
        </nav>
    `;

        container.innerHTML = html;
    },

    getKeyword: () => {
        return document.getElementById("searchInput")?.value.trim() || "";
    },

    // ===== MODAL =====
    fillEditForm: (user) => {
        document.getElementById("editId").value = user.id;
        document.getElementById("editUsername").value = user.username;
        document.getElementById("editFullName").value = user.fullName || "";
        document.getElementById("editEmail").value = user.email || "";
        document.getElementById("editPhone").value = user.phone || "";
        document.getElementById("editAge").value = user.age || "";
        document.getElementById("editAddress").value = user.address || "";
        document.getElementById("editStatus").value = user.status;
    },

    resetFilter: () => {

        document.getElementById("searchInput").value = "";

        currentPage = 0;
        loadUsers();
    },

    getEditData: () => ({
        id: document.getElementById("editId").value,
        data: {
            fullName: document.getElementById("editFullName").value,
            phone: document.getElementById("editPhone").value,
            age: document.getElementById("editAge").value,
            address: document.getElementById("editAddress").value
        },
        status: document.getElementById("editStatus").value
    }),

    showModal: () => {
        const modal = new bootstrap.Modal(document.getElementById("editUserModal"));
        modal.show();
    },

    hideModal: () => {
        const modalEl = document.getElementById("editUserModal");
        bootstrap.Modal.getInstance(modalEl).hide();
    },

    showCreateModal: () => {
        const modal = new bootstrap.Modal(document.getElementById("createStaffModal"));
        modal.show();
    },

    hideCreateModal: () => {
        const modalEl = document.getElementById("createStaffModal");
        bootstrap.Modal.getInstance(modalEl).hide();
    },

    getCreateData: () => ({
        username: document.getElementById("createUsername").value,
        email: document.getElementById("createEmail").value,
        password: document.getElementById("createPassword").value,
        roleCode: "STAFF"
    }),
};