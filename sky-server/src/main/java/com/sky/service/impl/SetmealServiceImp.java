package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImp implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public Result addSetmeal(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        int rows = setmealMapper.addSetmeal(setmeal);

        Long setmealId = setmealMapper.queryIdByName(setmeal.getName());

        List<SetmealDish> dishes= setmealDTO.getSetmealDishes();
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setmealId);
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

    @Override
    public PageResult queryByPage(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.queryByPage(setmealPageQueryDTO);

        long total = page.getTotal();
        List<SetmealVO> result = page.getResult();
        return new PageResult(total, result);

    }

    @Override
    public SetmealVO selectById(Long id) {

        Setmeal setmeal = setmealMapper.getSetmealById(id);

        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishes(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;

    }

    @Transactional
    @Override
    public Result updateSetmeal(SetmealDTO setmealDTO) {

        Long id = setmealDTO.getId();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setUpdateTime(LocalDateTime.now());

        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmealId(id);
        List<SetmealDish> setmealDishes= setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
            setmealDishMapper.addSetmealDish(setmealDish);
        }

        return Result.success();

    }

    @Transactional
    @Override
    public Result delete(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal == null) {
                return Result.error("菜品不存在");
            }
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                return Result.error("起售中的菜品不能删除");
            }
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        }
        return Result.success();
    }

    @Override
    public Result startOrStop(Integer status, Long id) {

        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishes(id);
        for (SetmealDish setmealDish : setmealDishes) {
            Long dishId = setmealDish.getDishId();
            Dish dish = dishMapper.selectById(dishId);
            if (dish.getStatus() == StatusConstant.DISABLE && status == StatusConstant.ENABLE) {
                return Result.error("关联的菜品未起售");
            }
        }

        setmealMapper.update(setmeal);
        return Result.success();

    }
}
