package blackjack.net;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
// These tests don't need internet, they only check the small helper methods.
public class SupabaseClientTest
{
    @Test
    public void okStatusCodesPass() throws IOException
    {
        SupabaseClient.checkStatus(200, "[]");
        SupabaseClient.checkStatus(201, "[]");
        SupabaseClient.checkStatus(204, "");
    }

    @Test
    public void wrongKeyGivesAClearMessage()
    {
        try
        {
            SupabaseClient.checkStatus(401, "{\"message\":\"Invalid API key\"}");
            fail("401 should throw");
        }
        catch (IOException e)
        {
            assertTrue(e.getMessage().contains("config.properties"));
        }
    }

    @Test
    public void serverErrorShowsTheStatus()
    {
        try
        {
            SupabaseClient.checkStatus(500, "oops");
            fail("500 should throw");
        }
        catch (IOException e)
        {
            assertTrue(e.getMessage().contains("500"));
        }
    }

    @Test
    public void goodRoomCodes()
    {
        assertTrue(SupabaseClient.isValidCode("AB12"));
        assertTrue(SupabaseClient.isValidCode("1001"));
    }

    @Test
    public void badRoomCodes()
    {
        assertFalse(SupabaseClient.isValidCode(null));
        assertFalse(SupabaseClient.isValidCode("ab12"));
        assertFalse(SupabaseClient.isValidCode("AB1"));
        assertFalse(SupabaseClient.isValidCode("AB123"));
        assertFalse(SupabaseClient.isValidCode("AB&1"));
    }
}