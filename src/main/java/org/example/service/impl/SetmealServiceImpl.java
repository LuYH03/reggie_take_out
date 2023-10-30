package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.CustomExcption;
import org.example.common.R;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;
import org.example.mapper.SetmealMapper;
import org.example.service.SetmealDishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     *  新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        // 保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        List<SetmealDish> dishList = setmealDto.getSetmealDishes();
        dishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishList);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);  // 状态1 在售中，不能删除

        int count = this.count(queryWrapper);
        if (count > 0){
            // 如果不能删除，抛出一个业务异常
            throw new CustomExcption("套餐正在上架售卖中，请先下架后再删除");
        }
        // 如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        // delete form setmeal_dish where setmealId in(1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        // 删除关系表中的数据
        setmealDishService.remove(lambdaQueryWrapper);
    }


    /**
     * 套餐修改，同时修改套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public R<String> updateWithDish(SetmealDto setmealDto) {

        if (setmealDto == null){
            return R.error("请求异常");
        }

        if (setmealDto.getSetmealDishes() == null){
            return R.error("套餐中没有菜品,请先添加菜品");
        }

        // 删除套餐基本信息
        this.updateById(setmealDto);

        // 先删除菜品关联关系，再填充新的数据
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(query);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        setmealDishService.saveBatch(setmealDishes);
        return R.success("套餐修改成功");
    }
}
