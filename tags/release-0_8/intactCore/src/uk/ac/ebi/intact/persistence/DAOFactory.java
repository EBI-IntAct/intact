/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

/**
 * <p>This class is a factory for providing data sources from which
 * a data source connection can be obtained.
 * The Factory Method pattern is used here, as for the present the
 * number of possible different data source types is expected to be small
 * (for example a Castor data source, an XML data source and oracle source etc) </p>
 *
 *
 * @author Chris Lewington
 */

public class DAOFactory {

    /**
    * This method returns a specific data source instance
     *
     * @param sourceType - The type of data source to be created
     *
     * @return The generated Data Source
     *
     * @exception DataSourceException - thrown if a data source
     * instance cannot be created, for example due to missing/invalid config files
     *
     */
    public static DAOSource getDAOSource(String sourceType) throws DataSourceException {

        try {

            return (DAOSource)Class.forName(sourceType).newInstance();

        }
        catch(Exception e) {

            String msg = "unable to create a data source - possible unkown type?" ;
            throw new DataSourceException(msg, e);

        }
    }

}
