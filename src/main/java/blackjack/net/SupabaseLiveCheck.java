package blackjack.net;

import blackjack.model.GameState;
import blackjack.model.GameStatus;
import java.io.IOException;

/**
 *
 * @author boldi
 */
// Not a unit test: run it by hand (Shift+F6) to see if our Supabase really works.
// It needs a filled config.properties and internet.
// Two clients play the two players, like two computers would.
public class SupabaseLiveCheck
{
    public static void main(String[] args)
    {
        try
        {
            AppConfig config = AppConfig.load(AppConfig.FILE_NAME);
            SupabaseClient player1 = new SupabaseClient(config);
            SupabaseClient player2 = new SupabaseClient(config);

            GameState room = player1.createGame(new GameState("TEST"));
            System.out.println("1. room created: " + room.getCode() + ", version " + room.getVersion());

            GameState joined = player2.joinGame(room.getCode());
            System.out.println("2. player 2 joined: " + (joined != null));

            GameState seen = player1.readGame(room.getCode());
            System.out.println("3. player 1 sees player 2: " + (seen.getP2Id() != null)
                    + ", version " + seen.getVersion());

            GameState next = seen.copy();
            next.setStatus(GameStatus.PLAYING);
            boolean saved = player1.updateGame(next, seen.getVersion());
            System.out.println("4. save with the right version: " + saved);

            boolean savedAgain = player2.updateGame(next, seen.getVersion());
            System.out.println("5. save with an old version is refused: " + !savedAgain);

            System.out.println("Everything works if all lines say true.");
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Bad setting in config.properties: " + e.getMessage());
        }
    }
}