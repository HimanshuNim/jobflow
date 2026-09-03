import axios from "axios";

const api = axios.create({
  baseURL: "https://jobflow-backend-j1u3.onrender.com/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Add JWT to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// Automatically remove expired/invalid JWT
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401||
        error.response?.status===403
      ) 
    {
      localStorage.removeItem("token");
      window.dispatchEvent(new Event("auth-expired"));
    }

    return Promise.reject(error);
  }
);

export default api;