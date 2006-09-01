/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Interaction;

import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class InteractionDaoTest extends TestCase
{

    private static final Log log = LogFactory.getLog(InteractionDaoTest.class);

    private InteractionDao interactionDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        interactionDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        interactionDao = null;
    }

    public void testGetInteractionsByInteractorAc()
    {
        List<Interaction> interactions = interactionDao.getInteractionsByInteractorAc("EBI-12331");
        assertNotNull(interactions);
        assertEquals("Expected 20 interactions for interactor: EBI-12231 ", 20, interactions.size());
    }

}
