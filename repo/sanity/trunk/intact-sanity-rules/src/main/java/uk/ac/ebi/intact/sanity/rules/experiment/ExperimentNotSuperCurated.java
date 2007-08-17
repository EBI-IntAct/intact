/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.apt.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.sanity.rules.util.CommonMethods;
import uk.ac.ebi.intact.sanity.rules.util.MethodArgumentValidator;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Experiment.class)

public class ExperimentNotSuperCurated  implements Rule {
    private static final String DESCRIPTION = "This/those experiments have not been super curated";
    private static final String SUGGESTION = "Ask a super-creator to add and accepted or to-be-reviewed stamp on it.";
    private static Date startingDateSuperCuration;

    static{
        Calendar calendar = new GregorianCalendar();
        calendar.set(2005, Calendar.SEPTEMBER, 1);
        startingDateSuperCuration = calendar.getTime();

    }

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Experiment.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Experiment experiment = (Experiment) intactObject;

        if(startingDateSuperCuration.before(experiment.getCreated())){
            if(!CommonMethods.isAccepted(experiment) && !CommonMethods.isToBeReviewed(experiment)){
                messages.add(new GeneralMessage(DESCRIPTION,GeneralMessage.AVERAGE_LEVEL,SUGGESTION, experiment));    
            }
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