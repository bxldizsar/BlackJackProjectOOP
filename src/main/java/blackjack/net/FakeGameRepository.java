
package blackjack.net;

import blackjack.model.GameState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import blackjack.model.GameStatus;
        

/**
 *
 * @author boldi
 */
//keeps the rooms in a list in memory instead of supabase
//use for tests and for trying the game withouy internet.
public class FakeGameRepository implements GameRepository
{
    //all rooms,like the rows of the games table
    private List<GameState> games = new ArrayList<GameState>();
    //counter for making a new code when one is already taken
    private int codeCounter = 0; 

    @Override
    public GameState createGame(GameState initial) throws IOException {
        GameState game = initial.copy();
        // the code must be unique, like the key in the real table
        while (findGame(game.getCode()) != null) 
        {
            game.setCode(nextCode());
        }
        games.add(game);
        return game.copy();
    }

    @Override
    public GameState joinGame(String code) throws IOException {
        GameState game = findGame(code);
        // wrong code, game already started, or someone already joined
        if (game == null || game.getStatus() != GameStatus.WAITING || game.getP2Id() != null) 
        {
            return null;
        }
        game.setP2Id("fake-p2");
        // every save makes the version one bigger
        game.setVersion(game.getVersion() + 1);
        return game.copy();
    }

    @Override
    public GameState readGame(String code) throws IOException {
        GameState game = findGame(code);
        if (game == null) {
            return null;
        }
        // give back a copy, like a real network answer
        return game.copy();
    }

    @Override
    public boolean updateGame(GameState newState, int expectedVersion) throws IOException {
        GameState stored = findGame(newState.getCode());
        // there is no room with this code
        if (stored == null) {
            return false;
        }
        // someone else saved first, the caller has an old state
        if (stored.getVersion() != expectedVersion) 
        {
            return false;
        }
        GameState saved = newState.copy();
        saved.setVersion(expectedVersion + 1);
        // put the new row in the place of the old one
        games.set(games.indexOf(stored), saved);
        return true; 
    }
    //finds the room with this codem or null if there is none
    private GameState findGame(String code)
    {
        for (GameState game : games)
        {
            if (game.getCode().equals(code))
            {
                return game;
            }
        }
        return null;
    }
    //makes a new room code: 1001,1002,...
    private String nextCode()
    {
        codeCounter ++;
        return String.valueOf(1000 + codeCounter);
    }
}
