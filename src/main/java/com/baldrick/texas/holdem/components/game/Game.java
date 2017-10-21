package com.baldrick.texas.holdem.components.game;

import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {

    private static final Logger logger = Logger.getLogger(Game.class);

    private final Deck deck;
    
    private final Pot pot;

    private final List<Player> currentPlayersInPlay = new ArrayList<>();

    private final List<Card> tableCards = new ArrayList<>();

    private final int maxNumPlayers;
    
    private Game(Deck deck, Pot pot, int maxNumPlayers) {
        this.deck = deck;
        this.pot = pot;
        this.maxNumPlayers = maxNumPlayers;
    }
    
    public static Game newInstance(Deck deck, Pot pot, int maxNumPlayers) {
        return new Game(deck, pot, maxNumPlayers);
    }
    
    public synchronized boolean addPlayer(Player player) {
        if (currentPlayersInPlay.size() > maxNumPlayers) {
            logger.warn("Could not add player to game as max players at table. username=" + player.getUsername());
            return false;
        }

        logger.warn("Added player to game. username=" + player.getUsername());
        currentPlayersInPlay.add(player);
        
        return true;
    }

    public boolean playerBet(String playerId, double amount) {
        logger.info("Player with id=" + playerId + " BET amount={}," + amount);
        return playerBetOrRaised(playerId, amount, PlayerStatus.BET);
    }

    public boolean playerRaise(String playerId, double amount) {
        logger.info("Player with id=" + playerId + "RAISED amount=" + amount);
        return playerBetOrRaised(playerId, amount, PlayerStatus.RAISE);
    }

    public boolean playerCheck(String playerId) {
        logger.info("Player with id=" + playerId + "CHECKED");
        return setPlayerStatus(playerId, PlayerStatus.CHECK);
    }

    public boolean playerFold(String playerId) {
        logger.info("Player with id=" + playerId + "FOLDED");
        return setPlayerStatus(playerId, PlayerStatus.FOLD);
    }

    public boolean playerLeftTable(String playerId) {
        logger.info("Player with id=" + playerId + " LEFT TABLE");
        return setPlayerStatus(playerId, PlayerStatus.LEFT_TABLE);
    }

    public Player playerHasFocus(int currentPlayerIndex) {
        Player player = this.getPlayer(currentPlayerIndex);
        player.setPlayerStatus(PlayerStatus.HAS_FOCUS);

        return player;
    }

    public List<Player> dealCardsToAllPlayers() {
        List<Player> players = this.getCurrentPlayers();


        players.forEach((player) -> {
            List<Card> cards = new ArrayList<>();
            for (int x = 0; x < 2 ; x++) {
                cards.add(this.dealCard());
            }

            player.getHand().addCards(cards);
        });

        return players;
    }

    public Player getPlayer(int index) {
        return currentPlayersInPlay.get(index);
    }
    
    public synchronized List<Player> getCurrentPlayers() {
        return currentPlayersInPlay;
    }

    public Pot getPot() {
        return Pot.newInstance(pot.getBalance());
    }

    public void resetPot() {
        pot.resetPot();
    }

    public void start() {
        this.shuffleDeck();
    }

    public void finish() {
        this.resetPot();
        this.deck.resetDeck();
    }

    public List<Card> dealTableCards(int num) {
        for (int x = 0; x < num ; x++) {
            tableCards.add(this.dealCard());
        }

        return tableCards;
    }

    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    private void shuffleDeck() {
        deck.shuffle();
    }

    private Card dealCard() {
        return deck.dealCard();
    }

    private double addToPot(double amount) {
        return pot.addToPot(amount);
    }

    private Optional<Player> findPlayer(String playerId) {
        return currentPlayersInPlay
                .stream()
                .filter((Player player) -> player.getPlayerId().equals(playerId))
                .findFirst();
    }

    private boolean playerBetOrRaised(String playerId, double amount, PlayerStatus playerStatus) {
        Optional<Player> currentPlayer = findPlayer(playerId);

        if(currentPlayer.isPresent()) {
            Player actualPlayer = currentPlayer.get();
            actualPlayer.setPlayerStatus(playerStatus);
            // TODO - player may not have enough money
            currentPlayer.get().setAccountBalance(actualPlayer.getAccountBalance() - amount);
            this.addToPot(amount);
            return true;
        }

        return false;
    }

    private boolean setPlayerStatus(String playerId, PlayerStatus status) {
        Optional<Player> player = findPlayer(playerId);

        if (player.isPresent()) {
            player.get().setPlayerStatus(status);
            return true;
        }

        return false;
    }
}
