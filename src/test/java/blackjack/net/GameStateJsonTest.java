package blackjack.net;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Turn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;


/**
 *
 * @author boldi
 */
public class GameStateJsonTest 
{
     @Test
    public void usesTheColumnNamesOfTheTable() 
    {
        JSONObject row = GameStateJson.toJson(new GameState("AB12"));
        assertEquals("AB12", row.getString("code"));
        assertEquals("waiting", row.getString("status"));
        assertEquals("", row.getString("dealer_hand"));
        assertEquals(1, row.getInt("round"));
    }

    @Test
    public void emptyTurnAndPlayerTwoBecomeJsonNull() 
    {
        JSONObject row = GameStateJson.toJson(new GameState("AB12"));
        assertTrue(row.isNull("turn"));
        assertTrue(row.isNull("p2_id"));
    }

    @Test
    public void stateSurvivesTheRoundTrip() 
    {
        // fill every field, so we can check nothing is lost
        GameState original = new GameState("AB12");
        original.setStatus(GameStatus.PLAYING);
        original.setTurn(Turn.P2);
        original.setDeck("2-C,3-D");
        original.setDealerHand("K-S");
        original.setP1Hand("A-C,10-H");
        original.setP2Hand("9-D,7-C");
        original.setRound(3);
        original.setVersion(7);
        original.setP1Id("id-1");
        original.setP2Id("id-2");
        // to text and back, like it goes to Supabase and comes back
        String text = GameStateJson.toJson(original).toString();
        GameState back = GameStateJson.fromJson(new JSONObject(text));
        assertEquals(GameStatus.PLAYING, back.getStatus());
        assertEquals(Turn.P2, back.getTurn());
        assertEquals("2-C,3-D", back.getDeck());
        assertEquals("K-S", back.getDealerHand());
        assertEquals("A-C,10-H", back.getP1Hand());
        assertEquals("9-D,7-C", back.getP2Hand());
        assertEquals(3, back.getRound());
        assertEquals(7, back.getVersion());
        assertEquals("id-1", back.getP1Id());
        assertEquals("id-2", back.getP2Id());
    }

    @Test
    public void readsTheFirstRowOfAnAnswer() 
    {
        // an answer like the one Supabase sends
        String body = "[{\"code\":\"AB12\",\"status\":\"waiting\",\"turn\":null,\"deck\":\"\","
                + "\"dealer_hand\":\"\",\"p1_hand\":\"\",\"p2_hand\":\"\",\"round\":1,"
                + "\"version\":0,\"p1_id\":\"id-1\",\"p2_id\":null}]";
        GameState state = GameStateJson.fromResponse(body);
        assertEquals("AB12", state.getCode());
        assertNull(state.getTurn());
        assertNull(state.getP2Id());
    }

    @Test
    public void emptyAnswerMeansNoRoom() 
    {
        assertNull(GameStateJson.fromResponse("[]"));
    }

    @Test
    public void brokenAnswerThrows() 
    {
        try 
        {
            GameStateJson.fromResponse("this is not json");
            fail("broken json should throw");
        } catch (JSONException e) {
            // this is what we want
        }
    }
}
