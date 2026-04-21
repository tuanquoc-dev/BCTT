const RegisterView = {

    getFormData: () => ({
        username: document.getElementById("regUsername").value.trim(),
        email: document.getElementById("regEmail").value.trim(),
        phone: document.getElementById("regPhone").value.trim(),
        address: document.getElementById("regAddress").value.trim(),
        password: document.getElementById("regPassword").value,
        confirmPassword: document.getElementById("regConfirmPassword").value
    }),

    showSuccess: () => showToast("Đăng ký thành công"),

    redirectLogin: () => {
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);
    }

};