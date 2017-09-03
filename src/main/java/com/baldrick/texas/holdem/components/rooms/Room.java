package com.baldrick.texas.holdem.components.rooms;

import com.baldrick.texas.holdem.enums.DealtCardsStatus;
import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.components.game.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.baldrick.texas.holdem.states.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Room implements Context {
    
    private final ExecutorService executor;
    
    private TexasHoldemState state;
    
    private final Game game;
    
    private final Consumer<StateChange> stateChangeNotifier;
    
    private boolean gameInProgress = false;
    
    private final long gameWaitPeriod;
    
    private int numOfPlayersTurnsTaken = 0; 
    
    private int currentGameStage = 0;
    
    private int currentPlayerIndex = 0;

    private Future playerTimer = null;
    
    private static final Logger logger = LogManager.getLogger(Room.class);
   
    
    public Room(Game game, Consumer<StateChange> stateChangeNotifier, long gameWaitPeriod, ExecutorService executor) {
        this.game = game;
        this.stateChangeNotifier = stateChangeNotifier;
        this.gameWaitPeriod = gameWaitPeriod;
        this.executor = executor;
    }
    
    public static Room newInstance(Game game, Consumer<StateChange> stateChangeNotifier, long gameWaitPeriod, ExecutorService executor, ExecutorService timerExecutor) {
        return new Room(game, stateChangeNotifier, gameWaitPeriod, executor);
    }
    
    private void start() {
        logger.info("Requested start of state machine");
        executor.execute(() -> {
            currentGameStage = 0;
            currentPlayerIndex = 0;
            this.state = game.getCurrentPlayers().size() >= 2 ? TexasHoldemState.START : TexasHoldemState.AMAITING_MORE_PLAYERS;
            this.state.process(this);
        });
    }

    @Override
    public void changeState(State state) {
        logger.info("State changed from currentState={} to newState={}", this.state.toString(), state.toString());
        this.state = (TexasHoldemState) state;
        this.state.process(this);
    }
    
    public State getState() {
        return this.state;
    }
      
    public void playerBet(String playerId, double amount) {
        logger.info("Player with id={} BET amount={}", playerId, amount);
        this.game.addToPot(amount);
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_BET, PlayerStatus.BET, playerId);
    }
    
    public void playerRaise(String playerId, double amount) {
        logger.info("Player with id={} RAISED amount={}", playerId, amount);
        this.game.addToPot(amount);
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_RAISED, PlayerStatus.RAISE, playerId);
    }
    
    
    public void playerCheck(String playerId) {
        logger.info("Player with id={} CHECKED");
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_CHECKED, PlayerStatus.CHECK, playerId);
    }
    
    public void playerFold(String playerId) {
        logger.info("Player with id={} FOLDED");
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_FOLDED, PlayerStatus.FOLD, playerId);
    }

    public void playerLeftTable(String playerId) {
        logger.info("Player with id={} LEFT TABLE");
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_LEFT, PlayerStatus.LEFT_TABLE, playerId);
    }
    
    private void doStart() {
        gameInProgress = true;
        changeState(TexasHoldemState.DEAL_PLAYER_CARDS);
    }
    
    private void doDealPlayerCards() {
        List<Player> players = this.game.getCurrentPlayers();
        
        players.forEach((player) -> {
            List<Card> cards = new ArrayList<>();
            for (int x = 0; x < 2 ; x++) {
                cards.add(this.game.dealCard());
            }
            
            player.getHand().addCards(cards);
            this.state.notify(this, new DealtCardsStateChange(new DealtCardsState(cards, DealtCardsStatus.PLAYER_CARDS, player.getPlayerId())));
        });
        
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }
    
    private void doAwaitPlayerAction() {
        logger.debug("Checking to request player action or change game stage");
        if (numOfPlayersTurnsTaken == this.game.getCurrentPlayers().size() && this.game.getCurrentPlayers().size() >= 2) {
            numOfPlayersTurnsTaken = 0;
            changeState(getGameStage(++currentGameStage));
        } else {
            Player player = this.game.getPlayer(currentPlayerIndex);
            String playerId = player.getPlayerId();
            List<Card> cards = player.getHand().getCards();
            currentPlayerIndex++;
            logger.info("Notifying player HAS_FOCUS playerId={}", playerId);
            this.state.notify(this, new PlayerStateChange(new PlayerState(PlayerStatus.HAS_FOCUS, playerId, cards, 0.0)));
            logger.debug("Try to starting timer");
            setPlayerWaitTimer();
        }
    }
    
    private void setPlayerWaitTimer() {
        logger.info("Starting player timer currentTime={}", System.currentTimeMillis());

        // TODO - this is pretty crap will change this later and have timeout managed outside of room. Will have some kind of game manager that manages all the games and
        // will be responsible for keeping track of all the timeouts in each game ensuring no threads are unncessarily blocked but this will do for now.

        playerTimer = executor.submit(() -> {
            try {
                Thread.sleep(this.gameWaitPeriod);
            } catch (InterruptedException ex) {
                logger.error(ex);
            } finally {
                logger.info("Ending player timer currentTime={}", System.currentTimeMillis());
                numOfPlayersTurnsTaken++;
                changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
            }
        });

    }
    
    private void notifyPlayerStatusChange(TexasHoldemState state, PlayerStatus status, String playerId) {
        notifyPlayerStatusChange(state, status, playerId, 0.0);
    }
    
    private void notifyPlayerStatusChange(TexasHoldemState state, PlayerStatus status, String playerId, double amount) {
        cancelPlayerWaitTimer();
        changeState(state);
        this.state.notify(this, new PlayerStateChange(new PlayerState(status, playerId, null, amount)));
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }
    
    private void cancelPlayerWaitTimer() {
        if (playerTimer != null) {
            playerTimer.cancel(true);
        }
        
        playerTimer = null;
    }
    
    private void doDealFlop() {
        dealTableCards(3, DealtCardsStatus.FLOP);
    }  
    
    private void doDealTurn() {
        dealTableCards(1, DealtCardsStatus.TURN);
    }
    
    private void doDealRiver() {
        dealTableCards(1, DealtCardsStatus.RIVER);
    }
    
    private void doCheckCards() {

        changeState(TexasHoldemState.ANNOUNCE_WINNER);
    }

    private void doAnnounceWinner() {
        this.state.notify(this, new PlayerStateChange(new PlayerState(PlayerStatus.WINNER, "0", null, 0.0)));
        changeState(TexasHoldemState.FINISHED);
    }

    private void doFinish() {
        //END
    }
    
    private List<Card> dealCards(int numOfCards) {
        List<Card> cards = new ArrayList<>();
        
        for(int x = 0; x < numOfCards; x++) {
            cards.add(this.game.dealCard());
        }
        
        currentPlayerIndex = 0;

        return cards;
    }
    
    private State getGameStage(int stage) {
        logger.info("Requesting game stage gameStage={}", stage);
        switch(stage) {
            case 0: return TexasHoldemState.DEAL_PLAYER_CARDS;
            case 1: return TexasHoldemState.DEAL_FLOP;
            case 2: return TexasHoldemState.DEAL_RIVER;
            case 3: return TexasHoldemState.DEAL_TURN;
            case 4: return TexasHoldemState.CHECK_HANDS;
        }
        
        return null;
    }

    private void dealTableCards(int num, DealtCardsStatus status) {
        this.state.notify(
                this,
                new DealtCardsStateChange(new DealtCardsState(this.dealCards(num), status , "TABLE")));
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }

    public boolean addPlayer(Player player) {
        if (gameInProgress) {
            return false;
        }
        
        boolean playerAdded = this.game.addPlayer(player);
        if (canStartGame()) {
            start();
        }
        return playerAdded;
    }
    
    public boolean canStartGame() {
        return this.game.getCurrentPlayers().size() >= 2 && !gameInProgress;
    }

    public boolean isPlayerInGame(String playerId) {
        return this.game.getCurrentPlayers().contains(playerId);
    }

    public enum TexasHoldemState implements State<Room, StateChange> {
        START {
            @Override
            public void process(Room context) {
                context.doStart();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        }, 
        DEAL_PLAYER_CARDS {
            @Override
            public void process(Room context) {
                context.doDealPlayerCards();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        AWAIT_PLAYER_ACTION{
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_FOLDED {
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_BET {
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_CHECKED {
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_RAISED {
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_LEFT {
            @Override
            public void process(Room context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_FLOP {
            @Override
            public void process(Room context) {
                context.doDealFlop();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        AMAITING_MORE_PLAYERS {
            @Override
            public void process(Room context) {
                
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_RIVER {
            @Override
            public void process(Room context) {
               context.doDealRiver();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_TURN {
            @Override
            public void process(Room context) {
               context.doDealTurn();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        CHECK_HANDS {
            @Override
            public void process(Room context) {
                context.doCheckCards();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        ANNOUNCE_WINNER {
            @Override
            public void process(Room context) {
                context.doAnnounceWinner();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        FINISHED {
            @Override
            public void process(Room context) {
            context.doFinish();
            }
            
            @Override
            public void notify(Room context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        };
        
        protected void notifyStateChange(Room context, StateChange stateChange) {
            logger.info("Notifying of state={} with change={}", context.state, stateChange.toString());
            context.stateChangeNotifier.accept(stateChange);
        };
    }
}
