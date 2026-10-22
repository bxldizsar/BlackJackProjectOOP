
package blackjack.net;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Turn;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author boldi
 */

// turns a GameState into a JSON row for Supabase and back.
// only this class knows the column names of the games table.
public class GameStateJson 
{
    public static final String CODE = "code";
    public static final String STATUS = "status";
    public static final String TURN = "turn";
    public static final String DECK = "deck";
    public static final String DEALER_HAND = "dealer_hand";
    public static final String P1_HAND = "p1_hand";
    public static final String P2_HAND = "p2_hand";
    public static final String ROUND = "round";
    public static final String VERSION = "version";
    public static final String P1_ID = "p1_id";
    public static final String P2_ID = "p2_id";
    
    // GameState -> {"code":"AB12","status":"waiting",...}
    
    public static JSONObject toJson(GameState state)
    {
        JSONObject row = new JSONObject();
        row.put(CODE, state.getCode());
        row.put(STATUS, state.getStatus().getDbValue());
        row.put(TURN, nullable(state.getTurn() == null ? null : state.getTurn().getDbValue()));
        row.put(DECK, state.getDeck());
        row.put(DEALER_HAND, state.getDealerHand());
        row.put(P1_HAND, state.getP1Hand());
        row.put(P2_HAND, state.getP2Hand());
        row.put(ROUND, state.getRound());
        row.put(VERSION, state.getVersion());
        row.put(P1_ID, nullable(state.getP1Id()));
        row.put(P2_ID, nullable(state.getP2Id()));
        return row;
    }
    // {"code":"AB12","status":"waiting",...} -> GameState
    public static GameState fromJson(JSONObject row) 
    {
        GameState state = new GameState(row.getString(CODE));
        state.setStatus(GameStatus.fromDbValue(row.getString(STATUS)));
        state.setTurn(Turn.fromDbValue(row.optString(TURN, null)));
        state.setDeck(row.getString(DECK));
        state.setDealerHand(row.getString(DEALER_HAND));
        state.setP1Hand(row.getString(P1_HAND));
        state.setP2Hand(row.getString(P2_HAND));
        state.setRound(row.getInt(ROUND));
        state.setVersion(row.getInt(VERSION));
        state.setP1Id(row.optString(P1_ID, null));
        state.setP2Id(row.optString(P2_ID, null));
        return state;
    }

    // supabase always answers with a list of rows: [] or [{...}].
    // returns the first row, or null if the list is empty.
    public static GameState fromResponse(String body) 
    {
        JSONArray rows = new JSONArray(body);
        if (rows.length() == 0) {
            return null;
        }
        return fromJson(rows.getJSONObject(0));
    }
    
    //json needs a jsnonobject.null instead of a java null
    private static Object nullable(String value) 
    {
        if (value == null) 
        {
            return JSONObject.NULL;
        }
        return value;
    }
            
}
