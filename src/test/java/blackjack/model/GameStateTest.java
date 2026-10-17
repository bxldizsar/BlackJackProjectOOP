/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author boldi
 */

//test for the gamestate class 
public class GameStateTest
{
     @Test
    public void newGameStartsWaitingInRoundOne() 
    {
        GameState s = new GameState("AB12");
        assertEquals("AB12", s.getCode());
        assertEquals(GameStatus.WAITING, s.getStatus());
        assertEquals(1, s.getRound());
        assertEquals(0, s.getVersion());
        assertEquals("", s.getP1Hand());
        assertNull(s.getP2Id());
    }

    @Test
    public void copyDoesNotChangeTheOriginal() 
    {
        GameState original = new GameState("AB12");
        original.setP1Hand("A-C,10-H");

        GameState copy = original.copy();
        copy.setP1Hand("A-C,10-H,7-S");
        copy.setVersion(5);

        // the original must still have the old values
        assertEquals("A-C,10-H", original.getP1Hand());
        assertEquals(0, original.getVersion());
    }

    @Test
    public void dealerIsNotAPlayer() 
    {
        GameState s = new GameState("AB12");
        try 
        {
            s.getPlayerHand(Turn.DEALER);
            fail("should have thrown an exception");
        } catch (IllegalArgumentException e) 
        {
            // this is what we expect
        }
    }
    
}
