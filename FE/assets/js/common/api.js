const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// request interceptor (gắn token)
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

// response interceptor
api.interceptors.response.use(
  (res) => res,
  (err) => {
    hideLoading();
    showToast(err.response?.data?.message || "Lỗi hệ thống", "danger");
    return Promise.reject(err);
  },
);
