package com.ggunlics.demo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ggunlics.demo.modules.test.entity.TUser;
import com.ggunlics.demo.modules.test.service.impl.TUserService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MybatisJpaApplicationTests {
    @Autowired
    TUserService tUserService;

    @Test
    void contextLoads() {
        List<TUser> list = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            TUser user = new TUser();
            user.setName("" + i);
            user.setAge(i);
            list.add(user);
        }

        tUserService.getBaseMapper().batchInsertWithMultiValuesForMysql(list);
    }

    @Test
    void name() {
        List<TUser> list = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            TUser user = new TUser();
            user.setName("" + i);
            user.setAge(i);
            list.add(user);
        }

        tUserService.getBaseMapper().batchUpdateWithCaseWhenForMysql(list, Wrappers.<TUser>lambdaQuery().isNotNull(TUser::getId).isNotNull(TUser::getName));
    }

    @Test
    void name2() {
        List<TUser> list = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            TUser user = new TUser();
            user.setName("" + i);
            user.setAge(i);
            list.add(user);
        }

        tUserService.updateBatchTest(list, Wrappers.<TUser>lambdaQuery().isNotNull(TUser::getId));
    }
}
