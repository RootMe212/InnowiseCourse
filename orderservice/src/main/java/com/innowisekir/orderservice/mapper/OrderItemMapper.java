package com.innowisekir.orderservice.mapper;

import com.innowisekir.orderservice.dto.create.CreateOrderItemDTO;
import com.innowisekir.orderservice.dto.response.OrderItemDTO;
import com.innowisekir.orderservice.entity.Item;
import com.innowisekir.orderservice.entity.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(target = "itemId", source = "item.id")
  @Mapping(target = "itemName", ignore = true)
  @Mapping(target = "itemPrice", ignore = true)
  OrderItemDTO toDTO(OrderItem entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "item", expression = "java(createItemFromId(dto.getItemId()))")
  OrderItem toEntity(CreateOrderItemDTO dto);

  default Item createItemFromId(Long itemId) {
    if (itemId == null) {
      return null;
    }
    Item item = new Item();
    item.setId(itemId);
    return item;
  }

  @AfterMapping
  default void enrichItemFields(OrderItem entity, @MappingTarget OrderItemDTO dto) {
    if (entity.getItem() != null) {
      dto.setItemName(entity.getItem().getName());
      dto.setItemPrice(entity.getItem().getPrice());
    }
  }
}
