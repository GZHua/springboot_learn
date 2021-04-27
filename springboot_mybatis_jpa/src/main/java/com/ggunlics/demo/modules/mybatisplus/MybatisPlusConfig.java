package com.ggunlics.demo.modules.mybatisplus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ggunlics
 * @date 2021/4/14
 **/
@Configuration
public class MybatisPlusConfig {
    /**
     * 注册自定义的 方法注册器
     *
     * @return MySqlInjector
     */
    @Bean
    public MySqlInjector mySqlInjector() {
        return new MySqlInjector();
    }
}
