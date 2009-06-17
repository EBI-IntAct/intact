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
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.util.XrefUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */

public class InstitutionEnricherTest extends EnricherBasicTestCase {

    @Autowired
    private InstitutionEnricher enricher;

    @Test
    public void enrich_intact() {
        Institution ebi = new Institution("ebi");

        enricher.enrich(ebi);

        Assert.assertEquals("intact", ebi.getShortLabel());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, XrefUtils.getPsiMiIdentityXref(ebi).getPrimaryId());
    }

    @Test
    public void enrich_mint() {
        Institution mint = new Institution("mint");

        enricher.enrich(mint);

        Assert.assertEquals("mint", mint.getShortLabel());
        Assert.assertEquals(CvDatabase.MINT_MI_REF, XrefUtils.getPsiMiIdentityXref(mint).getPrimaryId());
    }
    
    @Test
    public void enrich_dip() {
        Institution dip = new Institution("ucla");

        enricher.enrich(dip);

        Assert.assertEquals("dip", dip.getShortLabel());
        Assert.assertEquals(CvDatabase.DIP_MI_REF, XrefUtils.getPsiMiIdentityXref(dip).getPrimaryId());
    }

}