package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImp  implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCardMapper shoppingCardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;


//    @Autowired
//    private WeChatPayUtil weChatPayUtil;

    @Override
    public Result<OrderSubmitVO> submit(OrdersSubmitDTO ordersSubmitDTO) {

        //异常情况的处理（收货地址为空、超出配送范围、购物车为空）
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart build = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCardMapper.list(build);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setUserId(userId);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        //向订单表插入1条数据
        orderMapper.insert(order);
        Long orderId = order.getId();

        //订单明细数据
        OrderDetail orderDetail = new OrderDetail();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetailMapper.insert(orderDetail);
        }
        //清理购物车中的数据
        shoppingCardMapper.deleteById(userId);

        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orderId)
                .orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .build();

        return Result.success(orderSubmitVO);

    }

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception{
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单

        //发现没有将支付时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + outTradeNo);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    @Override
    public PageResult pageOrders(int page, int pageSize, Integer status) {

        PageHelper.startPage(page, pageSize);

        Long currentId = BaseContext.getCurrentId();
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(currentId);
        ordersPageQueryDTO.setStatus(status);

        List<OrderVO> list = new ArrayList<>();

        Page<Orders> queryPage = orderMapper.queryPage(ordersPageQueryDTO);
        if (queryPage != null && queryPage.getTotal() > 0) {

            for (Orders orders : queryPage) {
                List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }

        }

        return new PageResult(queryPage.getTotal(), list);
    }

    @Override
    public Result cancel(String id) {

        Orders orderDB = orderMapper.selectById(id);
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (orderDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setUserId(BaseContext.getCurrentId());
        orders.setId(orderDB.getId());

        if (orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            orders.setStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消订单");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

        return Result.success();
    }

    @Override
    public Result<OrderVO> orderDetail(String id) {

        return getOrderVOResult(id);
    }

    @Override
    public Result repetition(Long id) {

        List<OrderDetail> orderDetails = orderDetailMapper.listByOrderId(id);
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCartList.add(shoppingCart);
        }

        shoppingCardMapper.insertBatch(shoppingCartList);

        return Result.success();
    }

    @Override
    public PageResult pageQueryAdmin(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> queryPage = orderMapper.queryPage(ordersPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : queryPage) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            List<OrderDetail> orderDetails = orderDetailMapper.listByOrderId(orders.getId());
            List<String> dishList = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetails) {
                String dish = orderDetail.getName() + "*" + orderDetail.getNumber();
                dishList.add(dish);
            }
            orderVO.setOrderDishes(String.join(";", dishList));
            orderVOList.add(orderVO);
        }

        return new PageResult(queryPage.getTotal(), orderVOList);

    }

    @Override
    public Result<OrderVO> AdminOrderDetail(String id) {

        return getOrderVOResult(id);

    }

    @Override
    public Result<OrderStatisticsVO> statistics() {

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Integer toBeConfirmed = Orders.TO_BE_CONFIRMED;
        Integer confirmed = Orders.CONFIRMED;
        Integer deliveryInProgress = Orders.DELIVERY_IN_PROGRESS;

        orderStatisticsVO.setToBeConfirmed(orderMapper.countStatus(toBeConfirmed));
        orderStatisticsVO.setConfirmed(orderMapper.countStatus(confirmed));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countStatus(deliveryInProgress));

        return Result.success(orderStatisticsVO);
    }

    @Override
    public Result confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders build = Orders.builder()
                .status(Orders.CONFIRMED)
                .id(ordersConfirmDTO.getId())
                .build();
        orderMapper.update(build);
        return Result.success();
    }

    @Override
    public Result Rejection(OrdersRejectionDTO ordersRejectionDTO) {

        Orders orders = orderMapper.selectById(String.valueOf(ordersRejectionDTO.getId()));
        if (orders == null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders build = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelReason("商家拒绝接单")
                .cancelTime(LocalDateTime.now())
                .build();


        orderMapper.update(build);
        return Result.success();
    }

    @Override
    public Result AdminCancel(OrdersCancelDTO ordersCancelDTO) {


        Orders build = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(build);

        return Result.success();
    }

    @Override
    public Result delivery(Long id) {

        Orders orders = orderMapper.selectById(String.valueOf(id));
        if (orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders build = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(build);
        return Result.success();
    }

    @Override
    public Result complete(Long id) {

        Orders ordersDB = orderMapper.selectById(String.valueOf(id));
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders build = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();

        orderMapper.update(build);


        return Result.success();
    }

    @Override
    public Result reminder(Long id) {

        Orders orders = orderMapper.selectById(String.valueOf(id));
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap();
        map.put("type", 2);//2代表用户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
        return Result.success();
    }


    private Result<OrderVO> getOrderVOResult(String id) {
        Orders orders = orderMapper.selectById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> orderDetails = orderDetailMapper.listByOrderId(orders.getId());
        orderVO.setOrderDetailList(orderDetails);
        return Result.success(orderVO);
    }


}
