package com.priyankaa.enterprise_order_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(
		exclude = {DataSourceAutoConfiguration.class}
)
public class EnterpriseOrderManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnterpriseOrderManagementSystemApplication.class, args);
	}

}
