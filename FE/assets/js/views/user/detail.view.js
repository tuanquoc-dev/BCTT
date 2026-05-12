const DetailView = {

    render: (product) => {

        // =========================================
        // BASIC
        // =========================================

        document.getElementById("productName")
            .textContent = product.name;

        document.getElementById("productRating")
            .textContent = product.rating || 0;

        document.getElementById("soldQuantity")
            .textContent = product.soldQuantity || 0;

        document.getElementById("productRam")
            .textContent = product.ram || "Đang cập nhật";

        document.getElementById("productColor")
            .textContent = product.color || "Đang cập nhật";

        document.getElementById("brandName")
            .textContent = product.brandName || "";

        document.getElementById("categoryName")
            .textContent = product.categoryName || "";

        document.getElementById("productStock")
            .textContent = product.stock || 0;

        document.getElementById("productDescription")
            .innerHTML =
            product.description || "Đang cập nhật";

        // =========================================
        // PRICE
        // =========================================

        document.getElementById("finalPrice")
            .textContent =
            DetailView.formatPrice(product.finalPrice);

        const priceElement =
            document.getElementById("price");

        if (product.finalPrice < product.price) {

            priceElement.textContent =
                DetailView.formatPrice(product.price);

        } else {

            priceElement.textContent = "";
        }

        // =========================================
        // IMAGES
        // =========================================

        DetailView.renderImages(product);

        DetailView.renderVariants(product);

        DetailView.bindAddToCart(product);
    },

    // =========================================
    // CAROUSEL IMAGES
    // =========================================

    renderImages: (product) => {

        const images = [];

        // thumbnail
        if (product.thumbnail) {
            images.push(product.thumbnail);
        }

        // gallery
        if (product.images?.length) {

            product.images.forEach(img => {

                images.push(img.imgUrl);

            });
        }

        // fallback
        if (images.length === 0) {

            images.push("/assets/no-image.png");
        }

        // =====================================
        // MAIN CAROUSEL
        // =====================================

        const carousel =
            document.getElementById("carouselImages");

        carousel.innerHTML = images.map((img, index) => `

        <div class="carousel-item ${index === 0 ? "active" : ""}">

            <img src="${img}"
                 class="d-block w-100 rounded-4 bg-white border"
                 style="
                    height:500px;
                    object-fit:contain;
                 ">

        </div>

    `).join("");

        // =====================================
        // THUMBNAILS
        // =====================================

        const thumbs =
            document.getElementById("carouselThumbs");

        thumbs.innerHTML = images.map((img, index) => `

        <img
            src="${img}"

            class="
                border
                rounded-3
                thumb-image
            "

            style="
                width:80px;
                height:80px;
                object-fit:cover;
                cursor:pointer;
            "

            data-bs-target="#productCarousel"
            data-bs-slide-to="${index}"
        >

    `).join("");
    },

    renderVariants: (product) => {

        if (!product.variants?.length) return;

        const html = product.variants.map(v => `

        <a href="?slug=${v.slug}"
           class="
                btn
                ${v.slug === product.slug
            ? "btn-danger"
            : "btn-outline-secondary"}
           ">

            ${v.color || ""}
            ${v.ram ? ` - ${v.ram}` : ""}

        </a>

    `).join("");

        document.getElementById("variantColors").innerHTML = html;
    },

    bindAddToCart(product) {

        const btn =
            document.getElementById("addToCartBtn");

        if (!btn) return;

        btn.onclick = () => {

            CartStorage.addItem(product);

            setTimeout(() => {

                window.location.href =
                    "/MobileHub/FE/pages/user/cart.html";

            }, 500);
        };
    },

    // =========================================
    // FORMAT PRICE
    // =========================================

    formatPrice: (price) => {

        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND"
        }).format(price);
    }

};