const DiscountView = {

    modal: new bootstrap.Modal(
        document.getElementById("discountModal")
    ),

    renderTable: (data) => {

        const tbody =
            document.getElementById("discountTable");

        tbody.innerHTML = "";

        if (!data.length) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="8"
                        class="text-center py-4 text-muted">

                        Không có mã giảm giá

                    </td>
                </tr>
            `;

            return;
        }

        data.forEach(d => {

            tbody.innerHTML += `

                <tr>

                    <td>${d.id}</td>

                    <td class="fw-semibold">
                        ${d.code}
                    </td>

                    <td>
                        ${
                d.discountType === "PERCENT"
                    ? "Phần trăm"
                    : "Số tiền"
            }
                    </td>

                    <td>
                        ${d.discountValue}
                    </td>

                    <td>
                        ${d.limitNumber}
                    </td>

                    <td>
                        ${d.numberUsed}
                    </td>

                    <td>

                        <span class="badge ${
                d.status === "ACTIVE"
                    ? "bg-success"
                    : "bg-secondary"
            }">

                            ${
                d.status === "ACTIVE"
                    ? "Hoạt động"
                    : "Ngừng hoạt động"
            }

                        </span>

                    </td>

                    <td>

                        <div class="d-flex gap-2 justify-content-center">

                            <!-- EDIT -->
                            <button
                                class="btn btn-sm btn-light border"
                                title="Chỉnh sửa"
                                onclick="DiscountController.openEdit(${d.id})"
                            >
                                <i class="fa fa-edit text-dark"></i>
                            </button>

                            <!-- DELETE -->
                            <button
                                class="btn btn-sm btn-light border"
                                title="Xóa mã giảm giá"
                                onclick="DiscountController.delete(${d.id})"
                            >
                                <i class="fa fa-trash text-dark"></i>
                            </button>

                        </div>

                    </td>

                </tr>
            `;
        });
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {

        const container =
            document.getElementById("pagination");

        if (!container) return;

        let html = `<nav><ul class="pagination">`;

        html += `
            <li class="page-item ${
            currentPage === 0 ? "disabled" : ""
        }">
                <a class="page-link"
                   href="#"
                   data-page="${currentPage - 1}">
                    Trước
                </a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {

            html += `
                <li class="page-item ${
                i === currentPage ? "active" : ""
            }">

                    <a class="page-link"
                       href="#"
                       data-page="${i}">

                        ${i + 1}

                    </a>

                </li>
            `;
        }

        html += `
            <li class="page-item ${
            currentPage === totalPages - 1
                ? "disabled"
                : ""
        }">

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

    openModal: (title) => {

        document.getElementById(
            "discountModalTitle"
        ).innerText = title;

        DiscountView.modal.show();
    },

    closeModal: () =>
        DiscountView.modal.hide(),

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

        document.getElementById("code").value =
            d.code;

        document.getElementById("discountType").value =
            d.discountType;

        document.getElementById("discountValue").value =
            d.discountValue;

        document.getElementById("limitNumber").value =
            d.limitNumber;

        document.getElementById("minOrderValue").value =
            d.minOrderValue;

        document.getElementById("maxDiscount").value =
            d.maxDiscount;

        document.getElementById("limitPerUser").value =
            d.limitPerUser;

        document.getElementById("status").value =
            d.status || "ACTIVE";

        document.getElementById("startDate").value =
            d.startDate?.slice(0,16);

        document.getElementById("endDate").value =
            d.endDate?.slice(0,16);

        document.getElementById("description").value =
            d.description;
    }
};