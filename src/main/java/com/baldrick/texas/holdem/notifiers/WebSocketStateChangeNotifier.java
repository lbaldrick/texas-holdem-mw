package com.baldrick.texas.holdem.notifiers;

import com.baldrick.texas.holdem.states.StateChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class WebSocketStateChangeNotifier implements Notifier<StateChange, String> {

    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketStateChangeNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void notify(StateChange stateChange, String destination) {
        this.template.convertAndSend(destination, stateChange);
    }
}
