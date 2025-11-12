import React, { useState } from 'react';
import { Container, ButtonGroup, Button } from 'react-bootstrap';
import PostReportList from './PostReportList';

const AdminReportList = () => {
  const [activeTab, setActiveTab] = useState('POST');

  return (
    <Container className="mt-5" style={{ maxWidth: '800px' }}>
      <h3 className="fw-bold text-center mb-4">📋 신고 접수 목록</h3>

      {/* 상단 탭 버튼 */}
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

      {/* 신고 목록 조회 영역 */}
      {activeTab === 'POST' && (
        <div>
          <h5 className="fw-bold mb-3">📌 게시글 신고 목록</h5>
          <PostReportList />
        </div>
      )}
      {activeTab === 'COMMENT' && (
        <div>
          <h5 className="fw-bold mb-3">💬 댓글 신고 목록</h5>
          {/* 여기에 댓글 신고 리스트 컴포넌트 넣으면 됨 */}
        </div>
      )}
    </Container>
  );
};

export default AdminReportList;