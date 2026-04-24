const Sidebar = {

    load: async () => {
        const basePath = window.location.pathname.includes("/pages/")
            ? "../../"
            : "./";

        const res = await fetch(basePath + "components/sidebar.html");
        document.getElementById("sidebar").innerHTML = await res.text();

        Sidebar.renderUser();
        Sidebar.setActive();
        Sidebar.bindMenuToggle(); // ✅ QUAN TRỌNG
    },

    renderUser: async () => {
        try {
            const res = await UserModel.getMe();
            const user = res.data.data;

            const name = document.getElementById("sbUsername");
            const avatar = document.getElementById("sbAvatar");

            if (name) name.textContent = user.username;
            if (avatar) avatar.src = user.avatar || "https://i.pravatar.cc/80";

        } catch (e) {
            console.error("Load sidebar user lỗi:", e);
        }
    },

    // ✅ FIX ACTIVE MENU
    setActive: () => {
        const links = document.querySelectorAll(".child_menu a");
        const path = window.location.pathname;

        links.forEach(link => {
            const href = link.getAttribute("href");

            if (href && path.includes(href)) {
                link.closest("li").classList.add("active"); // highlight item
                link.closest(".menu-item")?.classList.add("active"); // mở menu cha
            }
        });
    },

    // ✅ TOGGLE MENU
    bindMenuToggle: () => {
        document.querySelectorAll(".menu-link").forEach(menu => {

            menu.onclick = () => {

                const parent = menu.parentElement;

                // 👉 chỉ mở 1 menu
                document.querySelectorAll(".menu-item").forEach(item => {
                    if (item !== parent) {
                        item.classList.remove("active");
                    }
                });

                parent.classList.toggle("active");
            };
        });
    }

};