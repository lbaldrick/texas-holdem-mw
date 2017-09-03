package com.baldrick.texas.holdem.assembly;

import com.baldrick.texas.holdem.components.rooms.RoomsManager;
import com.baldrick.texas.holdem.config.WebSocketConfig;
import com.baldrick.texas.holdem.services.GameService;
import com.baldrick.texas.holdem.states.StateChange;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.function.Consumer;

@EnableWebMvc
@Configuration
@PropertySource({
        "classpath:runtime.properties"
})
public class MainAssembly extends WebMvcConfigurerAdapter {

    private static final int MAX_NUM_OF_PLAYERS = 8;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static Consumer<StateChange> notifier() {
        return (StateChange state) -> {};
    }

    @Bean
    public static RoomsManager roomsManager() {
        return RoomsManager.newInstance(MAX_NUM_OF_PLAYERS, notifier());
    }

    @Bean
    public static GameService gameService() {
        return new GameService( roomsManager());
    }
}
    