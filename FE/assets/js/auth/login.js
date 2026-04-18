document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  showLoading();

  try {
    const res = await api.post("/auth/login", {
      username: username.value,
      password: password.value,
    });

    // lưu token
    Auth.setToken(res.data.data.token);

    if (window.handleAuthUI) {
      handleAuthUI();
    }

    showToast("Đăng nhập thành công");

    // redirect chuẩn (KHÔNG phụ thuộc folder)
    setTimeout(() => {
      window.location.href = "../../index.html";
    }, 1000);

  } catch (err) {
    // đã xử lý interceptor
  } finally {
    hideLoading();
  }
});

