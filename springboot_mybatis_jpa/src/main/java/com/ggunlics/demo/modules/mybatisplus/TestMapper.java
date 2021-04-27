package com.ggunlics.demo.modules.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义Mapper
 *
 * @author ggunlics
 * @date 2021/4/14
 **/
public interface TestMapper<T> extends BaseMapper<T> {

    /**
     * test
     *
     * @param queryWrapper wrapper
     * @return list
     */
    List<T> testMethod(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * mysql 批量插入  多values
     *
     * @param list 数据
     * @return
     */
    int batchInsertWithMultiValuesForMysql(@Param("list") List<T> list);

    int batchUpdateWithCaseWhenForMysql(@Param("list") List<T> list, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
