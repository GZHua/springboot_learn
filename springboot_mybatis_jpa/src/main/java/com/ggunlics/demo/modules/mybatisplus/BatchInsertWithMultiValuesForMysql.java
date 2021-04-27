package com.ggunlics.demo.modules.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * mysql 批量插入  多values
 *
 * @author ggunlics
 * @date 2021/4/18
 **/
public class BatchInsertWithMultiValuesForMysql extends AbstractMethod {
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
        final String sql = "<script>insert into %s %s values %s </script>";
        final String tableName = tableInfo.getTableName();
        final String filedSql = prepareFieldSql(tableInfo);
        final String modelValuesSql = prepareModelValuesSql(tableInfo);
        final String sqlResult = String.format(sql, tableName, filedSql, modelValuesSql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, "batchInsertWithMultiValuesForMysql", sqlSource, new NoKeyGenerator(), null, null);
    }

    /**
     * 准备属性名
     *
     * @param tableInfo
     * @return
     */
    private String prepareFieldSql(TableInfo tableInfo) {
        final StringBuilder duplicateKeySql = new StringBuilder();
        duplicateKeySql.append("(");
        if (!StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            duplicateKeySql.append(tableInfo.getKeyColumn());
        }

        tableInfo.getFieldList().forEach(x -> {
            duplicateKeySql.append(",").append(x.getColumn());
        });

        if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            duplicateKeySql.delete(1, 2);
        }

        duplicateKeySql.append(")");
        return duplicateKeySql.toString();
    }

    private String prepareModelValuesSql(TableInfo tableInfo) {
        final StringBuilder valueSql = new StringBuilder();
        valueSql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" separator=\"),(\" close=\")\">");
        if (!StringUtils.isEmpty(tableInfo.getKeyProperty())) {
            valueSql.append("#{item.").append(tableInfo.getKeyProperty()).append("},");
        }
        tableInfo.getFieldList().forEach(x -> valueSql.append("#{item.").append(x.getProperty()).append("},"));
        valueSql.delete(valueSql.length() - 1, valueSql.length());
        valueSql.append("</foreach>");
        return valueSql.toString();
    }
}
