/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvObject;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal Singleton that contains the Controlled Vocabularies, so they only have to be loaded once.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Mar-2006</pre>
 */
public abstract class CvContext {
    public static final String CONTEXT_KEY = CvContext.class.getName();

    public static final Logger log = Logger.getLogger( CvContext.class );

    private String srsUrl;

    // enumeration of the possible keys for the constant CvObjects
    public enum CvName {
        // Xref Databases
        UNIPROT_DB,
        INTACT_DB,
        SGD_DB,
        GO_DB,
        INTERPRO_DB,
        FLYBASE_DB,
        REACTOME_DB,
        HUGE_DB,

        IDENTITY_XREF_QUALIFIER,
        SECONDARY_XREF_QUALIFIER,

        ISOFORM_PARENT_XREF_QUALIFIER,

        ISOFORM_COMMENT,
        NO_UNIPROT_UPDATE,

        ISOFORM_SYNONYM,
        GENE_NAME_ALIAS_TYPE,
        GENE_NAME_SYNONYM_ALIAS_TYPE,
        ORF_NAME_ALIAS_TYPE,
        LOCUS_NAME_ALIAS_TYPE,

        PROTEIN_TYPE
    }

    // Map that contains all the CvObjects
    private final Map<CvName, CvObject> cvMap;

    protected CvContext() {
        cvMap = new HashMap<CvName, CvObject>();
    }

    // ThreadLocal pattern
    /*
    private static ThreadLocal<CvContext> currentInstance = new ThreadLocal()
    {
        // the initial value of the CvContext is null
        protected CvContext initialValue()
        {
            return null;
        }

    };   */

    /**
     * Gets the current instance of the CvContext
     *
     * @return the current instance of the CvContext
     */
    public static synchronized CvContext getCurrentInstance() {
        ServletContext servletContext = ExternalContext.getCurrentInstance().getServletContext();

        if ( servletContext == null ) {
            throw new NullPointerException( "ServletContext null in CvContext" );
        }

        CvContext cvContext = (CvContext) servletContext.getAttribute( CONTEXT_KEY );

        if ( cvContext == null ) {
            try {
                cvContext = CvContextFactory.createCvContext( new IntactHelper() );

                if ( log.isInfoEnabled() ) {
                    log.info( "New CvContext instance created" );
                }
            }
            catch ( IntactException e ) {
                e.printStackTrace();
            }

            servletContext.setAttribute( CvContext.CONTEXT_KEY, cvContext );
        } else {
            if ( log.isInfoEnabled() ) {
                log.info( "CvContext already exists" );
            }
        }

        return cvContext;
    }

    /**
     * Puts a CvObject in the map, using one of the enumeration values
     *
     * @param cvName   The enumeration value with will serve as map key
     * @param cvObject The cvObject to store
     */
    public void putCvObject( CvName cvName, CvObject cvObject ) {
        cvMap.put( cvName, cvObject );
    }

    /**
     * Gets the CvObject from the map, using a CvName key
     *
     * @param cvName the key to use (from the enumeration)
     *
     * @return the object stored with that key
     */
    public CvObject getCvObject( CvName cvName ) {
        return cvMap.get( cvName );
    }

    /**
     * Gets the key for a CvObject of the map
     *
     * @param cvObject The object to search the key for
     *
     * @return the key if the map contains de CvObject. Otherwise it returns <code>null</code>
     */
    public CvName getKeyForCvObject( CvObject cvObject ) {
        for ( Map.Entry<CvName, CvObject> entry : cvMap.entrySet() ) {
            if ( entry.getValue().equals( cvObject ) ) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Updates a CvObject already present in the context
     *
     * @param cvObject The new value for the cvObject. Note that it must be equal to the existing one in order to be
     *                 updated
     *
     * @return returns true if an object in the map has been updated. Otherwise it returns false.
     */
    public boolean updateCvObject( CvObject cvObject ) {
        CvName cvName = getKeyForCvObject( cvObject );

        if ( cvName == null ) {
            return false;
        }

        cvMap.put( cvName, cvObject );

        return true;
    }

    /**
     * The SRS url
     *
     * @return The SRS url
     */
    public String getSrsUrl() {
        return srsUrl;
    }

    /**
     * Sets a new value for the SRS url
     *
     * @param srsUrl the SRS url value
     */
    public void setSrsUrl( String srsUrl ) {
        this.srsUrl = srsUrl;
    }
}
