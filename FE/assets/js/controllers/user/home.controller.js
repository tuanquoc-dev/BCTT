document.addEventListener("DOMContentLoaded", async () => {

    await loadFeaturedProducts();

    await loadBrandSections();

    await loadCategories();

    initCategoryDropdown();

    await loadBanners();

});


// ======================================
// FEATURED PRODUCTS
// ======================================

async function loadFeaturedProducts() {

    try {

        showLoading();

        const res =
            await UserModel.searchProducts({
                sort: "newest",
                size: 12
            });

        document.getElementById("productList")
            .innerHTML =
            HomeView.renderProducts(
                res.data.data.content
            );

    } catch (err) {

        console.log(err);

    } finally {

        hideLoading();
    }
}


// ======================================
// BRAND CAROUSEL
// ======================================

async function loadBrandSections() {

    try {

        const brandRes =
            await UserModel.getBrands();

        const brands =
            brandRes.data.data;

        const container =
            document.getElementById("brandSections");

        container.innerHTML = "";

        for (const brand of brands) {

            const res =
                await UserModel.searchProducts({
                    brandId: brand.id,
                    size: 10
                });

            const products =
                res.data.data.content;

            // bỏ qua brand không có product
            if (products.length === 0) continue;

            container.innerHTML +=
                HomeView.renderBrandCarousel(
                    brand,
                    products
                );
        }

    } catch (e) {

        console.log(e);
    }
}

// ======================================
// LOAD CATEGORY
// ======================================

async function loadCategories() {

    try {

        const res =
            await UserModel.getCategories();

        HomeView.renderCategories(
            res.data.data
        );

    } catch (e) {

        console.log(e);
    }
}

// ======================================
// SEARCH EVENTS
// ======================================

function initSearchEvents() {

    const searchInput =
        document.getElementById("searchInput");

    const searchDropdown =
        document.getElementById("searchDropdown");

    const searchOverlay =
        document.getElementById("searchOverlay");

    const wrapper =
        document.querySelector(".header-search-wrapper");

    if (!searchInput) return;

    let timeout;

    // ======================================
    // FOCUS SEARCH
    // ======================================

    searchInput.addEventListener("focus", () => {

        HomeView.openSearch();

    });

    // ======================================
    // REALTIME SEARCH
    // ======================================

    searchInput.addEventListener("input", () => {

        clearTimeout(timeout);

        timeout = setTimeout(() => {

            HomeView.searchProducts();

        }, 400);

    });

    // ======================================
    // CLICK OUTSIDE
    // ======================================

    document.addEventListener("click", (e) => {

        const wrapper =
            document.querySelector(".header-search-wrapper");

        if (!wrapper.contains(e.target)) {

            document
                .getElementById("searchDropdown")
                .classList
                .add("d-none");

            document
                .getElementById("searchOverlay")
                .classList
                .remove("show");
        }
    });

    // ======================================
    // CLICK DROPDOWN KHÔNG ĐÓNG
    // ======================================

    searchDropdown.addEventListener("click", (e) => {

        e.stopPropagation();

    });

    // ======================================
    // CLICK INPUT KHÔNG ĐÓNG
    // ======================================

    searchInput.addEventListener("click", (e) => {

        e.stopPropagation();

    });

    // ======================================
    // CLICK OVERLAY
    // ======================================

    searchOverlay.addEventListener("click", () => {

        HomeView.closeSearch();

    });

    // ======================================
    // ESC CLOSE
    // ======================================

    document.addEventListener("keydown", (e) => {

        if (e.key === "Escape") {

            HomeView.closeSearch();

        }

    });
}

function initCategoryDropdown() {

    const btn =
        document.getElementById("categoryBtn");

    const dropdown =
        document.getElementById("categoryDropdown");

    const overlay =
        document.getElementById("searchOverlay");

    if (!btn || !dropdown) return;

    // OPEN
    btn.addEventListener("click", (e) => {

        e.stopPropagation();

        dropdown.classList.toggle("d-none");

        overlay.classList.toggle("show");
    });

    // CLICK INSIDE
    dropdown.addEventListener("click", (e) => {

        e.stopPropagation();
    });

    // CLICK OUTSIDE
    document.addEventListener("click", () => {

        dropdown.classList.add("d-none");

        overlay.classList.remove("show");
    });
}

// ================= BANNER =================

async function loadBanners() {

    try {

        // MAIN
        const mainRes =
            await PublicBannerModel.getBanners({

                position: "HOME_MAIN",

                status: 1,

                size: 5

            });

        // SIDE
        const sideRes =
            await PublicBannerModel.getBanners({

                position: "HOME_SIDE",

                status: 1,

                size: 2

            });

        HomeView.renderMainBanners(
            mainRes.data.data.content
        );

        HomeView.renderSideBanners(
            sideRes.data.data.content
        );

    } catch (err) {

        console.error(err);

    }

}