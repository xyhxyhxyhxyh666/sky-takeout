package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

import java.util.List;

public interface ShoppingCardService {
    Result add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    Result sub(ShoppingCartDTO shoppingCartDTO);

    Result clean();
}
