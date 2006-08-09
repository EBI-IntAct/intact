/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class ExperimentListGeneratorTest
{

    private static final Log log = LogFactory.getLog(ExperimentListGeneratorTest.class);

    public void testGenerateList()
    {
        ExperimentListGenerator gen = new ExperimentListGenerator();
        gen.setSearchPattern("gavin-%");
        gen.setOverwrite(true);
        gen.setOnlyWithPmid(true);

        gen.execute();
    }

}
