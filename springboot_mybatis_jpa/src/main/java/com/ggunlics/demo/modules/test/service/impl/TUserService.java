package com.ggunlics.demo.modules.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggunlics.demo.modules.test.dao.mapper.TUserMapper;
import com.ggunlics.demo.modules.test.entity.TUser;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ggunlics
 * @date 2021/4/18
 **/
@Service
public class TUserService extends ServiceImpl<TUserMapper, TUser> {


    /**
     * 多筛选条件批量更新
     * <p>一条一条的更新</p>
     *
     * @param list     数据
     * @param wrappers 条件
     * @return
     */
    public boolean updateBatchTest(List<TUser> list, LambdaQueryWrapper<TUser> wrappers) {
        return this.executeBatch(list, 1000, ((sqlSession, entity) -> {
            // 入参
            MapperMethod.ParamMap<Object> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            param.put(Constants.WRAPPER, wrappers);
            // 调用执行方法 使用原生的update(wrapper)
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE), param);
        }));
    }

}
