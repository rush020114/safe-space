import { useState } from "react";
import { axiosInstance } from "../apis/axiosInstance";

const useReport = (reporterId) => {
  const [reportTarget, setReportTarget] = useState({ type: "", id: null });
  const [showReportModal, setShowReportModal] = useState(false);

  const openReportModal = (type, id) => {
    setReportTarget({ type, id });
    setShowReportModal(true);
  };

  const closeReportModal = () => setShowReportModal(false);

  const submitReport = async (reason) => {
    try{
      const res = await axiosInstance.post(`/reports`, {
        targetType: reportTarget.type,
        targetId: reportTarget.id,
        reporterId,
        reportReason: reason
      });
      alert(res.data)
    }
    catch (e) {
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
    };
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