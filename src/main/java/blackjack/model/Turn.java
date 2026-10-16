/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author boldi
 */
public enum Turn {
    P1("p1"),
    P2("p2"),
    DEALER("dealer");
    
    private final String dbValue;
    
    Turn(String dbValue)
    {
        this.dbValue = dbValue;
    }
    
    public String getDbValue()
    {
        return dbValue;
    }
    
    public static Turn fromDbValue(String value)
    {
        if (value == null)
        {
            return null;
        }
        for (Turn turn : values())
        {
            if(turn.dbValue.equals(value))
            {
                return turn; 
            }
        }
        throw new IllegalArgumentException("Ismeretlen turn: " + value);
    }
            
}
