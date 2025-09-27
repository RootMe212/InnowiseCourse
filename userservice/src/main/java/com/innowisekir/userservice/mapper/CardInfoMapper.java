package com.innowisekir.userservice.mapper;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "holder", source = "holder")
  CardInfoDTO toDTO(CardInfo cardInfo);

  @Mapping(target = "user", ignore = true)
  CardInfo toEntity(CardInfoDTO cardInfoDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  CardInfo toEntityForUpdate(CardInfoDTO cardInfoDTO);

}
