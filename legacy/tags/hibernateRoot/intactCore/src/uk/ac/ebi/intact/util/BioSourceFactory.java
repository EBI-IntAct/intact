/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
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

        if ( helper == null ) {
            throw new RuntimeException( "The helper must not be null!" );
        }
        if ( institution == null ) {
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
     * Sets the Intact helper to access the persistent system. This method is package visible.
     *
     * @param helper the Intact helper to set.
     */
    void setIntactHelper( IntactHelper helper ) {
        this.helper = helper;
    }

    /**
     * Select a BioSource that has neither CvCellType nor CvTissue.
     *
     * @param biosources the Collection of BioSource that potentially contains some having CvCellType or CvTissue.
     *
     * @return the unique BioSource that has neither CvCellType nor CvTissue
     *
     * @throws IntactException if several of such BioSource are found
     */
    private BioSource getOriginalBioSource( Collection<BioSource> bioSources ) throws IntactException {
        BioSource original = null;

        for (BioSource bioSource : bioSources)
        {
             if (bioSource.getCvTissue() == null &&
                    bioSource.getCvCellType() == null)
            {
                if (original == null)
                {
                    // first on is found
                    original = bioSource;
                }
                else
                {
                    // multiple bioSource, error.
                    if (logger != null)
                    {
                        logger.error("More than one BioSource with this taxId found: " + original.getTaxId());
                    }
                    throw new IntactException("More than one BioSource with this taxId found: " + original.getTaxId());
                }
            }
        }
        return original;
    }

    private CvDatabase getNewt() throws IntactException {
        CvDatabase newt =  helper.getObjectByLabel( CvDatabase.class, CvDatabase.NEWT );
        return newt;
    }

    private CvXrefQualifier getIdentity() throws IntactException {
        CvXrefQualifier identity =  helper.getObjectByLabel( CvXrefQualifier.class, CvXrefQualifier.IDENTITY );
        return identity;
    }

    /**
     * Create or update a BioSource object from a taxid.
     *
     * @param aTaxId The tax id to create/update a biosource for
     *
     * @return a valid, persistent BioSource
     */
    public BioSource getValidBioSource( String aTaxId ) throws IntactException {

        // If a valid BioSource object already exists, return it.
        if ( bioSourceCache.containsKey( aTaxId ) ) {
            return (BioSource) bioSourceCache.get( aTaxId );
        }

        // Get all existing BioSources with aTaxId
        Collection currentBioSources = helper.search( BioSource.class.getName(), "taxId", aTaxId );

        if ( logger != null ) {
            logger.info( currentBioSources.size() + " BioSource found for " + aTaxId );
        }

        if ( null == currentBioSources ) {
            throw new IntactException( "Search for a BioSource having the taxId: " + aTaxId + " failed." );
        }

        BioSource intactBioSource = null;
        if ( currentBioSources.size() > 0 ) {
            intactBioSource = getOriginalBioSource( currentBioSources );
        }

        // The verified BioSource
        BioSource newBioSource = null;

        // Get a correct BioSource from Newt
        // we could have created our own biosource like 'in vitro' ... in which case, newt is unaware of it !
        // TODO in-vitro could be autogenerated by that factory if the user gives -1 !!
        BioSource validBioSource = getNewtBiosource( aTaxId );

        if ( null == validBioSource && intactBioSource == null ) {

            if ( logger != null ) {
                logger.error( "The taxId is invalid: " + aTaxId );
            }
            throw new IntactException( "The taxId is invalid: " + aTaxId );

        } else if ( null == validBioSource && intactBioSource != null ) {

            // we have a biosource in intact that Newt doesn't know about, return it.
            if ( logger != null ) {
                logger.error( "The taxId " + aTaxId + " was found in IntAct but doesn't exists in Newt." );
            }
            newBioSource = intactBioSource;

        } else if ( null != validBioSource && intactBioSource == null ) {

            // newt biosource has been found and nothing in intact.
            // chech if the given taxid was obsolete.
            if ( validBioSource.getTaxId().equals( aTaxId ) ) {

                // not in IntAct and found in Newt so make it persistent in IntAct
                helper.create( validBioSource );
                newBioSource = validBioSource;

            } else {
                // the given taxid was obsolete, check if intact contains already a biosource for the new taxid.

                // both were found but different taxid, ie. taxid was obsolete.
                final String newTaxid = validBioSource.getTaxId();
                Collection<BioSource> bioSources = helper.search( BioSource.class, "taxId", newTaxid );

                switch ( bioSources.size() ) {
                    case 0:
                        // doesn't exists, so create it.
                        if ( logger != null ) {
                            logger.info( "Creating new bioSource(" + newTaxid + ")." );
                        }
                        helper.create( validBioSource );
                        newBioSource = validBioSource;

                        // cache the new taxid as well.
                        bioSourceCache.put( newTaxid, newBioSource );
                        break;

                    case 1:
                        // it exists, try to update it.
                        BioSource intactBs = (BioSource) bioSources.iterator().next();
                        if ( logger != null ) {
                            logger.info( "Updating existing BioSource (" + newTaxid + ")" );
                        }
                        newBioSource = updateBioSource( intactBs, validBioSource );

                        // cache the new taxid as well.
                        bioSourceCache.put( newTaxid, newBioSource );
                        break;

                    default:
                        // more than one !
                        if ( logger != null ) {
                            logger.error( "More than one BioSource with this taxId found: " + aTaxId +
                                          ". Check for the original one." );
                        }

                        newBioSource = getOriginalBioSource( bioSources ); // fail if more than one !
                }
            }

        } else {

            // BioSource found in IntAct AND in Newt.

            if ( !intactBioSource.equals( validBioSource ) ) {
                if ( logger != null ) {
                    logger.info( "Updating existing BioSource (" + validBioSource.getTaxId() + ")" );
                }
                if ( validBioSource.getTaxId().equals( aTaxId ) ) {

                    // given taxid was ok
                    newBioSource = updateBioSource( intactBioSource, validBioSource );

                } else {

                    // The given taxid was obsolete.
                    // (!) It could be a problem if the taxid was obsolete, and there is already a biosource
                    // in intact with the new taxid. In which case, we can't just update or two BioSources
                    // will have the same taxid.
                    final String newTaxid = validBioSource.getTaxId();
                    Collection<BioSource> bioSources = helper.search( BioSource.class, "taxId", newTaxid );

                    switch ( bioSources.size() ) {
                        case 0:
                            // doesn't exists, so create it.
                            if ( logger != null ) {
                                logger.info( "Creating new bioSource(" + newTaxid + ")." );
                            }
                            helper.create( validBioSource );
                            newBioSource = validBioSource;

                            try {
                                // retreive required objects
                                CvDatabase newt = getNewt();
                                CvXrefQualifier identity = getIdentity();

                                // create identity Newt Xref
                                Xref xref = new Xref( institution, newt, newTaxid, null, null, identity );
                                newBioSource.addXref( xref );

                                // persist changes
                                helper.update( xref );

                            } catch ( IntactException e ) {
                                if ( logger != null ) {
                                    logger.error( "An error occured when trying to add Newt Xref to " + newBioSource, e );
                                }
                            }

                            break;

                        case 1:
                            // it exists, try to update it.
                            BioSource intactBs = (BioSource) bioSources.iterator().next();
                            if ( logger != null ) {
                                logger.info( "Updating existing BioSource (" + newTaxid + ")" );
                            }
                            newBioSource = updateBioSource( intactBs, validBioSource );

                            break;

                        default:
                            // more than one !
                            if ( logger != null ) {
                                logger.error( "More than one BioSource with this taxId found: " + aTaxId +
                                              ". Check for the original one." );
                            }

                            BioSource original = getOriginalBioSource( bioSources ); // fail if more than one !
                            newBioSource = updateBioSource( original, validBioSource );
                    }

                    // cache the new taxid as well.
                    bioSourceCache.put( newTaxid, newBioSource );
                }

            } else {
                // intact biosource was up-to-date
                newBioSource = intactBioSource;
            }
        }

        // The bioSourceCache will also contain associations from obsolete taxIds to valid
        // BioSource objects to avoid looking up the same obsolete Id over and over again.
        bioSourceCache.put( aTaxId, newBioSource );

        return newBioSource;
    } // getValidBioSource


    /**
     * Gives a valid taxid.
     *
     * @param taxid the original taxid
     *
     * @return a valid taxid (can be different from the original in case of obsoletness).
     *
     * @throws IntactException if an error occur when accessing IntAct or an inconstistancy.
     */
    public String getUpToDateTaxid( final String taxid ) throws IntactException {

        if ( taxid == null ) {
            return null;
        }
        BioSource validBioSource = getValidBioSource( taxid );
        return validBioSource.getTaxId();
    }


    /**
     * Update the given BioSource with data taken from Newt.<br> it assumes that the taxid is existing in the given
     * BioSource.
     *
     * @param taxid the taxid from which we want to get a Biosource
     *
     * @return an updated BioSource or null
     */
    private BioSource getNewtBiosource( String taxid ) throws IntactException {

        if ( taxid == null ) {
            return null;
        }

        if ( logger != null ) {
            logger.info( "Try to get BioSource data from Newt" );
        }
        NewtServerProxy.NewtResponse response = null;

        try {
            response = newtProxy.query( Integer.parseInt( taxid ) );
        } catch ( IOException e ) {
            if ( logger != null ) {
                logger.error( "Could not access the Newt web server.", e );
            }
            return null;
        } catch ( NumberFormatException e ) {
            if ( logger != null ) {
                logger.error( "invalid taxid: " + taxid, e );
            }
            return null;
        } catch ( NewtServerProxy.TaxIdNotFoundException e ) {
            if ( logger != null ) {
                logger.error( "taxId not found from Newt: " + taxid, e );
            }
            return null;
        }

        // the taxId can be different in obsoleteness case.
        BioSource bioSource = new BioSource( institution,
                                             response.getShortLabel().toLowerCase(),
                                             "" + response.getTaxId() );
        bioSource.setFullName( response.getFullName() );

        // add xref to it !

        // retreive required objects
        CvDatabase newt = getNewt();
        CvXrefQualifier identity = getIdentity();

        // create identity Newt Xref
        Xref xref = new Xref( institution, newt, "" + response.getTaxId(), null, null, identity );
        bioSource.addXref( xref );

        // Note: We do not persist that Xref as it will be used for checking against IntAct data.

        return bioSource;
    }


    /**
     * Try to update an existing IntAct BioSource from an other.
     * <p/>
     * Only the taxid is possibly updated. </p>
     *
     * @param bioSource     the IntAct BioSource
     * @param newtBioSource the one from which we get the up-to-date data
     *
     * @return an up-to-date IntAct BioSource
     *
     * @throws IntactException
     */
    private BioSource updateBioSource( BioSource bioSource,
                                       BioSource newtBioSource ) throws IntactException {

        boolean needUpdate = false;

        // compare these two BioSources and update in case of differences
        String newtTaxid = newtBioSource.getTaxId();
        if ( false == bioSource.getTaxId().equals( newtTaxid ) ) {
            bioSource.setTaxId( newtTaxid );
            if ( logger != null ) {
                logger.debug( "Obsolete taxid: taxid " + bioSource.getTaxId() +
                              " becomes " + newtTaxid );
            }
            needUpdate = true;
        }

        // retreive required objects
        CvDatabase newt = getNewt();
        CvXrefQualifier identity = getIdentity();

        // get the Newt/identity Xref
        // Note: if the BioSource lacks the identity Xref, we create it !
        boolean foundNewtIdentityXref = false;
        for ( Iterator iterator = bioSource.getXrefs().iterator(); iterator.hasNext() && foundNewtIdentityXref == false; ) {
            Xref xref = (Xref) iterator.next();

            CvXrefQualifier qualifier = xref.getCvXrefQualifier();

            if ( xref.getCvDatabase().equals( newt ) &&
                 ( qualifier != null && qualifier.equals( identity ) ) ) {
                // found it !
                foundNewtIdentityXref = true;
                if ( false == xref.getPrimaryId().equals( newtBioSource.getTaxId() ) ) {

                    if ( logger != null ) {
                        logger.debug( "The identity Xref for that BioSource was not set to the correct taxid, updating it..." );
                    }

                    xref.setPrimaryId( newtBioSource.getTaxId() );

                    // update the Xref.
                    helper.update( xref );

                    needUpdate = true;
                }
            }
        }

        if ( false == foundNewtIdentityXref ) {

            if ( logger != null ) {
                logger.debug( "The identity Xref for that BioSource was missing, creating it..." );
            }

            Xref xref = new Xref( institution, newt, newtBioSource.getTaxId(), null, null, identity );
            bioSource.addXref( xref );

            // persist changes
            helper.create( xref );
        }


        /**
         * The IntAct shortlabel and fullName has to be maintained.
         * eg. for the taxid 4932 we have the shortlabel 'yeast' in the database where Newt have 's cerevisae'
         * If the shortlabel has been changed via the editor, that change is maintained.
         */

        if ( needUpdate ) {

            if ( logger != null ) {
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


    public static void main( String[] args ) throws IntactException, SQLException, LookupException {
        // TODO global update of all existing bioSource here !!
    }

} // BioSourceFactory
