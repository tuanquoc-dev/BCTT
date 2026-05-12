const DiscountView = {

    modal: new bootstrap.Modal(document.getElementById("discountModal")),

    renderTable: (data) => {
        const tbody = document.getElementById("discountTable");
        tbody.innerHTML = "";

        data.forEach(d => {
            tbody.innerHTML += `
                <tr>
                    <td>${d.id}</td>
                    <td>${d.code}</td>
                    <td>${d.discountType}</td>
                    <td>${d.discountValue}</td>
                    <td>${d.limitNumber}</td>
                    <td>${d.numberUsed}</td>
                    <td>
                        <span class="badge ${d.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                            ${d.status}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-sm"
                            onclick="DiscountController.openEdit(${d.id})">
                            <i class="fa fa-edit"></i>
                        </button>

                        <button class="btn btn-sm"
                            onclick="DiscountController.delete(${d.id})">
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

    openModal: (title) => {
        document.getElementById("discountModalTitle").innerText = title;
        DiscountView.modal.show();
    },

    closeModal: () => DiscountView.modal.hide(),

    resetForm: () => {
        document.getElementById("code").value = "";
        document.getElementById("discountType").value = "PERCENT";
        document.getElementById("discountValue").value = "";
        document.getElementById("limitNumber").value = "";
        document.getElementById("minOrderValue").value = "";
        document.getElementById("maxDiscount").value = "";
        document.getElementById("limitPerUser").value = "";
        document.getElementById("startDate").value = "";
        document.getElementById("endDate").value = "";
        document.getElementById("description").value = "";
    },

    resetFilter: () => {

        document.getElementById("searchInput").value = "";
        document.getElementById("statusFilter").value = "";

        DiscountController.currentPage = 0;
        DiscountController.load();
    },

    fillForm: (d) => {
        document.getElementById("code").value = d.code;
        document.getElementById("discountType").value = d.discountType;
        document.getElementById("discountValue").value = d.discountValue;
        document.getElementById("limitNumber").value = d.limitNumber;
        document.getElementById("minOrderValue").value = d.minOrderValue;
        document.getElementById("maxDiscount").value = d.maxDiscount;
        document.getElementById("limitPerUser").value = d.limitPerUser;
        document.getElementById("status").value = d.status || "ACTIVE";
        document.getElementById("startDate").value = d.startDate?.slice(0,16);
        document.getElementById("endDate").value = d.endDate?.slice(0,16);
        document.getElementById("description").value = d.description;
    }
};