package uk.ac.ebi.intact.persistence;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache ObjectRelationalBridge" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache ObjectRelationalBridge", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerSQLException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.query.QueryByExample;
import org.apache.ojb.broker.query.Query;

import org.apache.ojb.broker.singlevm.PersistenceBrokerImpl;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.util.sequence.SequenceManager;
import org.apache.ojb.broker.util.sequence.SequenceEntry;
import org.apache.ojb.broker.util.sequence.SequenceConfiguration;
import org.apache.ojb.broker.util.configuration.impl.OjbConfiguration;
import org.apache.ojb.broker.util.configuration.ConfigurationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 * This sequence manager accesses a database sequence object to
 * get the next unique id, leaving the management of the sequence
 * to the database. In it's current implementation, this class is
 * Oracle-specific, but some other RDBMS also offer sequence objects.
 *
 * IDs are unique across all classes.
 *
 * @author: hhe
 */
public class DbSequenceManager implements SequenceManager {

    /**
     * reference to the PersistenceBroker
     */
    protected PersistenceBrokerImpl broker;

    /**
     * singleton instance of the SequenceManager
     */
    private static DbSequenceManager _instance;


    /**
     *   Prepared SQL statement to get a new id from a database sequence object.
     */
    private static PreparedStatement _stmt;

    /**
     *  The prefix for all ID strings
     */
    private static String _sitePrefix;

    /**
     *
     * Public constructor
     *
     */
    public DbSequenceManager(PersistenceBrokerImpl broker)
            throws ConfigurationException {
        this.broker = broker;

        try {

            OjbConfiguration configuration =
                    (OjbConfiguration) PersistenceBrokerFactory
                    .getConfigurator().getConfigurationFor(null);

            _sitePrefix = configuration.getString("IntAct.SitePrefix", "TEST");
        } catch (Exception e) {
            throw (new ConfigurationException("Could not read IntAct.SitePrefix property from configuration."));
        }
    }

    /**
     * Prepare a statement to retrieve the next unique ID
     * from a database sequence object.
     */
    private void prepareSequenceStm(Class clazz) {
        //  Class descriptor needed as a handle for the statement
        ClassDescriptor _cld;

        _cld = broker.getClassDescriptor(clazz);
        _stmt = broker
                .getStatementManager()
                .getPreparedStatement(_cld,
                        "SELECT intact_ac.nextval FROM DUAL",
                        Query.NOT_SCROLLABLE);

    }

    /**
     * returns a unique int for class clazz and field fieldName.
     * the returned uid is unique accross all tables in the extent of clazz.
     */
    public int getUniqueId(Class clazz, String fieldName) {
        // if no prepared statement exists, prepare one.
        if (null == _stmt) {
            prepareSequenceStm(clazz);
        }

        int result = 0;
        ResultSet rs = null;
        try {

            rs = _stmt.executeQuery();
            rs.next();
            result = rs.getInt(1);
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
            return result;
        }
    }

    /**
     * Returns an id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     *
     */
    public String getUniqueString(Class clazz, String fieldName) {
        return _sitePrefix + '-' + Integer.toString(getUniqueId(clazz, fieldName));
    }

    /**
     * Returns a long id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     *
     */
    public long getUniqueLong(Class clazz, String fieldName) {
        return (long) getUniqueId(clazz, fieldName);
    }

    /**
     *
     * Returns an id unique across all classes.
     * The clazz attribute is only maintained to implement the interface.
     *
     */
    public Object getUniqueObject(Class clazz, String
            fieldName) {
        return getUniqueString(clazz, fieldName);
    }
}
