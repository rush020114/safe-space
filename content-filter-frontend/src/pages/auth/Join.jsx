import React, { useState } from 'react';
import { Container, Form, Button } from 'react-bootstrap';

const Join = () => {

  // 회원가입 정보를 저장할 state 변수
  const [joinInfo, setJoinInfo] = useState({
    memEmail: ''
    , memPw: ''
    , memName: ''
  });

  return (
    <Container className="mt-5" style={{ maxWidth: '500px' }}>
      <h2 className="text-center mb-4">회원가입</h2>
      <Form>
        <Form.Group className="mb-3">
          <Form.Label>이메일</Form.Label>
          <Form.Control
            type="email"
            name="memEmail"
            placeholder="이메일 입력"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>비밀번호</Form.Label>
          <Form.Control
            type="password"
            name="memPw"
            placeholder="비밀번호 입력"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>이름</Form.Label>
          <Form.Control
            type="text"
            name="memName"
            placeholder="이름 입력"
          />
        </Form.Group>

        <Button type="submit" variant="primary" className="w-100">
          회원가입
        </Button>
      </Form>
    </Container>
  );
};

export default Join;