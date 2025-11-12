import React from 'react';
import { Modal, Button } from 'react-bootstrap';
import dayjs from 'dayjs';

const CommentReportModal = ({ show, onHide, report, onProcess, onReject }) => {
  if (!report || !report.commentDTO) return null;

  const { commentDTO } = report;

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>💬 댓글 신고 처리</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p><strong>신고 사유:</strong> {report.reportReason}</p>
        <p><strong>신고일:</strong> {dayjs(report.createdAt).format('YYYY-MM-DD HH:mm')}</p>
        <p><strong>작성자:</strong> {commentDTO.memId}</p>
        <hr />
        <p><strong>내용:</strong> {commentDTO.cmtContent}</p>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={() => {
          onReject(report.reportId, {
            ...report
            , reportStatus: 'REJECTED'
          });
          onHide();
        }}>반려</Button>
        <Button variant="primary" onClick={() => {
          onProcess(report.reportId, {
            ...report
            , reportStatus: 'APPROVED'
          });
          onHide();
        }}>처리</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default CommentReportModal;