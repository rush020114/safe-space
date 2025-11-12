import React, { useEffect, useState } from 'react';
import { Container, ButtonGroup, Button } from 'react-bootstrap';
import PostReportListComponent from './PostReportListComponent';
import CommentReportListComponent from './CommentReportListComponent';
import { axiosInstance } from '../../../apis/axiosInstance';
import { SERVER_URL } from '../../../constants/appConst';
import useAdminSSE from '../../../hooks/useAdminSSE';
import { useSelector } from 'react-redux';

const AdminReportList = () => {
  const token = useSelector(state => state.auth.token);

  // 탭 전환 state 변수
  const [activeTab, setActiveTab] = useState('POST');

  // 게시글 신고 목록
  const [PostReportList, setPostReportList] = useState([]);

  // 댓글 신고 목록
  const [commentReportList, setCommentReportList] = useState([]);

  // 초기 데이터 로딩
  useEffect(() => {
    axiosInstance.get(`${SERVER_URL}/admin/reports/POST`)
      .then(res => setPostReportList(res.data))
      .catch(handleError);

    axiosInstance.get(`${SERVER_URL}/admin/reports/COMMENT`)
      .then(res => setCommentReportList(res.data))
      .catch(handleError);
  }, []);

  // SSE 실시간 통신
  useAdminSSE({
    token,
    onReport: newReport => {
      alert(`새로운 신고가 접수되었습니다!\n내용: ${newReport.reportContent}`);
      if (newReport.reportTarget === 'POST') {
        setPostReportList(prev => [newReport, ...prev]);
      } else if (newReport.reportTarget === 'COMMENT') {
        setCommentReportList(prev => [newReport, ...prev]);
      }
    }
  });

  // 신고 처리 함수
  const processReport = (reportProccessId, reportProcessData) => {
    axiosInstance.put(`${SERVER_URL}/admin/reports/${reportProccessId}`, reportProcessData)
      .then(res => {
        alert(res.data);
        if (reportProcessData.reportTarget === 'POST') {
          setPostReportList(prev => prev.filter(r => r.reportId !== reportProccessId));
        } else if (reportProcessData.reportTarget === 'COMMENT') {
          setCommentReportList(prev => prev.filter(r => r.reportId !== reportProccessId));
        }
      })
      .catch(handleError);
  };

  // 공통 에러 핸들러
  const handleError = (e) => {
    if (e.status === 403) {
      alert('세션이 만료되어 로그인이 필요합니다.');
      window.location.replace('/login');
    } else if (e.response) {
      alert(e.response.data);
      console.log(e);
    } else if (e.request) {
      alert("서버로부터 응답이 없습니다.");
    } else {
      alert("요청 중 오류 발생: " + e.message);
    }
  };

  return (
    <Container className="mt-5" style={{ maxWidth: '800px' }}>
      <h3 className="fw-bold text-center mb-4">📋 신고 접수 목록</h3>

      <ButtonGroup className="d-flex justify-content-center mb-4">
        <Button
          variant={activeTab === 'POST' ? 'primary' : 'outline-primary'}
          onClick={() => setActiveTab('POST')}
        >
          게시글 신고
        </Button>
        <Button
          variant={activeTab === 'COMMENT' ? 'primary' : 'outline-primary'}
          onClick={() => setActiveTab('COMMENT')}
        >
          댓글 신고
        </Button>
      </ButtonGroup>

      {activeTab === 'POST' && (
        <div>
          <h5 className="fw-bold mb-3">📌 게시글 신고 목록</h5>
          <PostReportListComponent
            PostReportList={PostReportList}
            processReport={processReport}
          />
        </div>
      )}
      {activeTab === 'COMMENT' && (
        <div>
          <h5 className="fw-bold mb-3">💬 댓글 신고 목록</h5>
          <CommentReportListComponent
            commentReportList={commentReportList}
            processReport={processReport}
          />
        </div>
      )}
    </Container>
  );
};

export default AdminReportList;