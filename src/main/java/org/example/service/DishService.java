package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.DishDto;
import org.example.entity.Dish;

public interface DishService extends IService<Dish> {

    // 新增菜品的菜品口味 同时操作两张表
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品，同时更新菜品口味
    public void updateWithFlavor(DishDto dishDto);
}
