/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.enricher.standard;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.dataexchange.enricher.EnricherBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvExperimentalPreparation;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentEnricherTest extends EnricherBasicTestCase {

    @Autowired
    private ComponentEnricher enricher;

    @Test
    public void enrich_default() throws Exception {
        Component comp = getMockBuilder().createInteractionRandomBinary().getComponents().iterator().next();
        comp.setExpressedIn(null);

        enricher.enrich(comp);

        Assert.assertNull(comp.getExpressedIn());
    }

    @Test
    public void enrich_expressedIn() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "unknown");
        Component comp = getMockBuilder().createInteractionRandomBinary().getComponents().iterator().next();
        comp.setExpressedIn(human);

        enricher.enrich(comp);

        Assert.assertEquals("human", comp.getExpressedIn().getShortLabel());
    }

    @Test
    public void enrich_cvs() throws Exception {
        Component comp = getMockBuilder().createComponentRandom();
        comp.getParticipantDetectionMethods().clear();
        comp.getExperimentalPreparations().clear();

        CvExperimentalPreparation cvExperimentalPrep = getMockBuilder().createCvObject(CvExperimentalPreparation.class, CvExperimentalPreparation.PURIFIED_REF, "nothing");
        cvExperimentalPrep.setFullName("nothing");

        comp.getExperimentalPreparations().add(cvExperimentalPrep);

        enricher.enrich(comp);

        CvExperimentalPreparation enrichedExperimentalPreparation = comp.getExperimentalPreparations().iterator().next();
        Assert.assertEquals(CvExperimentalPreparation.PURIFIED, enrichedExperimentalPreparation.getShortLabel());
    }
}