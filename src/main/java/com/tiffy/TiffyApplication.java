package com.tiffy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tiffy.repository")
public class TiffyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiffyApplication.class, args);
	}

}
