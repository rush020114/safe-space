import React from 'react';
import { Container, Form, Button } from 'react-bootstrap';

const Post = () => {
  return (
    <Container style={{ maxWidth: '600px', marginTop: '40px' }}>
      <h2 className="mb-4">글쓰기</h2>
      <Form>
        <Form.Group className="mb-3" controlId="formTitle">
          <Form.Label>제목</Form.Label>
          <Form.Control type="text" placeholder="제목을 입력하세요" />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formContent">
          <Form.Label>내용</Form.Label>
          <Form.Control as="textarea" rows={10} placeholder="내용을 입력하세요" />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formFile">
          <Form.Label>파일 업로드</Form.Label>
          <Form.Control type="file" multiple />
        </Form.Group>

        <Button variant="primary" type="submit">
          작성하기
        </Button>
      </Form>
    </Container>
  );
};

export default Post;