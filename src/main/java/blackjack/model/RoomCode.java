package blackjack.model;

import java.util.Random;

/**
 *
 * @author boldi
 */
// The rules of the room code. The menu and SupabaseClient both use it, so they never disagree.

public class RoomCode
{
    // no O/0 and I/1, because people mix them up when they read the code
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LENGTH = 4;
    
    //4 capital letters or digits,like "AB12" (the same rule as in the database)
    public static boolean isValid(String code)
    {
        return code != null && code.matches("[A-Z0-9]{4}");
    }
    
    // " ab12 " -> "AB12", so the player can type small letters and spaces
    public static String clean(String text)
    {
        if (text == null)
        {
            return "";
        }
        return text.trim().toUpperCase(); 
    }
    // a random code like "K7Q2"
    public static String random(Random random)
    {
        String code = "";
        for (int i = 0; i < LENGTH; i++)
        {
            code = code + LETTERS.charAt(random.nextInt(LETTERS.length()));
        }
        return code;
    }
}