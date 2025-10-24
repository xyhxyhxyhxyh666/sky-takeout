package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    Result add(DishDTO dishDTO);

    PageResult queryByPage(DishPageQueryDTO dishPageQueryDTO);

    Result delete(List<Long> ids);

    DishVO getById(Long id);

    Result update(DishDTO dishDTO);

    List<Dish> dishListByCategoryId(Long categoryId);

    Result setStatus(Integer status, Long id);

    List<DishVO> listWithFlavor(Dish dish);
}
