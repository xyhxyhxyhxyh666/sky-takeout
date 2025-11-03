package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCardMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void updateById(ShoppingCart cart);

    void insert(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteById(Long currentId);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
