package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper {


    @AutoFill(value = OperationType.INSERT)
    void addSetmealDish(SetmealDish dish);


}
