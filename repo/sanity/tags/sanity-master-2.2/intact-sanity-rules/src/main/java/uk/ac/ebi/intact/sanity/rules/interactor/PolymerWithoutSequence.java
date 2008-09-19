/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interactor;

import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Polymer;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.InteractorMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check on polymer without sequence.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Polymer.class, group = {RuleGroup.INTACT, RuleGroup.IMEX} )

public class PolymerWithoutSequence implements Rule<Polymer> {

    public Collection<GeneralMessage> check( Polymer polymer ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        final String sequence = polymer.getSequence();
        if ( sequence == null || sequence.trim().length() == 0 ) {

            messages.add( new InteractorMessage( MessageDefinition.POLYMER_WITHOUT_SEQUENCE, ( Interactor ) polymer ) );

        }
        return messages;
    }
}