package blackjack.model;

/**
 *
 * @author boldi
 */
// Whose turn it is. dbValue is the text we store in the turn column.
// A null Turn means nobody is playing yet (waiting room).
public enum Turn
{
    P1("p1"),
    P2("p2"),
    DEALER("dealer");

    private final String dbValue;

    Turn(String dbValue)
    {
        this.dbValue = dbValue;
    }

    public String getDbValue()
    {
        return dbValue;
    }

    // "p1" -> P1. null stays null, an unknown text is an error.
    public static Turn fromDbValue(String value)
    {
        if (value == null)
        {
            return null;
        }
        for (Turn turn : values())
        {
            if (turn.dbValue.equals(value))
            {
                return turn;
            }
        }
        throw new IllegalArgumentException("Unknown turn: " + value);
    }
}