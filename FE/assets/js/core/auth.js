const Auth = {
  setToken: (token) => sessionStorage.setItem("token", token),
  getToken: () => sessionStorage.getItem("token"),
  logout: () => {
    sessionStorage.removeItem("token");
    window.location.href = "/MobileHub/FE/pages/auth/login.html";
  }
};