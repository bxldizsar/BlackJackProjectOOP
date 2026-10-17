/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
/**
 *
 * @author boldi
 */

//starts the program. one 600x600 window,the screens are switched inside it

public class App
{ 
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 600;
    //green table color, every screen uses this
    public static final Color TABLE_GREEN = new Color(14,92,52);
    
    //screen names for showScreen()
    public static final String SCREEN_MENU  = "menu";
    public static final String SCREEN_WAITING = "waiting";
    public static final String SCREEN_TABLE = "table";
    
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel screens;
    
    public App()
    {
        frame = new JFrame("Blackjack");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // center of the screen
        
        cardLayout = new CardLayout();
        screens = new JPanel(cardLayout);
           screens.setBackground(TABLE_GREEN);
        frame.add(screens, BorderLayout.CENTER);

        // temporary start screen until MenuPanel is ready
        addScreen(SCREEN_MENU, createPlaceholder("Blackjack - menu coming soon"));
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

    public JFrame getFrame() {
        return frame;
    }

    // simple green panel with a white text in the middle
    private JPanel createPlaceholder(String text) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TABLE_GREEN);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
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
                app.showScreen(SCREEN_MENU);
                app.getFrame().setVisible(true);
            }
        });
    }
}
