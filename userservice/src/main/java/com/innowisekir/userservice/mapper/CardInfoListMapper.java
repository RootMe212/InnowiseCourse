package com.innowisekir.userservice.mapper;

import com.innowisekir.userservice.entity.CardInfo;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {CardInfoMapper.class})
public interface CardInfoListMapper {


  List<CardInfo> toDTOList(List<CardInfo> cardInfoList);

}
