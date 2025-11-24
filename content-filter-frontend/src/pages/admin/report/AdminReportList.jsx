import React, { useEffect, useState } from 'react';
import { Container, ButtonGroup, Button } from 'react-bootstrap';
import PostReportListComponent from './PostReportListComponent';
import CommentReportListComponent from './CommentReportListComponent';
import { axiosInstance } from '../../../apis/axiosInstance';
import useAdminSSE from '../../../hooks/useAdminSSE';
import { useSelector } from 'react-redux';

const AdminReportList = () => {
  const token = useSelector(state => state.auth.token);
  const [reload, setReload] = useState(0);
  const [activeTab, setActiveTab] = useState('POST');
  const [PostReportList, setPostReportList] = useState([]);
  const [commentReportList, setCommentReportList] = useState([]);

  // 초기 데이터 로딩
  useEffect(() => {
    axiosInstance.get(`/admin/reports/POST`)
      .then(res => setPostReportList(res.data))
      .catch(handleError);

    axiosInstance.get(`/admin/reports/COMMENT`)
      .then(res => setCommentReportList(res.data))
      .catch(handleError);
  }, [reload]);

  // SSE 실시간 통신
  useAdminSSE({
    token,
    onReport: newReport => {
      console.log('새 신고:', newReport);
      alert(`새로운 신고가 접수되었습니다!\n내용: ${newReport.reportReason}`);
      
      // ✅ reload로 최신 데이터 가져오기
      setReload(prev => prev + 1);
    }
  });

  // 신고 처리 함수
  const processReport = (reportProccessId, reportProcessData) => {
    axiosInstance.put(`/admin/reports/${reportProccessId}`, reportProcessData)
      .then(res => {
        alert(res.data);
        
        // ✅ reload로 최신 데이터 가져오기
        setReload(prev => prev + 1);
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