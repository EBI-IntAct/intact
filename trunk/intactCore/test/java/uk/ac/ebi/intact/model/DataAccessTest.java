/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.junit.Before;
import org.junit.After;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import junit.framework.TestCase;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class DataAccessTest
{
    private IntactHelper helper;

    @Before
    public void beforeTest() throws IntactException
    {
        helper = new IntactHelper();
    }

    @After
    public void afterTest() throws IntactException
    {
        helper.closeStore();
        helper = null;
    }

    protected IntactHelper getHelper()
    {
        return helper;
    }
}
