/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.application.mine.business;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.ojb.broker.accesslayer.LookupException;

import uk.ac.ebi.intact.application.mine.business.graph.model.MineData;
import uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

/**
 * @author Andreas Groscurth
 */
public class IntactUser implements IntactUserI {
    private IntactHelper intactHelper;
    private Collection paths;
    private Map graphMap;
    private Collection singletons;

    /**
     * Creates a new intact user
     * 
     * @throws IntactException if the initiation of the intacthelper failed
     */
    public IntactUser() throws IntactException {
        intactHelper = new IntactHelper();
        graphMap = new Hashtable();
        paths = new HashSet();
        singletons = new HashSet();
    }

    /**
     * Returns the path of the minimal connecting network
     * 
     * @return Returns the path.
     */
    public Collection getPaths() {
        return paths;
    }

    /**
     * Returns the intacthelper
     * 
     * @return Returns the intactHelper.
     */
    public IntactHelper getIntactHelper() {
        return intactHelper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0) {
        try {
            this.intactHelper.closeStore();
            Constants.LOGGER.info("IntactHelper datasource closed "
                    + "(cause: removing attribute from session)");
        }
        catch (IntactException ie) {
            //failed to close the store - not sure what to do here yet....
            Constants.LOGGER.error("error when closing the IntactHelper store",
                    ie);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#search(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Collection search(String objectType, String searchParam,
            String searchValue) throws IntactException {
        return intactHelper.search(objectType, searchParam, searchValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#getUserName()
     */
    public String getUserName() {
        if (this.intactHelper != null) {
            try {
                return this.intactHelper.getDbUserName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.commons.business.IntactUserI#getDatabaseName()
     */
    public String getDatabaseName() {
        if (this.intactHelper != null) {
            try {
                return this.intactHelper.getDbName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getDBConnection()
     */
    public Connection getDBConnection() {
        try {
            return intactHelper.getJDBCConnection();
        }
        catch (IntactException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getMineData(uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey)
     */
    public MineData getMineData(NetworkKey key) {
        return (MineData) graphMap.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#addToGraphMap(uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey,
     *      uk.ac.ebi.intact.application.mine.business.graph.model.MineData)
     */
    public void addToGraphMap(NetworkKey key, MineData md) {
        graphMap.put(key, md);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#addToPath(uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey,
     *      java.util.Collection)
     */
    public void addToPath(Collection path) {
        paths.add(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#clearPaths()
     */
    public void clearAll() {
        if (paths != null) {
            paths.clear();
        }
        if (singletons != null) {
            singletons.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#addToSingletons()
     */
    public void addToSingletons(Collection element) {
        singletons.addAll(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getSingletons()
     */
    public Collection getSingletons() {
        return singletons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ebi.intact.application.mine.business.IntactUserI#getHVLink()
     */
    public String getHVLink() {
        StringBuffer acs = new StringBuffer();
        StringBuffer borders = new StringBuffer();
        int i = 0;
        Collection p;
        for (Iterator it = paths.iterator(); it.hasNext();) {
            p = (Collection) it.next();
            for (Iterator iter = p.iterator(); iter.hasNext();) {
                acs.append(iter.next().toString());

                if (iter.hasNext()) {
                    acs.append(",");
                }
            }
            i += p.size();
            borders.append(i);
            if (it.hasNext()) {
                acs.append(",");
                borders.append(",");
            }
        }
        return acs.append("&network=").append(borders).toString();
    }
}