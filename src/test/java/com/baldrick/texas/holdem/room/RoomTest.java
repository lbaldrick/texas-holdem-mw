/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baldrick.texas.holdem.room;

import com.baldrick.texas.holdem.components.rooms.Room;
import com.baldrick.texas.holdem.enums.PlayerStatus;
import com.baldrick.texas.holdem.components.game.Game;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Hand;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import com.baldrick.texas.holdem.states.StateChange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoomTest {
    
    private Room stateMachine;

    private Game game;
    
    private Consumer<StateChange> stateChangeNotifier;
    
    private final long gameWaitPeriod = 3L;
    
    private final ExecutorService executor =  Executors.newSingleThreadExecutor();
    
    private final ExecutorService timerExecutor =  Executors.newSingleThreadExecutor();
 
    private List<StateChange> stateChanges;

    private static final List<StateChange> EXPECTED_FULL_GAME_STATES = newArrayList();
    
    @Before
    public void setUp() {
        stateChanges = new ArrayList<>();
        stateChangeNotifier = (StateChange stateChange) -> {
            stateChanges.add(stateChange);
        };
        game = Game.newInstance(Deck.newInstance(), Pot.newInstance(0.0), 6);
        stateMachine = Room.newInstance(game, stateChangeNotifier, gameWaitPeriod, executor, timerExecutor);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testChangesToStartStateCorrectly() throws InterruptedException {
        stateMachine.addPlayer(Player.newInstance("testId1","testUsername1", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        stateMachine.addPlayer(Player.newInstance("testId2","testUsername2", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        createCountDownLatches(9, executor);
        
        assertThat(stateChanges.size()).isEqualTo(14);
    }

    private void createCountDownLatches(int num, ExecutorService executor) throws InterruptedException {
        for (int x = 0; x < num; x++) {
            CountDownLatch latch= new CountDownLatch(1);
            executor.submit(() -> latch.countDown());
            latch.await();
        }
    }

   
}
