/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.predict.business;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.application.predict.struts.view.ResultBean;
import uk.ac.ebi.intact.application.predict.util.PredictLogger;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.ObjectBridgeDAO;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * The super class for a predict user.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class PredictUser implements IntactUserI,
        HttpSessionBindingListener, Serializable {

    public static final String SELECTED_SPECIE = "mouse";

    /**
     * The intact helper to access the database.
     */
    private IntactHelper myHelper;

    /**
     * The current specie (as a link).
     */
    private String mySpecie;

    /**
     * The resource bundle for the application.
     */
    private ResourceBundle myResources;

    /**
     * Default constructor. Loads the resources file.
     */
    public PredictUser() {
        myResources = ResourceBundle.getBundle(
                "uk.ac.ebi.intact.application.predict.PredictResources");
    }

    /**
     * Factory method to create different instances of Predict user instances
     * depending on the JDBC subprotocol.
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @return an instance of this class created using the JDBC subprotocol.
     * A null object is returned for an unknown JDBC subprotocol. Currently
     * oracle and postgres are known types.
     * @exception DataSourceException for error in getting the data source; probably due to
     * missing mapping file.
     * @exception IntactException for errors in creating IntactHelper; problem with reading
     * the repository file.
     */
    public static PredictUser create(String mapping, String dsClass)
            throws DataSourceException, IntactException {
        // The data source for given ds class.
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source.
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // Connection to get the JDBC url.
        Connection conn = null;
        try {
            conn = ((ObjectBridgeDAO) ds.getDAO()).getJDBCConnection();
        }
        catch (LookupException le) {
            new IntactException("Failed to get JDBC Connection", le);
        }

        // The URL to extract the subprotocol.
        String url = null;
        try {
            url = conn.getMetaData().getURL();
        }
        catch (SQLException e) {
            new IntactException("Unable to get meta data to determine the protocol");
        }

        // Extract the subprotocol; ignore the first part before ':'.
        int start = url.indexOf(':') + 1;
        String subprotocol = url.substring(start, url.indexOf(':', start));

        // The user to return.
        PredictUser user = null;

        if (subprotocol.equals("oracle")) {
            user = new PredictUserOra();
        }
        else if (subprotocol.equals("postgresql")) {
            user = new PredictUserPg();
        }
        if (user != null) {
            user.setDS(ds);
        }
        return user;
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
            myHelper.closeStore();
        }
        catch(IntactException ie) {
            //failed to close the store - not sure what to do here yet....
        }
    }

    // Implementation of IntactUserI interface.

    public String getUserName() {
        if (myHelper != null) {
            try {
                return myHelper.getDbUserName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    public String getDatabaseName() {
        if (myHelper != null) {
            try {
                return myHelper.getDbName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    public Collection search(String objectType, String searchParam,
                             String searchValue) throws IntactException {
        return myHelper.search(objectType, searchParam, searchValue);
    }

    /**
     * Returns a list of tax ids for species from the database.
     * @return a list species as tax ids; no duplicates.
     * @exception IntactException for accessing the datastore
     * @exception SQLException thrown by the underlying database.
     *
     * <pre>
     * pre: results->forall(obj: Object | obj.oclIsTypeOf(String))
     * </pre>
     */
    public List getSpecies() throws IntactException, SQLException {
        List species = new ArrayList();
        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(getSpeciesSQL());
            while (rs.next()) {
                String taxid = rs.getString(1);
                species.add(myHelper.getBioSourceByTaxId(taxid).getShortLabel());
            }
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqle) {
                }
            }
        }
        String[] items = (String[]) species.toArray(new String[0]);
        Arrays.sort(items);
        return Arrays.asList(items);
    }

    public String getDefaultChoice() {
        return myResources.getString("default.organism");
    }

    /**
     * Returns a list of beans created from ia_payg database.
     * @param specie the species to get information from ia_payg database. The
     * database is queried with this value.
     * @return a list of ResultBean objects ready to display.
     * @exception IntactException for accessing the datastore
     * @exception SQLException thrown by the underlying database.
     *
     * <pre>
     * pre: results->forall(obj: Object | obj.oclIsTypeOf(ResultBean))
     * </pre>
     */
    public List getDbInfo(String specie) throws IntactException, SQLException {
        // Will contain beans for given species
        List results = new ArrayList();

        Statement stmt = null;
        try {
            // Get the tax id for given specie.
            String taxid = getTaxId(specie);

            stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(getDbInfoSQL(taxid));
            for (int i = 1; rs.next(); i++) {
                String nid = rs.getString(1);
                Protein protein = getProteinForTaxId(nid, taxid);
                if (protein != null) {
                    results.add(new ResultBean(protein, i));
                }
                else {
                    PredictLogger.getInstance().log("Found a null protein for " + nid);
                }
            }
        }
        catch (IntactException ie) {
            // Unable to get a connection to the datastore; print stack trace.
            PredictLogger.getInstance().log(ie);
            throw ie;
        }
        catch (SQLException sqlex) {
            // Log the SQL exception.
            PredictLogger.getInstance().log(sqlex);
            throw sqlex;
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException sqle) {
                }
            }
        }
        return results;
    }

    /**
     * Closes the connection to the database.
     */
    public void closeConnection() {
        try {
            myHelper.closeStore();
        }
        catch (IntactException e) {
            e.printStackTrace();
        }
    }

    // Set/Get methods for the current specie.

    public String getSpecieLink() {
       return mySpecie;
    }

    public void setSpecie(String specie) {
        mySpecie = "<a href=\"" + "javascript:show('BioSource', '" + specie
                + "')\">" + specie + "</a>";
    }

    // To be implemented by different users.

    protected abstract String getSpeciesSQL();
    protected abstract String getDbInfoSQL(String taxid);

    // Helper Methods

    private void setDS(DAOSource ds) throws IntactException {
        myHelper = new IntactHelper(ds);
    }

    /**
     * The JDBC connection
     * @return the underlying JDBC connection to the database.
     * @throws IntactException for errors in getting the connection.
     */
    private Connection getConnection() throws IntactException {
        return myHelper.getJDBCConnection();
    }

    /**
     * Returns a protein for given label and tax id.
     * @param label the short label to search for a Protein.
     * @param tax the tax id to match against the Protein.
     * @return a Protein with <code>label</code> and whose tax id equals to
     * <code>tax</code>; null is returned for all other instances.
     * @throws IntactException for errors in searching the database.
     */
    private Protein getProteinForTaxId(String label, String tax)
            throws IntactException {
        Collection proteins = myHelper.search(
                Protein.class.getName(), "shortLabel", label);
        for (Iterator iter = proteins.iterator(); iter.hasNext(); ) {
            Protein protein = (Protein) iter.next();
            // Only include the protein that has the same tax id as given tid.
            if (protein.getBioSource().getTaxId().equals(tax)) {
                return protein;
            }
        }
        // Dindn't find any protein for given label and tax id.
        return null;
    }

    /**
     * Returns the tax id for given short label.
     * @param shortLabel the short label to retrieve the tax id for.
     * @return the tax id for BioSource of <code>shortLabel</code>.
     * @throws IntactException for errors in searching the database.
     */
    private String getTaxId(String shortLabel) throws IntactException {
        BioSource biosrc = (BioSource) myHelper.getObjectByLabel(
                BioSource.class, shortLabel);
        return biosrc.getTaxId();
    }
}
