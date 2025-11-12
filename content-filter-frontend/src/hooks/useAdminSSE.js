import { useEffect } from "react"
import { SERVER_URL } from "../constants/appConst"

const useAdminSSE = ({token, onReport}) => {
  useEffect(() => {
    if(!token) return

    const eventSource = new EventSource(`${SERVER_URL}/admin/reports/stream`, {
      headers: {
        Authorization: token // Bearer가 붙어있으므로
      }
    });

    // spring에서 보낸 더미 데이터 (create할 때)
    eventSource.addEventListener('connect', e => {
      console.log('SSE 연결 성공:', e.data)
    });

    // sendToALl 메서드 실행 시 실행될 eventSource
    eventSource.addEventListener('newReport', e => {
      try {
        const newReport = JSON.parse(e.data);
        if(onReport) onReport(newReport);
      } catch (e) {
        console.log('JSON 파싱 실패:', e.data);
      };
    });
    eventSource.onerror = e => {
      console.error('SSE 에러:', e)
    };
    return () => eventSource.close();
  }, [token, onReport]);
}

export default useAdminSSE