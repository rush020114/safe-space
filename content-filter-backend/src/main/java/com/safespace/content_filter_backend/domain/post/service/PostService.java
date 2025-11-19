package com.safespace.content_filter_backend.domain.post.service;

import com.safespace.content_filter_backend.infra.filtering.ProfanityFilter;
import com.safespace.content_filter_backend.domain.post.dto.PostDTO;
import com.safespace.content_filter_backend.domain.post.dto.PostImgDTO;
import com.safespace.content_filter_backend.domain.post.mapper.PostMapper;
import com.safespace.content_filter_backend.common.util.FileUploadUtil;
import com.safespace.content_filter_backend.common.util.UploadPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
  public final PostMapper postMapper;
  public final ProfanityFilter profanityFilter;

  // 게시글 등록
  @Transactional(rollbackFor = Exception.class)
  public void regPost(MultipartFile postImg, PostDTO postDTO){
    String title = postDTO.getPostTitle();
    String content = postDTO.getPostContent();

    // 제목이 없다면
    if(title == null || title.trim().isEmpty())
      throw new RuntimeException("제목을 입력해주세요.");

    // 내용이 없다면
    if(content == null || content.trim().isEmpty())
      throw new RuntimeException("내용을 입력해주세요");

    // 욕설 판단
    if (profanityFilter.containsProfanityHybrid(title))
      throw new RuntimeException("제목에 부적절한 단어가 포함되어 있습니다.");

    if(profanityFilter.containsProfanityHybrid(content)){
      throw new RuntimeException("내용에 부적절한 단어가 포함되어 있습니다.");
    }

    // 게시글 번호 조회
    int postId = postMapper.getPostId();
    postDTO.setPostId(postId);

    log.info("postDTO의 값 : {}", postDTO);
    // 게시글 등록
    postMapper.regPost(postDTO);

    // 이미지 등록
    if(postImg != null && !postImg.isEmpty()){
      PostImgDTO postImgDTO = new PostImgDTO();
      String[] imgName = FileUploadUtil.uploadFile(postImg, UploadPath.POST);

      postImgDTO.setOriginImgName(imgName[0]);
      postImgDTO.setAttachedImgName(imgName[1]);
      postImgDTO.setPostId(postId);
      postMapper.regPostImg(postImgDTO);
    }
  }

  // 게시글 목록 조회
  public List<PostDTO> getPostList(){
    log.info("getPostList 실행");
    return postMapper.getPostList();
  }

  // 게시글 상세 조회
  public PostDTO getPostDetail(int postId){
    return postMapper.getPostDetail(postId);
  }
}
