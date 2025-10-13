package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */

    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation(value = "增加员工")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工信息：{}", employeeDTO);
        Result r = employeeService.add(employeeDTO);
        return r;
    }


    @ApiOperation(value = "员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> queryByPage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为{}", employeePageQueryDTO);
        Result r = employeeService.queryByPage(employeePageQueryDTO);
        return r;
    }

    @ApiOperation(value = "员工状态修改")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("员工状态修改，员工id为{}", id);
        Result r = employeeService.setStatus(status, id);
        return r;
    }

    @ApiOperation(value = "根据员工id查询")
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id){
        log.info("根据员工id查询，员工id为{}", id);
        Result r = employeeService.selectById(id);
        return r;
    }

    @PutMapping
    @ApiOperation(value = "员工信息修改")
    public Result updateInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改，参数为{}", employeeDTO);
        Result r = employeeService.updateInfo(employeeDTO);
        return r;
    }

}
