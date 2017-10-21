package com.baldrick.texas.holdem.controllers;

import com.baldrick.texas.holdem.dtos.AddPlayerDto;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.RoomDetails;
import com.baldrick.texas.holdem.services.GameService;
import com.baldrick.texas.holdem.services.LoginService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    private static final Logger logger = Logger.getLogger(GameController.class);

    private final GameService gameService;

    private final LoginService loginService;

    @Autowired
    public GameController(GameService gameService,  LoginService loginService) {
        this.gameService = gameService;
        this.loginService = loginService;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public List<Player> addPlayerToGame(@RequestBody AddPlayerDto request) {
        String tableId = request.getTableId();
        String username = request.getUsername();
        logger.info("Attempting to add player to table. username=" + username + "tableId=" + tableId);

        Optional<Player> player = loginService.getLoggedInPlayer(username);
        player.ifPresent((player1) ->  gameService.addPlayerToTable(tableId, player1));
        return gameService.getPlayersAtTable(tableId);
    }

    @CrossOrigin
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public List<RoomDetails> getAllRoomDetails() {
        return gameService.getRooms();
    }
}
