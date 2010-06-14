package uk.ac.ebi.intact.protein.mapping.curation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class InputFileUtils {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( InputFileUtils.class );

    public static boolean checkInputProteins(InputStream proteinsToIdentify){
        if (proteinsToIdentify == null){
            log.error("The inputStream is null and we need a file containing information about the proteins to retrieve the uniprot ID.");
            return false;
        }
        else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(proteinsToIdentify));
            try {
                // The first line should contain the titles of the columns
                String line = reader.readLine();

                if (line == null){
                    log.error("The file is empty and we can't retrieve any uniprot IDs.");
                    reader.close();
                    return false;
                }
                else{
                    if (reader.readLine() == null){
                       log.error("Apart from the titles, the file is empty and we can't retrieve any uniprot IDs.");
                        reader.close();
                       return false;
                    }
                }
                reader.close();
                return true;
            } catch (IOException e) {
                throw new InputFileException("We can't read the inputStream containing the information about the protein to identify",e);
            }
        }
    }

    public static ArrayList<String> split(String line, String charact){
        ArrayList<String> columns = new ArrayList<String> ();
        line = line.replace("\n","");

        int index = line.indexOf(charact);

        if (index == -1){
            columns.add(line);
            return columns;
        }

        while (index != -1){
            columns.add(line.substring(index, charact.length()));

            line = line.substring(index + charact.length());
            index = line.indexOf(charact);
        }
        return columns;
    }
}
