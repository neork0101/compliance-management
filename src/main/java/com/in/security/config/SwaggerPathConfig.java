package com.in.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerPathConfig implements WebMvcConfigurer {

    // This method adds a redirect from /swagger to the custom Swagger UI path
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger", "/swagger/identitymanagement/swagger-ui.html");
    }
}