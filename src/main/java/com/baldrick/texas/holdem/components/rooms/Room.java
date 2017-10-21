package com.baldrick.texas.holdem.components.rooms;

import com.baldrick.texas.holdem.enums.DealtCardsStatus;
import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.model.Card;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.components.game.Game;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.baldrick.texas.holdem.model.RoomDetails;
import com.baldrick.texas.holdem.notifiers.Notifier;
import com.baldrick.texas.holdem.states.*;
import org.apache.log4j.Logger;

public class Room implements Context {
    
    private final ExecutorService executor;
    
    private TexasHoldemState state;
    
    private final Game game;
    
    private final Notifier stateChangeNotifier;

    private final String roomId;
    
    private boolean gameInProgress = false;
    
    private final long gameWaitPeriod;
    
    private int numOfPlayersTurnsTaken = 0; 
    
    private int currentGameStage = 0;
    
    private int currentPlayerIndex = 0;

    private static final Logger logger = Logger.getLogger(Room.class);
    
    public Room(Game game, Notifier stateChangeNotifier, long gameWaitPeriod, ExecutorService executor, String roomId) {
        this.game = game;
        this.stateChangeNotifier = stateChangeNotifier;
        this.gameWaitPeriod = gameWaitPeriod;
        this.executor = executor;
        this.roomId = roomId;
    }
    
    private void start() {
        logger.info("Requested start of state machine");
        this.state = game.getCurrentPlayers().size() >= 2 ? TexasHoldemState.START : TexasHoldemState.AMAITING_MORE_PLAYERS;
        if (this.state.equals(TexasHoldemState.AMAITING_MORE_PLAYERS)) {
            this.state.process(this);
        } else {
            executor.execute(() -> {
                currentGameStage = 0;
                currentPlayerIndex = 0;
                this.state.process(this);
            });
        }
    }

    @Override
    public void changeState(State state) {
        logger.info("State changed from currentState=" + this.state.toString() + "to newState=" + state.toString());
        this.state = (TexasHoldemState) state;
        this.state.process(this);
    }
      
    public void playerBet(String playerId, double amount) {
        logger.info("Player with id=" + playerId + " BET amount="+ amount);
        if( this.game.playerBet(playerId, amount)) {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_BET, PlayerStatus.BET, playerId);
        } else {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_FOLDED, PlayerStatus.FOLD, playerId);
        }
    }
    
    public void playerRaise(String playerId, double amount) {
        logger.info("Player with id=" + playerId + " RAISED amount=" + amount );
        if( this.game.playerRaise(playerId, amount)) {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_RAISED, PlayerStatus.RAISE, playerId);
        } else {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_FOLDED, PlayerStatus.FOLD, playerId);
        }
    }

    public void playerCheck(String playerId) {
        logger.info("Player with id={} CHECKED");
        if(this.game.playerCheck(playerId)) {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_CHECKED, PlayerStatus.CHECK, playerId);
        } else {
            notifyPlayerStatusChange(TexasHoldemState.PLAYER_FOLDED, PlayerStatus.FOLD, playerId);
        }
    }
    
    public void playerFold(String playerId) {
        logger.info("Player with id={} FOLDED");
        this.game.playerFold(playerId);
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_FOLDED, PlayerStatus.FOLD, playerId);
    }

    public void playerLeftTable(String playerId) {
        logger.info("Player with id={} LEFT TABLE");
        this.game.playerLeftTable(playerId);
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_LEFT, PlayerStatus.LEFT_TABLE, playerId);
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isPlayerInGame(String playerId) {
        return this.game.getCurrentPlayers().contains(playerId);
    }

    public List<Player> getAllPlayers() {
        return this.game.getCurrentPlayers();
    }

    public RoomDetails getRoomDetails() {
        return new RoomDetails(roomId,
                this.game.getMaxNumPlayers(),
                this.game.getCurrentPlayers()
                        .stream()
                        .map((player) -> player.getPlayerId())
                        .collect(Collectors.toList()));
    }

    public boolean addPlayer(Player player) {
        if (gameInProgress) {
            logger.warn("Could not player to table as game in progress. username={}" + player.getUsername());
            return false;
        }

        boolean playerAdded = this.game.addPlayer(player);

        if (canStartGame()) {
            logger.warn("Starting game");
            start();
        }
        return playerAdded;
    }

    private void doStart() {
        gameInProgress = true;
        this.game.start();
        changeState(TexasHoldemState.DEAL_PLAYER_CARDS);
    }
    
    private void doDealPlayerCards() {
        List<Player> players = this.game.dealCardsToAllPlayers();
        
        players.forEach((player) -> {
            this.state.notify(this, new DealtCardsStateChange(new DealtCardsState(player.getHand().getCards(), DealtCardsStatus.PLAYER_CARDS, player.getPlayerId())));
        });
        
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }
    
    private void doAwaitPlayerAction() {
        logger.debug("Checking to request player action or change game stage");
        if (numOfPlayersTurnsTaken == this.game.getCurrentPlayers().size() && this.game.getCurrentPlayers().size() >= 2) {
            numOfPlayersTurnsTaken = 0;
            currentPlayerIndex = 0;
            changeState(getGameStage(++currentGameStage));
        } else {
            Player player = this.game.playerHasFocus(currentPlayerIndex);
            String playerId = player.getPlayerId();
            List<Card> cards = player.getHand().getCards();
            currentPlayerIndex++;
            logger.info("Notifying player HAS_FOCUS playerId=" + playerId);
            this.state.notify(this, new PlayerStateChange(new PlayerState(PlayerStatus.HAS_FOCUS, playerId, cards, 0.0)));
            logger.debug("Try to starting timer");
            try {
                setPlayerWaitTimer(playerId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setPlayerWaitTimer(String playerId) throws InterruptedException {
        logger.info("Starting player timer currentTime=" + System.currentTimeMillis());
        Thread.sleep(this.gameWaitPeriod);

        logger.info("Ending player timer currentTime="+ System.currentTimeMillis());
        numOfPlayersTurnsTaken++;
        // TODO - player wont always check if timedout can cold to so will have to implement that
        notifyPlayerStatusChange(TexasHoldemState.PLAYER_CHECKED, PlayerStatus.CHECK, playerId, false);
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }

    private void notifyPlayerStatusChange(TexasHoldemState state, PlayerStatus status, String playerId) {
        notifyPlayerStatusChange(state, status, playerId, 0.0, true);
    }

    private void notifyPlayerStatusChange(TexasHoldemState state, PlayerStatus status, String playerId, boolean shouldChangeState) {
        notifyPlayerStatusChange(state, status, playerId, 0.0, shouldChangeState);
    }
    
    private void notifyPlayerStatusChange(TexasHoldemState state, PlayerStatus status, String playerId, double amount, boolean shouldChangeState) {
        this.state.notify(this, new PlayerStateChange(new PlayerState(status, playerId, null, amount)));
        if (shouldChangeState) {
            changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
        }
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
        this.game.finish();
    }
    
    private State getGameStage(int stage) {
        logger.info("Requesting game stage gameStage="+ stage);
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
                new DealtCardsStateChange(new DealtCardsState(this.game.dealTableCards(num), status , "TABLE")));
        changeState(TexasHoldemState.AWAIT_PLAYER_ACTION);
    }

    private boolean canStartGame() {
        return this.game.getCurrentPlayers().size() >= 2 && !gameInProgress;
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
            public void process(Room context) {}
            
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
            logger.info("Notifying of state=" +  context.state + " with change=" + stateChange.toString());
            context.stateChangeNotifier.notify(stateChange, "/topic/" + context.getRoomId());
        };
    }

}
