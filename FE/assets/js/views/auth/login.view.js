const LoginView = {

    getFormData: () => ({
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value.trim()
    }),

    showSuccess: () => showToast("Đăng nhập thành công"),

    redirect: (role) => {
        setTimeout(() => {

            const routes = {
                ADMIN: "../admin/dashboard.html",
                STAFF: "../staff/dashboard.html",
                CUSTOMER: "../../index.html"
            };

            window.location.href = routes[role] || "../../index.html";

        }, 800); // nhanh hơn, mượt hơn
    }
};