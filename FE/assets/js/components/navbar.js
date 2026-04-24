const Navbar = {

    load: async () => {
        const basePath = window.location.pathname.includes("/pages/")
            ? "../../"
            : "./";

        const res = await fetch(basePath + "components/navbar.html");
        document.getElementById("navbar").innerHTML = await res.text();

        // 🔥 Đảm bảo DOM đã render xong
        setTimeout(() => {
            Navbar.bindEvents();
            Navbar.renderUser();
        }, 0);
    },

    bindEvents: () => {

        const toggle = document.getElementById("menu_toggle");

        if (toggle) {
            toggle.addEventListener("click", () => {
                document.body.classList.toggle("nav-sm");
            });
        }

        const logoutBtn = document.getElementById("logoutBtn");

        if (logoutBtn) {
            logoutBtn.addEventListener("click", () => {
                Auth.logout();
            });
        }
    },

    renderUser: async () => {
        try {
            const res = await UserModel.getMe();
            const user = res.data.data;

            document.getElementById("navUsername").textContent = user.username;

            document.getElementById("navAvatar").src =
                user.avatar || "https://i.pravatar.cc/40";

        } catch (e) {
            console.error("Load user lỗi:", e);
        }
    }
};