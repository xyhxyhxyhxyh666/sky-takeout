package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getSetByCategory(Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dishList = setmealService.getSetByCategory(categoryId);
        return Result.success(dishList);
    }

    @PostMapping
    @ApiOperation("新增套餐")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){

        log.info("新增套餐：{}", setmealDTO);
        Result r = setmealService.addSetmeal(setmealDTO);
        return r;

    }

}
