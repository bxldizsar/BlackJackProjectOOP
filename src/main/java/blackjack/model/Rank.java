/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author ministar
 */

// Rank of a playing card: text code (used in the database) and base value
public enum Rank {
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6),
    SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10),
    JACK("J", 10), QUEEN("Q", 10), KING("K", 10), ACE("A", 11);
    
    private final String code;
    private final int value;
    
    Rank(String code, int value)
    {
        this.code = code;
        this.value = value;
    }
    
    public String getCode(){ return code; }
    
    // Base value; the ace is 11 here, the Hand lowers it to 1 when needed
    public int getValue(){ return value; }
    
    public static Rank fromCode(String code)
    {
        for(Rank rank : values())
        {
            if(rank.getCode().equals(code))
            {
                return rank;
            }
        }
        throw new IllegalArgumentException("Unknown rank: " + code);
    }
}
