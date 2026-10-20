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
    public void dealGivesTwoCardsToEveryone() {
        GameState next = logic.dealNewRound(new GameState("AB12"));
        assertEquals(2, Hand.fromString(next.getDealerHand()).getCards().size());
        assertEquals(2, Hand.fromString(next.getP1Hand()).getCards().size());
        assertEquals(2, Hand.fromString(next.getP2Hand()).getCards().size());
        assertEquals(46, Deck.fromString(next.getDeck()).size());
    }

    // After dealing, P1 is next and the game is playing
    @Test
    public void dealSetsTurnAndStatus() {
        GameState next = logic.dealNewRound(new GameState("AB12"));
        assertEquals(Turn.P1, next.getTurn());
        assertEquals(GameStatus.PLAYING, next.getStatus());
    }

    // The given state is not changed
    @Test
    public void dealDoesNotChangeTheOriginal() {
        GameState original = new GameState("AB12");
        GameState next = logic.dealNewRound(original);
        assertNotSame(original, next);
        assertEquals("", original.getP1Hand());
        assertEquals(GameStatus.WAITING, original.getStatus());
    }

    // Builds a playing state where P1 is next, with the given hand and deck text
    private GameState playingState(String p1Hand, String deckText) {
        GameState s = new GameState("AB12");
        s.setStatus(GameStatus.PLAYING);
        s.setTurn(Turn.P1);
        s.setP1Hand(p1Hand);
        s.setDeck(deckText);
        return s;
    }

    // Hit adds the LAST card of the deck to the hand
    @Test
    public void hitAddsLastCardOfDeck() {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals("5-C,6-D,4-D", next.getP1Hand());
        assertEquals("2-H,3-S", next.getDeck());
    }

    // Without a bust, the turn stays with the same player
    @Test
    public void hitWithoutBustKeepsTurn() {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals(Turn.P1, next.getTurn());
    }

    // A bust passes the turn to P2
    @Test
    public void hitBustPassesTurnToP2() {
        GameState s = playingState("10-C,6-D", "2-H,3-S,K-D");
        GameState next = logic.hit(s, Turn.P1);
        assertEquals(Turn.P2, next.getTurn());
    }

    // Hitting when it is not your turn throws an exception
    @Test
    public void hitOutOfTurnThrows() {
        GameState s = playingState("5-C,6-D", "2-H,3-S,4-D");
        assertThrows(IllegalStateException.class, () -> logic.hit(s, Turn.P2));
    }

    // Stay passes the turn from P1 to P2
    @Test
    public void stayPassesTurnToP2() {
        GameState next = logic.stay(playingState("5-C,6-D", "2-H"), Turn.P1);
        assertEquals(Turn.P2, next.getTurn());
    }

    // Stay out of turn throws
    @Test
    public void stayOutOfTurnThrows() {
        GameState s = playingState("5-C,6-D", "2-H");
        assertThrows(IllegalStateException.class, () -> logic.stay(s, Turn.P2));
    }

    // Dealer with 10+5 = 15 draws a 3 (last card) and stops at 18
    @Test
    public void dealerDrawsUnderSeventeen() {
        GameState s = playingState("5-C,6-D", "2-H,3-S");
        s.setTurn(Turn.DEALER);
        s.setDealerHand("10-C,5-D");
        GameState next = logic.playDealer(s);
        assertEquals("10-C,5-D,3-S", next.getDealerHand());
        assertEquals(GameStatus.FINISHED, next.getStatus());
    }

    // Dealer with 17 does not draw
    @Test
    public void dealerStopsAtSeventeen() {
        GameState s = playingState("5-C,6-D", "2-H,3-S");
        s.setTurn(Turn.DEALER);
        s.setDealerHand("10-C,7-D");
        GameState next = logic.playDealer(s);
        assertEquals("10-C,7-D", next.getDealerHand());
        assertEquals(GameStatus.FINISHED, next.getStatus());
    }

    // Builds a finished state with the given P1 and dealer hands
    private GameState finishedState(String p1Hand, String dealerHand) {
        GameState s = new GameState("AB12");
        s.setStatus(GameStatus.FINISHED);
        s.setP1Hand(p1Hand);
        s.setDealerHand(dealerHand);
        return s;
    }

    @Test
    public void playerBustLosesEvenIfDealerBusts() {
        GameState s = finishedState("10-C,J-D,Q-C", "10-H,J-H,K-H");
        assertEquals(Outcome.LOSE, logic.outcomeFor(s, Turn.P1));
    }

    @Test
    public void dealerBustWinsForPlayer() {
        GameState s = finishedState("10-C,7-D", "10-H,J-H,K-H");
        assertEquals(Outcome.WIN, logic.outcomeFor(s, Turn.P1));
    }

    @Test
    public void higherValueWins() {
        GameState s = finishedState("10-C,9-D", "10-H,8-H");
        assertEquals(Outcome.WIN, logic.outcomeFor(s, Turn.P1));
    }

    @Test
    public void lowerValueLoses() {
        GameState s = finishedState("10-C,7-D", "10-H,8-H");
        assertEquals(Outcome.LOSE, logic.outcomeFor(s, Turn.P1));
    }

    @Test
    public void equalValuePushes() {
        GameState s = finishedState("10-C,8-D", "10-H,8-H");
        assertEquals(Outcome.PUSH, logic.outcomeFor(s, Turn.P1));
    }

    // P2 busts: the turn goes to the dealer
    @Test
    public void hitBustPassesTurnFromP2ToDealer() {
        GameState s = playingState("5-C,6-D", "2-H,3-S,K-D");
        s.setTurn(Turn.P2);
        s.setPlayerHand(Turn.P2, "10-C,6-D");
        GameState next = logic.hit(s, Turn.P2);
        assertEquals(Turn.DEALER, next.getTurn());
    }

    // playDealer outside the dealer's turn throws
    @Test
    public void playDealerOutOfTurnThrows() {
        GameState s = playingState("5-C,6-D", "2-H,3-S");
        assertThrows(IllegalStateException.class, () -> logic.playDealer(s));
    }

    // The outcome works for P2 too
    @Test
    public void outcomeForP2() {
        GameState s = finishedState("10-C,7-D", "10-H,8-H");
        s.setP2Hand("10-C,9-D");
        assertEquals(Outcome.WIN, logic.outcomeFor(s, Turn.P2));
        assertEquals(Outcome.LOSE, logic.outcomeFor(s, Turn.P1));
    }

    // Dealing after a finished round starts the next round number
    @Test
    public void dealAfterFinishedIncreasesRound() {
        GameState s = new GameState("AB12");
        s.setStatus(GameStatus.FINISHED);
        assertEquals(2, logic.dealNewRound(s).getRound());
    }

    // The very first deal stays in round 1
    @Test
    public void firstDealKeepsRoundOne() {
        assertEquals(1, logic.dealNewRound(new GameState("AB12")).getRound());
    }
}
