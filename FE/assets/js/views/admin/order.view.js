const OrderView = {


    formatPrice(value) {

        return new Intl.NumberFormat(
            "vi-VN",
            {
                style: "currency",
                currency: "VND",
                maximumFractionDigits: 0
            }
        ).format(Number(value || 0));
    },

    renderTable(orders = []) {

        const tbody =
            document.getElementById("orderTableBody");

        if (!tbody) return;

        if (orders.length === 0) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        No orders found
                    </td>
                </tr>
            `;

            return;
        }

        tbody.innerHTML = orders.map(order => `

            <tr>

                <td>${order.id}</td>

                <td>
                    <span class="fw-semibold">
                        ${order.code}
                    </span>
                </td>

                <td>${order.receiverName}</td>

                <td class="text-danger fw-semibold">
                    ${this.formatPrice(order.totalPrice)}
                </td>

                <td>
                    ${this.renderStatus(order.status)}
                </td>

                <td>
                    ${dayjs(order.createdAt)
            .format("DD/MM/YYYY HH:mm")}
                </td>

                <td>

                    <div class="d-flex gap-2 justify-content-center flex-wrap">

                        <button
                            class="btn btn-dark btn-sm"
                            onclick="OrderController.openDetail(${order.id})"
                        >
                            <i class="fa fa-eye"></i>
                        </button>

                        ${this.renderTableActions(order)}

                    </div>

                </td>

            </tr>

        `).join("");
    },

    renderTableActions(order) {

        if (order.status === "PENDING") {

            return `
                <button
                    class="btn btn-success btn-sm"
                    onclick="OrderController.confirm(${order.id})"
                >
                    <i class="fa fa-check"></i>
                </button>

                <button
                    class="btn btn-danger btn-sm"
                    onclick="OrderController.reject(${order.id})"
                >
                    <i class="fa fa-xmark"></i>
                </button>
            `;
        }

        if (order.status === "CONFIRMED") {

            return `
                <button
                    class="btn btn-primary btn-sm"
                    onclick="OrderController.shipping(${order.id})"
                >
                    <i class="fa fa-truck"></i>
                </button>
            `;
        }

        if (order.status === "SHIPPING") {

            return `
                <button
                    class="btn btn-success btn-sm"
                    onclick="OrderController.complete(${order.id})"
                >
                    <i class="fa fa-box"></i>
                </button>
            `;
        }

        return "";
    },

    renderStatus(status) {

        let color = "secondary";

        switch (status) {

            case "PENDING":
                color = "warning";
                break;

            case "CONFIRMED":
                color = "info";
                break;

            case "SHIPPING":
                color = "primary";
                break;

            case "COMPLETED":
                color = "success";
                break;

            case "REJECTED":
                color = "danger";
                break;

            case "CANCELLED":
                color = "dark";
                break;
        }

        return `
            <span class="badge bg-${color}">
                ${status}
            </span>
        `;
    },

    renderPagination(totalPages, currentPage) {

        const container =
            document.getElementById("pagination");

        if (!container) return;

        let html = `
            <nav>
                <ul class="pagination justify-content-center">
        `;

        for (let i = 0; i < totalPages; i++) {

            html += `
                <li class="page-item
                    ${i === currentPage ? "active" : ""}
                ">
                    <a
                        href="#"
                        class="page-link"
                        data-page="${i}"
                    >
                        ${i + 1}
                    </a>
                </li>
            `;
        }

        html += `
                </ul>
            </nav>
        `;

        container.innerHTML = html;
    },

    renderDetail(order) {

        document.getElementById("detailCode").innerText =
            order.code;

        document.getElementById("detailReceiver").innerText =
            order.receiverName;

        document.getElementById("detailPhone").innerText =
            order.phone;

        document.getElementById("detailAddress").innerText =
            `
            ${order.addressDetail},
            ${order.wardName},
            ${order.districtName},
            ${order.provinceName}
            `;

        document.getElementById("detailPaymentMethod").innerText =
            order.paymentMethod;

        document.getElementById("detailPaymentStatus").innerHTML =
            order.paymentStatus;

        document.getElementById("detailStatus").innerHTML =
            this.renderStatus(order.status);

        document.getElementById("detailCreatedAt").innerText =
            dayjs(order.createdAt)
                .format("DD/MM/YYYY HH:mm");

        document.getElementById("detailSubtotal").innerText =
            this.formatPrice(order.subtotal);

        document.getElementById("detailShippingFee").innerText =
            this.formatPrice(order.shippingFee);

        document.getElementById("detailDiscount").innerText =
            this.formatPrice(order.discountAmount);

        document.getElementById("detailTotal").innerText =
            this.formatPrice(order.totalPrice);

        // ITEMS
        document.getElementById("orderItems").innerHTML =

            order.items.map(item => `

                <div class="d-flex gap-3 border-bottom py-3">

                    <img
                        src="${item.thumbnail}"
                        width="80"
                        class="rounded"
                    >

                    <div class="flex-grow-1">

                        <div class="fw-semibold">
                            ${item.productName}
                        </div>

                        <div class="small text-muted">
                            RAM: ${item.ram || "-"}
                        </div>

                        <div class="small text-muted">
                            Color: ${item.color || "-"}
                        </div>

                        <div class="small">
                            Qty: ${item.quantity}
                        </div>

                    </div>

                    <div class="fw-bold text-danger">
                        ${Number(item.totalPrice)
                .toLocaleString()} đ
                    </div>

                </div>

            `).join("");

        this.renderActions(order);
    },

    renderActions(order) {

        const div =
            document.getElementById("orderActions");

        if (!div) return;

        let html = "";

        if (order.status === "PENDING") {

            html += `
                <button
                    class="btn btn-success"
                    onclick="OrderController.confirm(${order.id})"
                >
                    Confirm Order
                </button>

                <button
                    class="btn btn-danger"
                    onclick="OrderController.reject(${order.id})"
                >
                    Reject Order
                </button>
            `;
        }

        if (order.status === "CONFIRMED") {

            html += `
                <button
                    class="btn btn-primary"
                    onclick="OrderController.shipping(${order.id})"
                >
                    Shipping
                </button>
            `;
        }

        if (order.status === "SHIPPING") {

            html += `
                <button
                    class="btn btn-success"
                    onclick="OrderController.complete(${order.id})"
                >
                    Complete
                </button>
            `;
        }

        if (
            order.status !== "COMPLETED"
            &&
            order.status !== "CANCELLED"
            &&
            order.status !== "REJECTED"
        ) {

            html += `
                <button
                    class="btn btn-dark"
                    onclick="OrderController.cancel(${order.id})"
                >
                    Cancel
                </button>
            `;
        }

        div.innerHTML = html;
    }
};