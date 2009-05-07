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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.SmallMolecule;
import uk.ac.ebi.intact.model.util.ProteinUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */

public class InteractorEnricherTest extends IntactBasicTestCase {

    private InteractorEnricher enricher;

    @Before
    public void beforeMethod() {
        enricher = InteractorEnricher.getInstance();
    }

    @After
    public void afterMethod() {
        enricher.close();
        enricher = null;
    }


    @Test
    public void enrich_uniprot() {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        Protein protein = getMockBuilder().createProtein("P18850", "unknownName", human);

        enricher.enrich(protein);

        Assert.assertEquals("atf6a_human", protein.getShortLabel());
        Assert.assertEquals("Cyclic AMP-dependent transcription factor ATF-6 alpha", protein.getFullName());
        Assert.assertNotNull(protein.getSequence());
    }

    @Test
    public void enrich_chebi(){
        final SmallMolecule smallMolecule = getMockBuilder().createSmallMolecule( "CHEBI:15367", "unknownShortName" );
        Assert.assertEquals(0,smallMolecule.getAnnotations().size());

        enricher.enrich( smallMolecule );

        Assert.assertEquals("all-trans-retinoic acid",smallMolecule.getShortLabel());
        Assert.assertEquals("inchi id",smallMolecule.getAnnotations().iterator().next().getCvTopic().getShortLabel());
    }

    @Test
    public void enrich_uniprot_noXrefs() {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        Protein protein = getMockBuilder().createProtein("P18850", "atf6a_human", human);
        protein.getXrefs().clear();

        enricher.enrich(protein);

        Assert.assertEquals("atf6a_human", protein.getShortLabel());
        Assert.assertEquals("Cyclic AMP-dependent transcription factor ATF-6 alpha", protein.getFullName());

        Assert.assertFalse(protein.getXrefs().isEmpty());
    }

    @Test
    public void enrich_alias() {
        BioSource ecoli = getMockBuilder().createBioSource(561, "ecoli");
        Protein protein = getMockBuilder().createProtein("P45530", "tusb_ecoli", ecoli);
        protein.getAliases().clear();

        Assert.assertNotSame("tusB", ProteinUtils.getGeneName(protein));

        enricher.enrich(protein);

        Assert.assertEquals("tusB", ProteinUtils.getGeneName(protein));
    }

    @Test
    public void enrich_invalidLabel() {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        Protein protein = getMockBuilder().createProtein("EBI-12345", "EBI-12345", human);
        protein.getXrefs().iterator().next().setCvDatabase(
                getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT));

        enricher.enrich(protein);

        Assert.assertEquals("EBI12345", protein.getShortLabel());
    }

    @Test
    public void enrich_uniprot_biosource() {
        BioSource lalaOrganism = getMockBuilder().createBioSource(50, "lala");
        Protein protein = getMockBuilder().createProtein("P18850", "unknownName", lalaOrganism);

        enricher.enrich(protein);

        Assert.assertEquals("atf6a_human", protein.getShortLabel());
        Assert.assertEquals("Cyclic AMP-dependent transcription factor ATF-6 alpha", protein.getFullName());
        Assert.assertNotNull(protein.getSequence());
        Assert.assertEquals("9606", protein.getBioSource().getTaxId());
        Assert.assertEquals("human", protein.getBioSource().getShortLabel());
    }

}