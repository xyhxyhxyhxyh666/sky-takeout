package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

@Service
public class WorkSpaceServiceImp implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public Result<BusinessDataVO> getBusinessData(LocalDateTime begin, LocalDateTime end) {

        HashMap<Object, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", Orders.COMPLETED);
        Integer newUser = userMapper.getNewUserByDate(map);
        Integer totalUserByDate = userMapper.getTotalUserByDate(map);
        Double turnoverByDate = orderMapper.getTurnoverByDate(map);
        Integer totalOrdersByDate = orderMapper.getTotalOrdersByDate(map);
        Integer validOrdersByDate = orderMapper.getValidOrdersByDate(map);
        Double orderCompletionRate = 0.0;
        if (totalOrdersByDate != 0) {
            orderCompletionRate = validOrdersByDate.doubleValue() / totalOrdersByDate;
        }
        Double unitPrice = 0.0;
        if (totalUserByDate != 0 && turnoverByDate != null) {
            unitPrice = turnoverByDate / totalUserByDate;
        }
        if (turnoverByDate == null) {
            turnoverByDate = 0.0;
        }

        BusinessDataVO build = BusinessDataVO.builder()
                .newUsers(newUser)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .turnover(turnoverByDate)
                .validOrderCount(validOrdersByDate)
                .build();

        return Result.success(build);

    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countStatusByMap(map);

        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countStatusByMap(map);

        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countStatusByMap(map);

        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countStatusByMap(map);

        Integer allOrders = orderMapper.getTotal(map);
        OrderOverViewVO build = OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
        return build;
    }

    @Override
    public DishOverViewVO getDishOverView() {

        Integer start = dishMapper.countByStatus(StatusConstant.ENABLE);
        Integer stop = dishMapper.countByStatus(StatusConstant.DISABLE);

        DishOverViewVO build = DishOverViewVO.builder()
                .sold(start)
                .discontinued(stop)
                .build();
        return build;
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {

        Integer start = setmealMapper.countByStatus(StatusConstant.ENABLE);
        Integer stop = setmealMapper.countByStatus(StatusConstant.DISABLE);

        SetmealOverViewVO build = SetmealOverViewVO.builder()
                .sold(start)
                .discontinued(stop)
                .build();
        return build;
    }
}
