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
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
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

    private static Collection<String> cvDatabaseMis = new ArrayList<String>();

    static {
        cvDatabaseMis.add( CvDatabase.DDBG_MI_REF );
        cvDatabaseMis.add( CvDatabase.ENTREZ_GENE_MI_REF );
        cvDatabaseMis.add( CvDatabase.FLYBASE_MI_REF );
        cvDatabaseMis.add( CvDatabase.ENSEMBL_MI_REF );
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
                        messages.add( new GeneralMessage(MessageDefinition.NUC_ACID_IDENTITY_INVALID_DB, nucleicAcid ) );
                    }
                }
            }
        }
        if ( identityCount > 1 ) {
            messages.add( new GeneralMessage( MessageDefinition.NUC_ACID_IDENTITY_MULTIPLE, nucleicAcid ) );
        } else if ( identityCount == 0 ) {
            messages.add( new GeneralMessage( MessageDefinition.NUC_ACID_IDENTITY_MISSING, nucleicAcid ) );
        }

        return messages;
    }

}