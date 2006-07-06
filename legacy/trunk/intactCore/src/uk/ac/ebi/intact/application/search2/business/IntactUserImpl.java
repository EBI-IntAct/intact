/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.business;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.model.IntactObject;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.sql.SQLException;
import java.util.Collection;

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
public class IntactUserImpl<T extends IntactObject> implements IntactUserIF<T>, HttpSessionBindingListener {

    /**
     * Reference to the DAO.
     */
    private IntactHelper helper;

    private String myHelpLink;

    private String searchValue;

    private Class<T> searchClass;

    /**
     * Managment of the object chunk - Chunk to display
     */
    private int selectedChunk;



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
//        Map fileMap = new HashMap();
//        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
//        ds.setConfig(fileMap);

        // build a helper and XmlBuilder for use throughout a session
        this.helper = new IntactHelper(ds);
    }

    public int getSelectedChunk () {
        return selectedChunk;
    }

    public void setSelectedChunk ( int selectedChunk ) {
        this.selectedChunk = selectedChunk;
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
            return this.helper.getDbName();
        }
        return null;
    }

//    public String getSearchCritera() {
//        return this.searchCriteria;
//    }

    public <T extends IntactObject> Collection<T> search(Class<T> objectType, String searchParam,
                                String searchValue) throws IntactException {
        // Set the search criteria.
        // TODO remove it if not needed
//        this.searchCriteria = searchParam;

        //now retrieve an object...
        return helper.search(objectType, searchParam, searchValue);
    }

    public Class<T> getSearchClass () {
        return searchClass;
    }

    public void setSearchClass ( Class<T> searchClass ) {
        this.searchClass = searchClass;
    }

    public String getSearchValue () {
        return searchValue;
    }

    public void setSearchValue ( String searchValue ) {
        this.searchValue = searchValue;
    }

    public void setHelpLink(String link) {
        myHelpLink = link;
    }

    public String getHelpLink() {
        return myHelpLink;
    }
}
