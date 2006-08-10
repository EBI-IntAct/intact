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
import junit.framework.TestCase;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class ExperimentListGeneratorTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ExperimentListGeneratorTest.class);

    public void testGenerateListGavin()
    {

        ExperimentListGenerator gen = new ExperimentListGenerator("gavin%");

        List<ExperimentListItem> eliSpecies = gen.generateClassificationBySpecies();
        log.debug("By species: "+eliSpecies.size());

        assertEquals(3, eliSpecies.size());
        assertEquals("yeast_small-01.xml gavin-2006-1", eliSpecies.get(0).toString());
        assertEquals("yeast_small-02.xml gavin-2002-1", eliSpecies.get(1).toString());
        assertEquals("yeast_small-03.xml gavin-2006-2", eliSpecies.get(2).toString());


        List<ExperimentListItem> eliPublications = gen.generateClassificationByPublications();
        log.debug("By publications: "+eliPublications.size());

        assertEquals(3, eliPublications.size());
        assertEquals("2003/11805826.xml gavin-2002-1", eliPublications.get(0).toString());
        assertEquals("2005/16429126-01.xml gavin-2006-1", eliPublications.get(1).toString());
        assertEquals("2005/16429126-02.xml gavin-2006-2", eliPublications.get(2).toString());

        Set<Experiment> negativeExps = gen.getNegativeExperiments();
        assertEquals(0, negativeExps.size());

    }

    public void testNegativeExperimentCount()
    {

        ExperimentListGenerator gen = new ExperimentListGenerator("%-2004-%");

        Set<Experiment> negativeExps = gen.getNegativeExperiments();
        assertEquals(1, negativeExps.size());

    }

    public void testOnlyPmidTrue()
    {

        ExperimentListGenerator gen = new ExperimentListGenerator("human");
        gen.setOnlyWithPmid(true);

        List<ExperimentListItem> eliPublications = gen.generateClassificationByPublications();
        assertEquals(0, eliPublications.size());
        log.debug("By publications (onlyPmid=true): "+eliPublications.size());

        gen = new ExperimentListGenerator("human");
        gen.setOnlyWithPmid(false);

        eliPublications = gen.generateClassificationByPublications();
        assertEquals(1, eliPublications.size());
        log.debug("By publications (onlyPmid=false): "+eliPublications.size());

    }

}
