/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import org.apache.commons.collections.LRUMap;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Institution;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

/**
 * That class his hidding the logic which allow to get a valid biosource from a taxid.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceFactory {

    /**
     * Logger for the internal processing - can be optionally set
     */
    private org.apache.log4j.Logger logger = null;

    /**
     * Data access
     */
    private IntactHelper helper;

    /**
     * The institution to which we have to link all new BioSource
     */
    private Institution institution;

    /**
     * Cache valid BioSource objects for taxIds.
     */
    private static LRUMap bioSourceCache;
    private final static int DEFAULT_CACHE_SIZE = 200;

    /**
     * To retreive up to date biosource data
     */
    private static NewtServerProxy newtProxy;
    private static final String NEWT_URL = "http://www.ebi.ac.uk/newt/display";
    // http://web7-node1.ebi.ac.uk:9120/newt/display


    public BioSourceFactory( IntactHelper helper ) throws IntactException {
        this( helper, helper.getInstitution(), DEFAULT_CACHE_SIZE );
    }


    public BioSourceFactory( IntactHelper helper, Institution institution ) {
        this( helper, institution, DEFAULT_CACHE_SIZE );
    }


    public BioSourceFactory( IntactHelper helper, Institution institution, int cacheSize ) {

        if( helper == null ) {
            throw new RuntimeException( "The helper must not be null!" );
        }
        if( institution == null ) {
            throw new RuntimeException( "The institution must not be null!" );
        }

        this.helper = helper;
        this.institution = institution;

        URL url = null;
        try {
            url = new URL( NEWT_URL );
        } catch ( MalformedURLException e ) {
            throw new RuntimeException( "Unable to create Newt proxy, malformed URL: " + url );
        }

        newtProxy = new NewtServerProxy( url );
        newtProxy.disableCaching();

        bioSourceCache = new LRUMap( cacheSize );
    }


    /**
     * Set a logger to keep track of the internal processing.
     *
     * @param logger the logger to set
     */
    public void setLogger( Logger logger ) {
        this.logger = logger;
    }


    /**
     * Select a BioSource that has neither CvCellType nor CvTissue.
     *
     * @param biosources the Collection of BioSource that potentially contains some having
     *                   CvCellType or CvTissue.
     * @return the unique BioSource that has neither CvCellType nor CvTissue
     * @throws IntactException if several of such BioSource are found
     */
    private BioSource getOriginalBioSource( Collection biosources ) throws IntactException {
        BioSource original = null;

        for ( Iterator iterator = biosources.iterator(); iterator.hasNext(); ) {
            final BioSource bioSource = (BioSource) iterator.next();
            if( bioSource.getCvTissue() == null &&
                bioSource.getCvCellType() == null ) {
                if( original == null ) {
                    // first on is found
                    original = bioSource;
                } else {
                    // multiple bioSource, error.
                    if( logger != null ) {
                        logger.error( "More than one BioSource with this taxId found: " + original.getTaxId() );
                    }
                    throw new IntactException( "More than one BioSource with this taxId found: " + original.getTaxId() );
                }
            }
        }
        return original;
    }

    /**
     * Create or update a BioSource object from a taxid.
     *
     * @param aTaxId The tax id to create/update a biosource for
     * @return a valid, persistent BioSource
     */
    public BioSource getValidBioSource( String aTaxId ) throws IntactException {

        // If a valid BioSource object already exists, return it.
        if( bioSourceCache.containsKey( aTaxId ) ) {
            return (BioSource) bioSourceCache.get( aTaxId );
        }

        // Get all existing BioSources with aTaxId
        // Exception if there are more than one.
        Collection currentBioSources = helper.search( BioSource.class.getName(), "taxId", aTaxId );

        if( null == currentBioSources ) {
            throw new IntactException( "Search for a BioSource having the taxId: " + aTaxId + " failed." );
        }

        BioSource intactBioSource = null;
        if( currentBioSources.size() > 0 ) {
            intactBioSource = getOriginalBioSource( currentBioSources );
        }

        // Get a correct BioSource from Newt
        BioSource validBioSource = getNewtBiosource( aTaxId );
        if( null == validBioSource ) {
            if( logger != null ) {
                logger.error( "The taxId is invalid: " + aTaxId );
            }
            throw new IntactException( "The taxId is invalid: " + aTaxId );
        }

        // The verified BioSource
        BioSource newBioSource = null;

        // If there is no current BioSource, create it
        if( intactBioSource == null ) {
            if( validBioSource.getTaxId().equals( aTaxId ) ) {
                // not in IntAct and found in Newt so make it persistent in IntAct
                helper.create( validBioSource );
                newBioSource = validBioSource;
            } else {
                // taxid was obsolete
                Collection bioSources = helper.search( BioSource.class.getName(),
                                                       "taxId", validBioSource.getTaxId() );
                switch ( bioSources.size() ) {
                    case 0:
                        // doesn't exists, so create it.
                        if( logger != null ) {
                            logger.info( "Creating new bioSource(" + validBioSource.getTaxId() + ")." );
                        }
                        helper.create( validBioSource );
                        newBioSource = validBioSource;
                        break;

                    case 1:
                        // it exists, try to update it.
                        BioSource intactBs = (BioSource) bioSources.iterator().next();
                        if( logger != null ) {
                            logger.info( "Updating existing BioSource (" + validBioSource.getTaxId() + ")" );
                        }
                        newBioSource = updateBioSource( intactBs, validBioSource );
                        break;

                    default:
                        if( logger != null ) {
                            logger.error( "More than one BioSource with this taxId found: " + aTaxId );
                        }
                        throw new IntactException( "More than one BioSource with this taxId found: " + aTaxId );
                }
            }
        } else {
            // only one BioSource found with the original taxid
            // If it is obsolete, update current BioSource
            if( !intactBioSource.equals( validBioSource ) ) {
                if( logger != null ) {
                    logger.info( "Updating existing BioSource (" + validBioSource.getTaxId() + ")" );
                }
                newBioSource = updateBioSource( intactBioSource, validBioSource );
            } else {
                newBioSource = intactBioSource;
            }
        }

        // Return valid BioSource and update cache
        /* The bioSourceCache will also contain associations from obsolete taxIds
         * to valid BioSource objects to avoid looking up the same obsolete Id
         * over and over again.
         */
        bioSourceCache.put( aTaxId, newBioSource );

        return newBioSource;
    } // getValidBioSource


    /**
     * Gives a valid taxid.
     *
     * @param taxid the original taxid
     * @return a valid taxid (can be different from the original in case of obsoletness).
     * @throws IntactException if an error occur when accessing IntAct or an inconstistancy.
     */
    public String getUpToDateTaxid( final String taxid ) throws IntactException {

        if( taxid == null ) {
            return null;
        }
        BioSource validBioSource = getValidBioSource( taxid );
        return validBioSource.getTaxId();
    }


    /**
     * Update the given BioSource with data taken from Newt.<br>
     * it assumes that the taxid is existing in the given BioSource.
     *
     * @param taxid the taxid from which we want to get a Biosource
     * @return an updated BioSource or null
     */
    private BioSource getNewtBiosource( String taxid ) {

        if( taxid == null ) {
            return null;
        }

        if( logger != null ) {
            logger.info( "Try to get BioSource data from Newt" );
        }
        NewtServerProxy.NewtResponse response = null;

        try {
            response = newtProxy.query( Integer.parseInt( taxid ) );
        } catch ( IOException e ) {
            if( logger != null ) {
                logger.error( "Could not access the Newt web server.", e );
            }
            return null;
        } catch ( NumberFormatException e ) {
            if( logger != null ) {
                logger.error( "invalid taxid: " + taxid, e );
            }
            return null;
        } catch ( NewtServerProxy.TaxIdNotFoundException e ) {
            if( logger != null ) {
                logger.error( "taxId not found from Newt: " + taxid, e );
            }
            return null;
        }

        // the taxId can be different in obsoleteness case.
        BioSource bioSource = new BioSource( institution,
                                             response.getShortLabel().toLowerCase(), "" + response.getTaxId() );
        bioSource.setFullName( response.getFullName() );

        return bioSource;
    }


    /**
     * Try to update an existing IntAct BioSource from an other.
     * <p/>
     * Only the taxid is possibly updated.
     * </p>
     *
     * @param bioSource     the IntAct BioSource
     * @param newtBioSource the one from which we get the up-to-date data
     * @return an up-to-date IntAct BioSource
     * @throws IntactException
     */
    private BioSource updateBioSource( BioSource bioSource,
                                       BioSource newtBioSource ) throws IntactException {

        boolean needUpdate = false;

        // compare these two BioSource and update in case of differences
        String newtTaxid = newtBioSource.getTaxId();
        if( false == bioSource.getTaxId().equals( newtTaxid ) ) {
            bioSource.setTaxId( newtTaxid );
            if( logger != null ) {
                logger.debug( "Obsolete taxid: taxid " + bioSource.getTaxId() +
                              " becomes " + newtTaxid );
            }
            needUpdate = true;
        }

        /**
         * The IntAct shortlabel and fullName has to be maintained.
         * eg. for the taxid 4932 we have the shortlabel 'yeast' in the database where Newt have 's cerevisae'
         * If the shortlabel has been changed via the editor, that change is maintained.
         */

        if( needUpdate ) {
            if( logger != null ) {
                logger.info( "update biosource (taxid=" + bioSource.getTaxId() + ")" );
            }

            try {
                helper.update( bioSource );
            } catch ( IntactException ie ) {
                throw ie;
            }
        }

        return bioSource;
    } // updateBioSource

} // BioSourceFactory
