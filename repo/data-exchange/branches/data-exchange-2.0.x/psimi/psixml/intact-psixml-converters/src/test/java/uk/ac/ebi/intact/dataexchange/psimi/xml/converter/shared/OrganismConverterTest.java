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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.mi.xml.model.Organism;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OrganismConverterTest extends IntactBasicTestCase {

    @Test
    public void psiToIntact_default() throws Exception {
        Organism organism = PsiMockFactory.createMockOrganism();

        OrganismConverter organismConverter = new OrganismConverter(getMockBuilder().getInstitution());
        BioSource bioSource = organismConverter.psiToIntact(organism);

        Assert.assertNotNull(bioSource);
        Assert.assertNotNull(bioSource.getOwner().getShortLabel());
    }

}