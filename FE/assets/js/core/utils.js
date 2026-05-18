function showLoading() {
    document.getElementById("loadingSpinner")
        .classList.remove("d-none");
}

function hideLoading() {
    document.getElementById("loadingSpinner")
        .classList.add("d-none");
}

function showToast(message, type = "success") {

    const toast = document.createElement("div");

    toast.className =
        `toast align-items-center text-bg-${type} border-0 show mb-2`;

    toast.innerHTML = `
    <div class="d-flex">

      <div class="toast-body">
        ${message}
      </div>

      <button
          class="btn-close btn-close-white me-2 m-auto"
          onclick="this.parentElement.parentElement.remove()">
      </button>

    </div>
  `;

    document
        .getElementById("toastContainer")
        .appendChild(toast);

    setTimeout(() => toast.remove(), 3000);
}

function toastSuccess(message) {

    showToast(message, "success");
}

function toastError(message) {

    showToast(message, "danger");
}

function formatPrice(price) {

    return new Intl.NumberFormat(
        "vi-VN",
        {
            style: "currency",
            currency: "VND"
        }
    ).format(price || 0);
}

function parseJwt(token) {

    if (!token) return null;

    return JSON.parse(
        atob(token.split('.')[1])
    );
}

function formatTime(date) {

    return new Date(date)
        .toLocaleString("vi-VN");
}

function formatDateTime(date) {

    if (!date) return "";

    return new Date(date)
        .toLocaleString("vi-VN");
}