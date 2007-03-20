/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.interaction;

import uk.ac.ebi.intact.util.sanity.rules.Rule;
import uk.ac.ebi.intact.util.sanity.rules.util.MethodArgumentValidator;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Component;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InteractionWithNoComponent  implements Rule {

    private static final String DESCRIPTION = "This/those Interaction(s) do not have any Interactor. ";
    private static final String SUGGESTION = "Edit the Interaction and add component(s).";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Interaction.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Interaction interaction = (Interaction) intactObject;
        Collection<Component> components = interaction.getComponents();
        if(components.size() == 0){
            messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.AVERAGE_LEVEL, SUGGESTION, interaction));
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