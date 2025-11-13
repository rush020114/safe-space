package com.safespace.content_filter_backend.domain.sanction.mapper;

import com.safespace.content_filter_backend.domain.sanction.dto.SanctionDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SanctionMapper {
  // 제재 등록
  void regSanction(SanctionDTO sanctionDTO);
}
