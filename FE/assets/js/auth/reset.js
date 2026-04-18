  const params = new URLSearchParams(window.location.search);
  const token = params.get("token");

  const form = document.getElementById("resetForm");
  const newPasswordInput = document.getElementById("newPassword");
  const confirmPasswordInput = document.getElementById("confirmPassword");

  // debug (xóa sau)
  console.log("TOKEN:", token);

  // check token
  if (!token) {
    showToast("Link không hợp lệ hoặc thiếu token", "danger");

    setTimeout(() => {
      window.location.href = "forgot-password.html";
    }, 1500);

    throw new Error("Missing token");
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (newPasswordInput.value !== confirmPasswordInput.value) {
      return showToast("Mật khẩu không khớp", "danger");
    }

    showLoading();

    try {
      await api.post("/auth/reset-password", {
        token: token,
        newPassword: newPasswordInput.value,
        confirmPassword: confirmPasswordInput.value
      });

      showToast("Reset thành công");

      setTimeout(() => {
        window.location.href = "login.html";
      }, 1000);

    } finally {
      hideLoading();
    }
  });