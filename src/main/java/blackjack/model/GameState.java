/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blackjack.model;

/**
 *
 * @author boldi
 */
// possible states of a game room. dbValue is the text stored in the database.
public class GameState
{
    private String code;
    private GameStatus status;
    private Turn turn;
    private String deck;
    private String dealerHand;
    private String p1Hand;
    private String p2Hand;
    private int round;
    private int version;
    private String p1Id;
    private String p2Id;
    
    // converts the text from the database back to the enum value.
    public GameState(String code)
    {
        this.code = code;
        this.status = GameStatus.WAITING;
        this.turn = null;
        this.deck = "";
        this.dealerHand = "";
        this.p1Hand = "";
        this.p2Hand = "";
        this.round = 1;
        this.version = 0;
    }
}
