package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    Result addSetmeal(SetmealDTO setmealDTO);

    List<Dish> getSetByCategory(Long categoryId);

    PageResult queryByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO selectById(Long id);

    Result updateSetmeal(SetmealDTO setmealDTO);

    Result delete(List<Long> ids);
}
