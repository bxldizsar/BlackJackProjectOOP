/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author ministar
 */

// One playing card: a rank ("2".."10", "J", "Q", "K", "A") and a suit
public class Card {
    
    private final String rank;
    private final Suit suit;
    
    public Card(String rank, Suit suit)
    {
        this.rank = rank;
        this.suit = suit;
    }
    
    public String getRank(){ return rank; }
    public Suit getSuit(){ return suit; }
    
    @Override
    public String toString()
    {
        return rank + "-" + suit.getCode();
    }
    
    public String getImageName()
    {
        return toString() + ".png";
    }
    
    public static Card fromString(String text)
    {
        String[] parts = text.split("-");
        if(parts.length != 2)
        {
            throw new IllegalArgumentException("Invalid card: " + text);
        }
        return new Card(parts[0], Suit.fromCode(parts[1]));
    }
}
