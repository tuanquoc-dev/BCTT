document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    showLoading();

    try {
        const data = LoginView.getFormData();

        // ===== CALL API =====
        const res = await AuthModel.login(data);
        const loginData = res.data.data;

        // ===== LƯU TOKEN =====
        Auth.setToken(loginData.token);

        // ===== LƯU USER =====
        Auth.setUser({
            username: loginData.username,
            role: loginData.role,
            permissions: loginData.permissions
        });

        // ===== UI =====
        LoginView.showSuccess();

        // ===== REDIRECT =====
        LoginView.redirect(loginData.role);

    } catch (err) {
        // interceptor đã xử lý toast
    } finally {
        hideLoading();
    }
});