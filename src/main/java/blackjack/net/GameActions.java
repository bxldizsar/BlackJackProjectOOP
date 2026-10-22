package blackjack.net;

import blackjack.logic.BlackjackLogic;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 *
 * @author boldi
 */
// Saves our moves (and the dealer on player 2's computer) in the background.
// The rules come from BlackjackLogic, the saving from the GameRepository.
public class GameActions
{
    private final GameRepository repository;
    private final BlackjackLogic logic;
    private final GameSyncListener listener;

    public GameActions(GameRepository repository, BlackjackLogic logic, GameSyncListener listener)
    {
        this.repository = repository;
        this.logic = logic;
        this.listener = listener;
    }

    // HIT or STAY of this player. Returns at once, the result goes to the listener.
    public void submitMove(final GameState current, final Turn me, final MoveType move)
    {
        new SaveWorker()
        {
            @Override
            protected GameState doInBackground() throws IOException
            {
                return doMove(current, me, move);
            }
        }.execute();
    }

    // Deals a new round. Returns at once, the result goes to the listener.
    public void requestNewRound(final GameState current)
    {
        new SaveWorker()
        {
            @Override
            protected GameState doInBackground() throws IOException
            {
                return doNewRound(current);
            }
        }.execute();
    }

    // The 30 seconds are over: the room becomes TIMEOUT. Returns at once.
    public void requestTimeout(final GameState current)
    {
        new SaveWorker()
        {
            @Override
            protected GameState doInBackground() throws IOException
            {
                return doTimeout(current);
            }
        }.execute();
    }

    // Does the move and saves it. Runs on the background thread.
    GameState doMove(GameState current, Turn me, MoveType move) throws IOException
    {
        GameState next;
        try
        {
            if (move == MoveType.HIT)
            {
                next = logic.hit(current, me);
            }
            else
            {
                next = logic.stay(current, me);
            }
        }
        catch (IllegalStateException e)
        {
            throw new IOException("It is not your turn.");
        }
        next = save(current, next);
        // the dealer is always played on player 2's computer, so it happens only once
        if (me == Turn.P2 && next.getTurn() == Turn.DEALER && next.getStatus() == GameStatus.PLAYING)
        {
            next = save(next, logic.playDealer(next));
        }
        return next;
    }

    // Deals the new round and saves it. Runs on the background thread.
    GameState doNewRound(GameState current) throws IOException
    {
        return save(current, logic.dealNewRound(current));
    }

    // Marks the room as TIMEOUT and saves it. Runs on the background thread.
    GameState doTimeout(GameState current) throws IOException
    {
        GameState next = current.copy();
        next.setStatus(GameStatus.TIMEOUT);
        return save(current, next);
    }

    // Saves next only if nobody changed the room since current.
    private GameState save(GameState current, GameState next) throws IOException
    {
        next.setVersion(current.getVersion() + 1);
        if (!repository.updateGame(next, current.getVersion()))
        {
            throw new IOException("The other player changed the game first, please try again.");
        }
        return next;
    }

    // Every background job of this class: it gives back the saved state,
    // and done() runs on the Swing thread when the job is over.
    private abstract class SaveWorker extends SwingWorker<GameState, Void>
    {
        @Override
        protected void done()
        {
            try
            {
                listener.stateChanged(get());
            }
            catch (InterruptedException e)
            {
                listener.syncFailed("Saving was stopped.");
            }
            catch (ExecutionException e)
            {
                // get() wraps the real error, getCause() gives it back
                listener.syncFailed(e.getCause().getMessage());
            }
        }
    }
}