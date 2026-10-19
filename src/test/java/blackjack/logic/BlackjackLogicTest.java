/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    
    // Builds a playing state where P1 is next, with the given hand and deck text
    private GameState playingState(String p1Hand, String deckText)
    {
        GameState s = new GameState("AB12");
        s.setStatus(GameStatus.PLAYING);
        s.setTurn(Turn.P1);
        s.setP1Hand(p1Hand);
        s.setDeck(deckText);
        return s;
    }

    // Hit adds the LAST card of the deck to the hand
    @Test
    public void hitAddsLastCardOfDeck()
    {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals("5-C,6-D,4-D", next.getP1Hand());
        assertEquals("2-H,3-S", next.getDeck());
    }

    // Without a bust, the turn stays with the same player
    @Test
    public void hitWithoutBustKeepsTurn()
    {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals(Turn.P1, next.getTurn());
    }

    // A bust passes the turn to P2
    @Test
    public void hitBustPassesTurnToP2()
    {
        GameState s = playingState("10-C,6-D", "2-H,3-S,K-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals(Turn.P2, next.getTurn());
    }

    // Hitting when it is not your turn throws an exception
    @Test
    public void hitOutOfTurnThrows()
    {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        assertThrows(IllegalStateException.class, () -> logic.hit(s, Turn.P2));
    }
}
