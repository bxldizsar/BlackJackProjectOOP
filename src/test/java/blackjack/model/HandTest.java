/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ministar
 */

// Tests for the Hand class: card values, ace rule and bust
public class HandTest {

    // K + 7 = 17
    @Test
    public void kingAndSevenIsSeventeen() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        hand.addCard(new Card(Rank.SEVEN, Suit.HEARTS));
        assertEquals(17, hand.getValue());
    }

    // A + K = 21 (A = 11)
    @Test
    public void aceAndKingIsTwentyOne() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        assertEquals(21, hand.getValue());
    }

    // A + 9 + 5 = 15 (ace counts as 1)
    @Test
    public void aceNineFiveIsFifteen() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        hand.addCard(new Card(Rank.NINE, Suit.CLUBS));
        hand.addCard(new Card(Rank.FIVE, Suit.CLUBS));
        assertEquals(15, hand.getValue());
    }

    // A + A + 9 = 21 (one ace is 11, the other is 1)
    @Test
    public void twoAcesAndNineIsTwentyOne() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        hand.addCard(new Card(Rank.ACE, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.NINE, Suit.CLUBS));
        assertEquals(21, hand.getValue());
    }

    // 10 + J + Q = 30 (bust)
    @Test
    public void tenJackQueenIsBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.TEN, Suit.CLUBS));
        hand.addCard(new Card(Rank.JACK, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.QUEEN, Suit.CLUBS));
        assertEquals(30, hand.getValue());
        assertTrue(hand.isBust());
    }

    // 21 is not a bust
    @Test
    public void twentyOneIsNotBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        assertFalse(hand.isBust());
    }

    // Text conversion works both ways
    @Test
    public void toStringAndFromStringRoundTrip() {
        Hand hand = Hand.fromString("A-C,10-H");
        assertEquals("A-C,10-H", hand.toString());
        assertEquals(21, hand.getValue());
    }

    @Test
    public void addNullCardThrows() {
        Hand hand = new Hand();
        assertThrows(IllegalArgumentException.class, () -> hand.addCard(null));
    }

    @Test
    public void aceAndKingIsBlackjack() {
        Hand hand = Hand.fromString("A-C,K-H");
        assertTrue(hand.isBlackjack());
    }

    @Test
    public void threeCardTwentyOneIsNotBlackjack() {
        Hand hand = Hand.fromString("7-C,7-H,7-D");
        assertFalse(hand.isBlackjack());
    }

    @Test
    public void twoCardTwentyIsNotBlackjack() {
        Hand hand = Hand.fromString("10-C,K-H");
        assertFalse(hand.isBlackjack());
    }
}
