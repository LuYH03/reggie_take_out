package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    // 删除套餐，同时需要删除套餐和菜品的关联关系
    public void deleteWithDish(List<Long> ids);

    // 修改套餐，同时修改套餐和菜品的关联关系
    void updateWithDish(SetmealDto setmealDto);
}
