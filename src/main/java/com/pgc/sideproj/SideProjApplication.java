package com.pgc.sideproj;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.pgc.sideproj.mapper")
@SpringBootApplication
@EnableRetry
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class SideProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjApplication.class, args);
	}

}
