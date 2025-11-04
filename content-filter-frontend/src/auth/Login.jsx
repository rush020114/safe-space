import axios from 'axios';
import React from 'react'
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginReducer } from '../../redux/authSlice';

const Login = () => {

  // dispatch로 우리가 만든 state 변경 함수를 호출할 수 있다.
  // 대신 dispatch함수 안에서 함수를 호출해야 한다.(함수 호출을 위임한 느낌)
  const dispatch = useDispatch();
  const nav = useNavigate();

  // 로그인 요청 함수
  const login = () => {
    axios.post('/api/member/login', loginInfo)
    .then(res => {
      alert('로그인 성공');
      
      // 응답 헤더 중 'authorization 값을 가져옴.
      console.log(res.headers['authorization']);
      
      // 전달받은 jwt 토큰을 store에 저장
      const accessToken = res.headers['authorization'];
      // action 매개변수에 전달
      dispatch(loginReducer(accessToken));
      nav('/');
    })
    .catch(e => {
      if(e.status === 401){
        alert('로그인 실패');
      } else {
        console.log(e);
      }
    });
  };
  
  return (
    <div>Login</div>
  )
}

export default Login