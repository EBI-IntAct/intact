package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.model.IntactObject;

import java.util.Collection;
import uk.ac.ebi.intact.application.graph2MIF.exception.ElementNotParseableException;


/**
 * Interface defining the operations required to generate flat files.
 * Implementing classes thus generate flat files of differing formats
 * depending upon their implementation details.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public interface DataBuilder {

   //TBD...

    /**
     * Builds some file data for a specific Intact object. Most applicable
     * for XML style formats.
     * @param experiments for which file data is required
     */
    public void processExperiments(Collection experiments) throws ElementNotParseableException;

    /**
     * This method dumps the data created to the specified file destination.
     * @param fileName The name of the file to write to.
     * @exception DataConversionException thrown if there was a problem generating
     * the file.
     */
    public void writeData(String fileName) throws DataConversionException;
}
