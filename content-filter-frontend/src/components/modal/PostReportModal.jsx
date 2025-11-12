import React from 'react';
import { Modal, Button, Image } from 'react-bootstrap';
import dayjs from 'dayjs';
import { SERVER_URL } from '../../constants/appConst';

const PostReportModal = ({ show, onHide, report, onProcess, onReject }) => {
  if (!report || !report.postDTO) return null;

  const { postDTO } = report;
  const imageName = postDTO.postImgDTO?.attachedImgName;

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>📄 신고 처리</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p><strong>신고 사유:</strong> {report.reportReason}</p>
        <p><strong>신고일:</strong> {dayjs(report.createdAt).format('YYYY-MM-DD HH:mm')}</p>
        <p><strong>작성자:</strong> {postDTO.memId}</p>
        <hr />
        <p><strong>제목:</strong> {postDTO.postTitle}</p>
        <p><strong>내용:</strong> {postDTO.postContent}</p>

        {imageName && (
          <>
            <hr />
            <p><strong>사진:</strong></p>
            <Image
              src={`${SERVER_URL}/post/${imageName}`}
              alt="신고된 게시글 이미지"
              fluid
              style={{ maxHeight: '300px', objectFit: 'contain', backgroundColor: '#e9ecef' }}
            />
          </>
        )}
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

export default PostReportModal;