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
import blackjack.model.Card;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Hand;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import blackjack.logic.BlackjackLogic;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.ImageIcon;
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
    
    private final BlackjackLogic logic = new BlackjackLogic();
    
    private String baseStatus = "";   // the last status text without the countdown
    
    private GameState state;
    
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
        statusLabel.setForeground(Color.WHITE);
        buttonRow.setOpaque(false);
        buttonRow.add(hitButton);
        buttonRow.add(stayButton);
        buttonRow.add(newRoundButton);
        
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttonRow, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
        
        hitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actions.submitMove(me, MoveType.HIT);
            }
        });
        stayButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actions.submitMove(me, MoveType.STAY);
            }
        });
        newRoundButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actions.requestNewRound();
            }
        });
    }
    
    public void showState(GameState state) 
    {
        this.state = state;
        showHand(dealerPanel, state.getDealerHand(), state.getStatus() != GameStatus.FINISHED);
        showHand(p1Panel, state.getP1Hand(), false);
        showHand(p2Panel, state.getP2Hand(), false);
        
        boolean myTurn = state.getStatus() == GameStatus.PLAYING 
                && state.getTurn() == me;
        hitButton.setEnabled(myTurn);
        stayButton.setEnabled(myTurn);
        newRoundButton.setEnabled(state.getStatus() == GameStatus.FINISHED);
        baseStatus = statusText(state);
        statusLabel.setText(baseStatus);
    }
    
    private void showHand(JPanel panel, String handText, boolean hideFirst)
    {
        panel.removeAll();                          // clear the old cards
        Hand hand = Hand.fromString(handText);
        List<Card> cards = hand.getCards();
        for(int i = 0; i < cards.size(); i++)
        {
            ImageIcon icon;
            if(hideFirst && i == 0)
            {
                icon = images.getBack();            // face-down card
            }
            else
            {
                icon = images.getImage(cards.get(i));
            }
            JLabel label;
            if(icon == null)
            {
                if(hideFirst && i == 0)
                {
                    label = new JLabel("?");     // hidden card, never show its real text
                }
                else
                {
                    label = new JLabel(cards.get(i).toString());  // image missing: show text
                }
            }
            else
            {
                label = new JLabel(icon);
            }
            panel.add(label);
        }
        panel.revalidate();                         // recalculate the layout
        panel.repaint();                            // redraw
    }
    
    private String statusText(GameState state)
    {
        if(state.getStatus() == GameStatus.WAITING)
        {
            return "Waiting for the other player to join...";
        }
        else if(state.getStatus() == GameStatus.TIMEOUT)
        {
            return "Game over: the other player did not move";
        }
        else if(state.getStatus() == GameStatus.FINISHED)
        {
            return "Round over - P1: " + logic.outcomeFor(state, Turn.P1)
                    + ", P2: " + logic.outcomeFor(state, Turn.P2);
        }
        else if(state.getStatus() == GameStatus.PLAYING && state.getTurn() == me)
        {
            return "Your turn";
        }
        else if(state.getStatus() == GameStatus.PLAYING && state.getTurn() == Turn.DEALER)
        {
            return "The dealer is drawing...";
        }
        else if(state.getStatus() == GameStatus.PLAYING)
        {
            return "Waiting for the other player...";
        }
        else
        {
            return "";
        }
    }
    
    public void onCountdown(int secondsLeft)
    {
        if(state != null && state.getStatus() == GameStatus.PLAYING)
        {
            statusLabel.setText(baseStatus + " " + secondsLeft + " s");
        }
    }
    
    public void onConnectionProblem(String message)
    {
        statusLabel.setText("Connection problem: " + message);
    }
}
