import React, { useEffect, useState } from 'react';
import { axiosInstance } from '../../../apis/axiosInstance';
import { SERVER_URL } from '../../../constants/appConst'
import { Card, Container } from 'react-bootstrap';
import dayjs from 'dayjs';

const PostReportList = () => {

  // 게시글 신고 목록을 저장할 state 변수
  const [PostReportList, setPostReportList] = useState([]);

  // 게시글 신고 목록을 조회할 useEffect
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/admin/reports/${'POST'}`)
    .then(res => setPostReportList(res.data))
    .catch(() => {
      if (e.status === 403){
        alert('세션이 만료되어 로그인이 필요합니다.')
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
  }, []);

  console.log(PostReportList)

  return (
    <Container className="mt-4" style={{ maxWidth: '800px' }}>
      <h4 className="fw-bold mb-4 text-center">📌 게시글 신고 목록</h4>

      {PostReportList.length === 0 ? (
        <div className="text-muted text-center">신고된 게시글이 없습니다.</div>
      ) : (
        PostReportList.map((postReport, i) => (
          <Card key={i} className="mb-3 shadow-sm">
            <Card.Body>
              <Card.Title className="fw-bold">
                {postReport.postDTO.postTitle || '제목 없음'}
              </Card.Title>
              <Card.Text>
                <strong>신고 사유:</strong> {postReport.reportReason}<br />
                <strong>작성자:</strong> {postReport.postDTO.memId}<br />
                <strong>신고일:</strong> {dayjs(postReport.createdAt).format('YYYY-MM-DD HH:mm:ss')}
              </Card.Text>
            </Card.Body>
          </Card>
        ))
      )}
    </Container>
  );
};

export default PostReportList;