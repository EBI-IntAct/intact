/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Interaction.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })
public class InteractionWithNoComponent  implements Rule<Interaction> {

    public Collection<GeneralMessage> check(Interaction interaction) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Collection<Component> components = interaction.getComponents();
        if(components.size() == 0){
            messages.add(new GeneralMessage(MessageDefinition.INTERACTION_WITHOUT_COMPONENT, interaction));
        }
        return messages;
    }
}