package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;

import java.util.List;

public interface SetmealService {
    Result addSetmeal(SetmealDTO setmealDTO);

    List<Dish> getSetByCategory(Long categoryId);
}
