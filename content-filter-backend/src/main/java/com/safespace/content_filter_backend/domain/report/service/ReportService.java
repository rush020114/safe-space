package com.safespace.content_filter_backend.domain.report.service;

import com.safespace.content_filter_backend.domain.admin.service.SseEmitterService;
import com.safespace.content_filter_backend.domain.comment.dto.CommentDTO;
import com.safespace.content_filter_backend.domain.comment.mapper.CommentMapper;
import com.safespace.content_filter_backend.domain.member.mapper.MemberMapper;
import com.safespace.content_filter_backend.domain.post.dto.PostDTO;
import com.safespace.content_filter_backend.domain.post.mapper.PostMapper;
import com.safespace.content_filter_backend.domain.report.dto.ReportDTO;
import com.safespace.content_filter_backend.domain.report.mapper.ReportMapper;
import com.safespace.content_filter_backend.domain.report.model.ReportStatus;
import com.safespace.content_filter_backend.domain.report.model.ReportTargetType;
import com.safespace.content_filter_backend.domain.sanction.service.SanctionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportMapper reportMapper;
  private final MemberMapper memberMapper;
  private final PostMapper postMapper;
  private final CommentMapper commentMapper;
  private final SseEmitterService sseEmitterService;
  private final SanctionService sanctionService;

  // 신고 등록
  public void regReport(ReportDTO reportDTO){
    String reason = reportDTO.getReportReason();

    // 중복 신고라면
    if(reportMapper.getDupReport(reportDTO) == 1){
      throw new RuntimeException("중복된 신고입니다.");
    }

    // 신고 사유가 없다면
    if(reason == null || reason.isEmpty())
      throw new RuntimeException("신고 사유를 선택해주십시오");

    reportMapper.regReport(reportDTO);
    log.info("SSE 알림 전송 시작: {}", reportDTO);
    sseEmitterService.sendToAll(reportDTO);
  }

  // 관리자 신고 조회
  public List<ReportDTO> getReportListForAdmin(String targetType){

    // 게시글 유형 파악 메서드
    String type = ReportTargetType.from(targetType).name();

    return reportMapper.getReportListForAdmin(type);
  }

  // 관리자 신고 처리
  @Transactional(rollbackFor = Exception.class)
  public void handleReport(ReportDTO reportDTO, int adminId) {

    String status = ReportStatus.from(reportDTO.getReportStatus()).name();

    if (status.equals("APPROVED")) {
      approveReport(reportDTO, adminId);
    } else if (status.equals("REJECTED")) {
      rejectReport(reportDTO);
    } else {
      throw new RuntimeException("처리 가능한 신고 상태는 APPROVED 또는 REJECTED입니다. 현재 상태: " + status);
    }
  }

  // 신고 승인
  private void approveReport(ReportDTO reportDTO, int adminId){
    updateReportStatus(reportDTO);
    ReportTarget target = extractTarget(reportDTO);
    applyWarningAndFilter(target);

    // 제재 로직 분리
    sanctionService.applySanctionIfNeeded(target.memId, adminId);
  }

  // 신고 반려
  private void rejectReport (ReportDTO reportDTO){
    reportMapper.handleReport(reportDTO);
  }

  // 신고 상태 업데이트
  private void updateReportStatus (ReportDTO reportDTO){
    int updated = reportMapper.handleReport(reportDTO);
    if(updated == 0){
      throw new RuntimeException("이미 처리된 신고입니다.");
    }
  }

  // 게시글/댓글 판별 함수
  private ReportTarget extractTarget(ReportDTO reportDTO){
    String type = ReportTargetType.from(reportDTO.getTargetType()).name();
    boolean isPost = type.equals("POST");

    if(isPost){
      PostDTO postDTO = reportDTO.getPostDTO();
      return new ReportTarget(postDTO.getMemId(), postDTO.getPostId(), true);
    } else {
      CommentDTO commentDTO = reportDTO.getCommentDTO();
      return new ReportTarget(commentDTO.getMemId(), commentDTO.getCmtId(), false);
    }
  }

  // 경고 횟수 증가 및 필터링
  private void applyWarningAndFilter(ReportTarget reportTarget){
    memberMapper.addWarningCnt(reportTarget.memId);

    if(reportTarget.isPost){
      postMapper.filterPost(reportTarget.contentId);
    } else {
      commentMapper.filterComment(reportTarget.contentId);
    }
  }

  // static nested 클래스
  // static은 Outer 클래스의 자리를 잠시 빌려 쓴다 생각하고 그 클래스와 아무 상관 없다고 보는 것이 이해가 편하다.
  // 그러므로 자신을 감싸는 외부 클래스의 인스턴스와 상관 없이 static nested 클래스의 인스턴스 생성이 가능하다.
  // 인스턴스 생성 문법 : 외부클래스.static nested 클래스명 = new 외부클래스.static nested 클래스명();
  // outer 클래스 안의 static끼리는 서로 자원을 공유한다.(static nested 클래스 안의 메서드는 static 멤버변수를 사용할 수 있다.)
  // 단순한 분기 + 상태 전달이 필요한 상황에서는
  // static nested class로 응집력 있게 묶는 방식이 가장 실용적이고 유지보수에 강한 구조
  @AllArgsConstructor
  private static class ReportTarget{
    int memId;
    int contentId;
    boolean isPost;
  }
}
