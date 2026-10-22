package blackjack.model;

/**
 *
 * @author boldi
 */
// The state of a room. dbValue is the text we store in the status column.
public enum GameStatus
{
    WAITING("waiting"),
    PLAYING("playing"),
    FINISHED("finished"),
    TIMEOUT("timeout");

    private final String dbValue;

    GameStatus(String dbValue)
    {
        this.dbValue = dbValue;
    }

    public String getDbValue()
    {
        return dbValue;
    }

    // "waiting" -> WAITING. An unknown text (or null) is an error,
    // because every room must have a status.
    public static GameStatus fromDbValue(String value)
    {
        for (GameStatus status : values())
        {
            if (status.dbValue.equals(value))
            {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}