package blackjack.ui;

import blackjack.logic.BlackjackLogic;
import blackjack.logic.Outcome;
import blackjack.model.GameState;
import blackjack.model.GameStatus;
import blackjack.model.Turn;
import blackjack.net.FakeGameRepository;
import blackjack.storage.HandLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
// Checks that every finished round goes into hands.csv exactly once.
public class TableControllerTest
{
    private File file;
    private TableController controller;

    @BeforeEach
    public void setUp() throws IOException
    {
        file = File.createTempFile("hands", ".csv");
        file.delete();
        controller = new TableController(Turn.P1, "AB12", new FakeGameRepository(),
                new BlackjackLogic(), new HandLog(file.getPath()));
    }

    @AfterEach
    public void tearDown()
    {
        controller.stop();
        file.delete();
    }

    @Test
    public void resultWords()
    {
        assertEquals("nyert", TableController.resultText(Outcome.WIN));
        assertEquals("vesztett", TableController.resultText(Outcome.LOSE));
        assertEquals("dontetlen", TableController.resultText(Outcome.PUSH));
    }

    @Test
    public void finishedRoundIsLoggedOnce() throws IOException
    {
        GameState round1 = finishedRound(1, 5);

        controller.stateChanged(round1);
        controller.stateChanged(round1);

        // header + one round
        assertEquals(2, countLines());
    }

    @Test
    public void everyRoundGetsALine() throws IOException
    {
        controller.stateChanged(finishedRound(1, 5));
        controller.stateChanged(finishedRound(2, 9));

        assertEquals(3, countLines());
    }

    // a finished room with some cards in it
    private GameState finishedRound(int round, int version)
    {
        GameState state = new GameState("AB12");
        state.setStatus(GameStatus.FINISHED);
        state.setTurn(Turn.DEALER);
        state.setP1Hand("10-H,9-C");
        state.setP2Hand("8-D,7-S");
        state.setDealerHand("K-S,7-H");
        state.setRound(round);
        state.setVersion(version);
        return state;
    }

    private int countLines() throws IOException
    {
        int lines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try
        {
            while (reader.readLine() != null)
            {
                lines++;
            }
        }
        finally
        {
            reader.close();
        }
        return lines;
    }
}