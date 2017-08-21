package com.baldrick.texas.holdem.statemachine;

import com.baldrick.texas.holdem.game.Game;
import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TexasHoldemStateMachine implements Context {
    
    private final ExecutorService executor;
    
    private final ExecutorService timerExecutor = Executors.newSingleThreadExecutor();
    
    private TexasHoldemState state;
    
    private final Game game;
    
    private final Consumer<StateChange> stateChangeNotifier;
    
    private boolean gameInProgress = false;
    
    private final long gameWaitPeriod;
    
    private int numOfPlayersTurnsTaken = 0; 
    
    private int currentGameStage = 0;
    
    private int currentPlayerIndex = 0;
    
    private Future playerTimer = null;
    
    private static final Logger logger = LogManager.getLogger(TexasHoldemStateMachine.class);
   
    
    private TexasHoldemStateMachine(Game game, Consumer<StateChange> stateChangeNotifier, long gameWaitPeriod, ExecutorService executor) {
        this.game = game;
        this.stateChangeNotifier = stateChangeNotifier;
        this.gameWaitPeriod = gameWaitPeriod;
        this.executor = executor;
    }
    
    public static TexasHoldemStateMachine newInstance(Game game, Consumer<StateChange> stateChangeNotifier, long gameWaitPeriod, ExecutorService executor) {
        return new TexasHoldemStateMachine(game, stateChangeNotifier, gameWaitPeriod, executor);
    }
    
    private void start() {
        logger.info("Requested start of stae machine");
        executor.execute(() -> {
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
            this.state.notify(this, new DealtCardsStateChange(this.state, cards, player.getPlayerId()));
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
            this.state.notify(this, new PlayerStateChange(this.state, new PlayerState(PlayerStatus.HAS_FOCUS, playerId, cards, 0.0)));
            logger.debug("Try to starting timer");
            setPlayerWaitTimer();
        }
    }
    
    private void setPlayerWaitTimer() {
        logger.info("Starting player timer currentTime={}", System.currentTimeMillis());
        playerTimer = timerExecutor.submit(() -> {
            try {
                Thread.sleep(this.gameWaitPeriod);
            } catch(InterruptedException e) {
                logger.error(e);
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
        this.state.notify(this, new PlayerStateChange(state, new PlayerState(PlayerStatus.BET, playerId, null, amount)));
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }
    
    private void cancelPlayerWaitTimer() {
        if (playerTimer != null) {
            playerTimer.cancel(true);
        }
        
        playerTimer = null;
    }
    
    private void doDealFlop() {
        this.dealCards(3);
    }  
    
    private void doDealTurn() {
       this.dealCards(1);
    }
    
    private void doDealRiver() {
        this.dealCards(1);
    }
    
    private void doCheckCards() {
        // needs implemented
    }

    private void doFinish() {
        // needs implemented
    }
    
    private void dealCards(int numOfCards) {
        List<Card> cards = new ArrayList<>();
        
        for(int x = 0; x < numOfCards; x++) {
            cards.add(this.game.dealCard());
        }
        
        currentPlayerIndex = 0;
        this.state.notifyStateChange(
                     this, 
                     new DealtCardsStateChange(this.state, cards));
         changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }
    
    private State getGameStage(int stage) {
        switch(stage) {
            case 0: return TexasHoldemState.DEAL_PLAYER_CARDS;
            case 1: return TexasHoldemState.DEAL_FLOP;
            case 2: return TexasHoldemState.DEAL_RIVER;
            case 3: return TexasHoldemState.DEAL_TURN;
        }
        
        return null;
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

    public enum TexasHoldemState implements State<TexasHoldemStateMachine, StateChange> {
        START {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doStart();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        }, 
        DEAL_PLAYER_CARDS {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doDealPlayerCards();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        AWAIT_PLAYER_ACTION{
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_FOLDED {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_BET {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_CHECKED {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_RAISED {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        PLAYER_LEFT {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doAwaitPlayerAction();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_FLOP {
            @Override
            public void process(TexasHoldemStateMachine context) {
                context.doDealFlop();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        AMAITING_MORE_PLAYERS {
            @Override
            public void process(TexasHoldemStateMachine context) {
                
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_RIVER {
            @Override
            public void process(TexasHoldemStateMachine context) {
               context.doDealRiver();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        DEAL_TURN {
            @Override
            public void process(TexasHoldemStateMachine context) {
               context.doDealRiver();
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        CHECK_HANDS {
            @Override
            public void process(TexasHoldemStateMachine context) {
                
            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        ANNOUNCE_WINNER {
            @Override
            public void process(TexasHoldemStateMachine context) {

            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        WAITING {
            @Override
            public void process(TexasHoldemStateMachine context) {

            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        },
        FINISHED {
            @Override
            public void process(TexasHoldemStateMachine context) {

            }
            
            @Override
            public void notify(TexasHoldemStateMachine context, StateChange stateChange) {
                notifyStateChange(context, stateChange);
            }
        };
        
        public void notifyStateChange(TexasHoldemStateMachine context, StateChange stateChange) {
            logger.info("Notifying of state={} with change={}", context.state, stateChange.toString());
            context.stateChangeNotifier.accept(stateChange);
        };
    }
}
