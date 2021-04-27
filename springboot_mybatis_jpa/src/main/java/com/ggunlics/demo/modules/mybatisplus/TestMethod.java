package com.ggunlics.demo.modules.mybatisplus;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * Mybatis-plus 自定义全局方法的实现
 *
 * @author ggunlics
 * @date 2021/4/14
 **/
public class TestMethod extends AbstractMethod {
    /**
     * 注入自定义 MappedStatement
     *
     * @param mapperClass mapper 接口
     * @param modelClass  mapper 泛型
     * @param tableInfo   数据库表反射信息
     * @return MappedStatement
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 生成sql, 支持包含Mybatis标签
        SqlMethod sqlMethod = SqlMethod.SELECT_LIST;

        // 使用Wrapper 看AbstractMethod有wrapper、wq参数的方法
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlSelectColumns(tableInfo, true), tableInfo.getTableName(),
                sqlWhereEntityWrapper(true, tableInfo), sqlComment());

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);

        // 参数id 需要和Mapper的方法名一致
        return this.addSelectMappedStatementForTable(mapperClass, "testMethod", sqlSource, tableInfo);
    }
}
