import axios from 'axios';
import React, { useState } from 'react'
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginReducer } from '../../redux/authSlice';
import { SERVER_URL } from '../../constants/appConst';
import { Container, Form, Button } from 'react-bootstrap';

const Login = () => {

  // dispatch로 우리가 만든 state 변경 함수를 호출할 수 있다.
  // 대신 dispatch함수 안에서 함수를 호출해야 한다.(함수 호출을 위임한 느낌)
  const dispatch = useDispatch();
  const nav = useNavigate();

  // 로그인 정보를 저장할 state 변수
  const [loginInfo, setLoginInfo] = useState({
    memEmail: ''
    , memPw: ''
  });

  // 로그인 요청 함수
  const login = () => {
    axios.post(`${SERVER_URL}/member/login`, loginInfo)
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
      if (e.status === 403){
      // 제재된 사용자
      const data = e.response.data;
      
      // JSON 객체면 reason 추출, 문자열이면 그대로
      const message = typeof data === 'object' && data.reason
        ? `계정이 정지되었습니다.\n${data.reason}`
        : (data.error || data);
        
      alert(message);
      } else if (e.status === 401) {
        alert("로그인 실패");
      } else if (e.response) {
        // 서버가 응답했지만 오류 상태일 때
        alert(e.response.data);
        console.log(e);
      } else if (e.request) {
        // 요청은 보냈지만 응답이 없을 때
        alert("서버로부터 응답이 없습니다.");
      } else {
        // 요청 설정 중 오류 발생
        alert("요청 중 오류 발생: " + e.message);
      };
    });
  };

  // 회원 정보 데이터 입력 함수
  const handleUserInfo = e => {
    setLoginInfo({
      ...loginInfo
      , [e.target.name]: e.target.value
    });
  };

  console.log(loginInfo)
  
  return (
    <Container className="mt-5" style={{
      maxWidth: '500px'
      , border: '1px solid #cccccc'
      , borderRadius: '8px'
      , paddingTop: '10px'
      , paddingBottom: '10px'
    }}>
      <h2 className="text-center mb-4">로그인</h2>
      <Form.Group className="mb-3">
        <Form.Label>이메일</Form.Label>
        <Form.Control
          type="text"
          name="memEmail"
          value={loginInfo.memEmail}
          onChange={e => handleUserInfo(e)}
          placeholder="이메일 입력"
        />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>비밀번호</Form.Label>
        <Form.Control
          type="password"
          name="memPw"
          value={loginInfo.memPw}
          onChange={e => handleUserInfo(e)}
          placeholder="비밀번호 입력"
        />
      </Form.Group>

      <Button
        type="button"
        onClick={() => login()}
        className="w-100"
      >
        로그인
      </Button>
    </Container>
  )
}

export default Login