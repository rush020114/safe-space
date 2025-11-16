import axios from "axios";
import { SERVER_URL } from "../constants/appConst";

// axios 대신 사용 시 해당 설정을 유지해준다.
export const axiosInstance = axios.create({
  baseURL: `${SERVER_URL}` // 백엔드 주소
  , withCredentials: true // 필요 시 쿠키 인증도 함께 처리하기 위한 설정
});

// 모든 요청 전 헤더에 토큰을 담는 코드
axiosInstance.interceptors.request.use(
  // use의 첫번째 매개변수는 요청 전 수행할 작업
  // 두번째 매개변수는 요청 중 오류 발생 시 수행할 작업
  config => {
    const token = localStorage.getItem("accessToken");
    if(token) {
      config.headers.Authorization = token;
    }
    return config;
  },
  error => Promise.reject(error)
);