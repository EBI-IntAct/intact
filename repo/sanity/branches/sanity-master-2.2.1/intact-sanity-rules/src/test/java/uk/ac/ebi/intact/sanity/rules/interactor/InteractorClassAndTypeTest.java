package uk.ac.ebi.intact.sanity.rules.interactor;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.rules.InteractorMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * InteractorClassAndType Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class InteractorClassAndTypeTest {

    @Test
    public void check_null_type() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Protein protein = mockBuilder.createProteinRandom();
        protein.setCvInteractorType( null );
        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_MISSING_TYPE,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_type_has_mi() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Protein protein = mockBuilder.createProteinRandom();
        // remove MI identity !!
        protein.getCvInteractorType().getXrefs().clear();

        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_INVALID_TYPE,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_protein_mismatch() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Protein protein = mockBuilder.createProteinRandom();
        protein.setCvInteractorType( mockBuilder.createCvObject( CvInteractorType.class,
                                                                 CvInteractorType.INTERACTION_MI_REF,
                                                                 CvInteractorType.INTERACTION ) );
        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_protein() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Protein protein = mockBuilder.createProteinRandom();

        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_nucleicacid_mismatch() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final NucleicAcid na = mockBuilder.createNucleicAcidRandom();
        System.out.println( "na = " + na );
        na.setCvInteractorType( mockBuilder.createCvObject( CvInteractorType.class,
                                                            CvInteractorType.INTERACTION_MI_REF,
                                                            CvInteractorType.INTERACTION ) );
        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( na );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_smallmolecule_mismatch() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        sm.setCvInteractorType( mockBuilder.createCvObject( CvInteractorType.class,
                                                            CvInteractorType.INTERACTION_MI_REF,
                                                            CvInteractorType.INTERACTION ) );
        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( sm );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_interaction_mismatch() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Interaction interaction = mockBuilder.createInteractionRandomBinary();
        interaction.setCvInteractorType( mockBuilder.createCvObject( CvInteractorType.class,
                                                                     CvInteractorType.PROTEIN_MI_REF,
                                                                     CvInteractorType.PROTEIN ) );
        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( interaction );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH,
                             messages.iterator().next().getMessageDefinition() );
    }

    public class AlienProtein extends InteractorImpl {
        // this is not supported by the rule !!
        public AlienProtein( Institution owner, String shortLabel, CvInteractorType type ) {
            super( shortLabel, owner, type );
        }
    }

    @Test
    public void check_unsupported_interactor() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final CvInteractorType proteinType = mockBuilder.createCvObject( CvInteractorType.class,
                                                                         CvInteractorType.PROTEIN_MI_REF,
                                                                         CvInteractorType.PROTEIN );

        final AlienProtein ap = new AlienProtein( new Institution( "Mars" ), "popipo999", proteinType );

        Rule rule = new InteractorClassAndType();
        final Collection<InteractorMessage> messages = rule.check( ap );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTOR_WITH_TYPE_MISMATCH,
                             messages.iterator().next().getMessageDefinition() );
    }
}
