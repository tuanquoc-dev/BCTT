document.addEventListener("DOMContentLoaded", () => {

    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");

    // ❗ Không throw error nữa
    if (!token) {
        showToast("Token không được để trống", "danger");

        setTimeout(() => {
            window.location.href = "forgot-password.html";
        }, 1500);

        return;
    }

    const form = document.getElementById("resetForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const data = ResetView.getFormData();

        showLoading();

        try {
            await AuthModel.reset({
                token: token,           // 🔥 map chuẩn BE
                newPassword: data.newPassword,
                confirmPassword: data.confirmPassword
            });

            ResetView.showSuccess();
            ResetView.redirectLogin();

        } catch (err) {
            // interceptor xử lý message BE
        } finally {
            hideLoading();
        }
    });

});