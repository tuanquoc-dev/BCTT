const ForgotView = {

    getEmail: () =>
        document.getElementById("email").value.trim(),

    showSuccess: () =>
        showToast("Check email để reset mật khẩu")

};