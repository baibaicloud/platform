package com.loon.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ImportResource(locations = {"context.xml"})
@SpringBootApplication
public class LauncherApplication {

    public static void main(String[] args) {
		SpringApplication.run(LauncherApplication.class, args);
	}

}
