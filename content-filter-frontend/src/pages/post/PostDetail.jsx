import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { axiosInstance } from '../../apis/axiosInstance';
import { SERVER_URL } from '../../constants/appConst';
import { Card, Container } from 'react-bootstrap';
import dayjs from 'dayjs';

const PostDetail = () => {
  const {postId} = useParams();

  // 게시글 상세를 저장할 state 변수
  const [postDetail, setPostDetail] = useState({});

  // 게시글 상세를 조회할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/posts/${postId}`)
    .then(res => setPostDetail(res.data))
    .catch(e => console.log(e));
  }, [])

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
            <div>{dayjs(postDetail.createdAt).format('YYYY년 MM월 DD일 HH:mm')}</div>
          </div>
        </div>

        <hr />

        <Card.Text style={{ lineHeight: "1.8", whiteSpace: "pre-wrap", fontSize: "1.05rem" }}>
          {postDetail.postContent}
        </Card.Text>
      </Card.Body>
    </Card>
  </Container>
  )
}

export default PostDetail