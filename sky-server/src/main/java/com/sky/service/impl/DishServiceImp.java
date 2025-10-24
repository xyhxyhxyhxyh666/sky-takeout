package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImp implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public Result add(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setCreateTime(LocalDateTime.now());
        dishMapper.insert(dish);

        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        dishFlavorMapper.insertBatch(flavors);

        return Result.success();
    }

    @Override
    public PageResult queryByPage(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.queryByPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Result delete(List<Long> ids) {

        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                return Result.error("起售中的菜品不能删除");
            }
        }

        List<Long> setMeals = setmealMapper.selectIdsByDishIds(ids);
        if (setMeals != null && setMeals.size() > 0) {
            return Result.error("关联了套餐，不能删除");
        }

        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }

        return Result.success();
    }

    @Override
    public DishVO getById(Long id) {

        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    public Result update(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);

        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDTO.getId());
        }

        if (flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.insertBatch(flavors);
        }

        return Result.success();
    }

    @Override
    public List<Dish> dishListByCategoryId(Long categoryId) {

        Dish build = Dish.builder().status(StatusConstant.ENABLE).categoryId(categoryId).build();
        List<Dish> dishes = dishMapper.selectByCategoryId(build);
        return dishes;
    }

    @Override
    public Result setStatus(Integer status, Long id) {

        Dish build = Dish.builder().status(status).id(id).build();
        dishMapper.update(build);

        return Result.success();

    }

    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
