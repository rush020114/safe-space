import { createSlice } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";

// Redux는 데이터를 props로 전달하지 않아도 모든 컴포넌트에서 사용할 수 있다.
// Store : 공유해서 사용할 state 변수들의 저장소
// Slice : store에서 관리할 데이터의 단위(데이터 및 데이터 변경 로직 포함)
// Action : 상태 변화를 일으키기 위한 정보를 가지고 있는 객체
// Reducer : 상태를 업데이트하는 함수
// Dispatch : 상태를 업데이트하는 함수를 호출하는 행위

// createSlice: slice를 생성하는 명령어
// name은 상태를 저장할 변수 이름의 키값
// intitialState는 초기값

const getToken = () => {
  const token = localStorage.getItem('accessToken');

  if(token == null) return null;

  // 복호화된 토큰
  const decodeToken = jwtDecode(token);

  // 현재 날짜 및 시간
  const currentTime = Date.now() / 1000;

  // 토큰의 만료기간이 지났으면
  if(decodeToken.exp < currentTime){
    localStorage.removeItem('accessToken');
    return null;
  } else {
    return token;
  }
}

const authSlice = createSlice({
  name: 'auth',
  initialState: {token: getToken()},
  // state값을 변경하는 함수를 정의하는 영역
  reducers: {
    // state는 현재 값을 의미
    // action은 변경할 데이터를 담고 있다.(payload로 접근)
    loginReducer: (state, action) => {
      state.token = action.payload;
      localStorage.setItem('accessToken', action.payload);
    },
    logoutReducer: state => {
      state.token = null;
      localStorage.removeItem('accessToken');
    }
  }
});

// state 변경 함수 import 문법
export const {loginReducer, logoutReducer} = authSlice.actions;
export default authSlice;