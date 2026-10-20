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
    
    // The dealer draws while the hand value is below this
    private static final int DEALER_STAND_VALUE = 17;
    
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
    
    // Player draws one card; if the hand busts, the turn goes to the next one
    public GameState hit(GameState s, Turn player)
    {
        requireTurn(s, player);
        GameState next = s.copy();
        Deck deck = Deck.fromString(next.getDeck());
        Hand hand = Hand.fromString(next.getPlayerHand(player));
        hand.addCard(deck.draw());
        next.setDeck(deck.toString());
        next.setPlayerHand(player, hand.toString());
        if(hand.isBust())
        { 
            next.setTurn(nextTurn(player)); 
        }
        return next;
    }
    
    // Player stays: the turn goes to the next one
    public GameState stay(GameState s, Turn player)
    {
        requireTurn(s, player);
        GameState next = s.copy();
        next.setTurn(nextTurn(player));
        return next;
    }
    
    // Who comes after the given player: P1 -> P2, P2 -> DEALER
    private Turn nextTurn(Turn player)
    {
        if(player == Turn.P1) return Turn.P2;
        else return Turn.DEALER;
    }
    
    // Throws if it is not the given player's turn
    private void requireTurn(GameState s, Turn player)
    {
        if(s.getTurn() != player)
        {
            throw new IllegalStateException("It is not this player's turn");
        }
    }
    
    // Dealer draws while below DEALER_STAND_VALUE, then the round is finished
    public GameState playDealer(GameState s)
    {
        if(s.getTurn() != Turn.DEALER)
        {
            throw new IllegalStateException("It is not the dealer's turn");
        }
        GameState next = s.copy();
        Deck deck = Deck.fromString(next.getDeck());
        Hand dealer = Hand.fromString(next.getDealerHand());
        while(dealer.getValue() < DEALER_STAND_VALUE)
        {
            dealer.addCard(deck.draw());
        }
        next.setDeck(deck.toString());
        next.setDealerHand(dealer.toString());
        next.setStatus(GameStatus.FINISHED);
        return next;
    }
    
    // Result of one player against the dealer
    public Outcome outcomeFor(GameState s, Turn player)
    {
        Hand playerHand = Hand.fromString(s.getPlayerHand(player));
        Hand dealerHand = Hand.fromString(s.getDealerHand());
        if(playerHand.isBust()) return Outcome.LOSE;
        if(dealerHand.isBust()) return Outcome.WIN;
        if(playerHand.getValue() > dealerHand.getValue()) return Outcome.WIN;
        else if(playerHand.getValue() < dealerHand.getValue()) return Outcome.LOSE;
        else return Outcome.PUSH;
    }
}
