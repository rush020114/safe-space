import React, { useState } from 'react';
import { Container, Form, Button } from 'react-bootstrap';
import { axiosInstance } from '../../apis/axiosInstance';

const Post = () => {

  // 게시글 정보를 저장할 state 변수
  const [postInfo, setPostInfo] = useState({
    postTitle: ''
    , postContent: ''
  });

  // 게시글 정보를 세팅할 함수
  const handlePostInfo = e => {
    setPostInfo({
      ...postInfo
      , [e.target.name]: e.target.value
    });
  };
  
  const regPost = () => {
    axiosInstance.post('/posts', postInfo)
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
  return (
    <Container style={{ maxWidth: '600px', marginTop: '40px' }}>
      <h2 className="mb-4">글쓰기</h2>
      <Form.Group className="mb-3" controlId="formTitle">
        <Form.Label>제목</Form.Label>
        <Form.Control 
          type="text" 
          name='postTitle'
          value={postInfo.title}
          onChange={e => handlePostInfo(e)}
          placeholder="제목을 입력하세요" 
        />
      </Form.Group>

      <Form.Group className="mb-3" controlId="formContent">
        <Form.Label>내용</Form.Label>
        <Form.Control
          as="textarea" 
          name='postContent'
          value={postInfo.title}
          onChange={e => handlePostInfo(e)}
          rows={10} 
          placeholder="내용을 입력하세요" 
        />
      </Form.Group>

      <Form.Group className="mb-3" controlId="formFile">
        <Form.Label>파일 업로드</Form.Label>
        <Form.Control type="file" multiple />
      </Form.Group>

      <Button 
        variant="primary" 
        type="button"
        onClick={() => regPost()}
      >
        작성하기
      </Button>
    </Container>
  );
};

export default Post;