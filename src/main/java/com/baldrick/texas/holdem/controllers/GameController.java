package com.baldrick.texas.holdem.controllers;

import com.baldrick.texas.holdem.dtos.AddPlayerDto;
import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Hand;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.services.GameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(path="/game")
public class GameController {

    private static final Logger logger = LogManager.getLogger(GameController.class);

    private final GameService gameService;

    private int playerId = 0;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

   @CrossOrigin
   @RequestMapping(method = RequestMethod.POST)
   @ResponseBody
   public String addPlayerToGame(@RequestBody AddPlayerDto request) {
       String tableId = request.getTableId();
       String username = request.getUsername();
       logger.info("Attempting to add player to table. username={} tableId={}", username, tableId);
       Player player = Player.newInstance("playerId" + ++playerId, username, 0.0, Hand.newInstance(), PlayerStatus.JOINING_TABLE );
       gameService.addPlayerToTable(tableId, player);
       return "success";
   }

    @RequestMapping(method = RequestMethod.GET)
    public void addPlayerToGame() {
        logger.info("Got GET request");
    }
}
