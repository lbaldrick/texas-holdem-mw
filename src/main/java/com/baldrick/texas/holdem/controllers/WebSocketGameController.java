package com.baldrick.texas.holdem.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebSocketGameController {
    private static final Logger logger = LogManager.getLogger(WebSocketGameController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/topic/table1")
    public void game(Message<Object> message) throws InterruptedException {
        logger.info(message.getPayload().toString() + " got it ");
        template.convertAndSend("/topic/table1", "Message sent");
    }


    @RequestMapping("/send")
    public @ResponseBody String gameSend() throws InterruptedException {
        logger.info( " sent ");
        template.convertAndSend("/topic/table1", "Message sent");
        return "success";
    }
}