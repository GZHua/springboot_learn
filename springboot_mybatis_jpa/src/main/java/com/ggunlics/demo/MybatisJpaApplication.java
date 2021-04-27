package com.ggunlics.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Mapper 扫描包
 * <p>通配符 * , 如com.**</p>
 * <p>也可以使用{@linkplain @Mapper}注解标识</p>
 *
 * @author ggunlics
 */
@MapperScan("com.ggunlics.**.mapper")
@SpringBootApplication
public class MybatisJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatisJpaApplication.class, args);
    }
}
