package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Orders;
import org.example.mapper.OrdersMapper;

public interface OrdersService extends IService<Orders> {
    // 用户下单
    public void submit(Orders orders);
}
