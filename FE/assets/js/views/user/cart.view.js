const CartView = {

    render(cart) {

        const container =
            document.getElementById("cartContainer");

        const emptyCart =
            document.getElementById("emptyCart");

        const cartCountText =
            document.getElementById("cartCountText");

        if (cartCountText) {

            cartCountText.textContent =
                `${cart.length} sản phẩm`;
        }

        // EMPTY
        if (!cart.length) {

            container.classList.add("d-none");

            emptyCart.classList.remove("d-none");

            document.getElementById("cartSummary")
                .innerHTML = "";

            return;
        }

        container.classList.remove("d-none");

        emptyCart.classList.add("d-none");

        container.innerHTML = cart.map(item => `

        <div class="cart-item d-flex gap-3">

            <div>

                <img
                    src="${item.thumbnail}"
                    class="product-image"
                >

            </div>

            <div class="flex-grow-1">

                <div class="d-flex justify-content-between">

                    <div>

                        <a href="/MobileHub/FE/pages/user/detail.html?slug=${item.slug}"
                           class="product-name">

                            ${item.name}

                        </a>

                        <div class="variant-badge">

                            ${item.color || ""}
                            ${item.ram ? ` - ${item.ram}` : ""}

                        </div>

                    </div>

                    <button
                        class="btn btn-sm btn-light rounded-circle"

                        onclick="
                            CartController.remove(
                                ${item.productId}
                            )
                        "
                    >

                        <i class="fa-solid fa-trash text-danger"></i>

                    </button>

                </div>

                <div class="mt-3 d-flex justify-content-between align-items-center flex-wrap gap-3">

                    <!-- PRICE -->
                    <div>

                        <div class="price-final">

                            ${formatPrice(item.price)}

                        </div>

                    </div>

                    <!-- QUANTITY -->
                    <div class="input-group"
                         style="width:130px;">

                        <button
                               class="btn btn-outline-secondary"

    ${item.quantity <= 1 ? "disabled" : ""}

    onclick="
        CartController.changeQty(
            ${item.productId},
            ${item.quantity - 1}
        )
    "
>
    -
                        </button>

                        <input type="text"
                               class="form-control text-center"
                               value="${item.quantity}">

                        <button
                            class="btn btn-outline-secondary"

                            onclick="
                                CartController.changeQty(
                                    ${item.productId},
                                    ${item.quantity + 1}
                                )
                            "
                        >
                            +
                        </button>

                    </div>

                    <!-- TOTAL -->
                    <div class="text-end">

                        <div class="fw-bold text-danger fs-5">

                            ${formatPrice(
            item.price * item.quantity
        )}

                        </div>

                    </div>

                </div>

            </div>

        </div>

    `).join("");

        this.renderSummary(cart);
    },

    renderSummary(cart) {

        const subtotal = cart.reduce(

            (sum, item) =>

                sum + item.price * item.quantity,

            0
        );

        const shippingFee = 30000;

        const total = subtotal + shippingFee;

        document.getElementById("cartSummary")
            .innerHTML = `

        <div class="card border-0 shadow-sm rounded-4">

            <div class="card-body">

                <h5 class="fw-bold mb-4">
                    Thông tin đặt hàng
                </h5>

                <!-- NAME -->
                <div class="mb-3">

                    <label class="form-label">
                        Người nhận
                    </label>

                    <input
                        type="text"
                        id="receiverName"
                        class="form-control"
                        placeholder="Nhập tên người nhận"
                    >

                </div>

                <!-- PHONE -->
                <div class="mb-3">

                    <label class="form-label">
                        Số điện thoại
                    </label>

                    <input
                        type="text"
                        id="phone"
                        class="form-control"
                        placeholder="Nhập số điện thoại"
                    >

                </div>

                <!-- ADDRESS -->
                <div class="mb-3">

                    <label class="form-label">
                        Địa chỉ chi tiết
                    </label>

                    <textarea
                        id="addressDetail"
                        class="form-control"
                        rows="3"
                        placeholder="Số nhà, tên đường..."
                    ></textarea>

                </div>

                <!-- LOCATION -->
                <div class="mb-3">

                    <select
                        id="province"
                        class="form-select mb-2"
                    >
                        <option value="">
                            Chọn tỉnh/thành
                        </option>
                    </select>

                    <select
                        id="district"
                        class="form-select mb-2"
                    >
                        <option value="">
                            Chọn quận/huyện
                        </option>
                    </select>

                    <select
                        id="ward"
                        class="form-select"
                    >
                        <option value="">
                            Chọn phường/xã
                        </option>
                    </select>

                </div>

                <!-- NOTE -->
                <div class="mb-4">

                    <label class="form-label">
                        Ghi chú
                    </label>

                    <textarea
                        id="note"
                        class="form-control"
                        rows="2"
                        placeholder="Ghi chú thêm..."
                    ></textarea>

                </div>

                <!-- SUMMARY -->

                <div class="d-flex justify-content-between mb-2">

                    <span>Tạm tính</span>

                    <strong>
                        ${formatPrice(subtotal)}
                    </strong>

                </div>

                <div class="d-flex justify-content-between mb-2">

                    <span>Phí ship</span>

                    <strong>
                        ${formatPrice(shippingFee)}
                    </strong>

                </div>

                <hr>

                <div class="d-flex justify-content-between mb-4">

                    <span class="fw-bold">
                        Tổng tiền
                    </span>

                    <span class="fw-bold text-danger fs-5">

                        ${formatPrice(total)}

                    </span>

                </div>

                <button
                    class="btn btn-danger w-100 py-3 fw-bold rounded-3"

                    onclick="CartController.submitOrder()"
                >

                    Đặt hàng

                </button>

            </div>

        </div>
    `;
    }
};