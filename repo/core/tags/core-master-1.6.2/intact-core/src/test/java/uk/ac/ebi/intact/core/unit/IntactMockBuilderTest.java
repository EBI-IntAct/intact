package uk.ac.ebi.intact.core.unit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.model.Interaction;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactMockBuilderTest
{
    private IntactMockBuilder mockBuilder;

    @Before
    public void before() {
        mockBuilder = new IntactMockBuilder();
    }

    @After
    public void after() {
        mockBuilder = null;
    }

    @Test
    public void randomString_default() throws Exception {
        String randomString = mockBuilder.randomString(10);
        Assert.assertNotNull(randomString);
        Assert.assertEquals(10, randomString.length());
    }

    @Test
    public void createInteractionRandomBinary() throws Exception {
        Interaction interaction = mockBuilder.createInteractionRandomBinary();

        Assert.assertNotNull(interaction);
        Assert.assertNotNull(interaction.getShortLabel());
        Assert.assertEquals(2, interaction.getComponents().size());
    }
}
