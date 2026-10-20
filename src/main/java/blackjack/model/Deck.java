/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author ministar
 */

// A deck of cards: build, shuffle, draw from the END, convert to/from text
public class Deck {
    
    private final List<Card> cards = new ArrayList<>();
    
    // Creates a new full deck of 52 cards, shuffled
    public static Deck createShuffled()
    {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        Deck deck = new Deck();
        
        for(Suit suit : Suit.values())
        {
            for(String rank : ranks)
            {
                deck.cards.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck.cards);
        return deck;
    }
    
    // Removes and returns the LAST card of the deck
    public Card draw()
    {
        if(cards.isEmpty())
        {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.remove(cards.size() - 1);
    }
    
    public int size()
    {
        return cards.size();
    }
    
    // Text form for the database, e.g. "A-C,10-H,7-S"
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(Card card : cards)
        {
            if(builder.length() > 0)
            {
                builder.append(",");
            }
            builder.append(card.toString());
        }
        return builder.toString();
    }
    
    // Builds a deck from the database text; empty text gives an empty deck
    public static Deck fromString(String text)
    {
        Deck deck = new Deck();
        if(text == null || text.isEmpty())
        {
            return deck;
        }
        
        String[] parts = text.split(",");
        
        for(String part : parts)
        {
            deck.cards.add(Card.fromString(part));
        }
        return deck;
    }
}
