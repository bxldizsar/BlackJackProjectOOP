/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package blackjack.net;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

//test for the in-memory repository 
public class FakeGameRepositoryTest 
{
    
@Test 
public void createdGameCanBeRead() throws IOException
{
    FakeGameRepository repo = new FakeGameRepository();
    repo.createGame(new GameState("AB12"));
    GameState read = repo.readGame("AB12");
    assertNotNull(read);
    assertEquals(GameStatus.WAITING,read.getStatus());
    //a code that was never created gives null
    assertNull(repo.readGame("XXXX"));
}


@Test 
public void takenCodeGetsANewCode() throws IOException
{
    FakeGameRepository repo = new FakeGameRepository();
    GameState first = repo.createGame(new GameState("AB12"));
    GameState second = repo.createGame(new GameState("AB12"));
    assertEquals("AB12",first.getCode());
    assertNotEquals("AB12",second.getCode());
}

@Test 
public void onlyOnePlayerCanJoin() throws IOException
{
    FakeGameRepository repo = new FakeGameRepository();
    repo.createGame(new GameState("AB12"));
    //wrong code 
    assertNull(repo.joinGame("XXXX"));
    GameState joined = repo.joinGame("AB12");
    assertNotNull(joined);
    assertEquals(1,joined.getVersion());
    //the room is full now
    assertNull(repo.joinGame("AB12"));
}

@Test
public void saveWithOldVersionFails() throws IOException 
{
        FakeGameRepository repo = new FakeGameRepository();
        repo.createGame(new GameState("AB12"));
        // both players read version 0
        GameState p1 = repo.readGame("AB12");
        GameState p2 = repo.readGame("AB12");
        p1.setP1Hand("A-C,10-H");
        assertTrue(repo.updateGame(p1, 0));
        // p2 is too late, the room is already version 1
        p2.setP2Hand("9-D,7-C");
        assertFalse(repo.updateGame(p2, 0));
        // p1's cards are still there
        GameState now = repo.readGame("AB12");
        assertEquals("A-C,10-H", now.getP1Hand());
        assertEquals("", now.getP2Hand());
        assertEquals(1, now.getVersion());
    }

    @Test
    public void changingAReadCopyDoesNotChangeTheRoom() throws IOException 
    {
        FakeGameRepository repo = new FakeGameRepository();
        repo.createGame(new GameState("AB12"));
        GameState read = repo.readGame("AB12");
        read.setP1Hand("A-C");
        // without updateGame the room must stay the same
        assertEquals("", repo.readGame("AB12").getP1Hand());
    }
}


