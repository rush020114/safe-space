import { Modal, Button, Form } from "react-bootstrap";
import { useState } from "react";

const ReportModal = ({ show, handleClose, handleSubmit }) => {
  const [selectedReason, setSelectedReason] = useState("");
  const [customReason, setCustomReason] = useState("");

  const onSubmit = () => {
    const finalReason = selectedReason === "기타" ? customReason : selectedReason;
    handleSubmit(finalReason);
    setSelectedReason("");
    setCustomReason("");
    handleClose();
  };

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>댓글 신고</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Check
            type="radio"
            label="부적절한 단어 사용"
            name="reportReason"
            value="부적절한 단어 사용"
            checked={selectedReason === "부적절한 단어 사용"}
            onChange={(e) => setSelectedReason(e.target.value)}
          />
          <Form.Check
            type="radio"
            label="유해하거나 불쾌한 이미지 포함"
            name="reportReason"
            value="유해하거나 불쾌한 이미지 포함"
            checked={selectedReason === "유해하거나 불쾌한 이미지 포함"}
            onChange={(e) => setSelectedReason(e.target.value)}
          />
          <Form.Check
            type="radio"
            label="감정적이거나 선동적인 표현 사용"
            name="reportReason"
            value="감정적이거나 선동적인 표현 사용"
            checked={selectedReason === "감정적이거나 선동적인 표현 사용"}
            onChange={(e) => setSelectedReason(e.target.value)}
          />
          <Form.Check
            type="radio"
            label="기타"
            name="reportReason"
            value="기타"
            checked={selectedReason === "기타"}
            onChange={(e) => setSelectedReason(e.target.value)}
          />

          {selectedReason === "기타" && (
            <Form.Group controlId="customReason" className="mt-3">
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="신고 사유를 입력해주세요"
                value={customReason}
                onChange={(e) => setCustomReason(e.target.value)}
              />
            </Form.Group>
          )}
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={handleClose}>
          취소
        </Button>
        <Button
          variant="danger"
          onClick={onSubmit}
          disabled={
            !selectedReason || (selectedReason === "기타" && !customReason.trim())
          }
        >
          신고하기
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ReportModal;