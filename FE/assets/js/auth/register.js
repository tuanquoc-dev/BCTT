document.getElementById("registerForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  showLoading();

  try {
    await api.post("/auth/register", {
      username: regUsername.value,
      email: regEmail.value,
      password: regPassword.value,
      confirmPassword: regPassword.value
    });

    showToast("Đăng ký thành công");

    setTimeout(() => {
      window.location.href = "login.html";
    }, 1000);

  } finally {
    hideLoading();
  }
});