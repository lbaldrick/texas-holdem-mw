package com.baldrick.texas.holdem.model;

import com.baldrick.texas.holdem.enums.Suit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeckTest {
    
    private Deck deck;
    
    private final List<Suit> expectedSuits = new ArrayList<>(Arrays.asList(Suit.values()));
    
    public DeckTest() {
    }

    @Before
    public void setUp() {
        deck = Deck.newInstance();
    }
    
     @Test
     public void testCreatingFreshDeckSize() {
         assertThat(52).isEqualTo(deck.getDeckSize());
     }
     
     @Test
     public void testCardsInFreshDeck() {
       Map<Suit, List<Card>> suitCount = getCardCount(deck);
         
         expectedSuits.forEach((suit) -> {
             List<Card> cards = suitCount.get(suit);
             int count = cards.size();
             assertThat(cards)
                     .filteredOn("suit", suit)
                     .hasSize(count);
             
              assertThat(cards)
                     .extracting("number")
                     .containsOnly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
         });
     }
     
     @Test
     public void testDealingSingleCardsFromFreshDeck() {     
          assertThat(deck.dealCard()).hasNoNullFieldsOrProperties();
     }
     
     @Test
     public void testDealingMultipleCardsFromFreshDeck() {     
          assertThat(deck.dealCards(5)).hasSize(5);
     }
     
     @Test
     public void testDealingAllCardsFromFreshDeck() {     
          assertThat(new HashSet(deck.dealCards(52))).hasSize(52);
     }
     
      @Test
     public void testDealingMoreThanAllCardsFromFreshDeck() {     
          assertThat(new HashSet(deck.dealCards(54))).hasSize(52);
     }
 
     private Map<Suit, List<Card>> getCardCount(Deck deck) {
         Map<Suit, List<Card>> suitCount = new HashMap<>();
         for (int x = 0; x < 4; x++) {
             for (int y = 0; y < 13; y++) {
                 Card card = deck.dealCard();
                 Suit suit = card.getSuit();
                 if (!suitCount.containsKey(suit)) {
                     suitCount.put(suit, new ArrayList<>());
                 }
                 suitCount.get(suit).add(card);
             }
         }
         
         return suitCount;
     }
}
