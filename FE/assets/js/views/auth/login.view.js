const LoginView = {
    getFormData: () => ({
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    }),

    showSuccess: () => showToast("Đăng nhập thành công"),

    redirect: () => {
        setTimeout(() => {
            window.location.href = "../../index.html";
        }, 1000);
    }
};