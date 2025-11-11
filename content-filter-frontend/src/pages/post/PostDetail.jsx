import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { axiosInstance } from '../../apis/axiosInstance';
import { SERVER_URL } from '../../constants/appConst';
import { Accordion, Alert, Button, Card, Container, Dropdown, Form } from 'react-bootstrap';
import dayjs from 'dayjs';
import { useSelector } from 'react-redux';
import { isAdmin, isAuthenticated } from '../../apis/authCheck';
import { jwtDecode } from 'jwt-decode';

const PostDetail = () => {
  const token = useSelector(state => state.auth.token);
  const {postId} = useParams();
  const loginData = token ? jwtDecode(token) : null;

  // 댓글을 저장할 state 변수
  const [comment, setComment] = useState('');

  // 게시글 상세를 저장할 state 변수
  const [postDetail, setPostDetail] = useState({});

  // 게시글 상세를 조회할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/posts/${postId}`)
    .then(res => setPostDetail(res.data))
    .catch(e => console.log(e));
  }, []);
  
  // 댓글을 등록할 함수
  const regComment = () => {
    axiosInstance.post(`${SERVER_URL}/comments`, {
      cmtContent: comment
      , postId
    })
    .then(res => alert(res.data))
    .catch(e => {
      if (e.status === 403){
        alert('로그인이 필요한 서비스입니다.')
        window.location.replace('/login');
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
  }

  console.log(postDetail);

  return (
    <Container style={{ maxWidth: "800px", marginTop: "40px" }}>
    <Card className="shadow-sm border-0">
      {postDetail?.postImgDTO?.attachedImgName && (
        <Card.Img
          variant="top"
          src={`${SERVER_URL}/post/${postDetail.postImgDTO.attachedImgName}`}
          alt="게시글 이미지"
          style={{
            objectFit: "contain",
            maxHeight: "400px",
            width: "100%",
            backgroundColor: "#e9ecef",
            padding: '10px'
          }}
        />
      )}

      <Card.Body style={{ padding: "2rem" }}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h3 className="fw-bold mb-0">{postDetail.postTitle}</h3>
          <div className="text-muted" style={{ fontSize: "0.9rem", textAlign: "right" }}>
            <div>작성자: {postDetail.memId || "익명"}</div>
            <div>{dayjs(postDetail.createdAt).format('YYYY-MM-DD HH:mm')}</div>
          </div>
        </div>

        <hr />

        <Card.Text style={{ lineHeight: "1.8", whiteSpace: "pre-wrap", fontSize: "1.05rem" }}>
          {postDetail.postContent}
        </Card.Text>
      </Card.Body>
    </Card>
    <hr className="my-4" />
    <Accordion defaultActiveKey={null} className="mb-4">
      <Accordion.Item eventKey="0">
        <Accordion.Header>댓글 보기</Accordion.Header>
        <Accordion.Body>
          {postDetail?.commentDTOList?.length ? (
          postDetail.commentDTOList.map((comment, index) => (
            <Card key={index} className="mb-3">
              <Card.Body className="position-relative">
                {/* Dropdown Toggle 우측 상단 */}
                <Dropdown
                  className="position-absolute"
                  style={{ top: "10px", right: "10px" }}
                >
                  <Dropdown.Toggle
                    variant=""
                    size="sm"
                    className="border-0"
                    id={`dropdown-${index}`}
                  >
                    ⋮
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    {
                      loginData?.memId === postDetail.memId
                      &&
                      <>
                        <Dropdown.Item onClick={() => console.log("삭제 클릭")}>삭제</Dropdown.Item>
                        <Dropdown.Item onClick={() => console.log("")}>수정</Dropdown.Item>
                        <Dropdown.Divider />
                      </>
                    }
                    {
                      isAdmin(token)
                      ?
                      <Dropdown.Item
                        onClick={() => isAuthenticated(token) ? null : alert('로그인 후 이용해주세요.')}
                        className='text-danger'
                      >신고 및 삭제</Dropdown.Item>
                      :
                      <Dropdown.Item
                        onClick={() => isAuthenticated(token) ? null : alert('로그인 후 이용해주세요.')}
                        className='text-danger'
                      >신고</Dropdown.Item>
                    }
                  </Dropdown.Menu>
                </Dropdown>

                {/* 댓글 내용 */}
                <Card.Subtitle className="mb-2" style={{ fontSize: "1rem", fontWeight: "600" }}>
                  {comment.memId}
                </Card.Subtitle>

                <Card.Text
                  className="mb-2 text-muted"
                  style={{ fontSize: "0.95rem", whiteSpace: "pre-wrap", lineHeight: "1.6" }}
                >
                  {comment.cmtContent}
                </Card.Text>

                <Card.Subtitle className="text-muted" style={{ fontSize: "0.85rem" }}>
                  {dayjs(comment.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                </Card.Subtitle>
              </Card.Body>
            </Card>
          ))
        ) : (
          <div className="d-flex justify-content-center align-items-center">
            <p className="text-center mb-0">
              조회된 댓글이 없습니다.
            </p>
          </div>
        )}
        </Accordion.Body>
      </Accordion.Item>
    </Accordion>
    <h5>댓글 작성</h5>
    <Form.Group className="mb-3" controlId="formComment">
      <Form.Control
        as="textarea"
        value={comment}
        onChange={e => setComment(e.target.value)}
        rows={3}
        placeholder="댓글을 입력하세요"
      />
    </Form.Group>
    <div className='text-end mb-5'>
      <Button
        variant="secondary"
        type="button"
        onClick={() => regComment()}
      >
        댓글 등록
      </Button>
    </div>
  </Container>
  )
}

export default PostDetail