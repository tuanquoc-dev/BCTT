const HomeView = {

    renderCard: (product) => {

        return `
        <div class="col-lg-2 col-md-3 col-6">

            <div class="card h-100 border-0 shadow-sm product-card">

                <a href="./pages/user/detail.html?slug=${product.slug}"
                   class="text-decoration-none text-dark">

                    <img src="${product.thumbnail || '/assets/no-image.png'}"
                         class="card-img-top p-3"
                         style="height:220px;object-fit:contain;">

                    <div class="card-body">

                        <div class="small text-muted mb-2">
                            ${product.brandName || ""}
                        </div>

                        <h6 class="product-name fw-bold">
                            ${product.name || ""}
                        </h6>

                        <div class="small text-muted mb-2">

                            ${
            [
                product.ram,
                product.color
            ]
                .filter(Boolean)
                .join(" • ")
        }

                        </div>

                        <div class="text-danger fw-bold fs-5">
                            ${HomeView.formatPrice(product.finalPrice)}
                        </div>

                        ${
            product.finalPrice < product.price
                ? `
                                    <div class="small text-muted text-decoration-line-through">
                                        ${HomeView.formatPrice(product.price)}
                                    </div>
                                `
                : ""
        }

                    </div>

                </a>

            </div>

        </div>
    `;
    },

    renderProducts: (products) => {

        return products
            .map(HomeView.renderCard)
            .join("");
    },

    renderBrandCarousel: (brand, products) => {

        return `
        <section class="brand-section">

            <div class="d-flex justify-content-between align-items-center mb-4">

                <div class="d-flex align-items-center gap-3">

                    <h4 class="fw-bold m-0">
                        ${brand.name}
                    </h4>

                </div>

                <a href="#"
                   class="text-success text-decoration-none fw-semibold">

                    Xem tất cả

                </a>

            </div>

            <div id="carousel-brand-${brand.id}"
                 class="carousel slide">

                <div class="carousel-inner">

                    ${HomeView.renderCarouselItems(products)}

                </div>

                <button class="carousel-control-prev"
                        type="button"
                        data-bs-target="#carousel-brand-${brand.id}"
                        data-bs-slide="prev">

                    <span class="carousel-control-prev-icon bg-dark rounded-circle"></span>

                </button>

                <button class="carousel-control-next"
                        type="button"
                        data-bs-target="#carousel-brand-${brand.id}"
                        data-bs-slide="next">

                    <span class="carousel-control-next-icon bg-dark rounded-circle"></span>

                </button>

            </div>

        </section>
    `;
    },

    renderCarouselItems: (products) => {

        let html = "";

        for (let i = 0; i < products.length; i += 5) {

            const chunk = products.slice(i, i + 5);

            html += `
                <div class="carousel-item ${i === 0 ? "active" : ""}">

                    <div class="row g-3">

                        ${chunk.map(HomeView.renderCard).join("")}

                    </div>

                </div>
            `;
        }

        return html;
    },

    formatPrice: (price) => {

        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND"
        }).format(price);
    },

    // ======================================
// SEARCH OVERLAY
// ======================================

    openSearch: () => {

        document
            .getElementById("searchOverlay")
            .classList
            .remove("d-none");

        document.body.style.overflow = "hidden";

        setTimeout(() => {

            document
                .getElementById("searchInput")
                ?.focus();

        }, 200);
    },

    closeSearch: () => {

        document
            .getElementById("searchOverlay")
            .classList
            .add("d-none");

        document.body.style.overflow = "auto";

        document.getElementById("searchResult").innerHTML = "";
    },

    searchProducts: async () => {

        const keyword =
            document.getElementById("searchInput").value;

        const dropdown =
            document.getElementById("searchDropdown");

        // SHOW BLUR KHI FOCUS SEARCH
        HomeView.openSearch();

        if (!keyword.trim()) {

            dropdown.classList.add("d-none");

            return;
        }

        try {

            const res =
                await UserModel.searchProducts({
                    keyword,
                    size: 8
                });

            const products =
                res.data.data.content;

            dropdown.classList.remove("d-none");

            document
                .getElementById("searchOverlay")
                .classList
                .add("show");

            HomeView.renderSearchResult(products);

        } catch (e) {

            console.log(e);
        }
    },

    renderSearchResult: (products) => {

        const container =
            document.getElementById("searchResult");

        container.innerHTML = "";

        if (products.length === 0) {

            container.innerHTML = `
            <div class="text-center py-5 text-muted">
                Không tìm thấy sản phẩm
            </div>
        `;

            return;
        }

        products.forEach(product => {

            container.innerHTML += `

            <div class="col-lg-3 col-md-4 col-6 mb-3">

                <div class="card border-0 shadow-sm h-100 search-product">

                    <a href="./pages/user/detail.html?slug=${product.slug}"
                       class="text-decoration-none text-dark">

                        <img src="${product.thumbnail}"
                             class="card-img-top p-3"
                             style="
                             height:200px;
                             object-fit:contain;
                             ">

                        <div class="card-body">

                            <div class="small text-muted">
                                ${product.brandName}
                            </div>

                            <div class="fw-bold product-name">
                                ${product.name}
                            </div>

                            <div class="small text-muted mb-2">

                                ${product.ram || ""}
                                •
                                ${product.storage || ""}

                            </div>

                            <div class="text-danger fw-bold">

                                ${HomeView.formatPrice(product.finalPrice)}

                            </div>

                            ${
                product.finalPrice < product.price
                    ?
                    `
                                    <div class="small text-decoration-line-through text-muted">
                                        ${HomeView.formatPrice(product.price)}
                                    </div>
                                `
                    :
                    ""
            }

                        </div>

                    </a>

                </div>

            </div>
        `;
        });
    },

    // ======================================
// CATEGORY
// ======================================

    renderCategories: (categories) => {

        const html = categories.map(category => `

        <a href="#"
           class="d-flex align-items-center gap-3 text-decoration-none text-dark mb-3 category-item">

            <span class="fw-semibold">
                ${category.name}
            </span>

        </a>

    `).join("");

        document.getElementById("categoryList").innerHTML = html;

        document.getElementById("categoryGrid").innerHTML = html;
    }

};