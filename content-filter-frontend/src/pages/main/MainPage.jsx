import React, { useEffect, useState } from 'react'
import { Button, Card, Container } from 'react-bootstrap'
import { axiosInstance } from '../../apis/axiosInstance';
import { SERVER_URL } from '../../constants/appConst';
import dayjs from 'dayjs';

const MainPage = () => {
  
  // 게시글 목록을 저장할 state 변수
  const [postList, setPostList] = useState([]);

  // 게시글 목록을 세팅할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/posts`)
    .then(res => setPostList(res.data))
    .catch(e => console.log(e));
  }, []);

  return (
    <Container style={{ maxWidth: "800px", marginTop: "40px" }}>
      <h2 className="mb-4 fw-bold text-center">최근 게시글</h2>

      {postList.length === 0 ? (
        <p className="text-center text-muted">아직 작성된 글이 없습니다.</p>
      ) : (
        postList.map((post, i) => (
          <Card key={i} className="mb-4 shadow-sm">
            {post.postImgDTO.attachedImgName !== null ? (
              <Card.Img
                variant="top"
                src={`${SERVER_URL}/post/${post.postImgDTO.attachedImgName}`}
                alt="게시글 이미지"
              />
            ) : null}
            <Card.Body>
              <Card.Title className="fw-bold">{post.postTitle}</Card.Title>
              <Card.Text className="text-truncate" style={{ maxHeight: "4.5em" }}>
                {post.postContent}
              </Card.Text>
              <div className="d-flex justify-content-between align-items-center">
                <small className="text-muted">
                  작성자: { "익명"} |{" "}
                  {dayjs(post.createdAt).format('YYYY-MM-DD')}
                </small>
                <Button
                  variant="outline-primary"
                  size="sm"
                >
                  자세히 보기
                </Button>
              </div>
            </Card.Body>
          </Card>
        ))
      )}
    </Container>
  )
}

export default MainPage