/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.util;

import uk.ac.ebi.intact.model.CvComponentRole;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * Gives easy access to some CVs
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Jun-2005</pre>
 */
public class CvObjectHelper {

    ///////////////////////////
    // Singleton

    private static CvObjectHelper ourInstance = new CvObjectHelper();

    public static CvObjectHelper getInstance() {
        return ourInstance;
    }

    private CvObjectHelper() {
    }


    ////////////////////////////
    // Private methods

    private static CvObject getCvObject( String psiId ) throws IntactException {

        if( psiId == null || psiId.trim().equals( "" ) ) {
            throw new IllegalArgumentException( "the given psi ID is null or empty. abort." );
        }

        if( cache.containsKey( psiId ) ) {
            return (CvObject) cache.get( psiId );
        }

        IntactHelper helper = new IntactHelper( );

        CvDatabase psi = (CvDatabase) helper.getObjectByXref( CvDatabase.class, "MI:0488" );

        CvObject cvObjet = null;
        Collection cvObjets = helper.getObjectsByXref( CvComponentRole.class, psi, psiId );

        if( cvObjets.size() == 1 ) {
            cvObjet = (CvObject) cvObjets.iterator().next();
        } else {
            System.err.println( "Error, found " + cvObjets.size() + " CvObject for " + psiId + ". only 1 was expected." );
        }

        helper.closeStore();

        if( cvObjet != null ) {
            // update cache
            cache.put( psiId, cvObjet );
        }

        return cvObjet;
    }


    private static Map cache = new HashMap();


    /**
     * Method allowing an initialisation of the helper.
     * <br>
     * Should only be used for unnit testing purpose.
     * <br>
     * That way the test can be disconnected from the database.
     *
     * @param psiId
     * @param cvObject
     */
    public void setCvObject( String psiId, CvObject cvObject ) {
        cache.put( psiId, cvObject );
    }


    ////////////////////////////
    // Public methods

    public CvComponentRole getBait() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.BAIT_PSI_REF );
    }

    public CvComponentRole getPrey() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.PREY_PSI_REF );
    }

    public CvComponentRole getNeutral() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.NEUTRAL_PSI_REF );
    }

    public CvComponentRole getSelf() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.SELF_PSI_REF );
    }

    public CvComponentRole getEnzyme() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.ENZYME_PSI_REF );
    }

    public CvComponentRole getEnzymeTarget() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.ENZYME_TARGET_PSI_REF );
    }

    public CvComponentRole getUnspecified() throws IntactException {

        return (CvComponentRole) getCvObject( CvComponentRole.UNSPECIFIED_PSI_REF );
    }
}
