/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.logic;

import blackjack.model.Deck;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Hand;
import blackjack.model.Turn;
/**
 *
 * @author ministar
 */

// Pure game rules: methods never change the given state, they return a new one
public class BlackjackLogic {
    
    // Starts a new round: new shuffled deck, 2 cards for everyone, P1 is next
    public GameState dealNewRound(GameState s)
    {
        GameState next = s.copy();
        Deck deck = Deck.createShuffled();
        Hand dealer = new Hand();
        Hand p1 = new Hand();
        Hand p2 = new Hand();
        for(int i = 0; i < 2; i++)
        {
            dealer.addCard(deck.draw());
            p1.addCard(deck.draw());
            p2.addCard(deck.draw());
        }
        next.setDeck(deck.toString());
        next.setDealerHand(dealer.toString());
        next.setPlayerHand(Turn.P1, p1.toString());
        next.setPlayerHand(Turn.P2, p2.toString());
        next.setTurn(Turn.P1);
        next.setStatus(GameStatus.PLAYING);
        return next;
    }
}
