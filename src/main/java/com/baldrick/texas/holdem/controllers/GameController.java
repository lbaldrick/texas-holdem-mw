package com.baldrick.texas.holdem.controllers;

import com.baldrick.texas.holdem.dtos.AddPlayerDto;
import com.baldrick.texas.holdem.services.GameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/game")
public class GameController {

    private final GameService gameService;

    private static final Logger logger = LogManager.getLogger(GameController.class);

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
   @RequestMapping(method = RequestMethod.POST)
   @ResponseBody
   public String addPlayerToGame(@RequestBody AddPlayerDto request) {
       String tableId = request.getTableId();
       String username = request.getUsername();

       //gameService.addPlayerToTable(tableId, )
       return null;
   }

    @RequestMapping(method = RequestMethod.GET)
    public void addPlayerToGame() {
        logger.info("Got GET request");
    }
}
