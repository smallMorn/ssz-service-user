package com.ssz.user.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(basePackages = "com.ssz.user.client.api")
@ComponentScan(basePackages = "com.ssz.user.client")
public class UserClientConfig {

}
