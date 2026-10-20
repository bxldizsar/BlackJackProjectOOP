package blackjack.net;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

// Holds the Supabase settings from config.properties.
// Only this class knows the file name and the keys inside it.
public class AppConfig 
{

    public static final String FILE_NAME = "config.properties";

    private final String supabaseUrl;
    private final String anonKey;

    public AppConfig(String supabaseUrl, String anonKey) 
    {
        this.supabaseUrl = removeEndSlash(clean(supabaseUrl));
        this.anonKey = clean(anonKey);
        if (!this.supabaseUrl.startsWith("https://")) 
        {
            throw new IllegalArgumentException("supabase.url must start with https://");
        }
        if (this.anonKey.isEmpty()) 
        {
            throw new IllegalArgumentException("supabase.anonKey is empty");
        }
        // the secret key skips every rule in the database, it must never be in the game
        if (this.anonKey.startsWith("sb_secret_")) 
        {
            throw new IllegalArgumentException("Use the publishable key, never the secret key");
        }
    }

    // Reads the settings from a .properties file.
    public static AppConfig load(String fileName) throws IOException 
    {
        FileReader reader;
        try 
        {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) 
        {
            throw new IOException(fileName + " not found. Copy config.example.properties, "
                    + "rename it to config.properties and fill it in (see README).");
        }
        Properties settings = new Properties();
        try 
        {
            settings.load(reader);
        } finally {
            reader.close();
        }
        return new AppConfig(settings.getProperty("supabase.url"), settings.getProperty("supabase.anonKey"));
    }

    public String getSupabaseUrl() 
    {
        return supabaseUrl;
    }

    public String getAnonKey() 
    {
        return anonKey;
    }

    // null becomes "" and spaces at the ends are removed
    private static String clean(String value) 
    {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    // "https://x.supabase.co/" -> "https://x.supabase.co", so we don't get "//" later
    private static String removeEndSlash(String url) 
    {
        if (url.endsWith("/")) 
        {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
