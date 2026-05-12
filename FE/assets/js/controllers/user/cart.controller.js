const CartController = {

    async load() {

        const cart =
            CartStorage.getCart();

        CartView.render(cart);

        if (!LocationService.data.length) {

            await LocationService.init();
        }

        this.loadProvinces();

        this.bindLocationEvents();
    },

    remove(productId) {

        CartStorage.removeItem(productId);

        this.load();
    },

    changeQty(productId, quantity) {

        if (quantity < 1) return;

        CartStorage.updateQuantity(
            productId,
            Number(quantity)
        );

        CartView.render(
            CartStorage.getCart()
        );

        this.loadProvinces();

        this.bindLocationEvents();
    },

    submitOrder: async function () {

        try {

            showLoading();

            const cart =
                CartStorage.getCart();

            if (!cart.length) {

                toastError("Giỏ hàng trống");

                return;
            }

            const body = {

                receiverName:
                document.getElementById("receiverName").value,

                phone:
                document.getElementById("phone").value,

                provinceId:
                document.getElementById("province").value,

                districtId:
                document.getElementById("district").value,

                wardId:
                document.getElementById("ward").value,

                addressDetail:
                document.getElementById("addressDetail").value,

                note:
                document.getElementById("note").value,

                paymentMethod: "COD",

                items: cart.map(item => ({

                    productId: item.productId,

                    quantity: item.quantity
                }))
            };

            const res =
                await UserModel.createOrder(body);

            toastSuccess(
                res.data.message
            );

            CartStorage.clear();

            setTimeout(() => {

                window.location.href =
                    "/MobileHub/FE/pages/user/my-orders.html";

            }, 1000);

        } catch (e) {

            console.log(e);

            toastError(
                e.response?.data?.message ||
                "Đặt hàng thất bại"
            );

        } finally {

            hideLoading();
        }
    },

    // =====================================
    // LOCATION
    // =====================================

    loadProvinces() {

        const provinces =
            LocationService.getProvinces();

        const provinceSelect =
            document.getElementById("province");

        if (!provinceSelect) return;

        provinceSelect.innerHTML = `

        <option value="">
            Chọn tỉnh/thành
        </option>

    `;

        provinces.forEach(province => {

            provinceSelect.innerHTML += `

            <option value="${province.code}">
                ${province.name}
            </option>

        `;
        });
    },

    loadDistricts(provinceCode) {

        const districts =
            LocationService.getDistricts(provinceCode);


        const districtSelect =
            document.getElementById("district");

        districtSelect.innerHTML = `

        <option value="">
            Chọn quận/huyện
        </option>

    `;

        districts.forEach(district => {

            districtSelect.innerHTML += `

            <option value="${district.code}">
                ${district.name}
            </option>

        `;
        });
    },

    loadWards(provinceCode, districtCode) {

        const wards =
            LocationService.getWards(
                provinceCode,
                districtCode
            );

        const wardSelect =
            document.getElementById("ward");

        wardSelect.innerHTML = `

        <option value="">
            Chọn phường/xã
        </option>

    `;

        wards.forEach(ward => {

            wardSelect.innerHTML += `

            <option value="${ward.code}">
                ${ward.name}
            </option>

        `;
        });
    },

    bindLocationEvents() {

        const province =
            document.getElementById("province");

        const district =
            document.getElementById("district");

        if (!province || !district) return;

        province.onchange = (e) => {

            const provinceCode = e.target.value;


            this.loadDistricts(provinceCode);

            document.getElementById("ward").innerHTML = `
            <option value="">
                Chọn phường/xã
            </option>
        `;
        };

        district.onchange = (e) => {

            const districtCode = e.target.value;
            

            this.loadWards(
                province.value,
                districtCode
            );
        };
    }
};

document.addEventListener("DOMContentLoaded", () => {

    CartController.load();
});