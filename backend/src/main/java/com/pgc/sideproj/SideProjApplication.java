package com.pgc.sideproj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.pgc.sideproj.mapper")
@SpringBootApplication
@EnableRetry
@EnableScheduling
public class SideProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjApplication.class, args);
	}

}
