/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.rules.util.CommonMethods;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Experiment.class)

public class ExperimentNotSuperCurated  implements Rule<Experiment> {

    private static final Date startingDateSuperCuration;

    static{
        Calendar calendar = new GregorianCalendar();
        calendar.set(2005, Calendar.SEPTEMBER, 1);
        startingDateSuperCuration = calendar.getTime();
    }

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if(startingDateSuperCuration.before(experiment.getCreated())){
            if(!CommonMethods.isAccepted(experiment) && !CommonMethods.isToBeReviewed(experiment)){
                messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_NOT_SUPER_CURATED, experiment));
            }
        }

        return messages;
    }
}