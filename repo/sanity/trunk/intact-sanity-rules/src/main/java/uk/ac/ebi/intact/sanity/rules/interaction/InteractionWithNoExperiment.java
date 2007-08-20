/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.util.MethodArgumentValidator;

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

public class InteractionWithNoExperiment implements Rule {

    private static final String DESCRIPTION = "This/these Interaction(s) are not attached to any experiment. ";
    private static final String SUGGESTION = "Delete the Interaction(s) or attach them to an Experiment.";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityRuleException {
        MethodArgumentValidator.isValidArgument(intactObject, Interaction.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Interaction interaction = (Interaction) intactObject;
        Collection<Experiment> experiments = interaction.getExperiments();
        if(experiments.size() == 0){
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