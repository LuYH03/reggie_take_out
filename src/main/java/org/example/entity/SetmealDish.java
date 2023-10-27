package org.example.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系
 */
@Data
@ApiModel("套餐和菜品数据")
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("套餐id")
    //套餐id
    private Long setmealId;

    @ApiModelProperty("菜品id")
    //菜品id
    private Long dishId;

    @ApiModelProperty("菜品名称")
    //菜品名称 （冗余字段）
    private String name;
    @ApiModelProperty("菜品原价")
    //菜品原价
    private BigDecimal price;
    @ApiModelProperty("份数")
    //份数
    private Integer copies;

    @ApiModelProperty("排序")
    //排序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否删除
    private Integer isDeleted;
}
