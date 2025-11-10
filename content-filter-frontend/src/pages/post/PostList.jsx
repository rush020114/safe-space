import React, { useEffect, useState } from 'react'
import { Button, Card, Container, Dropdown } from 'react-bootstrap'
import { axiosInstance } from '../../apis/axiosInstance';
import { SERVER_URL } from '../../constants/appConst';
import dayjs from 'dayjs';
import { useSelector } from 'react-redux';
import { jwtDecode } from 'jwt-decode';
import { isAuthenticated } from '../../apis/authCheck';
import { useNavigate } from 'react-router-dom';

const PostList = () => {
  const token = useSelector(state => state.auth.token);
  const username = token ? jwtDecode(token).sub : null

  const nav = useNavigate();
  
  // 게시글 목록을 저장할 state 변수
  const [postList, setPostList] = useState([]);

  // 게시글 목록을 세팅할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/posts`)
    .then(res => setPostList(res.data))
    .catch(e => console.log(e));
  }, []);

  console.log(postList);

  return (
    <Container style={{ maxWidth: "800px", marginTop: "40px" }}>
      <h2 className="mb-4 fw-bold text-center">최근 게시글</h2>

      {postList.length === 0 ? (
        <p className="text-center text-muted">아직 작성된 글이 없습니다.</p>
      ) : (
        postList.map((post, i) => (
          <Card key={i} className="mb-4 shadow-sm">
            {post.postImgDTO.attachedImgName !== null && (
              <Card.Img
                variant="top"
                src={`${SERVER_URL}/post/${post.postImgDTO.attachedImgName}`}
                alt="게시글 이미지"
                style={{
                  padding: "10px",
                  objectFit: "contain",
                  maxHeight: "300px",
                  width: "100%",
                  backgroundColor: "#e9ecef"
                }}
              />
            )}
            <Card.Body>
              <Card.Title 
                className="fw-bold"
                onClick={() => nav(`/post-detail/${post.postId}`)} 
                style={{
                  cursor: 'pointer'
                }}
              >{post.postTitle}</Card.Title>
              <div className="d-flex justify-content-between align-items-center">
                <small className="text-muted">
                  작성자: {post.memId} |{" "}
                  {dayjs(post.createdAt).format('YYYY-MM-DD')}
                </small>
                <Dropdown align="end">
                  <Dropdown.Toggle
                    variant="outline-primary"
                    size="sm"
                  >
                    <span style={{ marginRight: "10px" }}>⋮</span>
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    {
                      username === post.memId
                      &&
                      <>
                        <Dropdown.Item onClick={() => console.log("수정 클릭")}>수정</Dropdown.Item>
                        <Dropdown.Item onClick={() => console.log("삭제 클릭")}>삭제</Dropdown.Item>
                        <Dropdown.Divider />
                      </>
                    }
                    <Dropdown.Item
                      onClick={() => isAuthenticated(token) ? null : alert('로그인 후 이용해주세요.')}
                      className='text-danger'
                    >신고</Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </div>
            </Card.Body>
          </Card>
        ))
      )}
    </Container>
  )
}

export default PostList