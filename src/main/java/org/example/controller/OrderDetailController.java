package org.example.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.Orders;
import org.example.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
@Slf4j
@Api(tags = "订单明细相关接口")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

}
