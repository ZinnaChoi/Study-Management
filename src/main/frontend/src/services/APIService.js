import axios from "axios";

// JWT 인증 필요한 API 요청에 사용
export const authClient = axios.create({
  baseURL: "api/v1",
  timeout: 5000,
  headers: {
    "Cache-Control": "no-cache",
    "Content-Type": "application/json",
    Authorization: localStorage.getItem("token"),
  },
  responseType: "json",
});

authClient.interceptors.request.use(
  function (config) {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = token;
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// JWT 인증 필요 없는 API 요청에 사용(로그인, 회원가입, 중복 아이디 체크 등)
export const client = axios.create({
  baseURL: "api/v1",
  timeout: 5000,
  headers: {
    "Cache-Control": "no-cache",
    "Content-Type": "application/json",
  },
  responseType: "json",
});
