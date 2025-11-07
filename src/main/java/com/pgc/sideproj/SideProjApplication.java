package com.pgc.sideproj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@MapperScan("com.pgc.sideproj.mapper")
@SpringBootApplication
@EnableRetry
public class SideProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjApplication.class, args);
	}

}
