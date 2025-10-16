package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class SetmealServiceImp implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public Result addSetmeal(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmeal.setCreateTime(LocalDateTime.now());
        int rows = setmealMapper.addSetmeal(setmeal);

        Long setId = setmealDTO.getId();

        List<SetmealDish> dishes= setmealDTO.getSetmealDishes();
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setId);
            setmealDishMapper.addSetmealDish(dish);
        }

        return Result.success(rows);

    }

    @Override
    public List<Dish> getSetByCategory(Long categoryId) {

        Dish build = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<Dish> dishList = setmealMapper.getDishByCategory(build);
        return dishList;

    }
}
