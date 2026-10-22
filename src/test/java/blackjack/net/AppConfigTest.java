package blackjack.net;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

// Tests for reading and checking the Supabase settings.
public class AppConfigTest 
{

    @Test
    public void goodSettingsAreKeptAndCleaned() {
        AppConfig config = new AppConfig(" https://abc.supabase.co/ ", " sb_publishable_123 ");
        assertEquals("https://abc.supabase.co", config.getSupabaseUrl());
        assertEquals("sb_publishable_123", config.getAnonKey());
    }

    @Test
    public void urlMustStartWithHttps() 
    {
        try 
        {
            new AppConfig("abc.supabase.co", "sb_publishable_123");
            fail("a url without https:// should not be accepted");
        } catch (IllegalArgumentException e) 
        {
            // this is what we want
        }
    }

    @Test
    public void emptyKeyIsRejected() 
    {
        try 
        {
            new AppConfig("https://abc.supabase.co", "");
            fail("an empty key should not be accepted");
        } catch (IllegalArgumentException e) 
        {
            // this is what we want
        }
    }

    @Test
    public void secretKeyIsRejected() 
    {
        try 
        {
            new AppConfig("https://abc.supabase.co", "sb_secret_123");
            fail("the secret key should never be accepted");
        } catch (IllegalArgumentException e) 
        {
            // this is what we want
        }
    }

    @Test
    public void missingFileGivesAClearMessage() 
    {
        try 
        {
            AppConfig.load("no-such-file.properties");
            fail("a missing file should throw an IOException");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("config.example.properties"));
        }
    }
}