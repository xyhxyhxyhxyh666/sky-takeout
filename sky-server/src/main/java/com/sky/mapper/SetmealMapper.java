package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    List<Long> selectIdsByDishIds(List<Long> dishIds);

    @AutoFill(value = OperationType.INSERT)
    int addSetmeal(Setmeal setmeal);

    List<Dish> getDishByCategory(Dish dish);

    Long queryIdByName(String name);

    Page<SetmealVO> queryByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getSetmealById(Long id);


    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
}
