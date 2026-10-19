/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import blackjack.model.Deck;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Hand;
import blackjack.model.Turn;
import org.junit.jupiter.api.Test;
/**
 *
 * @author ministar
 */

// Tests for BlackjackLogic
public class BlackjackLogicTest {
    
    private final BlackjackLogic logic = new BlackjackLogic();
    
    // Everyone gets 2 cards, the deck has 52 - 6 = 46 left
    @Test
    public void dealGivesTwoCardsToEveryone()
    {
        GameState next = logic.dealNewRound(new GameState("AB12"));
        assertEquals(2, Hand.fromString(next.getDealerHand()).getCards().size());
        assertEquals(2, Hand.fromString(next.getP1Hand()).getCards().size());
        assertEquals(2, Hand.fromString(next.getP2Hand()).getCards().size());
        assertEquals(46, Deck.fromString(next.getDeck()).size());
    }
    
    // After dealing, P1 is next and the game is playing
    @Test
    public void dealSetsTurnAndStatus()
    {
        GameState next = logic.dealNewRound(new GameState("AB12"));
        assertEquals(Turn.P1, next.getTurn());
        assertEquals(GameStatus.PLAYING, next.getStatus());
    }
    
    // The given state is not changed
    @Test
    public void dealDoesNotChangeTheOriginal()
    {
        GameState original = new GameState("AB12");
        GameState next = logic.dealNewRound(original);
        assertNotSame(original, next);
        assertEquals("", original.getP1Hand());
        assertEquals(GameStatus.WAITING, original.getStatus());
    }
}
