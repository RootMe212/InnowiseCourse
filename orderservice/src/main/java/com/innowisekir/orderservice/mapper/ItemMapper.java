package com.innowisekir.orderservice.mapper;

import com.innowisekir.orderservice.dto.create.CreateItemDTO;
import com.innowisekir.orderservice.dto.response.ItemDTO;
import com.innowisekir.orderservice.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

  ItemDTO toDTO(Item entity);

  @Mapping(target = "id", ignore = true)
  Item toEntity(CreateItemDTO dto);
}
