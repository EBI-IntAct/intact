/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>HttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Originaly : Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 *         Modified by Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class IntactUser implements HttpSessionBindingListener {

    // LOGGER
    static Logger logger = Logger.getLogger("IntactUser");

    private IntactHelper intactHelper;

    public IntactHelper getIntactHelper() {
        return intactHelper;
    }


    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception IntactException thrown for any error in creating lists such
     *  as topics, database names etc.
     */
    public IntactUser (String mapping, String dsClass)
        throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put (Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig (fileMap);


        // build a helper for use throughout a session
        this.intactHelper = new IntactHelper (ds);
        logger.info("IntactHelper created.");
    }



    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Not doing anything.
     */
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * Will call this method when an object is unbound from a session.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        try {
            this.intactHelper.closeStore();
        }
        catch(IntactException ie) {
            //failed to close the store - not sure what to do here yet....
            logger.severe ("error when closing the IntactHelper store");
            logger.severe (ie.toString());
        }
    }

} // IntactUser
