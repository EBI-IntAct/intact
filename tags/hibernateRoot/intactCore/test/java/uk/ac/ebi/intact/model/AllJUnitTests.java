/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.junit.runner.JUnitCore;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class AllJUnitTests
{  

    public static void main(String[] args)
    {
        JUnitCore.runClasses(InstitutionTest.class,
                             BioSourceTest.class,
                             ExperimentTest.class,
                             InteractionTest.class,
                             ComponentTest.class);
    }
}
