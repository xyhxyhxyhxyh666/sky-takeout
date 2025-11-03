package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders order);

    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{checkOutTime} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, String orderNumber);

    Page<Orders> queryPage(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders selectById(String id);

    @Select("select count(status) from orders where status = #{toBeConfirmed}")
    Integer countStatus(Integer toBeConfirmed);


    @Select("select * from orders where status = #{toBeConfirmed} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeOut(Integer toBeConfirmed, LocalDateTime time);
}
