/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.JdbcAccess;
import org.apache.ojb.broker.accesslayer.StatementManagerIF;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.util.logging.LoggerFactory;
import org.apache.ojb.broker.util.logging.Logger;
import org.apache.ojb.broker.util.sequence.AbstractSequenceManager;
import org.apache.ojb.broker.util.sequence.SequenceManagerException;
import org.apache.commons.lang.SystemUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * This class is used by Intact to generate intact-specific sequences. These sequences
 * are obtained form whichever database platform is being used, in order to generate
 * intact ACs, which are then used as primary keys in the database. Only a single class
 * is needed because OJB now handles a number of different platforms automatically for
 * sequence generation - currently Intact uses oracle and postgres, booth of which are
 * supported by OJB.
 * @author Chris Lewington
 * @version $Id$
 */
public class IntactSequenceManager extends AbstractSequenceManager {

    /**
     * Identified the file location of the site prefix to be used for sequences.
     */
    public static final String CONFIG_FILE = "/config/Institution.properties";

    private Logger log = LoggerFactory.getLogger(IntactSequenceManager.class);

    /**
     *  The prefix for all ID strings. Protected in case anyone may wish to subclass.
     * Defaults to 'PREFIX'.
     */
    protected static String _sitePrefix = "PREFIX";

    /**
     * This constructor uses the parent code to set up OJB specific configuration
     * information, and then loads the site prefix to be used from a known property file.
     * NOte: if the prefix is not specified in a file then the default String 'PREFIX'
     * will be used.
     */
    public IntactSequenceManager( PersistenceBroker broker ) throws IntactException {

        //this call sets up various OJB config details that may have
        //been supplied in a properties file, eg platform etc
        super(broker);

        Properties props = PropertyLoader.load ( CONFIG_FILE );
        if (props != null) {
            _sitePrefix = props.getProperty ( "ac.prefix" );
        } else {
            throw new IntactException( "Could not find the configuration file: "+ CONFIG_FILE +"." );
        }
    }

    //****************************************************************
    // method implementations of SequenceManager interface
    //****************************************************************
    /**
     * Returns a unique object for the given field attribute.
     * The returned value takes in account the jdbc-type
     * and the FieldConversion.sql2java() conversion defined for <code>field</code>.
     * The returned object is unique accross all tables in the extent
     * of class the field belongs to.
     * NB This method is overridden from the base AbstractSequenceManager OJB class because
     * there it only returns a Long and we require a String.
     * @param field The field which is identified as the primary key
     * @return Object The resulting sequence. For Intact this will always be a String
     * of the form <sitePrefix_n>.
     * @throws SequenceManagerException thron if there was a problem obtaining a sequence
     * value from the database.
     */
    public Object getUniqueValue(FieldDescriptor field) throws SequenceManagerException
    {
        Object result = field.getJdbcType().sequenceKeyConversion(new Long(getUniqueLong(field)));
        // perform a sql to java conversion here, so that clients do
        // not see any db specific values
        result = field.getFieldConversion().sqlToJava(result);

        //for Intact we always want a String sequence
        return _sitePrefix + '-' +  result.toString();
    }

    /**
     * noop
     */
    public void afterStore(JdbcAccess dbAccess, ClassDescriptor cld, Object obj)
            throws SequenceManagerException
    {
    }

    /**
     * noop
     */
    public void setReferenceFKs(Object obj, ClassDescriptor cld)
            throws SequenceManagerException
    {
    }

    //-------------- helper methods, derived from the OJB SequenceManagerNextValImpl class -----------------

    /**
     * returns a unique long value for a field in a class.
     * @param field The field of a class - should be the primary key field.
     * @return long A primitive long sequence value obtained from the DB platform.
     * This svalue is unique across all the extents (subclasses) of the class containing
     * the field parameter.
     * @throws SequenceManagerException thrown if there was a DB problem obtaining
     * the sequence value.
     */
    protected long getUniqueLong(FieldDescriptor field) throws SequenceManagerException
    {
        long result = 0;
        // lookup sequence name - uses parent class method to do this
        String sequenceName = calculateSequenceName(field);
        try
        {
            result = buildNextSequence(field.getClassDescriptor(), sequenceName);
        }
        catch (Throwable e)
        {
            // maybe the sequence was not created
            try
            {
                log.info("Create DB sequence key '"+sequenceName+"'");
                createSequence(field.getClassDescriptor(), sequenceName);
            }
            catch (Exception e1)
            {
                throw new SequenceManagerException(
                        SystemUtils.LINE_SEPARATOR +
                        "Could not grab next id, failed with " + SystemUtils.LINE_SEPARATOR +
                        e.getMessage() + SystemUtils.LINE_SEPARATOR +
                        "Creation of new sequence failed with " +
                        SystemUtils.LINE_SEPARATOR + e1.getMessage() + SystemUtils.LINE_SEPARATOR
                        , e1);
            }
            try
            {
                result = buildNextSequence(field.getClassDescriptor(), sequenceName);
            }
            catch (Throwable e1)
            {
                throw new SequenceManagerException("Could not grab next id, sequence seems to exist", e);
            }
        }
        return result;
    }

    /**
     * Obtains the next sequence value from the DB platform. This metyhod is the
     * one which actually executes the platform-specific sequence SQL code.
     * @param cld The Class Descriptor we are interested in.
     * @param sequenceName The name of the sequence. If not defined in the Class Descriptor
     * then a default one will be created by OJB.
     * @return  long A primitive sequence value.
     * @throws Exception thrown if there were problems either accssing the DB, or with
     * the ResultSet.
     */
    protected long buildNextSequence(ClassDescriptor cld, String sequenceName) throws Exception
    {
        ResultSet rs = null;
        Statement stmt = null;
        long result = -1;
        StatementManagerIF stmtMan = getBrokerForClass().serviceStatementManager();
        try
        {
            stmt = stmtMan.getGenericStatement(cld, Query.NOT_SCROLLABLE);
            rs = stmt.executeQuery(getPlatform().nextSequenceQuery(sequenceName));
            rs.next();
            result = rs.getLong(1);
        }
        finally
        {
            stmtMan.closeResources(stmt, rs);
        }
        return result;
    }

    /**
     * This method will be called if for some reason the sequence was not created
     * in the first place (and so it could not have been built). This method will
     * therefore create a sequence to enable a long value to be built.
     * @param cld  The Class Descriptor we are interested in.
     * @param sequenceName The name of the seuqnece to be used.
     * @throws Exception Thrown if there were any unusual problems.
     */
    protected void createSequence(ClassDescriptor cld, String sequenceName) throws Exception
    {
        Statement stmt = null;
        StatementManagerIF stmtMan = getBrokerForClass().serviceStatementManager();
        try
        {
            stmt = stmtMan.getGenericStatement(cld, Query.NOT_SCROLLABLE);
            stmt.execute(getPlatform().dropSequenceQuery(sequenceName));
        }
        catch (Exception ignore)
        {
        }
        finally
        {
            try
            {
                stmtMan.closeResources(stmt, null);
            }
            catch (Exception ignore)
            {
            }
        }

        try
        {
            stmt = getBrokerForClass().serviceStatementManager().getGenericStatement(cld, Query.NOT_SCROLLABLE);
            stmt.execute(getPlatform().createSequenceQuery(sequenceName));
        }
        finally
        {
            try
            {
                getBrokerForClass().serviceStatementManager().closeResources(stmt, null);
            }
            catch (Exception ignore)
            {
            }
        }
    }
}









