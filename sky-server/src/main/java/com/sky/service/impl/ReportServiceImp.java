package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ReportServiceImp implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<TurnoverReportVO> turnoverStatistics(LocalDate begin, LocalDate end) {

        ArrayList<LocalDate> dateTimes = new ArrayList<>();
        dateTimes.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateTimes.add(begin);
        }

        ArrayList<Double> turnover = new ArrayList<>();

        for (LocalDate date : dateTimes) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<Object, Object> map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", beginTime);
            map.put("end", endTime);

            Double turnoverByDate = orderMapper.getTurnoverByDate(map);
            turnover.add(turnoverByDate == null ? 0.0 : turnoverByDate);
        }

        TurnoverReportVO build = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateTimes, ","))
                .turnoverList(StringUtils.join(turnover, ","))
                .build();
        return Result.success(build);
    }

    @Override
    public Result<UserReportVO> userStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> date = new ArrayList<>();
        date.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            date.add(begin);
        }

        ArrayList<Integer> newUsers = new ArrayList<>();
        ArrayList<Integer> totalUsers = new ArrayList<>();

        for (LocalDate localDate : date) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer newUser = userMapper.getNewUserByDate(map);
            Integer totalUser = userMapper.getTotalUserByDate(map);

            newUsers.add(newUser);
            totalUsers.add(totalUser);
        }

        UserReportVO build = UserReportVO.builder()
                .dateList(StringUtils.join(date, ","))
                .newUserList(StringUtils.join(newUsers, ","))
                .totalUserList(StringUtils.join(totalUsers, ","))
                .build();
        return Result.success(build);
    }

    @Override
    public Result<OrderReportVO> ordersStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> date = new ArrayList<>();
        date.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            date.add(begin);
        }
        ArrayList<Integer> totalOrderList = new ArrayList<>();
        ArrayList<Integer> validOrderList = new ArrayList<>();

        for (LocalDate localDate : date) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Integer totalOrder = orderMapper.getTotalOrdersByDate(map);
            Integer validOrder = orderMapper.getValidOrdersByDate(map);
            totalOrderList.add(totalOrder);
            validOrderList.add(validOrder);
        }

        Integer totalCount = 0;
        Integer validCount = 0;
        for (Integer integer : totalOrderList) {
            totalCount += integer;
        }
        for (Integer integer : validOrderList) {
            validCount += integer;
        }

        Double orderCompletionRate = 0.0;
        if (totalCount != 0) {
            orderCompletionRate = validCount.doubleValue() / totalCount.doubleValue();
        }
        OrderReportVO build = OrderReportVO.builder()
                .dateList(StringUtils.join(date, ","))
                .totalOrderCount(totalCount)
                .validOrderCount(validCount)
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .orderCompletionRate(orderCompletionRate)
                .build();
        return Result.success(build);

    }
}
