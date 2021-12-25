package com.webAnalytic.Config;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:dbConfig.properties")
@ComponentScan({
        "com.webAnalytic.utils",
        "com.webAnalytic.Services"
})
public class SpringConfig {
}
