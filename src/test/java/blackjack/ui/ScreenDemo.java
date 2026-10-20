/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.ui;

import blackjack.model.GameState;
import blackjack.logic.BlackjackLogic;
import blackjack.model.MoveType;
import blackjack.model.Turn;
import javax.swing.JFrame;
/**
 *
 * @author ministar
 */

// Manual demo: shows the table with one dealt round (not an automatic test)
public class ScreenDemo {
    
    public static void main(String[] args)
    {
        final BlackjackLogic logic = new BlackjackLogic();
        GameState state = new GameState("TEST");
        state = logic.dealNewRound(state);
        
        BlackjackScreen screen = new BlackjackScreen(Turn.P1, new BlackjackScreen.Actions(){
            @Override
            public void submitMove(Turn me, MoveType move)
            {
                System.out.println("Move: " + me + " " + move);
            }
            @Override
            public void requestNewRound()
            {
                System.out.println("New round requested");
            }
        });
        
        state = logic.stay(state, Turn.P1);
        state = logic.stay(state, Turn.P2);
        state =logic.playDealer(state);
        
        screen.showState(state);
        
        JFrame frame = new JFrame("Blackjack demo");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(screen);
        frame.setVisible(true);
    }
}
