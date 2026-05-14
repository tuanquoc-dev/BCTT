const MyOrderController = {

    async load() {

        try {

            showLoading();

            if (!Auth.isLoggedIn()) {

                window.location.href =
                    "/MobileHub/FE/pages/auth/login.html";

                return;
            }

            const res =
                await UserModel.getMyOrders();

            const orders =
                res.data.data || [];

            MyOrderView.render(orders);

            const params =
                new URLSearchParams(window.location.search);

            const code =
                params.get("code");

            if (code) {

                MyOrderController.showDetail(code);
            }

        } catch (e) {

            console.log(e);

            showToast(
                "Không thể tải đơn hàng",
                "danger"
            );

        } finally {

            hideLoading();
        }
    },

    async showDetail(code) {

        try {

            const modal =
                new bootstrap.Modal(
                    document.getElementById(
                        "orderDetailModal"
                    )
                );

            modal.show();

            const content =
                document.getElementById(
                    "orderDetailContent"
                );

            content.innerHTML = `

            <div class="text-center py-5">

                <div class="spinner-border text-danger"></div>

            </div>

        `;

            const res =
                await UserModel.getOrderDetail(code);

            const order =
                res.data.data;

            content.innerHTML = `

            <!-- INFO -->
            <div class="row g-4 mb-4">

                <div class="col-md-6">

                    <div class="border rounded-4 p-4 h-100">

                        <h6 class="fw-bold mb-3">
                            Thông tin đơn hàng
                        </h6>

                        <div class="mb-2">
                            Mã đơn:
                            <strong>#${order.code}</strong>
                        </div>

                        <div class="mb-2">
                            Ngày đặt:
                            ${formatDate(order.createdAt)}
                        </div>

                        <div class="mb-2">
                            Trạng thái:
                            <span class="
                                status-badge
                                ${getStatusClass(order.status)}
                            ">
                                ${translateStatus(order.status)}
                            </span>
                        </div>

                    </div>

                </div>

                <div class="col-md-6">

                    <div class="border rounded-4 p-4 h-100">

                        <h6 class="fw-bold mb-3">
                            Thông tin nhận hàng
                        </h6>

                        <div class="mb-2">
                            ${order.receiverName}
                        </div>

                        <div class="mb-2">
                            ${order.phone}
                        </div>

                        <div>
                            ${order.addressDetail}
                        </div>

                    </div>

                </div>

            </div>

            <!-- PRODUCTS -->
            <div class="border rounded-4 overflow-hidden">

                ${order.items.map(item => `

                    <div class="
                        d-flex
                        gap-3
                        p-4
                        border-bottom
                    ">

                        <img
                            src="${item.thumbnail}"
                            style="
                                width:90px;
                                height:90px;
                                object-fit:cover;
                                border-radius:12px;
                            "
                        >

                        <div class="flex-grow-1">

                            <div class="fw-semibold mb-2">
                                ${item.productName}
                            </div>

                            <div class="text-secondary small mb-2">

                                ${item.color || ""}

                                ${item.ram
                ? ` - ${item.ram}`
                : ""
            }

                            </div>

                            <div>
                                SL: ${item.quantity}
                            </div>

                        </div>

                        <div class="fw-bold text-danger">

                            ${formatPrice(item.totalPrice)}

                        </div>

                    </div>

                `).join("")}

            </div>

            <!-- TOTAL -->
            <div class="text-end mt-4">

                <div class="text-secondary mb-2">
                    Tổng thanh toán
                </div>

                <div class="
                    text-danger
                    fw-bold
                    fs-3
                ">

                    ${formatPrice(order.totalPrice)}

                </div>

            </div>

        `;

        } catch (e) {

            console.log(e);

            showToast(
                "Không thể tải chi tiết đơn hàng",
                "danger"
            );
        }
    },

    openCancelModal(code) {

        const modal =
            new bootstrap.Modal(
                document.getElementById(
                    "cancelOrderModal"
                )
            );

        modal.show();

        document
            .getElementById("confirmCancelBtn")
            .onclick = async () => {

            const reason =
                document.getElementById(
                    "cancelReason"
                ).value;

            if (!reason.trim()) {

                showToast(
                    "Vui lòng nhập lý do hủy",
                    "danger"
                );

                return;
            }

            try {

                showLoading();

                await UserModel.cancelMyOrder(
                    code,
                    {
                        reason: reason
                    }
                );

                modal.hide();

                showToast(
                    "Đã hủy đơn hàng"
                );

                await MyOrderController.load();

            } catch (e) {

                console.log(e);

                showToast(
                    "Không thể hủy đơn",
                    "danger"
                );

            } finally {

                hideLoading();
            }
        };
    }
};

// =====================================
// HELPERS
// =====================================

function translateStatus(status) {

    switch (status) {

        case "PENDING":
            return "Chờ xác nhận";

        case "CONFIRMED":
            return "Đã xác nhận";

        case "SHIPPING":
            return "Đang giao";

        case "COMPLETED":
            return "Hoàn thành";

        case "CANCELLED":
            return "Đã hủy";

        default:
            return status;
    }
}

function getStatusClass(status) {

    switch (status) {

        case "PENDING":
            return "status-pending";

        case "CONFIRMED":
            return "status-confirmed";

        case "SHIPPING":
            return "status-shipping";

        case "COMPLETED":
            return "status-completed";

        case "CANCELLED":
            return "status-cancelled";

        default:
            return "status-pending";
    }
}

function formatDate(date) {

    return new Date(date).toLocaleString(
        "vi-VN"
    );
}


document.addEventListener(
    "DOMContentLoaded",
    () => {
        MyOrderController.load();
    }
);