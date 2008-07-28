/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.smallmolecule;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Small molecule have to have a CHEBI identity.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = SmallMolecule.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })

public class SmallMoleculeIdentity implements Rule<SmallMolecule> {

    public Collection<GeneralMessage> check( SmallMolecule smallMolecule ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        final Collection<InteractorXref> identities = XrefUtils.getIdentityXrefs( smallMolecule );
        switch( identities.size() ) {
            case 0:
                messages.add( new GeneralMessage( MessageDefinition.SMALL_MOLECULE_IDENTITY_MISSING, smallMolecule ) );
                break;

            case 1:
                final InteractorXref xref = identities.iterator().next();
                if ( ! CvObjectUtils.hasIdentity( xref.getCvDatabase(), CvDatabase.CHEBI_MI_REF ) ) {
                    messages.add( new GeneralMessage(MessageDefinition.SMALL_MOLECULE_IDENTITY_INVALID_DB, smallMolecule ) );
                }
                break;

            default:
                // more than 1
                messages.add( new GeneralMessage( MessageDefinition.SMALL_MOLECULE_IDENTITY_MULTIPLE, smallMolecule ) );
        }

        return messages;
    }
}