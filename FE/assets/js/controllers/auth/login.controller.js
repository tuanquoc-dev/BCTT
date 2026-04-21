document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    showLoading();

    try {
        const data = LoginView.getFormData();

        const res = await AuthModel.login(data);

        Auth.setToken(res.data.data.token);

        if (window.handleAuthUI) handleAuthUI();

        LoginView.showSuccess();
        LoginView.redirect();

    } catch (err) {
        // interceptor xử lý rồi
    } finally {
        hideLoading();
    }
});