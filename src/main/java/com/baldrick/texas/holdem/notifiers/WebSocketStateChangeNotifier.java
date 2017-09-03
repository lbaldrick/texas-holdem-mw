package com.baldrick.texas.holdem.notifiers;

import com.baldrick.texas.holdem.states.StateChange;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.function.Consumer;

public class WebSocketStateChangeNotifier implements Notifier<StateChange> {

    private final SimpMessagingTemplate template;

    public WebSocketStateChangeNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void notify(StateChange stateChange) {
        this.template.convertAndSend("/topic/greetings", stateChange);
    }

    @Override
    public Consumer<StateChange> getNotifier() {
        return null;
    }
}
