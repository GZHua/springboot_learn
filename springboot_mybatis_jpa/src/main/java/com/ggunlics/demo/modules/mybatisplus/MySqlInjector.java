package com.ggunlics.demo.modules.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * Mybatis-plus 全局方法注册
 *
 * @author ggunlics
 * @date 2021/4/14
 **/
public class MySqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new TestMethod());
        methodList.add(new BatchInsertWithMultiValuesForMysql());
        methodList.add(new BatchUpdateWithCaseWhenForMysql());
        return methodList;
    }
}
