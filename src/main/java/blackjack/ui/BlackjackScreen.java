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
import blackjack.logic.Outcome;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
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
    private static final int MAX_CARDS_PER_ROW = 4;
    private static final Font FONT = new Font("Arial", Font.BOLD, 13);
    
    public interface Actions
    {
        void submitMove(Turn me, MoveType move);
        void requestNewRound();
        // the "Back to menu" / "Cancel" button
        void backToMenu();
    }
    
    private final Turn me;
    private final Actions actions;
    
    private final CardImages images = new CardImages();
    
    private final JPanel dealerPanel = new JPanel(new BorderLayout());
    private final JPanel p1Panel = new JPanel(new BorderLayout());
    private final JPanel p2Panel = new JPanel(new BorderLayout());
    
    private final JButton hitButton = new JButton("HIT");
    private final JButton stayButton = new JButton("STAY");
    private final JButton newRoundButton = new JButton("NEW ROUND");
    private final JButton menuButton = new JButton("BACK TO MENU");
    
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
        if(actions == null)
        {
            throw new IllegalArgumentException("Actions must not be null");
        }
        this.me = me;
        this.actions = actions;
        TableBackground table = new TableBackground();
        table.setLayout(new BorderLayout());
        table.add(buildCenter(), BorderLayout.CENTER);
        table.add(buildSouth(), BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        addListeners();
    }
    
    // Dealer on top, P1 and P2 side by side below, each with a title
    private JPanel buildCenter()
    {
        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomRow.setOpaque(false);
        dealerPanel.setOpaque(false);
        p1Panel.setOpaque(false);
        p2Panel.setOpaque(false);
        center.add(makeArea("Dealer", dealerPanel));
        bottomRow.add(makeArea(titleFor(Turn.P1), p1Panel));
        bottomRow.add(makeArea(titleFor(Turn.P2), p2Panel));
        center.add(bottomRow);
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
        buttonRow.add(menuButton);
        
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttonRow, BorderLayout.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));   // space under the text
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
        menuButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                actions.backToMenu();
            }
        });
    }
    
    public void showState(GameState state) 
    {
        this.state = state;
        
        GameStatus status = state.getStatus();
        if(status == GameStatus.WAITING)
        {
            menuButton.setText("CANCEL");
        }
        else
        {
            menuButton.setText("BACK TO MENU");
        }
        
        showHand(dealerPanel, state.getDealerHand(), state.getStatus() == GameStatus.PLAYING);
        showHand(p1Panel, state.getP1Hand(), false);
        showHand(p2Panel, state.getP2Hand(), false);
        
        boolean myTurn = state.getStatus() == GameStatus.PLAYING 
                && state.getTurn() == me;
        hitButton.setEnabled(myTurn);
        stayButton.setEnabled(myTurn);
        newRoundButton.setEnabled(state.getStatus() == GameStatus.FINISHED);
        menuButton.setEnabled(status != GameStatus.PLAYING);
        baseStatus = statusText(state);
        statusLabel.setText(baseStatus);
    }
    
    private void showHand(JPanel panel, String handText, boolean hideFirst)
    {
        panel.removeAll();                          // clear the old cards
        Hand hand = Hand.fromString(handText);
        JPanel rows = new JPanel(new GridLayout(0, 1));   // the rows are packed tightly together
        rows.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());   // the card rows with the score right under them
        top.setOpaque(false);
        top.add(rows, BorderLayout.CENTER);
        top.add(createScoreLabel(hand, hideFirst), BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        List<Card> cards = hand.getCards();
        JPanel row = null;
        for(int i = 0; i < cards.size(); i++)
        {
            if(i % MAX_CARDS_PER_ROW == 0)
            {
                row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
                row.setOpaque(false);
                rows.add(row);
            }
            row.add(createCardLabel(cards.get(i), hideFirst && i == 0));
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
    
    // Score text under a hand; empty while the dealer's first card is hidden
    private JLabel createScoreLabel(Hand hand, boolean hidden)
    {
        if(hidden || hand.getCards().isEmpty())
        {
            return new JLabel(" ");
        }
        String text = "Score: " + hand.getValue();
        if(hand.isBust())
        {
            text += " - BUST";
        }
        else if(hand.isBlackjack())
        {
            text += " - BLACKJACK";
        }
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(FONT);
        return label;
    }
    
    private String statusText(GameState state)
    {
        if(state.getStatus() == GameStatus.WAITING)
        {
            return "Waiting for the other player to join...";
        }
        else if(state.getStatus() == GameStatus.TIMEOUT)
        {
            if(state.getP2Id() == null){ return "Game over: nobody joined the room in time"; }
            Turn late = state.getTurn();
            if(late == Turn.DEALER)
            {
                late = Turn.P2;   // the machine of P2 plays the dealer
            }
            if(late == me)
            {
                return "Game over: you did not move in time";
            }
            if(late != null)
            {
                return "Game over: " + titleFor(late) + " did not move in time";
            }
            return "Game over: the other player did not move in time";   // fallback
        }
        else if(state.getStatus() == GameStatus.FINISHED)
        {
            return resultText(state);
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
        // After a timeout the game is over: the game over text must stay on the screen
        if(state != null && state.getStatus() == GameStatus.TIMEOUT)
        {
            return;
        }
        statusLabel.setText("Connection problem: " + message);
    }
    
    private String titleFor(Turn player)
    {
        String title;
        if(player == Turn.P1)
        {
            title = "Player 1";
        }
        else
        {
            title = "Player 2";
        }
        if(player == me)
        {
            title += " (You)";
        }
        return title;
    }
    
    // A titled area: a white title above the cards of one player
    private JPanel makeArea(String title, JPanel cardsPanel)
    {
        JPanel area = new JPanel(new BorderLayout());
        area.setOpaque(false);
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        
        label.setFont(FONT);
        area.add(label, BorderLayout.NORTH);
        area.add(cardsPanel, BorderLayout.CENTER);
        return area;
    }
    
    // Display text for a result, from the point of view of the player who sees it
    private String outcomeText(Outcome outcome)
    {
        if(outcome == Outcome.WIN)
        {
            return "You win!";
        }
        if(outcome == Outcome.LOSE)
        {
            return "You lose";
        }
        return "It is a push";
    }
    
    // The other player of the table
    private Turn opponentOf(Turn player)
    {
        if(player == Turn.P1)
        {
            return Turn.P2;
        }
        return Turn.P1;
    }
    
    // Result of ANOTHER player in words, e.g. "Player 2 loses"
    private String otherOutcomeText(Turn player, Outcome outcome)
    {
        String name = titleFor(player);
        if(outcome == Outcome.WIN)
        {
            return name + " wins";
        }
        if(outcome == Outcome.LOSE)
        {
            return name + " loses";
        }
        return name + " pushes";
    }
    
    // Text at the end of a round, from the point of view of this player
    private String resultText(GameState state)
    {
        Turn other = opponentOf(me);
        Outcome mine = logic.outcomeFor(state, me);
        Outcome theirs = logic.outcomeFor(state, other);
        if(mine == theirs)
        {
            return "Round over - " + bothText(mine);
        }
        return "Round over - " + outcomeText(mine) + ", " + otherOutcomeText(other, theirs);
    }
    
    // One sentence when both players have the same result
    private String bothText(Outcome outcome)
    {
        if(outcome == Outcome.WIN)
        {
            return "both players win";
        }
        if(outcome == outcome.LOSE)
        {
            return "both players lose";
        }
        return "both players draw";
    }
}
