package blackjack.model;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
public class RoomCodeTest
{
    @Test
    public void goodCodes()
    {
        assertTrue(RoomCode.isValid("AB12"));
        assertTrue(RoomCode.isValid("1001"));
    }

    @Test
    public void badCodes()
    {
        assertFalse(RoomCode.isValid(null));
        assertFalse(RoomCode.isValid("ab12"));
        assertFalse(RoomCode.isValid("AB1"));
        assertFalse(RoomCode.isValid("AB&1"));
    }

    @Test
    public void typedTextIsCleaned()
    {
        assertEquals("AB12", RoomCode.clean("  ab12 "));
        assertEquals("", RoomCode.clean(null));
    }

    @Test
    public void randomCodesAreValid()
    {
        Random random = new Random();
        for (int i = 0; i < 100; i++)
        {
            assertTrue(RoomCode.isValid(RoomCode.random(random)));
        }
    }
}