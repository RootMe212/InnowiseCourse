package com.innowisekir.orderservice.mapper;

import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.response.OrderDTO;
import com.innowisekir.orderservice.entity.Order;
import com.innowisekir.orderservice.entity.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

  @Mapping(target = "items", source = "orderItems")
  OrderDTO toDTO(Order entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "orderItems", source = "items")
  Order toEntity(CreateOrderDTO dto);

  @AfterMapping
  default void linkOrderItems(@MappingTarget Order entity) {
    if (entity.getOrderItems() != null) {
      for (OrderItem orderItem : entity.getOrderItems()) {
        orderItem.setOrder(entity);
      }
    }
  }

}
