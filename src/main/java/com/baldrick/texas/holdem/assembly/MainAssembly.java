package com.baldrick.texas.holdem.assembly;

import com.baldrick.texas.holdem.config.WebSocketConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@EnableWebMvc
@Configuration
@PropertySource({
        "classpath:runtime.properties"
})
@ComponentScan(basePackages = "com.baldrick.texas.holdem")
@Import({ WebSocketConfig.class })
public class MainAssembly extends WebMvcConfigurerAdapter {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
    