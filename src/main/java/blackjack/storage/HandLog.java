package blackjack.storage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author boldi
 */

//writes one line into hand.csv after every finished round 
//it only writes the file, it does not know the rules 
public class HandLog 
{
    public static final String FILE_NAME = "hands.csv";
    public static final String HEADER = "kor;sajat_kez;ellenfel_keze;dealer_keze;eredmeny";
    private static final String SEPARATOR = ";";
    
    private final File file;
    
    public HandLog(String fileName)
    {
        this.file = new File(fileName); 
    }
    
    //adds one round to the end of the file, the header is written when the file is new
    
    public void logRound(int round, String ownHand, String opponentHand, String dealerHand, String result) throws IOException   
    {
        boolean newFile = !file.exists() || file.length() == 0;
        //true = append to the end, do not delete the old lines 
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try
        {
            if (newFile)
            {
                writer.write(HEADER);
                writer.newLine();
            }
            writer.write(round + SEPARATOR + ownHand + SEPARATOR + opponentHand + SEPARATOR + dealerHand + SEPARATOR + result);
            writer.newLine(); 
        }
        finally
        {
            writer.close();
        }
    }
}
