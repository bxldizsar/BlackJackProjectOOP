/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;


/**
 *
 * @author ministar
 */

// Table screen: dealer, two players, buttons and a status line
import blackjack.model.GameState;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class BlackjackScreen extends JPanel{
    
    public interface Actions
    {
        void submitMove(Turn me, MoveType move);
        void requestNewRound();
    }
    
    private final Turn me;
    private final Actions actions;
    private final CardImages images = new CardImages();
    private JPanel dealerPanel;
    private JPanel p1Panel;
    private JPanel p2Panel;
    private JButton hitButton;
    private JButton stayButton;
    private JButton newRoundButton;
    private JLabel statusLabel;
    
    public BlackjackScreen(Turn me, Actions actions)
    {
        this.me = me;
        this.actions = actions;
        setLayout(new BorderLayout());
        setBackground(new Color(0, 100, 0)); //green table
        
        JPanel center = new JPanel(new GridLayout(3, 1));
        center.setOpaque(false);
        dealerPanel = new JPanel();
        p1Panel = new JPanel();
        p2Panel = new JPanel();
        dealerPanel.setOpaque(false);
        p1Panel.setOpaque(false);
        p2Panel.setOpaque(false);
        center.add(dealerPanel);
        center.add(p1Panel);
        center.add(p2Panel);
        add(center, BorderLayout.CENTER);
        
        // default layout is FlowLayout
        JPanel buttonRow = new JPanel();
        hitButton = new JButton("HIT");
        stayButton = new JButton("STAY");
        newRoundButton = new JButton("NEW ROUND");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        buttonRow.setOpaque(false);
        buttonRow.add(hitButton);
        buttonRow.add(stayButton);
        buttonRow.add(newRoundButton);
        
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttonRow, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }
    
    // empty body for now, we fill it in later
    public void showState(GameState state) 
    {
        
    }
}
