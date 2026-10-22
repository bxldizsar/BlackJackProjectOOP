package blackjack.model;

/**
 *
 * @author boldi
 */
// One row of the Supabase games table (one row = one room).
// Cards are kept as text like in the table, for example "A-C,10-H".
public class GameState
{
    private String code;
    private GameStatus status;
    // null = nobody is playing yet (waiting room)
    private Turn turn;
    private String deck;
    private String dealerHand;
    private String p1Hand;
    private String p2Hand;
    private int round;
    // goes up by one on every save, so we notice an old save
    private int version;
    // Supabase ids of the players, p2Id is null until someone joins
    private String p1Id;
    private String p2Id;

    // a new empty room: waiting, round 1, no cards yet
    public GameState(String code)
    {
        this.code = notNull(code, "code");
        this.status = GameStatus.WAITING;
        this.turn = null;
        this.deck = "";
        this.dealerHand = "";
        this.p1Hand = "";
        this.p2Hand = "";
        this.round = 1;
        this.version = 0;
    }

    // makes an exact copy, so the logic can change the copy
    // and the original stays the same if saving fails.
    public GameState copy()
    {
        GameState c = new GameState(code);
        c.status = status;
        c.turn = turn;
        c.deck = deck;
        c.dealerHand = dealerHand;
        c.p1Hand = p1Hand;
        c.p2Hand = p2Hand;
        c.round = round;
        c.version = version;
        c.p1Id = p1Id;
        c.p2Id = p2Id;
        return c;
    }

    public String getPlayerHand(Turn player)
    {
        if (player == Turn.P1)
        {
            return p1Hand;
        }
        if (player == Turn.P2)
        {
            return p2Hand;
        }
        throw new IllegalArgumentException("Not a player: " + player);
    }

    public void setPlayerHand(Turn player, String hand)
    {
        if (player == Turn.P1)
        {
            setP1Hand(hand);
        }
        else if (player == Turn.P2)
        {
            setP2Hand(hand);
        }
        else
        {
            throw new IllegalArgumentException("Not a player: " + player);
        }
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = notNull(code, "code");
    }

    public GameStatus getStatus()
    {
        return status;
    }

    public void setStatus(GameStatus status)
    {
        if (status == null)
        {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
    }

    public Turn getTurn()
    {
        return turn;
    }

    // null is allowed here: nobody is playing yet
    public void setTurn(Turn turn)
    {
        this.turn = turn;
    }

    public String getDeck()
    {
        return deck;
    }

    public void setDeck(String deck)
    {
        this.deck = notNull(deck, "deck");
    }

    public String getDealerHand()
    {
        return dealerHand;
    }

    public void setDealerHand(String dealerHand)
    {
        this.dealerHand = notNull(dealerHand, "dealerHand");
    }

    public String getP1Hand()
    {
        return p1Hand;
    }

    public void setP1Hand(String p1Hand)
    {
        this.p1Hand = notNull(p1Hand, "p1Hand");
    }

    public String getP2Hand()
    {
        return p2Hand;
    }

    public void setP2Hand(String p2Hand)
    {
        this.p2Hand = notNull(p2Hand, "p2Hand");
    }

    public int getRound()
    {
        return round;
    }

    public void setRound(int round)
    {
        this.round = round;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getP1Id()
    {
        return p1Id;
    }

    public void setP1Id(String p1Id)
    {
        this.p1Id = p1Id;
    }

    public String getP2Id()
    {
        return p2Id;
    }

    // null is allowed here: nobody has joined yet
    public void setP2Id(String p2Id)
    {
        this.p2Id = p2Id;
    }

    // An empty hand is "", never null, so the other classes don't have to check for null.
    private static String notNull(String value, String name)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(name + " must not be null, use \"\" instead");
        }
        return value;
    }
}