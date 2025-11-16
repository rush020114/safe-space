import React, { useState } from 'react';
import { Card, Container } from 'react-bootstrap';
import dayjs from 'dayjs';
import PostReportModal from '../../../components/modal/PostReportModal';

const PostReportListComponent = ({ PostReportList, processReport }) => {
  const [selectedReport, setSelectedReport] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <Container className="mt-4 mb-5" style={{ maxWidth: '800px' }}>
      {PostReportList.length === 0 ? (
        <div className="text-muted text-center">신고된 게시글이 없습니다.</div>
      ) : (
        PostReportList.map((postReport, i) => (
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
                게시글 ID : {postReport.postDTO.postId || ''}
              </Card.Title>
              <Card.Text>
                <strong>신고 사유:</strong> {postReport.reportReason}<br />
                <strong>작성자:</strong> user3<br />
                <strong>신고일:</strong> {dayjs(postReport.createdAt).format('YYYY-MM-DD HH:mm:ss')}
              </Card.Text>
            </Card.Body>
          </Card>
        ))
      )}

      {selectedReport && (
        <PostReportModal
          show={modalOpen}
          onHide={() => setModalOpen(false)}
          report={selectedReport}
          onProcess={(reportId, updatedReport) => {
            processReport(reportId, updatedReport);
            setModalOpen(false);
          }}
          onReject={(reportId, updatedReport) => {
            processReport(reportId, updatedReport);
            setModalOpen(false);
          }}
        />
      )}
    </Container>
  );
};

export default PostReportListComponent;