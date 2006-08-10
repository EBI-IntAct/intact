/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.intact.model.Experiment;

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

        ExperimentListGenerator gen = new ExperimentListGenerator("ab%");
        gen.setOnlyWithPmid(true);

        List<ExperimentListItem> eliSpecies = gen.generateClassificationBySpecies();
        System.out.println("By species: "+eliSpecies.size());

        List<ExperimentListItem> eliPublications = gen.generateClassificationByPublications();
        System.out.println("By publications: "+eliPublications.size());

        Set<Experiment> negativeExps = gen.getNegativeExperiments();
        System.out.println("Negative experiments: "+negativeExps.size());

        for (Experiment e : negativeExps)
        {
            System.out.println("\tNegative: "+e.getShortLabel());
        }

    }

}
