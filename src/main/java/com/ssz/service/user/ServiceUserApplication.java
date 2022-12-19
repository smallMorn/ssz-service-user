package com.ssz.service.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



@SpringBootApplication(scanBasePackages = {"com.ssz.common.web","com.ssz.service.user"})
@EnableDiscoveryClient
@MapperScan(basePackages = "com.ssz.service.user.mapper")
public class ServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUserApplication.class, args);
	}

}
