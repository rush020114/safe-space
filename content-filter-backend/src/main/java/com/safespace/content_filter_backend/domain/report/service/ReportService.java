package com.safespace.content_filter_backend.domain.report.service;

import com.safespace.content_filter_backend.domain.admin.service.SseEmitterService;
import com.safespace.content_filter_backend.domain.comment.mapper.CommentMapper;
import com.safespace.content_filter_backend.domain.member.mapper.MemberMapper;
import com.safespace.content_filter_backend.domain.post.mapper.PostMapper;
import com.safespace.content_filter_backend.domain.report.dto.ReportDTO;
import com.safespace.content_filter_backend.domain.report.mapper.ReportMapper;
import com.safespace.content_filter_backend.domain.report.model.ReportStatus;
import com.safespace.content_filter_backend.domain.report.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public void handleReport(ReportDTO reportDTO) {

    String status = ReportStatus.from(reportDTO.getReportStatus()).name();
    String type = ReportTargetType.from(reportDTO.getTargetType()).name();

    boolean isApproved = status.equals("APPROVED");
    boolean isRejected = status.equals("REJECTED");
    boolean isPost = type.equals("POST");

    if (isApproved) {
      // 신고 상태 처리
      reportMapper.handleReport(reportDTO);

      // 신고 대상 정보 추출
      int targetMemId = isPost
              ? reportDTO.getPostDTO().getMemId()
              : reportDTO.getCommentDTO().getMemId();

      int targetContentId = isPost
              ? reportDTO.getPostDTO().getPostId()
              : reportDTO.getCommentDTO().getCmtId();

      // 회원 상태 처리
      memberMapper.handleMemberStatus(targetMemId);

      // 콘텐츠 필터링 처리
      if (isPost) {
        postMapper.filterPost(targetContentId);
      } else {
        commentMapper.filterComment(targetContentId);
      }

    } else if (isRejected) {
      // 신고 상태 처리
      reportMapper.handleReport(reportDTO);
    } else {
      throw new IllegalArgumentException("처리 가능한 신고 상태는 APPROVED 또는 REJECTED입니다. 현재 상태: " + status);
    }
  }
}
