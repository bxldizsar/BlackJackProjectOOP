package blackjack.net;

import blackjack.logic.BlackjackLogic;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
// Tests the do... methods with the fake repository and the real logic, without threads.
public class GameActionsTest
{
    private FakeGameRepository repository;
    private GameActions actions;
    private GameState room;

    @BeforeEach
    public void makeRoom() throws IOException
    {
        repository = new FakeGameRepository();
        // the listener is only used by the background part, so null is fine here
        actions = new GameActions(repository, new BlackjackLogic(), null);
        room = repository.createGame(new GameState("AB12"));
    }

    @Test
    public void newRoundIsSaved() throws IOException
    {
        GameState dealt = actions.doNewRound(room);

        assertEquals(GameStatus.PLAYING, dealt.getStatus());
        assertEquals(Turn.P1, dealt.getTurn());
        assertEquals(1, repository.readGame("AB12").getVersion());
    }

    @Test
    public void stayIsSavedAndTheTurnMoves() throws IOException
    {
        GameState dealt = actions.doNewRound(room);

        GameState next = actions.doMove(dealt, Turn.P1, MoveType.STAY);

        assertEquals(Turn.P2, next.getTurn());
        assertEquals(Turn.P2, repository.readGame("AB12").getTurn());
        assertEquals(2, repository.readGame("AB12").getVersion());
    }

    @Test
    public void dealerPlaysAfterPlayerTwo() throws IOException
    {
        GameState dealt = actions.doNewRound(room);
        GameState afterP1 = actions.doMove(dealt, Turn.P1, MoveType.STAY);

        GameState afterP2 = actions.doMove(afterP1, Turn.P2, MoveType.STAY);

        assertEquals(GameStatus.FINISHED, afterP2.getStatus());
        assertEquals(GameStatus.FINISHED, repository.readGame("AB12").getStatus());
    }

    @Test
    public void oldStateIsRefused() throws IOException
    {
        GameState dealt = actions.doNewRound(room);
        actions.doMove(dealt, Turn.P1, MoveType.STAY);
        try
        {
            // dealt is old now, the room already has a newer version
            actions.doNewRound(dealt);
            fail("an old state should not be saved");
        }
        catch (IOException e)
        {
            assertEquals(Turn.P2, repository.readGame("AB12").getTurn());
        }
    }

    @Test
    public void wrongTurnGivesAnError() throws IOException
    {
        GameState dealt = actions.doNewRound(room);
        try
        {
            actions.doMove(dealt, Turn.P2, MoveType.HIT);
            fail("player 2 cannot move when it is player 1's turn");
        }
        catch (IOException e)
        {
            assertEquals("It is not your turn.", e.getMessage());
        }
    }

    @Test
    public void timeoutIsSaved() throws IOException
    {
        GameState dealt = actions.doNewRound(room);

        GameState ended = actions.doTimeout(dealt);

        assertEquals(GameStatus.TIMEOUT, ended.getStatus());
        assertEquals(GameStatus.TIMEOUT, repository.readGame("AB12").getStatus());
    }
}