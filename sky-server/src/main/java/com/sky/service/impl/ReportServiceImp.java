package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
}
