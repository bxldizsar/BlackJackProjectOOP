package blackjack.ui;

import blackjack.logic.BlackjackLogic;
import blackjack.logic.Outcome;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import blackjack.net.GameActions;
import blackjack.net.GameRepository;
import blackjack.net.GameSync;
import blackjack.net.GameSyncListener;
import blackjack.storage.HandLog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;

/**
 *
 * @author boldi
 */
// Runs one game on this computer: it is the link between the table screen and the network.
public class TableController implements GameSyncListener, BlackjackScreen.Actions
{
    // App gets told here when the player leaves the table
    public interface Listener
    {
        void tableClosed(TableController table);
    }
    public static final int TIMEOUT_SECONDS = 30;

    private final Turn me;
    private final BlackjackLogic logic;
    private final HandLog handLog;
    private final Listener listener;
    private final BlackjackScreen screen;
    private final GameActions actions;
    private final GameSync sync;
    // ticks once a second on the Swing thread
    private final Timer timer;

    private GameState current;
    private int secondsLeft = TIMEOUT_SECONDS;
    // every round is written into hands.csv only once
    private int lastLoggedRound = 0;

    public TableController(Turn me, String code, GameRepository repository, BlackjackLogic logic,
            HandLog handLog, Listener listener)
    {
        this.me = me;
        this.logic = logic;
        this.handLog = handLog;
        this.listener = listener;
        this.screen = new BlackjackScreen(me, this);
        this.actions = new GameActions(repository, logic, this);
        this.sync = new GameSync(repository, code, this);
        this.timer = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tick();
            }
        });
    }

    public BlackjackScreen getScreen()
    {
        return screen;
    }

    // Shows the room and starts reading it. Player 2 deals the first round right after joining.
    public void start(GameState room)
    {
        stateChanged(room);
        sync.start();
        if (me == Turn.P2)
        {
            actions.requestNewRound(room);
        }
    }

    public void stop()
    {
        sync.stop();
        timer.stop();
    }

    // --- GameSyncListener: called on the Swing thread ---

    @Override
    public void stateChanged(GameState state)
    {
        // an older answer can arrive after our own save, we skip it
        if (current != null && state.getVersion() < current.getVersion())
        {
            return;
        }
        boolean isNew = current == null || state.getVersion() != current.getVersion();
        current = state;
        screen.showState(state);
        if (isNew)
        {
            restartCountdown();
        }
        if (state.getStatus() == GameStatus.FINISHED)
        {
            logRound(state);
        }
        if (state.getStatus() == GameStatus.TIMEOUT)
        {
            stop();
        }
    }

    @Override
    public void syncFailed(String message)
    {
        screen.onConnectionProblem(message);
    }

    // --- BlackjackScreen.Actions: the buttons ---

    @Override
    public void submitMove(Turn player, MoveType move)
    {
        actions.submitMove(current, player, move);
    }

    @Override
    public void requestNewRound()
    {
        actions.requestNewRound(current);
    }
      // The player leaves. If the game is not over yet, it ends for the other player too,
    // so nobody waits for us and nobody can join an empty room.
    @Override
    public void backToMenu()
    {
        if (current.getStatus() != GameStatus.TIMEOUT)
        {
            actions.requestTimeout(current);
        }
        stop();
        listener.tableClosed(this);
    }
    // --- countdown ---

    private void restartCountdown()
    {
        secondsLeft = TIMEOUT_SECONDS;
        timer.restart();
    }

    // Nobody moved for 30 seconds while waiting or playing: the game ends.
    private void tick()
    {
        if (current.getStatus() != GameStatus.WAITING && current.getStatus() != GameStatus.PLAYING)
        {
            return;
        }
        secondsLeft--;
        screen.onCountdown(secondsLeft);
        if (secondsLeft <= 0)
        {
            timer.stop();
            actions.requestTimeout(current);
        }
    }

    // --- hands.csv ---

    void logRound(GameState state)
    {
        if (state.getRound() <= lastLoggedRound)
        {
            return;
        }
        lastLoggedRound = state.getRound();
        Turn other = Turn.P1;
        if (me == Turn.P1)
        {
            other = Turn.P2;
        }
        try
        {
            handLog.logRound(state.getRound(), state.getPlayerHand(me), state.getPlayerHand(other),
                    state.getDealerHand(), resultText(logic.outcomeFor(state, me)));
        }
        catch (IOException e)
        {
            screen.onConnectionProblem("Could not write " + HandLog.FILE_NAME + ": " + e.getMessage());
        }
    }

    // the result column of hands.csv is in Hungarian, like the header
    static String resultText(Outcome outcome)
    {
        if (outcome == Outcome.WIN)
        {
            return "nyert";
        }
        if (outcome == Outcome.LOSE)
        {
            return "vesztett";
        }
        return "dontetlen";
    }
}