package org.example.dto;

import org.example.entity.Dish;
import org.example.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 *  DTO 全称为Data Transfer Object 即数据传输对象，一般用于展示层与服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {    // 继承了Dish 中的实体属性


    // 封装了flavors实体中的属性
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
