package com.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
@EnableSwagger2                            // Enabling Swagger Annotations
public class EmployeeApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
	}
	@Bean
	public Docket swaggerConfiguration(){                    // Configuring Swagger

		return  new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.organization"))
				.build()
				.apiInfo(apiDetails());
	}

	private ApiInfo apiDetails(){                           // Function For Providing Custom Configuration to Swagger
		return  new ApiInfo(
				"Employee Management System API",
				"All REST API Present In EMS",
				"1.0",
				"In Trial Phase",
				new Contact("Prashant Singh","","prashantsingh@fico.com"),
				"API License",
				"",
				Collections.emptyList());
	}

}
