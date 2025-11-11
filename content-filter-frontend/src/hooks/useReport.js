import { useState } from "react";
import { axiosInstance } from "../apis/axiosInstance";
import { SERVER_URL } from "../constants/appConst";

const useReport = (reporterId) => {
  const [reportTarget, setReportTarget] = useState({ type: "", id: null });
  const [showReportModal, setShowReportModal] = useState(false);

  const openReportModal = (type, id) => {
    setReportTarget({ type, id });
    setShowReportModal(true);
  };

  const closeReportModal = () => setShowReportModal(false);

  const submitReport = async (reason) => {
    await axiosInstance.post(`${SERVER_URL}/report`, {
      targetType: reportTarget.type,
      targetId: reportTarget.id,
      reporterId,
      reportReason: reason
    });
    closeReportModal();
  };

  return {
    showReportModal,
    openReportModal,
    closeReportModal,
    submitReport
  };
};

export default useReport;