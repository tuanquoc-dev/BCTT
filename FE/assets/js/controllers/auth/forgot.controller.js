document.getElementById("forgotForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = ForgotView.getEmail();

    showLoading();

    try {
        await AuthModel.forgot(email);

        ForgotView.showSuccess();

    } catch (err) {
        // interceptor xử lý
    } finally {
        hideLoading();
    }
});