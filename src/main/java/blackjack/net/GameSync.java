package blackjack.net;

import blackjack.model.GameState;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author boldi
 */
// Reads one room every second on a background thread
// and tells the listener on the Swing thread when the room changed.
public class GameSync
{
    public static final int POLL_MS = 1000;

    private final GameRepository repository;
    private final String code;
    private final GameSyncListener listener;

    // version of the last state we gave to the listener, -1 = nothing yet
    private int lastVersion = -1;
    // volatile: the background thread always sees the newest value
    private volatile boolean running = false;
    private Thread thread;

    public GameSync(GameRepository repository, String code, GameSyncListener listener)
    {
        this.repository = repository;
        this.code = code;
        this.listener = listener;
    }

    // Starts reading the room in the background.
    public void start()
    {
        if (running)
        {
            return;
        }
        running = true;
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                pollLoop();
            }
        });
        // a daemon thread does not keep the program open after the window is closed
        thread.setDaemon(true);
        thread.start();
    }

    // Stops reading, for example when the game is over or we go back to the menu.
    public void stop()
    {
        running = false;
        if (thread != null)
        {
            thread.interrupt();
        }
    }

    // Runs on the background thread until stop() is called.
    private void pollLoop()
    {
        while (running)
        {
            try
            {
                GameState changed = pollOnce();
                if (changed != null)
                {
                    tellChanged(changed);
                }
            }
            catch (IOException e)
            {
                tellFailed(e.getMessage());
            }
            try
            {
                Thread.sleep(POLL_MS);
            }
            catch (InterruptedException e)
            {
                // stop() woke us up, the while checks running again
            }
        }
    }

    // Reads the room once. Gives back the state if it has a new version, otherwise null.
    GameState pollOnce() throws IOException
    {
        GameState state = repository.readGame(code);
        if (state == null)
        {
            throw new IOException("Room " + code + " does not exist any more.");
        }
        if (state.getVersion() == lastVersion)
        {
            return null;
        }
        lastVersion = state.getVersion();
        return state;
    }

    // Swing may only be used on its own thread, so we hand the state over with invokeLater.
    private void tellChanged(final GameState state)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                listener.stateChanged(state);
            }
        });
    }

    private void tellFailed(final String message)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                listener.syncFailed(message);
            }
        });
    }
}