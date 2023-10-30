package org.example.dto;

import lombok.Data;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
