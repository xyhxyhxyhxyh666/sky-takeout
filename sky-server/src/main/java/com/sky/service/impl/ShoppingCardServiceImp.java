package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCardMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCardService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShoppingCardServiceImp implements ShoppingCardService {

    @Autowired
    private ShoppingCardMapper shoppingCardMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public Result add(ShoppingCartDTO shoppingCartDTO) {

        log.info("当前用户ID: {}", BaseContext.getCurrentId());

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCardMapper.list(shoppingCart);
        if (list != null && list.size() == 1) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCardMapper.updateById(cart);
        }else {
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();

            if (dishId != null) {
                shoppingCart.setDishId(dishId);
                Dish dish = dishMapper.selectById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }

            if (setmealId != null) {
                shoppingCart.setSetmealId(setmealId);
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCardMapper.insert(shoppingCart);
            log.info("添加购物车成功：{}", shoppingCart);
        }
        return Result.success();
    }

    @Override
    public List<ShoppingCart> list() {

        ShoppingCart build = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> list = shoppingCardMapper.list(build);
        log.info("查询购物车成功：{}", list);
        return list;
    }

    @Override
    public Result sub(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCardMapper.list(shoppingCart);
        if (list != null && list.size() == 1) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() - 1);
            shoppingCardMapper.updateById(cart);
            return Result.success();
        }
        return Result.error("操作失败");
    }

    @Override
    public Result clean() {
        log.info("清空购物车");
        Long currentId = BaseContext.getCurrentId();
        shoppingCardMapper.deleteById(currentId);
        return Result.success();
    }
}
