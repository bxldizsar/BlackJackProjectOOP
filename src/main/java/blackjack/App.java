package blackjack;

import blackjack.logic.BlackjackLogic;
import blackjack.model.GameState;
import blackjack.model.Turn;
import blackjack.net.AppConfig;
import blackjack.net.GameRepository;
import blackjack.net.SupabaseClient;
import blackjack.storage.HandLog;
import blackjack.ui.MenuPanel;
import blackjack.ui.TableController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author boldi
 */
// Starts the program: one 600x600 window, the screens are switched inside it.
// Only this class creates the real classes (SupabaseClient, HandLog...) and connects them.
public class App
{
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 600;
    // green table color, every screen uses this
    public static final Color TABLE_GREEN = new Color(14, 92, 52);

    // screen names for showScreen()
    public static final String SCREEN_MENU = "menu";
    public static final String SCREEN_WAITING = "waiting";
    public static final String SCREEN_TABLE = "table";

    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel screens;

    public App()
    {
        frame = new JFrame("Blackjack");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // center of the screen

        cardLayout = new CardLayout();
        screens = new JPanel(cardLayout);
        screens.setBackground(TABLE_GREEN);
        frame.add(screens, BorderLayout.CENTER);
    }

    // adds a screen with a name, e.g. SCREEN_TABLE
    public void addScreen(String name, JComponent screen)
    {
        screens.add(screen, name);
    }

    // shows the screen with this name
    public void showScreen(String name)
    {
        cardLayout.show(screens, name);
    }

    public JFrame getFrame()
    {
        return frame;
    }

    // Reads the settings and shows the menu.
    private void start()
    {
        final GameRepository repository;
        try
        {
            repository = new SupabaseClient(AppConfig.load(AppConfig.FILE_NAME));
        }
        catch (IOException e)
        {
            showErrorAndExit(e.getMessage());
            return;
        }
        catch (IllegalArgumentException e)
        {
            showErrorAndExit("Bad setting in config.properties: " + e.getMessage());
            return;
        }
        MenuPanel menu = new MenuPanel(repository, new MenuPanel.Listener()
        {
            @Override
            public void roomReady(GameState room, Turn me)
            {
                startGame(repository, room, me);
            }
        });
        addScreen(SCREEN_MENU, menu);
        showScreen(SCREEN_MENU);
    }

    // The room is ready: build the table for it and switch to it.
    private void startGame(GameRepository repository, GameState room, Turn me)
    {
        TableController controller = new TableController(me, room.getCode(), repository,
        new BlackjackLogic(), new HandLog(HandLog.FILE_NAME), new TableController.Listener()
        {
            @Override
            public void tableClosed(TableController table)
            {
                closeTable(table);
            }
        });
        addScreen(SCREEN_TABLE, controller.getScreen());
        frame.setTitle("Blackjack - room " + room.getCode() + " - you are " + me);
        showScreen(SCREEN_TABLE);
        controller.start(room);
    }
        // The player left the table: remove it and show the menu again.
    private void closeTable(TableController table)
    {
        screens.remove(table.getScreen());
        frame.setTitle("Blackjack");
        showScreen(SCREEN_MENU);
    }


    // without settings the game can't work, so we tell why and close
    private void showErrorAndExit(String message)
    {
        JOptionPane.showMessageDialog(frame, message, "Blackjack", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public static void main(String[] args)
    {
        // Swing windows must be created on the Swing thread
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                App app = new App();
                app.getFrame().setVisible(true);
                app.start();
            }
        });
    }
}