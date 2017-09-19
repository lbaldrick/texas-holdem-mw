/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baldrick.texas.holdem.room;

import com.baldrick.texas.holdem.components.rooms.Room;
import com.baldrick.texas.holdem.enums.DealtCardsStatus;
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

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.baldrick.texas.holdem.notifiers.Notifier;
import com.baldrick.texas.holdem.notifiers.WebSocketStateChangeNotifier;
import com.baldrick.texas.holdem.states.DealtCardsState;
import com.baldrick.texas.holdem.states.PlayerState;
import com.baldrick.texas.holdem.states.StateChange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RunWith(MockitoJUnitRunner.class)
public class RoomTest {
    
    private Room stateMachine;

    private Game game;
    
    private Notifier stateChangeNotifier = null;
    
    private final long gameWaitPeriod = 3L;
    
    private final ExecutorService executor =  Executors.newSingleThreadExecutor();
    
    @Before
    public void setUp() {
        stateChangeNotifier = mock(WebSocketStateChangeNotifier.class);
        game = Game.newInstance(Deck.newInstance(), Pot.newInstance(0.0), 6);
        stateMachine = new Room(game, stateChangeNotifier, gameWaitPeriod, executor, "ROOM_ID");
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testChangesToStartStateCorrectly() throws InterruptedException {
        stateMachine.addPlayer(Player.newInstance("testId1","testUsername1", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        stateMachine.addPlayer(Player.newInstance("testId2","testUsername2", 0.0, Hand.newInstance(), PlayerStatus.WAITING));
        createCountDownLatches(1, executor);

        ArgumentCaptor<StateChange> stateChangeCaptor = ArgumentCaptor.forClass(StateChange.class);
        verify(stateChangeNotifier, times(22)).notify(stateChangeCaptor.capture(), any());

        List<StateChange> captureStateChanges = stateChangeCaptor.getAllValues();
        DealtCardsState state1 = (DealtCardsState)captureStateChanges.get(0).getStateValue();
        DealtCardsState state2 = (DealtCardsState)captureStateChanges.get(1).getStateValue();
        PlayerState state3 = (PlayerState)captureStateChanges.get(2).getStateValue();
        PlayerState state4 = (PlayerState)captureStateChanges.get(3).getStateValue();
        PlayerState state5 = (PlayerState)captureStateChanges.get(4).getStateValue();
        PlayerState state6 = (PlayerState)captureStateChanges.get(5).getStateValue();
        DealtCardsState state7 = (DealtCardsState)captureStateChanges.get(6).getStateValue();
        PlayerState state8 = (PlayerState)captureStateChanges.get(7).getStateValue();
        PlayerState state9 = (PlayerState)captureStateChanges.get(8).getStateValue();
        PlayerState state10 = (PlayerState)captureStateChanges.get(9).getStateValue();
        PlayerState state11 = (PlayerState)captureStateChanges.get(10).getStateValue();
        DealtCardsState state12 = (DealtCardsState)captureStateChanges.get(11).getStateValue();
        PlayerState state13 = (PlayerState)captureStateChanges.get(12).getStateValue();
        PlayerState state14 = (PlayerState)captureStateChanges.get(13).getStateValue();
        PlayerState state15= (PlayerState)captureStateChanges.get(14).getStateValue();
        PlayerState state16 = (PlayerState)captureStateChanges.get(15).getStateValue();
        DealtCardsState state17 = (DealtCardsState)captureStateChanges.get(16).getStateValue();
        PlayerState state18 = (PlayerState)captureStateChanges.get(17).getStateValue();
        PlayerState state19 = (PlayerState)captureStateChanges.get(18).getStateValue();
        PlayerState state20 = (PlayerState)captureStateChanges.get(19).getStateValue();
        PlayerState state21 = (PlayerState)captureStateChanges.get(20).getStateValue();
        PlayerState state22 = (PlayerState)captureStateChanges.get(21).getStateValue();

        assertEquals(state1.getStatus(), DealtCardsStatus.PLAYER_CARDS);
        assertEquals(state2.getStatus(), DealtCardsStatus.PLAYER_CARDS);
        assertEquals(state3.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state4.getPlayerStatus(),  PlayerStatus.CHECK);
        assertEquals(state5.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state6.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state7.getStatus(), DealtCardsStatus.FLOP);
        assertEquals(state8.getPlayerStatus(),PlayerStatus.HAS_FOCUS);
        assertEquals(state9.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state10.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state11.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state12.getStatus(), DealtCardsStatus.RIVER);
        assertEquals(state13.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state14.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state15.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state16.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state17.getStatus(),  DealtCardsStatus.TURN);
        assertEquals(state18.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state19.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state20.getPlayerStatus(), PlayerStatus.HAS_FOCUS);
        assertEquals(state21.getPlayerStatus(), PlayerStatus.CHECK);
        assertEquals(state22.getPlayerStatus(), PlayerStatus.WINNER);
    }

    private void createCountDownLatches(int num, ExecutorService executor) throws InterruptedException {
        for (int x = 0; x < num; x++) {
            CountDownLatch latch= new CountDownLatch(1);
            executor.submit(() -> latch.countDown());
            latch.await();
        }
    }

   
}
