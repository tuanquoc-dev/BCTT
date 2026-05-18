const MyOrderView = {

    render(orders) {

        const container =
            document.getElementById("orderContainer");

        const emptyBox =
            document.getElementById("emptyOrder");

        const orderCount =
            document.getElementById("orderCount");

        orderCount.innerText =
            `${orders.length} đơn hàng`;

        if (!orders.length) {

            container.innerHTML = "";

            emptyBox.classList.remove("d-none");

            return;
        }

        emptyBox.classList.add("d-none");

        container.innerHTML = orders.map(order => `

            <div class="card order-card">

                <!-- HEADER -->

                <div class="order-header d-flex justify-content-between align-items-center flex-wrap gap-3">

                    <div>

                        <div class="fw-bold fs-5 mb-1">
                            #${order.code}
                        </div>

                        <div class="text-secondary small">
                            ${formatDate(order.createdAt)}
                        </div>

                    </div>

                    <div class="d-flex align-items-center gap-3">

                        <div class="status-badge ${getStatusClass(order.status)}">
                            ${translateStatus(order.status)}
                        </div>

                        <button
    class="btn btn-outline-danger rounded-3 fw-semibold"

    onclick="
        MyOrderController.showDetail(
            '${order.code}'
        )
    "
>

    Chi tiết

</button>

<button
    class="btn btn-danger rounded-3 fw-semibold"

    onclick="
        MyOrderController.openCancelModal(
            '${order.code}'
        )
    "
>
    Hủy đơn
</button>
                    </div>

                </div>

                <!-- ITEMS -->

                ${order.items.map(item => `

                    <div class="order-item d-flex gap-3 flex-wrap">

                        <img src="${item.thumbnail}"
                             class="product-image">

                        <div class="flex-grow-1">

                            <a href="/MobileHub/FE/pages/user/detail.html?slug=${item.slug || ''}"
                               class="product-name">

                                ${item.productName}

                            </a>

                            <div class="variant-badge">
                                ${item.color || ''}
                                ${item.ram ? ` - ${item.ram}` : ''}
                            </div>

                            <div class="mt-2 text-secondary">
                                Số lượng: ${item.quantity}
                            </div>

                        </div>

                        <div class="text-end">

                            <div class="price">
                                ${formatPrice(item.totalPrice)}
                            </div>
                            
                                ${order.status === "COMPLETED" ? `

        <button
            class="btn btn-sm btn-outline-danger"

            onclick="
                MyOrderController.openReviewModal(
                     ${item.productId},
                    ${item.id}
                )
            "
        >
            Đánh giá
        </button>

    ` : ""}


                        </div>

                    </div>

                `).join("")}

                <!-- FOOTER -->

                <div class="p-4 bg-white d-flex justify-content-between align-items-center flex-wrap gap-3">

                    <div>

                        <div class="text-secondary mb-1">
                            Người nhận: ${order.receiverName}
                        </div>

                        <div class="text-secondary">
                            ${order.phone}
                        </div>

                    </div>

                    <div class="text-end">

                        <div class="text-secondary mb-1">
                            Tổng thanh toán
                        </div>

                        <div class="price fs-4">
                            ${formatPrice(order.totalPrice)}
                        </div>

                    </div>

                </div>

            </div>

        `).join("");
    }
};