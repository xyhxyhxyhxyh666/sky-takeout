package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "订单管理")
@RequestMapping("/admin/order")
@Slf4j
@Component("AdminOrderController")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQueryAdmin(ordersPageQueryDTO);
        return Result.success(pageResult);

    }

    @GetMapping("details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> detatils(@PathVariable String id){
        log.info("订单详情：{}", id);
        Result<OrderVO> r = orderService.AdminOrderDetail(id);
        return r;
    }

    @GetMapping("statistics")
    @ApiOperation("统计数据")
    public Result<OrderStatisticsVO> statistics(){
        log.info("统计数据");
        Result<OrderStatisticsVO> r = orderService.statistics();
        return r;
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单：{}", ordersConfirmDTO);
        Result r = orderService.confirm(ordersConfirmDTO);
        return r;
    }

    @PutMapping("rejection")
    @ApiOperation("拒单")
    public Result cancel(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单：{}", ordersRejectionDTO);
        Result r = orderService.Rejection(ordersRejectionDTO);
        return r;
    }

    @PutMapping("cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}", ordersCancelDTO);
        Result r = orderService.AdminCancel(ordersCancelDTO);
        return r;
    }

    @PutMapping("delivery/{id}")
    @ApiOperation("派单")
    public Result delivery(@PathVariable Long id){
        log.info("派单：{}", id);
        Result r = orderService.delivery(id);
        return r;
    }

    @PutMapping("complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("完成订单：{}", id);
        Result r = orderService.complete(id);
        return r;
    }

}
