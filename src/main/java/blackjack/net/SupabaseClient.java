package blackjack.net;

import blackjack.model.GameState;
import blackjack.model.GameStatus;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author boldi
 */
// Saves the rooms in the Supabase games table over the internet.
// It only sends HTTP requests, the JSON part is done by GameStateJson.
public class SupabaseClient implements GameRepository
{
    private static final int TIMEOUT_MS = 5000;
    private static final String GAMES_PATH = "/rest/v1/games";
    private static final String SIGNUP_PATH = "/auth/v1/signup";
    private static final int MAX_CODE_TRIES = 5;
    private static final String CODE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final AppConfig config;
    private final HttpClient http;
    private final Random random = new Random();

    // we get these from Supabase when we sign in
    private String accessToken;
    private String userId;

    public SupabaseClient(AppConfig config)
    {
        this.config = config;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(TIMEOUT_MS))
                .build();
    }

    @Override
    public GameState createGame(GameState initial) throws IOException
    {
        signInIfNeeded();
        GameState state = initial.copy();
        state.setP1Id(userId);
        for (int i = 0; i < MAX_CODE_TRIES; i++)
        {
            HttpResponse<String> response = send("POST", GAMES_PATH,
                    GameStateJson.toJson(state).toString());
            // 409 means the code is already taken, so we try a new one
            if (response.statusCode() == 409)
            {
                state.setCode(newCode());
            }
            else
            {
                checkStatus(response.statusCode(), response.body());
                return readFirstRow(response.body());
            }
        }
        throw new IOException("Could not find a free room code, please try again.");
    }

    @Override
    public GameState joinGame(String code) throws IOException
    {
        GameState game = readGame(code);
        if (game == null || game.getStatus() != GameStatus.WAITING || game.getP2Id() != null)
        {
            return null;
        }
        GameState joined = game.copy();
        joined.setP2Id(userId);
        joined.setVersion(game.getVersion() + 1);
        // if someone else joined at the same time, the version check stops us
        if (!updateGame(joined, game.getVersion()))
        {
            return null;
        }
        return joined;
    }

    @Override
    public GameState readGame(String code) throws IOException
    {
        if (!isValidCode(code))
        {
            return null;
        }
        signInIfNeeded();
        HttpResponse<String> response = send("GET", GAMES_PATH + "?code=eq." + code, null);
        checkStatus(response.statusCode(), response.body());
        return readFirstRow(response.body());
    }

    @Override
    public boolean updateGame(GameState newState, int expectedVersion) throws IOException
    {
        if (!isValidCode(newState.getCode()))
        {
            return false;
        }
        signInIfNeeded();
        JSONObject row = GameStateJson.toJson(newState);
        row.put(GameStateJson.VERSION, expectedVersion + 1);
        // only the row with the old version is changed
        String path = GAMES_PATH + "?code=eq." + newState.getCode() + "&version=eq." + expectedVersion;
        HttpResponse<String> response = send("PATCH", path, row.toString());
        checkStatus(response.statusCode(), response.body());
        // no row in the answer = the version was old, nothing was saved
        return readFirstRow(response.body()) != null;
    }

    // Signs in as an anonymous user once, so the database knows who we are.
    private void signInIfNeeded() throws IOException
    {
        if (accessToken != null)
        {
            return;
        }
        HttpResponse<String> response = send("POST", SIGNUP_PATH, "{}");
        checkStatus(response.statusCode(), response.body());
        try
        {
            JSONObject answer = new JSONObject(response.body());
            accessToken = answer.getString("access_token");
            userId = answer.getJSONObject("user").getString("id");
        }
        catch (JSONException e)
        {
            throw new IOException("Bad answer from Supabase when signing in.");
        }
    }

    // Sends one request and waits for the answer (max 5 seconds).
    private HttpResponse<String> send(String method, String path, String body) throws IOException
    {
        HttpRequest.BodyPublisher content;
        if (body == null)
        {
            content = HttpRequest.BodyPublishers.noBody();
        }
        else
        {
            content = HttpRequest.BodyPublishers.ofString(body);
        }
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(config.getSupabaseUrl() + path))
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .header("apikey", config.getAnonKey())
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .method(method, content);
        if (accessToken != null)
        {
            request.header("Authorization", "Bearer " + accessToken);
        }
        try
        {
            return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
        }
        catch (InterruptedException e)
        {
            throw new IOException("The request was stopped.");
        }
        catch (IOException e)
        {
            //no internet,wrong url or no answer in 5 seconds
            throw new IOException("No connection to Supabase.Check your internet" + "and supabase.url in config.properties");
        }
    }

    // 200, 201 and 204 are fine, everything else becomes an IOException with a clear message.
    static void checkStatus(int status, String body) throws IOException
    {
        if (status == 200 || status == 201 || status == 204)
        {
            return;
        }
        if (status == 401 || status == 403)
        {
            throw new IOException("Supabase refused the request (" + status
                    + "). Check supabase.anonKey in config.properties.");
        }
        throw new IOException("Supabase error " + status + ": " + body);
    }

    // A broken answer would throw a RuntimeException, we turn it into an IOException
    // so the caller only has to catch one kind of error.
    private GameState readFirstRow(String body) throws IOException
    {
        try
        {
            return GameStateJson.fromResponse(body);
        }
        catch (JSONException e)
        {
            throw new IOException("Bad answer from Supabase.");
        }
        catch (IllegalArgumentException e)
        {
            throw new IOException("Bad answer from Supabase: " + e.getMessage());
        }
    }

    // The table only allows 4 capital letters or digits, like "AB12".
    // This also stops strange text from getting into the URL.
    static boolean isValidCode(String code)
    {
        return code != null && code.matches("[A-Z0-9]{4}");
    }

    // makes a random 4-character code, like "K7Q2"
    private String newCode()
    {
        String code = "";
        for (int i = 0; i < 4; i++)
        {
            code = code + CODE_LETTERS.charAt(random.nextInt(CODE_LETTERS.length()));
        }
        return code;
    }
}