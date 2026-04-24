const Auth = {

  setToken: (token) => {
    sessionStorage.setItem("token", token);
  },

  getToken: () => {
    return sessionStorage.getItem("token");
  },

  setUser: (user) => {
    sessionStorage.setItem("user", JSON.stringify(user));
  },

  getUser: () => {
    const user = sessionStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },

  getUserSafe: () => {
    try {
      return JSON.parse(sessionStorage.getItem("user"));
    } catch {
      return null;
    }
  },

  isLoggedIn: () => {
    return !!sessionStorage.getItem("token");
  },

  hasRole: (role) => {
    return Auth.getUser()?.role === role;
  },

  hasPermission: (permission) => {
    return Auth.getUser()?.permissions?.includes(permission);
  },

  logout: () => {
    sessionStorage.clear();
    window.location.href = "/MobileHub/FE/pages/auth/login.html";
  }
};