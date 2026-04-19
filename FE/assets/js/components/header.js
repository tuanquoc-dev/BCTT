const Header = (() => {

    const load = async () => {
        const res = await fetch("/MobileHub/FE/components/header.html");
        const html = await res.text();

        const container = document.getElementById("header");

        if (!container) return;

        container.innerHTML = html;

        // delay 1 tick để DOM update
        setTimeout(() => {
            bindEvents();
            renderAuth();
        }, 0);
    };

    // 🔥 bind event
    const bindEvents = () => {
        const logoutBtn = document.getElementById("logoutBtn");

        if (logoutBtn) {
            logoutBtn.addEventListener("click", () => {
                Auth.logout();
            });
        }
    };

    // 🔥 render UI login/logout
    const renderAuth = () => {
        const token = Auth.getToken();

        const loginLink = document.getElementById("loginLink");
        const userDropdown = document.getElementById("userDropdown");

        if (!loginLink || !userDropdown) return;

        if (token) {
            loginLink.classList.add("d-none");
            userDropdown.classList.remove("d-none");
        } else {
            loginLink.classList.remove("d-none");
            userDropdown.classList.add("d-none");
        }
    };

    return {
        load,
        renderAuth
    };

})();