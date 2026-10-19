/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author ministar
 */

// Suit of a playing card: clubs, diamonds, hearts, spades
public enum Suit {
    
    CLUBS("C"), DIAMONDS("D"), HEARTS("H"), SPADES("S");
    
    private final String code;
    
    Suit(String code)
    {
        this.code = code;
    }
    
    public String getCode(){ return code; }
    
    public static Suit fromCode(String code)
    {
        for(Suit suit : values())
        {
            if(suit.code.equals(code))
            {
                return suit;
            }
        }
        throw new IllegalArgumentException("Unknown suit: " + code);
    }
}
