/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;

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
/**
 *
 * @author ministar
 */

// Table screen: dealer, two players, buttons and a status line
public class BlackjackScreen extends JPanel{
    
    private static final Color TABLE_GREEN = new Color(20, 100, 0);

    public interface Actions
    {
        void submitMove(Turn me, MoveType move);
        void requestNewRound();
    }
    
    private final Turn me;
    private final Actions actions;
    
    private final CardImages images = new CardImages();
    
    private final JPanel dealerPanel = new JPanel();
    private final JPanel p1Panel = new JPanel();
    private final JPanel p2Panel = new JPanel();
    
    private final JButton hitButton = new JButton("HIT");
    private final JButton stayButton = new JButton("STAY");
    private final JButton newRoundButton = new JButton("NEW ROUND");
    
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    
    private final BlackjackLogic logic = new BlackjackLogic();
    
    private String baseStatus = "";   // the last status text without the countdown
    
    private GameState state;
    
    public BlackjackScreen(Turn me, Actions actions)
    {
        if(me != Turn.P1 && me != Turn.P2)
        {
            throw new IllegalArgumentException("The screen belongs to P1 or P2");
        }
        this.me = me;
        this.actions = actions;
        setLayout(new BorderLayout());
        setBackground(TABLE_GREEN);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildSouth(), BorderLayout.SOUTH);
        addListeners();
    }
    
    // The three hand panels (dealer, P1, P2) on the green table
    private JPanel buildCenter()
    {
        JPanel center = new JPanel(new GridLayout(3, 1));
        center.setOpaque(false);
        dealerPanel.setOpaque(false);
        p1Panel.setOpaque(false);
        p2Panel.setOpaque(false);
        center.add(dealerPanel);
        center.add(p1Panel);
        center.add(p2Panel);
        return center;
    }
    
    // Button row and status line
    private JPanel buildSouth()
    {
        // default layout is FlowLayout
        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.add(hitButton);
        buttonRow.add(stayButton);
        buttonRow.add(newRoundButton);
        
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttonRow, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        return south;
    }
    
    // Button click handlers
    private void addListeners()
    {
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
            panel.add(createCardLabel(cards.get(i), hideFirst && i == 0));
        }
        panel.revalidate();                         // recalculate the layout
        panel.repaint();                            // redraw
    }
    
    // One label for a card: face-down "?", the card image, or its text if the image is missing
    private JLabel createCardLabel(Card card, boolean faceDown)
    {
        if(faceDown)
        {
            ImageIcon image = images.getBack();
            if(image == null)
            {
                return new JLabel("?");
            }
            else
            {
                return new JLabel(image);
            }
        }
        ImageIcon icon = images.getImage(card);
        if(icon == null)
        {
            return new JLabel(card.toString());
        }
        return new JLabel(icon);
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
