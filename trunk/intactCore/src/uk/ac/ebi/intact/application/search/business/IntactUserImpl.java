/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

import java.util.*;
import java.sql.SQLException;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.XmlBuilder;
import org.apache.ojb.broker.accesslayer.LookupException;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>ttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactUserImpl implements IntactUserIF, HttpSessionBindingListener {

    /**
     * Reference to the DAO.
     */
    private IntactHelper helper;

    /**
     * An Xml Builder for use in XML creation and
     * manipulation during a user session
     */
    private XmlBuilder builder;

    /**
     * The search criteria.
     */
    private String searchCriteria;

    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class. Side-effects of this constructor
     * are that the User instance also has an <code>IntactHelper</code> and
     * an <code>XmlBuilder</code> created for use during a user session.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception IntactException thrown for any error in creating an IntactHelper,
     * XmlBuilder etc
     */
    public IntactUserImpl(String mapping, String dsClass)
        throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // build a helper and XmlBuilder for use throughout a session
        this.helper = new IntactHelper(ds);
        this.builder = new XmlBuilder(this.helper);
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
            this.helper.closeStore();
            this.builder = null;
        }
        catch(IntactException ie) {
            //failed to close the store - not sure what to do here yet....
        }
    }

    // Implementation of IntactUserI interface.

    public String getUserName() {
        if (this.helper != null) {
            try {
                return this.helper.getDbUserName();
            }
            catch (LookupException e) {}
            catch (SQLException e) {}
        }
        return null;
    }

    public String getDatabaseName() {
        if (this.helper != null) {
            try {
                return this.helper.getDbName();
            }
            catch (LookupException e) {}
            catch (SQLException e) {}
        }
        return null;
    }

    public String getSearchCritera() {
        return this.searchCriteria;
    }

    public Collection search(String objectType, String searchParam,
                              String searchValue) throws IntactException {
        // Set the search criteria.
        this.searchCriteria = searchParam;

        //now retrieve an object...
        return helper.search(objectType, searchParam, searchValue);
    }

    public IntactHelper getHelper() {

        return this.helper;
    }

    public XmlBuilder getXmlBuilder() {
        return this.builder;
    }
}
