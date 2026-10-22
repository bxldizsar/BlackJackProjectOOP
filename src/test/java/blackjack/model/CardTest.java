/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
/**
 *
 * @author ministar
 */
// Tests for the Card class: text conversion, image name, validation
public class CardTest {
    
    @Test
    public void toStringGivesRankAndSuitCode()
    {
        Card card = new Card(Rank.ACE, Suit.CLUBS);
        assertEquals("A-C", card.toString());
    }

    @Test
    public void imageNameEndsWithPng()
    {
        Card card = new Card(Rank.TEN, Suit.HEARTS);
        assertEquals("10-H.png", card.getImageName());
    }

    @Test
    public void fromStringBuildsTheSameCard()
    {
        Card card = Card.fromString("K-D");
        assertEquals(Rank.KING, card.getRank());
        assertEquals(Suit.DIAMONDS, card.getSuit());
    }

    @Test
    public void unknownRankThrows()
    {
        assertThrows(IllegalArgumentException.class, () -> Card.fromString("Z-C"));
    }

    @Test
    public void nullRankThrows()
    {
        assertThrows(IllegalArgumentException.class, () -> new Card(null, Suit.CLUBS));
    }
    
    @Test
    public void fromNullTextThrows()
    {
        assertThrows(IllegalArgumentException.class, () -> Card.fromString(null));
    }
}
