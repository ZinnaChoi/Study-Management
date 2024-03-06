import axios from "axios";
import { menuTree } from "../constants/constants";

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

authClient.interceptors.response.use(
  (response) => {
    return response; // 응답 데이터 반환
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // 토큰 만료 에러 처리
      moveLogin();
    }
  }
);

// JWT 인증 및 form 데이터 사용이 필요한 API 요청에 사용
export const authFormClient = axios.create({
  baseURL: "api/v1",
  timeout: 5000,
  headers: {
    "Cache-Control": "no-cache",
    "Content-Type": "multipart/form-data",
    Authorization: localStorage.getItem("token"),
  },
  responseType: "json",
});

authFormClient.interceptors.request.use(
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

authFormClient.interceptors.response.use(
  (response) => {
    return response; // 응답 데이터 반환
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // 토큰 만료 에러 처리
      moveLogin();
    }
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

function moveLogin() {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  alert("토큰이 만료되었습니다. \n로그인 화면으로 이동합니다");
  window.location.replace(
    window.location.protocol + "//" + window.location.host + menuTree.login.path
  );
}
