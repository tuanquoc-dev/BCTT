const Sidebar = {

    load: async () => {

        // =====================================================
        // BASE PATH HTML
        // =====================================================

        const assetPath = window.location.pathname.includes("/pages/")
            ? "../../"
            : "./";

        // =====================================================
        // ROLE BASE PATH
        // =====================================================

        const path = window.location.pathname;

        let rolePath = "/MobileHub/FE/pages/user";

        if (path.includes("/admin/")) {
            rolePath = "/MobileHub/FE/pages/admin";
        }

        else if (path.includes("/staff/")) {
            rolePath = "/MobileHub/FE/pages/staff";
        }

        // =====================================================
        // LOAD HTML
        // =====================================================

        const res =
            await fetch(assetPath + "components/sidebar.html");

        document.getElementById("sidebar").innerHTML =
            await res.text();

        // =====================================================
        // REPLACE LINK
        // =====================================================

        document.querySelectorAll("[data-sidebar]").forEach(link => {

            const page =
                link.getAttribute("data-sidebar");

            link.href = `${rolePath}/${page}`;
        });

        // =====================================================
        // INIT
        // =====================================================

        await Sidebar.renderUser();

        Sidebar.setActive();

        Sidebar.bindMenuToggle();
    },

    // =====================================================
    // USER
    // =====================================================

    renderUser: async () => {

        try {

            const res = await UserModel.getMe();

            const user = res.data.data;

            const name =
                document.getElementById("sbUsername");

            const avatar =
                document.getElementById("sbAvatar");

            if (name) {
                name.textContent = user.username;
            }

            if (avatar) {
                avatar.src =
                    user.avatar ||
                    "https://i.pravatar.cc/80";
            }

        } catch (e) {

            console.error(
                "Load sidebar user lỗi:",
                e
            );
        }
    },

    // =====================================================
    // ACTIVE MENU
    // =====================================================

    setActive: () => {

        const links =
            document.querySelectorAll(".menu-link");

        const path =
            window.location.pathname;

        links.forEach(link => {

            const href =
                link.getAttribute("href");

            if (!href) return;

            const currentPage =
                path.split("/").pop();

            const linkPage =
                href.split("/").pop();

            if (currentPage === linkPage) {

                link.classList.add("active");

                link.closest(".menu-item")
                    ?.classList.add("active");
            }
        });
    },

    // =====================================================
    // TOGGLE MENU
    // =====================================================

    bindMenuToggle: () => {

        document
            .querySelectorAll(".menu-link")
            .forEach(menu => {

                menu.onclick = () => {

                    const parent =
                        menu.parentElement;

                    document
                        .querySelectorAll(".menu-item")
                        .forEach(item => {

                            if (item !== parent) {
                                item.classList.remove("active");
                            }
                        });

                    parent.classList.toggle("active");
                };
            });
    }
};