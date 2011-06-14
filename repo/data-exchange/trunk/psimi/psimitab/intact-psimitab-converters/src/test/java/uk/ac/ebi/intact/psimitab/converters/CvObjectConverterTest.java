package uk.ac.ebi.intact.psimitab.converters;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import psidev.psi.mi.tab.model.InteractionDetectionMethod;
import psidev.psi.mi.tab.model.InteractionDetectionMethodImpl;
import psidev.psi.mi.tab.model.InteractionType;
import psidev.psi.mi.tab.model.InteractionTypeImpl;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvInteractionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * CvObjectConverter Tester.
 *
 * @author Nadin Neuhauser
 * @version 1.0
 * @since <pre>11/13/2007</pre>
 */
public class CvObjectConverterTest extends IntactBasicTestCase {

    @Test
    @DirtiesContext
    public void convertToMitab_InteractionType() throws Exception {
        CvObjectConverter converter = new CvObjectConverter();

        CvInteractionType t = getMockBuilder().createCvObject( CvInteractionType.class, "MI:0407", "direct interaction" );
        InteractionType interactionType = ( InteractionType ) converter.toCrossReference( InteractionTypeImpl.class, t );
        assertNotNull( interactionType );
        assertEquals( "psi-mi", interactionType.getDatabase() );
        assertEquals( "MI:0407", interactionType.getIdentifier() );
        assertEquals( "direct interaction", interactionType.getText() );
    }

    @Test
    @DirtiesContext
    public void convertToMitab_InteractionDetectionMethod() throws Exception {
        CvObjectConverter converter = new CvObjectConverter();

        CvInteraction t = getMockBuilder().createCvObject( CvInteraction.class, "MI:0027", "cosedimentation" );

        InteractionDetectionMethod detectionMethod = ( InteractionDetectionMethod ) converter.toCrossReference( InteractionDetectionMethodImpl.class, t );
        assertNotNull( detectionMethod );
        assertEquals( "psi-mi", detectionMethod.getDatabase() );
        assertEquals( "MI:0027", detectionMethod.getIdentifier() );
        assertEquals( "cosedimentation", detectionMethod.getText() );
    }

}
