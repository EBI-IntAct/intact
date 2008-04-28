/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ExperimentNotSuperCurated Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class ExperimentNotSuperCuratedTest extends IntactBasicTestCase {

//    @Test
//    public void check() throws Exception {
//        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();
//
//        // Give the check method an experiment newer then september 2005 and super-curated, check that it return no error
//        // message.
//        Experiment experiment = ButkevitchMock.getMock();
//        Collection<GeneralMessage> messages = rule.check( experiment );
//        assertEquals( 0, messages.size() );
//
//        // Give the check method an experiment newer then september 2005 and not super-curated, check that it returns an
//        // error message.
//        Collection<Annotation> annotations = new ArrayList<Annotation>();
//        experiment.setAnnotations( annotations );
//        messages = rule.check( experiment );
//        assertEquals( 1, messages.size() );
//        for ( GeneralMessage message : messages ) {
//            assertEquals( MessageDefinition.EXPERIMENT_NOT_SUPER_CURATED, message.getMessageDefinition() );
//        }
//
//        // Give the check method an experiment older then september 2005 and not super-curated, check that it returns no
//        // error message.
//        Calendar calendar = new GregorianCalendar();
//        calendar.set( 2003, Calendar.SEPTEMBER, 1 );
//        Date createdDate = calendar.getTime();
//        experiment.setCreated( createdDate );
//        messages = rule.check( experiment );
//        assertEquals( 0, messages.size() );
//    }

    @Test
    public void check_to_be_reviewed() throws Exception {
        Calendar calendar = new GregorianCalendar();
        calendar.set( 2005, Calendar.SEPTEMBER, 2 );
        Date sept2005 = calendar.getTime();

        final Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setCreated( sept2005 );

        final Institution owner = getMockBuilder().getInstitution();
        final CvTopic toBeReviewed = new CvTopic( owner, CvTopic.TO_BE_REVIEWED );
        final CvTopic reviewer = new CvTopic( owner, CvTopic.REVIEWER );

        exp.addAnnotation( getMockBuilder().createAnnotation( "", toBeReviewed ) );
        exp.addAnnotation( getMockBuilder().createAnnotation( "Sam", reviewer ) );

        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();

        Collection<GeneralMessage> messages = rule.check( exp );
        assertEquals( 1, messages.size() );
        assertEquals( MessageDefinition.EXPERIMENT_TO_BE_REVIEWED, messages.iterator().next().getMessageDefinition() );

        CvTopic accepted = new CvTopic( owner, CvTopic.ACCEPTED );
        exp.addAnnotation( getMockBuilder().createAnnotation( "", accepted ) );
        messages = rule.check( exp );
        assertEquals( 0, messages.size() );
    }

    @Test
    public void check_to_be_reviewed_2() throws Exception {
        Calendar calendar = new GregorianCalendar();
        calendar.set( 2005, Calendar.AUGUST, 31 ); // only experiment curated after this date are checked on
        Date sept2005 = calendar.getTime();

        final Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setCreated( sept2005 );

        final Institution owner = getMockBuilder().getInstitution();
        final CvTopic toBeReviewed = new CvTopic( owner, CvTopic.TO_BE_REVIEWED );
        final CvTopic reviewer = new CvTopic( owner, CvTopic.REVIEWER );

        exp.addAnnotation( getMockBuilder().createAnnotation( "", toBeReviewed ) );
        exp.addAnnotation( getMockBuilder().createAnnotation( "Sam", reviewer ) );

        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();

        Collection<GeneralMessage> messages = rule.check( exp );
        assertEquals( 0, messages.size() );
    }

    @Test
    public void check_to_be_super_curated() throws Exception {
        Calendar calendar = new GregorianCalendar();
        calendar.set( 2005, Calendar.SEPTEMBER, 2 );
        Date sept2005 = calendar.getTime();

        final Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setCreated( sept2005 );

        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();

        Collection<GeneralMessage> messages = rule.check( exp );
        assertEquals( 1, messages.size() );
        assertEquals( MessageDefinition.EXPERIMENT_NOT_SUPER_CURATED, messages.iterator().next().getMessageDefinition() );

        final Institution owner = getMockBuilder().getInstitution();
        final CvTopic accepted = new CvTopic( owner, CvTopic.ACCEPTED );
        exp.addAnnotation( getMockBuilder().createAnnotation( "", accepted ) );
        messages = rule.check( exp );
        assertEquals( 0, messages.size() );
    }

    @Test
    public void check_to_be_super_curated_2() throws Exception {
        Calendar calendar = new GregorianCalendar();
        calendar.set( 2005, Calendar.AUGUST, 31 ); // only experiment curated after this date are checked on
        Date sept2005 = calendar.getTime();

        final Experiment exp = getMockBuilder().createExperimentEmpty();
        exp.setCreated( sept2005 );

        ExperimentNotSuperCurated rule = new ExperimentNotSuperCurated();

        Collection<GeneralMessage> messages = rule.check( exp );
        assertEquals( 0, messages.size() );
    }
}