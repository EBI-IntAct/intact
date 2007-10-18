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

@SanityRule(target = Interaction.class)

public class InteractionWithNoComponent  implements Rule<Interaction> {

    private static final String DESCRIPTION = "This/these Interaction(s) do not have any Interactor. ";
    private static final String SUGGESTION = "Edit the Interaction and add component(s).";

    public Collection<GeneralMessage> check(Interaction interaction) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Collection<Component> components = interaction.getComponents();
        if(components.size() == 0){
            messages.add(new GeneralMessage(DESCRIPTION, MessageLevel.INFO, SUGGESTION, interaction));
        }
        return messages;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}