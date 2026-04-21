document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = RegisterView.getFormData();

    showLoading();

    try {
        await AuthModel.register(data);

        RegisterView.showSuccess();
        RegisterView.redirectLogin();

    } catch (err) {
        // interceptor xử lý
    } finally {
        hideLoading();
    }
});