package com.in.compliance;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.in.compliance","com.in.compliance.repository"})
public class ApplicationConfig {

}
