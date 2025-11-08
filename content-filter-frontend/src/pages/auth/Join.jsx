import React, { useState } from 'react';
import { Container, Form, Button } from 'react-bootstrap';
import { axiosInstance } from '../../apis/axiosInstance';
import { useNavigate } from 'react-router-dom';

const Join = () => {
  const nav = useNavigate();

  // 회원가입 정보를 저장할 state 변수
  const [joinInfo, setJoinInfo] = useState({
    memEmail: ''
    , memPw: ''
    , memName: ''
  });

  // 회원가입 정보를 세팅할 함수
  const handleJoinInfo = e => {
    setJoinInfo({
      ...joinInfo
      , [e.target.name]: e.target.value
    })
  };

  // 회원가입 함수
  const join = () => {
    axiosInstance.post('/members', joinInfo)
    .then(res => {
      alert(res.data);
      nav('/')
    })
    .catch(e => {
      if (e.response) {
        // 서버가 응답했지만 오류 상태일 때
        alert(e.response.data);
      } else if (e.request) {
        // 요청은 보냈지만 응답이 없을 때
        alert("서버로부터 응답이 없습니다.");
      } else {
        // 요청 설정 중 오류 발생
        alert("요청 중 오류 발생: " + e.message);
      };
    });
  };

  console.log(joinInfo);

  return (
    <Container className="mt-5" style={{ 
      maxWidth: '500px'
      , border: '1px solid #cccccc'
      , borderRadius: '8px'
      , paddingTop: '10px'
      , paddingBottom: '10px'
    }}>
      <h2 className="text-center mb-4">회원가입</h2>
      <Form.Group className="mb-3">
        <Form.Label>이메일</Form.Label>
        <Form.Control
          type="email"
          name="memEmail"
          value={joinInfo.memEmail}
          onChange={e => handleJoinInfo(e)}
          placeholder="이메일 입력"
        />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>비밀번호</Form.Label>
        <Form.Control
          type="password"
          name="memPw"
          value={joinInfo.memPw}
          onChange={e => handleJoinInfo(e)}
          placeholder="비밀번호 입력"
        />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>이름</Form.Label>
        <Form.Control
          type="text"
          name="memName"
          value={joinInfo.memName}
          onChange={e => handleJoinInfo(e)}
          placeholder="이름 입력"
        />
      </Form.Group>

      <Button 
        type="button" 
        variant="primary" 
        className="w-100"
        onClick={() => join()}
      >
        회원가입
      </Button>
    </Container>
  );
};

export default Join;