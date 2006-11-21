package uk.ac.ebi.intact.modelt;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 21-Nov-2006
 * Time: 15:49:40
 * To change this template use File | Settings | File Templates.
 */
public class InteractionTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testClone() throws Exception{
        InteractionDao interactionDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao();
        Interaction interaction = interactionDao.getByShortLabel("cara-7");

        if (interaction != null)
        {
            System.out.println(interaction.getAc());
        }
    }
}
