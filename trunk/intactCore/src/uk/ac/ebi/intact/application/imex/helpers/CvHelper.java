/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.imex.helpers;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;

/**
 * Utility methods for CVs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-May-2006</pre>
 */
public class CvHelper {

    //////////////////////////////
    // CvDatabase

    public static CvDatabase getPubmed( IntactHelper helper ) {
        CvDatabase pubmed = helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.PUBMED_MI_REF );
        if ( pubmed == null ) {
            throw new IllegalStateException( "Could not find PubMed." );
        }
        return pubmed;
    }

    public static CvDatabase getImex( IntactHelper helper ) {
        CvDatabase pubmed = helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.IMEX_MI_REF );
        if ( pubmed == null ) {
            throw new IllegalStateException( "Could not find CvDatabase(imex) by MI: " + CvDatabase.IMEX_MI_REF );
        }
        return pubmed;
    }

    public static CvDatabase getIntact( IntactHelper helper ) {
        CvDatabase intact = helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.INTACT_MI_REF );
        if ( intact == null ) {
            throw new IllegalStateException( "Could not find CvDatabase(intact) by MI: " + CvDatabase.INTACT_MI_REF );
        }
        return intact;
    }

    public static CvDatabase getPsi( IntactHelper helper ) {
        CvDatabase psi = helper.getObjectByPrimaryId( CvDatabase.class, CvDatabase.PSI_MI_MI_REF );
        if ( psi == null ) {
            throw new IllegalStateException( "Could not find CvDatabase(psi) by MI: " + CvDatabase.PSI_MI_MI_REF );
        }
        return psi;
    }

    //////////////////////////////
    // CvXrefQualifier

    public static CvXrefQualifier getPrimaryReference( IntactHelper helper ) {
        CvXrefQualifier primaryReference = helper.getObjectByPrimaryId( CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF );
        if ( primaryReference == null ) {
            throw new IllegalStateException( "Could not find CvXrefQualifier(primary-reference) by MI: " + CvXrefQualifier.PRIMARY_REFERENCE_MI_REF );
        }
        return primaryReference;
    }

    public static CvXrefQualifier getImexPrimary( IntactHelper helper ) {
        CvXrefQualifier imexPrimary = helper.getObjectByPrimaryId( CvXrefQualifier.class, CvXrefQualifier.IMEX_PRIMARY_MI_REF );
        if ( imexPrimary == null ) {
            throw new IllegalStateException( "Could not find CvXrefQualifier(imex-primary) by MI: " + CvXrefQualifier.IMEX_PRIMARY_MI_REF );
        }
        return imexPrimary;
    }

    public static CvXrefQualifier getIdentity( IntactHelper helper ) {
        CvXrefQualifier identity = helper.getObjectByPrimaryId( CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF );
        if ( identity == null ) {
            throw new IllegalStateException( "Could not find CvXrefQualifier(identity) by MI: " + CvXrefQualifier.IDENTITY_MI_REF );
        }
        return identity;
    }

    ///////////////////////////////
    // CvInteractorType

    public static CvInteractorType getProteinType( IntactHelper helper ) {
        CvInteractorType proteinType = helper.getObjectByPrimaryId( CvInteractorType.class, CvInteractorType.getProteinMI() );
        if ( proteinType == null ) {
            throw new IllegalStateException( "Could not find CvInteractorType by MI: " + CvInteractorType.getProteinMI() );
        }
        return proteinType;
    }

    ////////////////////////////////
    // CvTopic

    public static CvTopic getImexRangeRequested( IntactHelper helper ) throws IntactException {
        CvTopic imexRangeRequested = helper.getObjectByLabel( CvTopic.class, CvTopic.IMEX_RANGE_REQUESTED );
        if ( imexRangeRequested == null ) {
            throw new IllegalStateException( "Could not find CvTopic by name: " + CvTopic.IMEX_RANGE_REQUESTED );
        }
        return imexRangeRequested;
    }

    public static CvTopic getImexRangeAssigned( IntactHelper helper ) throws IntactException {
        CvTopic imexRangeAssigned = helper.getObjectByLabel( CvTopic.class, CvTopic.IMEX_RANGE_ASSIGNED );
        if ( imexRangeAssigned == null ) {
            throw new IllegalStateException( "Could not find CvTopic by name: " + CvTopic.IMEX_RANGE_ASSIGNED );
        }
        return imexRangeAssigned;
    }
}