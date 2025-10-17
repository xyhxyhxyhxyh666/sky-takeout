package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("新增菜品信息：{}", dishDTO);
        Result r = dishService.add(dishDTO);
        return r;
    }

    @GetMapping("page")
    public Result queryByPage(DishPageQueryDTO dishPageQueryDTO){

        log.info("分页查询菜品信息：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.queryByPage(dishPageQueryDTO);
        Result r = Result.success(pageResult);
        return r;

    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}", ids);
        Result r = dishService.delete(ids);
        return r;
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result getById(@PathVariable Long id){

        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);

    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){

        log.info("修改菜品：{}", dishDTO);
        Result r = dishService.update(dishDTO);
        return r;
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> dishList(Long categoryId){

        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dishes = dishService.dishListByCategoryId(categoryId);
        return Result.success(dishes);

    }

}
