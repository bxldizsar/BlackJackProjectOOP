/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
/**
 *
 * @author ministar
 */

// Tests for the Deck class: size, draw, text conversion
public class DeckTest {
    
    // A new deck has 52 cards
    @Test
    public void newDeckHasFiftyTwoCards() {
        Deck deck = Deck.createShuffled();
        assertEquals(52, deck.size());
    }

    // Drawing removes one card
    @Test
    public void drawRemovesOneCard() {
        Deck deck = Deck.createShuffled();
        deck.draw();
        assertEquals(51, deck.size());
    }

    // Drawing from an empty deck throws an exception
    @Test
    public void drawFromEmptyDeckThrows() {
        Deck deck = Deck.fromString("");
        assertThrows(IllegalStateException.class, () -> deck.draw());
    }

    // Draw takes the LAST card of the text
    @Test
    public void drawTakesLastCard() {
        Deck deck = Deck.fromString("A-C,10-H,7-S");
        Card card = deck.draw();
        assertEquals("7-S", card.toString());
    }

    // Text conversion works both ways, without a trailing comma
    @Test
    public void toStringAndFromStringRoundTrip() {
        Deck deck = Deck.fromString("A-C,10-H,7-S");
        assertEquals("A-C,10-H,7-S", deck.toString());
    }

    // A shuffled deck has no duplicate cards
    @Test
    public void shuffledDeckHasNoDuplicates() {
        Deck deck = Deck.createShuffled();
        String text = deck.toString();
        String[] parts = text.split(",");
        Set<String> unique = new HashSet<>(Arrays.asList(parts));
        assertEquals(52, unique.size());
    }
}
