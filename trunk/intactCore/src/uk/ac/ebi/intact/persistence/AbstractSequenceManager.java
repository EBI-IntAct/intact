/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerSQLException;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.util.logging.LoggerFactory;
import org.apache.ojb.broker.util.sequence.SequenceManager;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractSequenceManager implements SequenceManager {

    public static final String CONFIG_FILE = "/config/Institution.properties";

    /**
     * reference to the PersistenceBroker
     */
    protected PersistenceBroker broker;

    /**
     *   Prepared SQL statement to get a new id from a database sequence object.
     */
    protected static PreparedStatement _stmt;

    /**
     *  The prefix for all ID strings
     */
    protected static String _sitePrefix;

    /**
     *
     * Public constructor
     *
     */
    public AbstractSequenceManager( PersistenceBroker broker ) throws IntactException {
        this.broker = broker;

        Properties props = PropertyLoader.load ( CONFIG_FILE );
        if (props != null) {
            _sitePrefix = props.getProperty ( "ac.prefix" );
            // TODO is it a problem is the prefix is empty ?
        } else {
            throw new IntactException( "Could not find the configuration file: "+ CONFIG_FILE +"." );
        }
    }

    /**
     * Called by the vendor specific implementation to obtain the SQL
     * statement which allows to get the next unique ID.
     *
     * @return the SQL statement which allows to get the next unique ID
     */
    abstract protected String getNextSequence();

    /**
     * Prepare a statement to retrieve the next unique ID
     * from a database sequence object.
     */
    protected final void prepareSequenceStm(Class clazz) {
        //  Class descriptor needed as a handle for the statement
        ClassDescriptor _cld;

        String nextSequenceSQLstatement = getNextSequence();

        _cld = broker.getClassDescriptor(clazz);
        _stmt = broker
                .getStatementManager()
                .getPreparedStatement(_cld,
                                      nextSequenceSQLstatement,
                                      Query.NOT_SCROLLABLE);
    }



    //////////////////////////////////////////////////////////
    // Implementation of the methods from
    // org.apache.ojb.broker.util.sequence.SequenceManager
    //////////////////////////////////////////////////////////

    /**
     * returns a unique int for class clazz and field fieldName.
     * the returned uid is unique accross all tables in the extent of clazz.
     */
    public int getUniqueId(Class clazz, String fieldName) {
        // if no prepared statement exists, prepare one.
        if (null == _stmt) {
            prepareSequenceStm(clazz);
        }

        ResultSet rs = null;
        try {

            rs = _stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Throwable t) {
            LoggerFactory.getDefaultLogger().error(t);
            throw new PersistenceBrokerException(t);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                LoggerFactory.getDefaultLogger().error(e);
                throw new PersistenceBrokerSQLException(e);
            }
        }
    }

    /**
     * Returns an id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     */
    public String getUniqueString(Class clazz, String fieldName) {
        return _sitePrefix + '-' + Integer.toString(getUniqueId(clazz, fieldName));
    }

    /**
     * Returns a long id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     */
    public long getUniqueLong(Class clazz, String fieldName) {
        return (long) getUniqueId(clazz, fieldName);
    }

    /**
     * Returns an id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     */
    public Object getUniqueObject(Class clazz, String fieldName ) {
        return getUniqueString(clazz, fieldName);
    }
}









