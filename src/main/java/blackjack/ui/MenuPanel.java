package blackjack.ui;

import blackjack.model.GameState;
import blackjack.model.RoomCode;
import blackjack.model.Turn;
import blackjack.net.GameRepository;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 *
 * @author boldi
 */
// Start screen: create a new room or join one with a code.
public class MenuPanel extends JPanel
{
    // App gets the room from here when it is ready
    public interface Listener
    {
        void roomReady(GameState room, Turn me);
    }

    private static final Color TABLE_GREEN = new Color(14, 92, 52);

    private final GameRepository repository;
    private final Listener listener;
    private final Random random = new Random();

    private final JButton createButton = new JButton("Create room");
    private final JTextField codeField = new JTextField(6);
    private final JButton joinButton = new JButton("Join");
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);

    public MenuPanel(GameRepository repository, Listener listener)
    {
        this.repository = repository;
        this.listener = listener;
        setLayout(new BorderLayout());
        setBackground(TABLE_GREEN);
        add(buildTitle(), BorderLayout.NORTH);
        add(buildButtons(), BorderLayout.CENTER);
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel, BorderLayout.SOUTH);
        addListeners();
    }

    private JLabel buildTitle()
    {
        JLabel title = new JLabel("Blackjack", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        return title;
    }

    // two rows: "Create room" and "code + Join"
    private JPanel buildButtons()
    {
        JPanel createRow = new JPanel();
        createRow.setOpaque(false);
        createRow.add(createButton);

        JPanel joinRow = new JPanel();
        joinRow.setOpaque(false);
        JLabel codeLabel = new JLabel("Room code:");
        codeLabel.setForeground(Color.WHITE);
        joinRow.add(codeLabel);
        joinRow.add(codeField);
        joinRow.add(joinButton);

        JPanel rows = new JPanel(new GridLayout(2, 1));
        rows.setOpaque(false);
        rows.add(createRow);
        rows.add(joinRow);
        return rows;
    }

    private void addListeners()
    {
        createButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createRoom();
            }
        });
        joinButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                joinRoom();
            }
        });
    }

    // We become player 1 of a new room.
    private void createRoom()
    {
        final GameState initial = new GameState(RoomCode.random(random));
        setBusy("Creating the room...");
        new RoomWorker(Turn.P1)
        {
            @Override
            protected GameState doInBackground() throws Exception
            {
                return repository.createGame(initial);
            }
        }.execute();
    }

    // We become player 2 of the room with the typed code.
    private void joinRoom()
    {
        final String code = RoomCode.clean(codeField.getText());
        if (!RoomCode.isValid(code))
        {
            statusLabel.setText("The room code is 4 letters or digits, like AB12.");
            return;
        }
        setBusy("Joining room " + code + "...");
        new RoomWorker(Turn.P2)
        {
            @Override
            protected GameState doInBackground() throws Exception
            {
                return repository.joinGame(code);
            }
        }.execute();
    }

    // while the network works, the buttons can't be pressed again
    private void setBusy(String message)
    {
        createButton.setEnabled(false);
        joinButton.setEnabled(false);
        statusLabel.setText(message);
    }

    // Creating or joining in the background; done() runs on the Swing thread.
    private abstract class RoomWorker extends SwingWorker<GameState, Void>
    {
        private final Turn me;

        RoomWorker(Turn me)
        {
            this.me = me;
        }

        @Override
        protected void done()
        {
            createButton.setEnabled(true);
            joinButton.setEnabled(true);
            try
            {
                GameState room = get();
                if (room == null)
                {
                    statusLabel.setText("There is no free room with this code.");
                    return;
                }
                statusLabel.setText(" ");
                listener.roomReady(room, me);
            }
            catch (InterruptedException e)
            {
                statusLabel.setText("Stopped.");
            }
            catch (ExecutionException e)
            {
                statusLabel.setText("Error: " + e.getCause().getMessage());
            }
        }
    }
}