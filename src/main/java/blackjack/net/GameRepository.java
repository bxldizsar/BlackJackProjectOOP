/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package blackjack.net;
import blackjack.model.GameState;
import java.io.IOException;
/**
 *
 * @author boldi
 */

//everything the game needs from the shared table
//supabase does it over the network, fakegamerepo in memory for tests
//all methods wait for network, so never call them on the swing thread 
public interface GameRepository 
{
 //saves a new room. if the code is alreadt taken, it tries a new code
 //returns the saved state (with the final code)
    GameState createGame(GameState initial) throws IOException;
    
 //joins a waiting room as player2
 // returns null if the code is wrong, the game already strted or the room is full.
    GameState joinGame(String code) throws IOException;
    
 //returns the current state of the room, or null if ther is no such code
    GameState readGame(String code) throws IOException;
    
   //saves newState only if the version in the table is still expectedVersion
  //returns false if someon else saved first, then we have to read again.
    
    boolean updateGame(GameState newState, int expectedVersion) throws IOException;
}
