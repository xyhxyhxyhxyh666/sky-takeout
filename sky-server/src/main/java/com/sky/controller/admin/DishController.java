package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        Result r = dishService.queryByPage(dishPageQueryDTO);
        return r;

    }

}
