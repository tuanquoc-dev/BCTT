const ResetView = {

    getFormData: () => ({
        newPassword: document.getElementById("newPassword").value,
        confirmPassword: document.getElementById("confirmPassword").value
    }),

    showSuccess: () => showToast("Reset mật khẩu thành công"),

    redirectLogin: () => {
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);
    }

};