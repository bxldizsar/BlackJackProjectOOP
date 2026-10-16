/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ministar
 */
public class Hand {
    
    private final List<Card> cards = new ArrayList<>();
    
    public void addCard(Card card)
    {
        cards.add(card);
    }
    
    public List<Card> getCards()
    {
        return new ArrayList<>(cards);
    }
    
    public int getValue()
    {
        int sum = 0;
        int aceCount = 0;
        for(Card card : cards)
        {
            sum += cardValue(card);
            if(card.getRank().equals("A"))
            {
                aceCount++;
            }
        }
        
        //If the value of the cards went above 21, count A as 1
        while(sum > 21 && aceCount > 0)
        {
            sum -= 10;
            aceCount--;
        }
        return sum;
    }
    
    public boolean isBust()
    {
        return getValue() > 21;
    }
    
    private int cardValue(Card card)
    {
        String rank = card.getRank();
        if(rank.equals("A"))
        {
            return 11;
        }
        else if(rank.equals("J") || rank.equals("Q") || rank.equals("K"))
        {
            return 10;
        }
            else
            {
                return Integer.parseInt(rank);
            }
    }
    
    // Text form for the database, e.g. "A-C,10-H"
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(Card card : cards)
        {
            if(builder.length() > 0)
            {
                builder.append(",");
            }
            builder.append(card.toString());
        }
        return builder.toString();
    }
    
    // Builds a hand from the database text; empty text gives an empty hand
    public static Hand fromString(String text)
    {
        Hand hand = new Hand();
        if(text == null || text.isEmpty())
        {
            return hand;
        }
        
        String[] parts = text.split(",");
        for(String part : parts)
        {
            hand.addCard(Card.fromString(part));
        }
        return hand;
    }
}
