package blackjack.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author boldi
 */
// Writes into a temporary file, so the real hands.csv is not touched.
public class HandLogTest
{
    private File file;

    @BeforeEach
    public void makeTempFile() throws IOException
    {
        file = File.createTempFile("hands", ".csv");
        file.delete();
    }

    @AfterEach
    public void deleteTempFile()
    {
        file.delete();
    }

    @Test
    public void newFileGetsTheHeaderAndTheRound() throws IOException
    {
        HandLog log = new HandLog(file.getPath());

        log.logRound(1, "A-C,10-H", "9-D,7-C", "K-S,8-H", "nyert");

        List<String> lines = readLines();
        assertEquals(2, lines.size());
        assertEquals(HandLog.HEADER, lines.get(0));
        assertEquals("1;A-C,10-H;9-D,7-C;K-S,8-H;nyert", lines.get(1));
    }

    @Test
    public void roundsAreAddedToTheEnd() throws IOException
    {
        HandLog log = new HandLog(file.getPath());

        log.logRound(1, "A-C,10-H", "9-D,7-C", "K-S,8-H", "nyert");
        log.logRound(2, "5-C,6-H", "10-D,K-C", "9-S,9-H", "vesztett");

        List<String> lines = readLines();
        assertEquals(3, lines.size());
        assertEquals("2;5-C,6-H;10-D,K-C;9-S,9-H;vesztett", lines.get(2));
    }

    @Test
    public void oldLinesAreKeptAfterRestart() throws IOException
    {
        new HandLog(file.getPath()).logRound(1, "A-C", "9-D", "K-S", "nyert");
        // a new HandLog is like starting the program again
        new HandLog(file.getPath()).logRound(2, "2-C", "3-D", "4-S", "vesztett");

        List<String> lines = readLines();
        assertEquals(3, lines.size());
        assertEquals(HandLog.HEADER, lines.get(0));
    }

    // reads the whole temporary file line by line
    private List<String> readLines() throws IOException
    {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try
        {
            String line = reader.readLine();
            while (line != null)
            {
                lines.add(line);
                line = reader.readLine();
            }
        }
        finally
        {
            reader.close();
        }
        return lines;
    }
}