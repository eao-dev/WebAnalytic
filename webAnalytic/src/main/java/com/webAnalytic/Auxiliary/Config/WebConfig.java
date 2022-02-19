package com.webAnalytic.Auxiliary.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:dbConfig.properties")
@PropertySource("classpath:application.properties")
@ComponentScan({
        "com.webAnalytic.utils",
        "com.webAnalytic.Services"
})
public class WebConfig implements WebMvcConfigurer {

    @Value("${loginPage}")
    private String loginPage;

    @Value("${staticContent}")
    private String staticContent;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(loginPage).setViewName(loginPage);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:"+staticContent);
    }
}