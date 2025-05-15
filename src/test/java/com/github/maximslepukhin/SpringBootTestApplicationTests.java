package com.github.maximslepukhin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.github.maximslepukhin")
public class SpringBootTestApplicationTests {

    @Test
    public void contextLoads() {
    }
}
