package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.common.BaseContext;
import org.example.common.R;
import org.example.dto.OrderDto;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.entity.ShoppingCart;
import org.example.service.OrderDetailService;
import org.example.service.OrdersService;
import org.example.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "订单分页查询接口")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String number,String beginTime, String endTime){

        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>()
                .like(number !=null && number != "",Orders::getNumber,number)
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 移动端用户查询订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    @ApiOperation(value = "用户订单分页查询接口")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageDto = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>()
                //这里是直接把当前用户分页的结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
                .eq(Orders::getUserId, BaseContext.getCurrentId())
                .orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,queryWrapper);

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            Long id = item.getId();
            List<OrderDetail> orderDetails = getOrderDetailListById(id);
            BeanUtils.copyProperties(item, orderDto);
            orderDto.setOrderDetails(orderDetails);
            return orderDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);

        return R.success(pageDto);
    }
    //抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListById(Long OrderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId,OrderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }


    /**
     * 修改订单信息
     * @param map
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改订单信息接口")
    public R<String> update(@RequestBody Map<String,String> map){
        log.info("map = {}",map);
        String id = map.get("id");
        long orderId = Long.parseLong(id);
        String status = map.get("status");
        int orderStatus = Integer.parseInt(status);

        if (orderStatus == 0 || orderId == 0) {
            return R.error("传入信息不合法");
        }
        Orders order = ordersService.getById(id);
        order.setStatus(orderStatus);
        ordersService.updateById(order);

        return R.success("订单信息修改成功");
    }


    /**
     * 客户端用户再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    @ApiOperation(value = "客户端再来一单接口")
    public R<String> againSub(@RequestBody Map<String,String> map) {

        String StrId = map.get("id");
        long id = Long.parseLong(StrId);
        // 查询当前用户订单数据
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
        // 清除购物车数据
        shoppingCartService.clean();
        Long userId = BaseContext.getCurrentId();
        // 给订单明细重新赋值
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                // 如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            }
            if (setmealId != null) {
                // 如果是套餐那就添加套餐的查询条件
                shoppingCart.setSetmealId(setmealId);
            }

            shoppingCart.setName(item.getName());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setDishFlavor(item.getDishFlavor());

            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("操作成功");

    }


}
