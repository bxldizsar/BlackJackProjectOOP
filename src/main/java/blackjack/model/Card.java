/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author ministar
 */

// One playing card: a rank and a suit
public class Card {
    
    private final Rank rank;
    private final Suit suit;
    
    public Card(Rank rank, Suit suit)
    {
        if(rank == null || suit == null)
        {
            throw new IllegalArgumentException("Card needs a rank and a suit");
        }
        this.rank = rank;
        this.suit = suit;
    }
    
    public Rank getRank(){ return rank; }
    public Suit getSuit(){ return suit; }
    
    @Override
    public String toString()
    {
        return rank.getCode() + "-" + suit.getCode();
    }
    
    public String getImageName()
    {
        return toString() + ".png";
    }
    
    public static Card fromString(String text)
    {
        if(text == null)
        {
            throw new IllegalArgumentException("Card text must not be null");
        }
        String[] parts = text.split("-");
        if(parts.length != 2)
        {
            throw new IllegalArgumentException("Invalid card: " + text);
        }
        return new Card(Rank.fromCode(parts[0]), Suit.fromCode(parts[1]));
    }
}
