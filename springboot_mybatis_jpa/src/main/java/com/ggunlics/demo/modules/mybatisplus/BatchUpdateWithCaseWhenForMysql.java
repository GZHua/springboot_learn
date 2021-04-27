package com.ggunlics.demo.modules.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author ggunlics
 * @date 2021/4/19
 **/
public class BatchUpdateWithCaseWhenForMysql extends AbstractMethod {
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
        String sqlWhereEntityWrapper = sqlWhereEntityWrapper(true, tableInfo);
        String logicSet = sqlLogicSet(tableInfo);
        String sqlSet = sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, ENTITY, ENTITY_DOT);
        String sqlComment = sqlComment();
        String sqlCount = sqlCount();
        String sqlFirst = sqlFirst();
        String sqlSelectColumns = sqlSelectColumns(tableInfo, true);
        String sqlWhereByMap = sqlWhereByMap(tableInfo);
        String sqlSelectObjsColumns = sqlSelectObjsColumns(tableInfo);

        // updateById
        String sql = "<script>\n<foreach collection=\"list\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
        String additional = tableInfo.isWithVersion() ? tableInfo.getVersionFieldInfo().getVersionOli("item", "item.") : "" + tableInfo.getLogicDeleteSql(true, true);
        String setSql = sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "item", "item.");
        String sqlResult = String.format(sql, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(), additional);
        //log.debug("sqlResult----->{}", sqlResult);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        // 第三个参数必须和RootMapper的自定义方法名一致
        return this.addUpdateMappedStatement(mapperClass, modelClass, "batchUpdateWithCaseWhenForMysql", sqlSource);
    }
}
