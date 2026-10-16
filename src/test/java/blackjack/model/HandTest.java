/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
/**
 *
 * @author ministar
 */

// Tests for the Hand class: card values, ace rule and bust
public class HandTest {
    
    // K + 7 = 17
    @Test
    public void kingAndSevenIsSeventeen()
    {
        Hand hand = new Hand();
        hand.addCard(new Card("K", Suit.SPADES));
        hand.addCard(new Card("7", Suit.HEARTS));
        assertEquals(17, hand.getValue());
    }
    
    // A + K = 21 (A = 11)
    @Test
    public void aceAndKingIsTwentyOne()
    {
        Hand hand = new Hand();
        hand.addCard(new Card("A", Suit.DIAMONDS));
        hand.addCard(new Card("K", Suit.SPADES));
        assertEquals(21, hand.getValue());
    }
    
    // A + 9 + 5 = 15 (ace counts as 1)
    @Test
    public void aceNineFiveIsFifteen() {
        Hand hand = new Hand();
        hand.addCard(new Card("A", Suit.CLUBS));
        hand.addCard(new Card("9", Suit.CLUBS));
        hand.addCard(new Card("5", Suit.CLUBS));
        assertEquals(15, hand.getValue());
    }

    // A + A + 9 = 21 (one ace is 11, the other is 1)
    @Test
    public void twoAcesAndNineIsTwentyOne() {
        Hand hand = new Hand();
        hand.addCard(new Card("A", Suit.CLUBS));
        hand.addCard(new Card("A", Suit.DIAMONDS));
        hand.addCard(new Card("9", Suit.CLUBS));
        assertEquals(21, hand.getValue());
    }

    // 10 + J + Q = 30 (bust)
    @Test
    public void tenJackQueenIsBust() {
        Hand hand = new Hand();
        hand.addCard(new Card("10", Suit.CLUBS));
        hand.addCard(new Card("J", Suit.DIAMONDS));
        hand.addCard(new Card("Q", Suit.CLUBS));
        assertEquals(30, hand.getValue());
        assertTrue(hand.isBust());
    }

    // 21 is not a bust
    @Test
    public void twentyOneIsNotBust() {
        Hand hand = new Hand();
        hand.addCard(new Card("A", Suit.DIAMONDS));
        hand.addCard(new Card("K", Suit.SPADES));
        assertFalse(hand.isBust());
    }
}
