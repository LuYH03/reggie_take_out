package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.example.entity.ShoppingCart;
import org.example.service.OrderDetailService;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Transactional
@Slf4j
@Api(tags = "购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "购物车新增接口")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);
        // 设置用户id，指定当前是那个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            // 添加购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        // 查询当前菜品或者套餐是否在购物车中
        if (cartServiceOne != null) {
            // 如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            // 如果不存在，则添加到购物车，数量默认为一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }


    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "购物车条件查询接口")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "购物车批量删除接口")
    public R<String> delete(){
        // SQL：delete from shoppingCart where user_id = ?
        shoppingCartService.clean();
        return R.success("数据清空成功");
    }


    /**
     * 移动端购物车数量减少
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    @Transactional
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){  // 传过来的是菜品id，需要修改菜品的数量
            //通过dishId查出购物车对象
            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>()
                    .eq(ShoppingCart::getDishId,dishId)
                    .eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart cartdish = shoppingCartService.getOne(queryWrapper);
            cartdish.setNumber(cartdish.getNumber() -1);
            Integer lastNumber = cartdish.getNumber();
            if (lastNumber > 0){
                shoppingCartService.updateById(cartdish);
            }else if (lastNumber == 0){
                shoppingCartService.removeById(cartdish.getId());
            }else if (lastNumber < 0){
                return R.error("操作异常");
            }
            return R.success(cartdish);
         }

        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null){   // 传过来的是套餐id，修改套餐数量
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>()
                    .eq(ShoppingCart::getSetmealId,setmealId)
                    .eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart cartSetmeal = shoppingCartService.getOne(queryWrapper);

            cartSetmeal.setNumber(cartSetmeal.getNumber() -1);
            Integer lastNumber = cartSetmeal.getNumber();
            if (lastNumber > 0){
                shoppingCartService.updateById(cartSetmeal);
            }else if (lastNumber == 0){
                shoppingCartService.removeById(cartSetmeal.getId());
            }else if (lastNumber < 0){
                return R.error("操作异常");
            }
            return R.success(cartSetmeal);
        }
        // 如果两个if判断都进不去
        return R.error("操作异常");
    }





}
