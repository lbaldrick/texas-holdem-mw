/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baldrick.texas.holdem.statemachine;

import com.baldrick.texas.holdem.game.Game;
import com.baldrick.texas.holdem.model.Deck;
import com.baldrick.texas.holdem.model.Hand;
import com.baldrick.texas.holdem.model.Player;
import com.baldrick.texas.holdem.model.Pot;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TexasHoldemStateMachineTest {
    
    private TexasHoldemStateMachine stateMachine;

    private Game game;
    
    @Mock
    private Consumer<StateChange> stateChangeNotifier;
    
    private final long gameWaitPeriod = 3L;
    
    private final ExecutorService executor =  Executors.newSingleThreadExecutor();
 
    
    @Before
    public void setUp() {
        game = Game.newInstance(Deck.newInstance(), Pot.newInstance(0.0), 6);
        stateMachine = TexasHoldemStateMachine.newInstance(game, stateChangeNotifier, gameWaitPeriod, executor);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testChangesToStartStateCorrectly() {
        CountDownLatch latch = new CountDownLatch(2);
        
        stateMachine.addPlayer(new Player("testId1","testUsername1", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        stateMachine.addPlayer(new Player("testId2","testUsername2", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        
//        executor.submit(() -> {
//            assertThat(stateMachine.getState()).isEqualTo(TexasHoldemStateMachine.TexasHoldemState.AMAITING_MORE_PLAYERS);
//            latch.countDown();
//        });
//        
//        
//        executor.submit(() -> {
//            assertThat(stateMachine.getState()).isEqualTo(TexasHoldemStateMachine.TexasHoldemState.START);
//            latch.countDown();
//        });
//    assertThat(stateMachine.getState()).isEqualTo(TexasHoldemStateMachine.TexasHoldemState.AMAITING_MORE_PLAYERS);
    }
   
}
