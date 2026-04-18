document.getElementById("forgotForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  showLoading();

  try {
    await api.post("/auth/forgot-password", {
      email: email.value
    });

    showToast("Check email để reset mật khẩu");

  } finally {
    hideLoading();
  }
});