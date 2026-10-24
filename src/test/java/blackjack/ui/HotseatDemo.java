/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;

import blackjack.logic.BlackjackLogic;
import blackjack.model.GameState;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import java.awt.GridLayout;
import javax.swing.JFrame;
/*
 *
 * @author ministar
 */

// Manual demo: two tables in one window, one shared game state (not an automatic test)
public class HotseatDemo implements BlackjackScreen.Actions{
    
    private BlackjackLogic logic = new BlackjackLogic();
    private GameState state;
    private BlackjackScreen p1Screen;
    private BlackjackScreen p2Screen;
    
    public HotseatDemo()
    {
        state = logic.dealNewRound(new GameState("TEST"));
        p1Screen = new BlackjackScreen(Turn.P1, this);
        p2Screen = new BlackjackScreen(Turn.P2, this);
        refresh();
        JFrame frame = new JFrame("Hotseat demo");
        frame.setLayout(new GridLayout(1, 2));
        frame.add(p1Screen);
        frame.add(p2Screen);
        frame.setSize(1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    @Override
    public void submitMove(Turn me, MoveType move)
    {
        if(move == MoveType.HIT)
        {
            state = logic.hit(state, me);
        }
        else
        {
            state = logic.stay(state, me);
        }
        if(state.getTurn() == Turn.DEALER)
        {
            state = logic.playDealer(state);
        }
        refresh();
    }
    
    @Override
    public void requestNewRound()
    {
        state = logic.dealNewRound(state);
        refresh();
    }
    
    @Override
    public void backToMenu()
    {
        // nothing to do in the demo
    }
    
    private void refresh()
    {
        p1Screen.showState(state);
        p2Screen.showState(state);
    }
    
    public static void main(String[] args)
    {
        new HotseatDemo();
    }
}
