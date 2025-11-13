package com.safespace.content_filter_backend.common.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {
  // 업로드 경로
  private static final String BASE_URL = "C:\\dev\\safe-space\\content-filter-backend\\src\\main\\resources\\static\\";

  // 단일 파일 업로드
  public static String[] uploadFile(MultipartFile img, UploadPath uploadPath){
    log.info("업로드 이미지 : {}", img.getOriginalFilename());
    // 최종 경로
    String fullPath = BASE_URL + uploadPath.getPath();

    // 폴더가 없으면 생성
    File directory = new File(fullPath);

    if(!directory.exists()){
      directory.mkdirs();
    }

    String attachedImgName = UUID.randomUUID().toString();

    String extension = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf('.'));

    // 파일명
    attachedImgName += extension;

    File savefile = new File(fullPath + File.separator + attachedImgName);

    try {
      log.info("첨부파일명 : {}", attachedImgName);
      img.transferTo(savefile);
      if (!savefile.exists() || savefile.length() == 0) {
        throw new RuntimeException("");
      }
    } catch (Exception e) {
      throw new RuntimeException("파일 저장 실패 : 경로 문제");
    }

    return new String[] {img.getOriginalFilename(), attachedImgName};
  }
}
