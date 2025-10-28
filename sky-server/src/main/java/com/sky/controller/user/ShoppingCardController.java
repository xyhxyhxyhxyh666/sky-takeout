package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "用户购物车接口")
@RequestMapping("/user/shoppingCart")
public class ShoppingCardController {

    @Autowired
    private ShoppingCardService shoppingCardService;

    @PostMapping("add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车");
        Result r = shoppingCardService.add(shoppingCartDTO);
        return r;
    }

    @GetMapping("list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){

        log.info("查看购物车");
        List<ShoppingCart> list = shoppingCardService.list();
        return Result.success(list);

    }

    @PostMapping("sub")
    @ApiOperation("减少菜品")
    public Result sub (@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("减少菜品");
        Result r = shoppingCardService.sub(shoppingCartDTO);
        return r;
    }

    @DeleteMapping("clean")
    @ApiOperation("清空购物车")
    public Result clean(){
        log.info("清空购物车");
        Result r = shoppingCardService.clean();
        return r;
    }

}
