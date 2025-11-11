import React, { useEffect, useState } from 'react'
import { Card, Container, Dropdown } from 'react-bootstrap'
import { axiosInstance } from '../../apis/axiosInstance';
import { SERVER_URL } from '../../constants/appConst';
import dayjs from 'dayjs';
import { useSelector } from 'react-redux';
import { jwtDecode } from 'jwt-decode';
import { isAdmin, isAuthenticated } from '../../apis/authCheck';
import { useNavigate } from 'react-router-dom';
import ReportModal from '../../components/modal/ReportModal';
import useReport from '../../hooks/useReport';

const PostList = () => {
  const token = useSelector(state => state.auth.token);
  const loginData = token ? jwtDecode(token) : null

  const nav = useNavigate();

  // 신고처리를 위한 함수와 모달 생성 여부를 세팅할 hook
  const {showReportModal, openReportModal, closeReportModal, submitReport} = useReport(loginData?.memId);

  // 게시글 목록을 저장할 state 변수
  const [postList, setPostList] = useState([]);

  // 게시글 목록을 세팅할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/posts`)
    .then(res => setPostList(res.data))
    .catch(e => console.log(e));
  }, []);

  console.log(postList);
  console.log(loginData);

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
                    variant=""
                    size="sm"
                  >
                    <span style={{ marginRight: "10px" }}>⋮</span>
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    {
                      loginData?.memId === post.memId
                      &&
                      <>
                        <Dropdown.Item onClick={() => console.log("수정 클릭")}>수정</Dropdown.Item>
                        <Dropdown.Item onClick={() => console.log("삭제 클릭")}>삭제</Dropdown.Item>
                        {
                          loginData?.memId !== post.memId
                          &&
                          <Dropdown.Divider />
                        }
                      </> 
                    }
                    {
                      isAdmin(token)
                      ?
                      <Dropdown.Item
                        onClick={() => isAuthenticated(token) ? 
                          null : 
                          (alert('로그인 후 이용해주세요.'), nav('/login'))}
                        className='text-danger'
                      >신고 및 삭제</Dropdown.Item>
                      :
                      <>
                        {
                          loginData?.memId !== post.memId
                          &&
                          <Dropdown.Item
                            onClick={() => isAuthenticated(token) ? 
                              openReportModal("POST", post.postId) : 
                              (alert('로그인 후 이용해주세요.'), nav('/login'))}
                            className='text-danger'
                          >신고</Dropdown.Item>
                        }
                        <ReportModal
                          show={showReportModal}
                          handleClose={closeReportModal}
                          handleSubmit={submitReport}
                        />
                      </>
                    }
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