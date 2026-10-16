/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author boldi
 */
public enum GameStatus {
    WAITING("waiting"),
    PLAYING("playing"),
    FINISHED("finished"),
    TIMEOUT("timeout"); 
    
    private final String dbValue;
    
    GameStatus(String dbValue) 
    {
        this.dbValue = dbValue;
    }
    
    public String getDbValue()
    {
        return dbValue;
    }
    
    public static GameStatus
            fromDbValue(String value)
            {
                for (GameStatus status : values())
                {
                    if (status.dbValue.equals(value))
                    {
                        return status;
                    }
                }
                throw new IllegalArgumentException("Ismeretlen status: " + value);
            }
            
    
}
