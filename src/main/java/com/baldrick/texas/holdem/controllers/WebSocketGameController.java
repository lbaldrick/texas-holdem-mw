package com.baldrick.texas.holdem.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketGameController {
    private static final Logger logger = LogManager.getLogger(WebSocketGameController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/topic/{tableId}")
    public void game(@DestinationVariable String tableId, Message<Object> message) throws InterruptedException {
        logger.info(message.getPayload().toString() + " got it ");
        template.convertAndSend("/topic/" + tableId, "Message sent");
    }
}