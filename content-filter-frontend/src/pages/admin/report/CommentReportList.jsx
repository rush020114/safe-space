import React, { useEffect, useState } from 'react'
import { Card, Container } from 'react-bootstrap';
import { axiosInstance } from '../../../apis/axiosInstance';
import { SERVER_URL } from '../../../constants/appConst';
import dayjs from 'dayjs';
import CommentReportModal from '../../../components/modal/CommentReportModal';

const CommentReportList = () => {

  // 리렌더링용 state 변수
  const [reload, setReload] = useState(0);

  // 댓글 신고 목록을 저장할 state 변수
  const [commentReportList, setCommentReportList] = useState([]);

  // 모달 상태 및 선택된 신고 데이터
  const [selectedReport, setSelectedReport] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);

  // 댓글 신고 목록을 조회할 useEffect
    useEffect(() => {
      axiosInstance.get(`${SERVER_URL}/admin/reports/${'COMMENT'}`)
      .then(res => setCommentReportList(res.data))
      .catch(e => {
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
    }, [reload]);

    // 신고 처리 함수
    const processReport = (reportProccessId, reportProcessData) => {
      axiosInstance.put(`${SERVER_URL}/admin/reports/${reportProccessId}`, reportProcessData)
      .then(res => {
        alert(res.data);
        setReload(prev => prev + 1);
      })
      .catch(e => {
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
    };
  
  return (
    <Container className="mt-4 mb-5" style={{ maxWidth: '800px' }}>
      {commentReportList.length === 0 ? (
        <div className="text-muted text-center">신고된 게시글이 없습니다.</div>
      ) : (
        commentReportList.map((postReport, i) => (
          <Card
            key={i}
            className="mb-3 shadow-sm"
            onClick={() => {
              setSelectedReport(postReport);
              setModalOpen(true);
            }}
            style={{ cursor: 'pointer' }}
          >
            <Card.Body>
              <Card.Title className="fw-bold">
                댓글 ID : {postReport.commentDTO.cmtId || ''}
              </Card.Title>
              <Card.Text>
                <strong>신고 사유:</strong> {postReport.reportReason}<br />
                <strong>작성자:</strong> {postReport.commentDTO.memId}<br />
                <strong>신고일:</strong> {dayjs(postReport.createdAt).format('YYYY-MM-DD HH:mm:ss')}
              </Card.Text>
            </Card.Body>
          </Card>
        ))
      )}
      {/* 신고 처리 모달 */}
      {selectedReport && (
        <CommentReportModal
          show={modalOpen}
          onHide={() => setModalOpen(false)}
          report={selectedReport}
          onProcess={(reportId, updatedReport) => processReport(reportId, updatedReport)}
          onReject={(reportId, updatedReport) => processReport(reportId, updatedReport)}
        />
      )}
    </Container>
  )
}

export default CommentReportList