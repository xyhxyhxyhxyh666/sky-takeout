package com.sky.controller.user;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "C端订单接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}", ordersSubmitDTO);
        Result<OrderSubmitVO> r = orderService.submit(ordersSubmitDTO);
        return r;
    }

    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("historyOrders")
    @ApiOperation("查看历史订单")
    public Result<PageResult> pageHistory(int pageSize, int page, Integer status){
        log.info("查看历史订单，页码{}，页大小{}，订单状态{}", page, pageSize, status);
        PageResult pageResult = orderService.pageOrders(page, pageSize, status);
        return Result.success(pageResult);
    }

    @PutMapping("cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable String id){

        log.info("取消订单：{}", id);
        Result r =orderService.cancel(id);
        return r;
    }

    @GetMapping("orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable String id){
        log.info("查询订单详情：{}", id);
        Result<OrderVO> r = orderService.orderDetail(id);
        return r;
    }

    @PostMapping("repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}", id);
        Result r = orderService.repetition(id);
        return r;
    }

    @GetMapping("reminder/{id}")
    @ApiOperation("订单催单")
    public Result reminder(@PathVariable Long id){
        log.info("订单催单：{}", id);
        Result r = orderService.reminder(id);
        return r;
    }


}
