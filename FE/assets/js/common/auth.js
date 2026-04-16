function saveAuth(data) {
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
}

function logout() {
    localStorage.clear();
    window.location.href = "/pages/auth/login.html";
}

function isAdmin() {
    return localStorage.getItem("role") === "ADMIN";
}