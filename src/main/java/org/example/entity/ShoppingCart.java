package org.example.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
@ApiModel("购物车")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("名称")
    //名称
    private String name;

    @ApiModelProperty("用户id")
    //用户id
    private Long userId;

    @ApiModelProperty("菜品id")
    //菜品id
    private Long dishId;

    @ApiModelProperty("套餐id")
    //套餐id
    private Long setmealId;

    @ApiModelProperty("口味")
    //口味
    private String dishFlavor;

    @ApiModelProperty("数量")
    //数量
    private Integer number;

    @ApiModelProperty("金额")
    //金额
    private BigDecimal amount;

    @ApiModelProperty("图片")
    //图片
    private String image;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
