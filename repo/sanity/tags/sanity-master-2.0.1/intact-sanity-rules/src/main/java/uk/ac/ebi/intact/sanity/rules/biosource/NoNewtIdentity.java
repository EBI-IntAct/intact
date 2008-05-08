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
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check on Biosource having a Newt Xref.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = BioSource.class, group = RuleGroup.INTACT )
public class NoNewtIdentity implements Rule<BioSource> {

    public Collection<GeneralMessage> check( BioSource bs ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        int validIdentityXref = 0;
        for ( BioSourceXref bioSourceXref : bs.getXrefs() ) {

            String qualifierMi = bioSourceXref.getCvXrefQualifier().getMiIdentifier();

            if ( CvXrefQualifier.IDENTITY_MI_REF.equals( qualifierMi ) ) {
                String dbMi = bioSourceXref.getCvDatabase().getMiIdentifier();

                if ( CvDatabase.NEWT_MI_REF.equals( dbMi ) ) {
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