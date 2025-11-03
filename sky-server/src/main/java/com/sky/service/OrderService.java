package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    Result<OrderSubmitVO> submit(OrdersSubmitDTO ordersSubmitDTO);
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    void paySuccess(String outTradeNo);

    PageResult pageOrders(int page, int pageSize, Integer status);

    Result cancel(String id);

    Result<OrderVO> orderDetail(String id);

    Result repetition(Long id);

    PageResult pageQueryAdmin(OrdersPageQueryDTO ordersPageQueryDTO);

    Result<OrderVO> AdminOrderDetail(String id);

    Result<OrderStatisticsVO> statistics();

    Result confirm(OrdersConfirmDTO ordersConfirmDTO);

    Result Rejection(OrdersRejectionDTO ordersRejectionDTO);

    Result AdminCancel(OrdersCancelDTO ordersCancelDTO);

    Result delivery(Long id);

    Result complete(Long id);
}
