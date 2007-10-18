/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.biosource;

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
 * Check on Biosource having a Newt Xref.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = BioSource.class )

public class NoNewtIdentity implements Rule<BioSource> {

    public Collection<GeneralMessage> check( BioSource bs ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        int validIdentityXref = 0;
        for ( BioSourceXref bioSourceXref : bs.getXrefs() ) {

            CvObjectXref qualifierMi = CvObjectUtils.getPsiMiIdentityXref( bioSourceXref.getCvXrefQualifier() );

            if ( qualifierMi != null && CvXrefQualifier.IDENTITY_MI_REF.equals( qualifierMi.getPrimaryId() ) ) {
                CvObjectXref dbMi = CvObjectUtils.getPsiMiIdentityXref( bioSourceXref.getCvDatabase() );

                if ( CvDatabase.NEWT_MI_REF.equals( dbMi.getPrimaryId() ) ) {
                    validIdentityXref++;
                }
            }
        }

        if ( validIdentityXref == 0 ) {
            messages.add( new GeneralMessage( MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF, bs ) );
        }

        return messages;
    }
}