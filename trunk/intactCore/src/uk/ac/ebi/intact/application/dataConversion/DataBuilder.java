package uk.ac.ebi.intact.application.dataConversion;

import uk.ac.ebi.intact.model.IntactObject;

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
     * @param obj The object for which file data is required
     */
    public void createData(IntactObject obj);

    /**
     * This method dumps the data created to the specified file destination.
     * @param fileName The name of the file to write to.
     * @exception DataConversionException thrown if there was a problem generating
     * the file.
     */
    public void writeData(String fileName) throws DataConversionException;
}
