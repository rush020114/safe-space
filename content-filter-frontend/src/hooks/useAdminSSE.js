  import { useEffect } from "react"

  const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

  const useAdminSSE = ({token, onReport}) => {
    useEffect(() => {
      if(!token) return

      // sse 연결
      const eventSource = new EventSource(`${API_URL}/admin/reports/stream?token=${token}`);

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

      // 컴포넌트 언마운트시 연결 종료(useEffect의 return 함수는 cleanup 함수)
      return () => eventSource.close();
    }, [token, onReport]);
  }

  export default useAdminSSE