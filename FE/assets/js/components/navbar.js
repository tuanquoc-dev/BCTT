const Navbar = {

    load: async () => {

        const basePath =
            window.location.pathname.includes("/pages/")
                ? "../../"
                : "./";

        // ================= ROLE PATH =================

        const path = window.location.pathname;

        let rolePath =
            "/MobileHub/FE/pages/user";

        if (path.includes("/admin/")) {
            rolePath =
                "/MobileHub/FE/pages/admin";
        }

        else if (path.includes("/staff/")) {
            rolePath =
                "/MobileHub/FE/pages/staff";
        }

        // ================= LOAD HTML =================

        const res =
            await fetch(basePath + "components/navbar.html");

        document.getElementById("navbar").innerHTML =
            await res.text();

        // ================= SET PROFILE LINK =================

        const profileLink =
            document.getElementById("profileLink");

        if (profileLink) {
            profileLink.href =
                `${rolePath}/profile.html`;
        }

        // ================= INIT =================

        setTimeout(() => {

            Navbar.bindEvents();

            Navbar.renderUser();

        }, 0);
    },

    bindEvents: () => {

        const toggle =
            document.getElementById("menu_toggle");

        if (toggle) {

            toggle.addEventListener("click", () => {

                document.body.classList.toggle("nav-sm");
            });
        }

        const logoutBtn =
            document.getElementById("logoutBtn");

        if (logoutBtn) {

            logoutBtn.addEventListener("click", () => {

                Auth.logout();
            });
        }
    },

    renderUser: async () => {

        try {

            const res =
                await UserModel.getMe();

            const user =
                res.data.data;

            document.getElementById("navUsername")
                .textContent = user.username;

            document.getElementById("navAvatar").src =
                user.avatar ||
                "https://i.pravatar.cc/40";

        } catch (e) {

            console.error(
                "Load user lỗi:",
                e
            );
        }
    }
};