// lấy token từ URL (?token=xxx)
const params = new URLSearchParams(window.location.search);
const token = params.get("token");

document.getElementById("resetForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  if (newPassword.value !== confirmPassword.value) {
    return showToast("Mật khẩu không khớp", "danger");
  }

  showLoading();

  try {
    await api.post("/auth/reset-password", {
      token: token,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value
    });

    showToast("Reset thành công");

    setTimeout(() => {
      window.location.href = "login.html";
    }, 1000);

  } finally {
    hideLoading();
  }
});