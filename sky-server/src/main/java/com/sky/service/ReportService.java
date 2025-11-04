package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {
    Result<TurnoverReportVO> turnoverStatistics(LocalDate begin, LocalDate end);
}
