/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.nucleicacid;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule( target = NucleicAcid.class )

public class NucleicAcidIdentity implements Rule<NucleicAcid> {

    private static String NON_ALLOWED_IDENTITY_DESCRIPTION = "This/these NucleicAcid(s) have a not allowed " +
                                                             "identity xref ";
    private static final String NON_ALLOWED_IDENTITY_SUGGESTION = "Edit the NucleicAcid and change the CvXrefQualifier.";

    private static final String MULTIPLE_IDENTITY_DESCRIPTION = "This/these NucleicAcid(s) have more then one xref identity.";
    private static final String MULTIPLE_IDENTITY_SUGGESTION = "Edit the NucleicAcid and change the xrefs.";

    private static final String NO_IDENTITY_DESCRIPTION = "This/these NucleicAcid(s) have no identity xref.";
    private static final String NO_IDENTITY_SUGGESTION = "Edit the NucleicAcid(s) and add an identity xref.";

    private static Collection<String> cvDatabaseMis = new ArrayList<String>();

    static {
        cvDatabaseMis.add( CvDatabase.DDBG_MI_REF );
        cvDatabaseMis.add( CvDatabase.ENTREZ_GENE_MI_REF );
        cvDatabaseMis.add( CvDatabase.FLYBASE_MI_REF );
        cvDatabaseMis.add( CvDatabase.ENSEMBL_MI_REF );

        NON_ALLOWED_IDENTITY_DESCRIPTION = NON_ALLOWED_IDENTITY_DESCRIPTION + "(";
        for ( String mi : cvDatabaseMis ) {
            NON_ALLOWED_IDENTITY_DESCRIPTION = NON_ALLOWED_IDENTITY_DESCRIPTION + mi + " ";
        }
        NON_ALLOWED_IDENTITY_DESCRIPTION = NON_ALLOWED_IDENTITY_DESCRIPTION + ")";

    }

    public Collection<GeneralMessage> check( NucleicAcid nucleicAcid ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        Collection<InteractorXref> xrefs = nucleicAcid.getXrefs();
        int identityCount = 0;
        for ( InteractorXref xref : xrefs ) {
            if ( xref.getCvXrefQualifier() != null ) {
                CvObjectXref qualifierIdentity = CvObjectUtils.getPsiMiIdentityXref( xref.getCvXrefQualifier() );
                if ( qualifierIdentity != null && CvXrefQualifier.IDENTITY_MI_REF.equals( qualifierIdentity.getPrimaryId() ) ) {
                    CvObjectXref databaseIdentity = CvObjectUtils.getPsiMiIdentityXref( xref.getCvDatabase() );
                    if ( cvDatabaseMis.contains( databaseIdentity.getPrimaryId() ) ) {
                        identityCount++;
                    } else {
                        messages.add( new GeneralMessage( NON_ALLOWED_IDENTITY_DESCRIPTION, GeneralMessage.AVERAGE_LEVEL, NON_ALLOWED_IDENTITY_SUGGESTION, nucleicAcid ) );
                    }
                }
            }
        }
        if ( identityCount > 1 ) {
            messages.add( new GeneralMessage( MULTIPLE_IDENTITY_DESCRIPTION, MessageLevel.NORMAL, MULTIPLE_IDENTITY_SUGGESTION, nucleicAcid ) );
        } else if ( identityCount == 0 ) {
            messages.add( new GeneralMessage( NO_IDENTITY_DESCRIPTION, MessageLevel.NORMAL, NO_IDENTITY_SUGGESTION, nucleicAcid ) );
        }

        return messages;
    }


    public static String getMultipleIdentityDescription() {
        return MULTIPLE_IDENTITY_DESCRIPTION;
    }

    public static String getMultipleIdentitySuggestion() {
        return MULTIPLE_IDENTITY_SUGGESTION;
    }

    public static String getNoIdentityDescription() {
        return NO_IDENTITY_DESCRIPTION;
    }

    public static String getNoIdentitySuggestion() {
        return NO_IDENTITY_SUGGESTION;
    }

    public static String getNonAllowedIdentityDescription() {
        return NON_ALLOWED_IDENTITY_DESCRIPTION;
    }

    public static String getNonAllowedIdentitySuggestion() {
        return NON_ALLOWED_IDENTITY_SUGGESTION;
    }
}