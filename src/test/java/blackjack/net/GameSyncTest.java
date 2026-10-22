package blackjack.net;

import blackjack.model.GameState;
import blackjack.model.GameStatus;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
// We only test pollOnce() with the fake repository, so no internet and no threads are needed.
public class GameSyncTest
{
    // a listener that does nothing, pollOnce() does not use it
    private final GameSyncListener emptyListener = new GameSyncListener()
    {
        @Override
        public void stateChanged(GameState state)
        {
        }

        @Override
        public void syncFailed(String message)
        {
        }
    };

    @Test
    public void firstPollGivesTheRoom() throws IOException
    {
        FakeGameRepository repository = new FakeGameRepository();
        GameState room = repository.createGame(new GameState("AB12"));
        GameSync sync = new GameSync(repository, room.getCode(), emptyListener);

        GameState state = sync.pollOnce();

        assertNotNull(state);
        assertEquals("AB12", state.getCode());
    }

    @Test
    public void sameVersionGivesNothing() throws IOException
    {
        FakeGameRepository repository = new FakeGameRepository();
        GameState room = repository.createGame(new GameState("AB12"));
        GameSync sync = new GameSync(repository, room.getCode(), emptyListener);

        sync.pollOnce();

        assertNull(sync.pollOnce());
    }

    @Test
    public void newVersionIsNoticed() throws IOException
    {
        FakeGameRepository repository = new FakeGameRepository();
        GameState room = repository.createGame(new GameState("AB12"));
        GameSync sync = new GameSync(repository, room.getCode(), emptyListener);
        sync.pollOnce();

        GameState next = room.copy();
        next.setStatus(GameStatus.PLAYING);
        repository.updateGame(next, room.getVersion());

        GameState state = sync.pollOnce();
        assertNotNull(state);
        assertEquals(GameStatus.PLAYING, state.getStatus());
    }

    @Test
    public void missingRoomGivesAnError()
    {
        GameSync sync = new GameSync(new FakeGameRepository(), "ZZZZ", emptyListener);
        try
        {
            sync.pollOnce();
            fail("a missing room should throw");
        }
        catch (IOException e)
        {
            assertNotNull(e.getMessage());
        }
    }
}