package com.baldrick.texas.holdem.assembly;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan( basePackages = "com.baldrick.texas.holdem.*" )
@PropertySource({ 
  "classpath:runtime.properties"
})
public class MainAssembly{
 
   @Bean
   public static PropertySourcesPlaceholderConfigurer properties() {
         return new PropertySourcesPlaceholderConfigurer();
   }
}
    