/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ExperimentNotSuperCuratedTest {


    @Test
    public void check() throws Exception {
        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();

        // Give the check method an experiment newer then september 2005 and super-curated, check that it return no error
        // message.
        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages =  rule.check(experiment);
        assertEquals(0,messages.size());

        // Give the check method an experiment newer then september 2005 and not super-curated, check that it returns an
        // error message.
        Collection<Annotation> annotations = new ArrayList<Annotation>();
        experiment.setAnnotations(annotations);
        messages =  rule.check(experiment);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.EXPERIMENT_NOT_SUPER_CURATED, message.getMessageDefinition());
        }

        // Give the check method an experiment older then september 2005 and not super-curated, check that it returns no
        // error message.
        Calendar calendar = new GregorianCalendar();
        calendar.set(2003, Calendar.SEPTEMBER, 1);
        Date createdDate = calendar.getTime();
        experiment.setCreated(createdDate);
        messages =  rule.check(experiment);
        assertEquals(0,messages.size());
    }

}