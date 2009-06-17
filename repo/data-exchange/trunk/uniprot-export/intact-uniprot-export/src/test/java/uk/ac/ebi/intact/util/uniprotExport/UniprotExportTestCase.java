/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import org.junit.After;
import org.junit.Before;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.config.CvPrimer;
import uk.ac.ebi.intact.core.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvAliasType;

/**
 * Abstract test that sets up the database for the uniprot export tests.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class UniprotExportTestCase extends IntactBasicTestCase {


    @Before
    public void setUp() throws Exception {
        IntactMockBuilder builder = new IntactMockBuilder();
        final CvDatabase uniprotkb = builder.createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);
        final CvXrefQualifier isoformParent = builder.createCvObject(CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF, CvXrefQualifier.ISOFORM_PARENT);
        final CvTopic noUniprotUpdate = builder.createCvObject(CvTopic.class, null, CvTopic.NON_UNIPROT);
        final CvTopic negative = builder.createCvObject(CvTopic.class, null, CvTopic.NEGATIVE);
        final CvTopic ccNote = builder.createCvObject(CvTopic.class, null, "uniprot-cc-note");
        final CvAliasType locusName = builder.createCvObject(CvAliasType.class, CvAliasType.LOCUS_NAME_MI_REF, "locus name");
        final CvAliasType orfName = builder.createCvObject(CvAliasType.class, CvAliasType.ORF_NAME_MI_REF, "orf name");

        PersisterHelper.saveOrUpdate(uniprotkb, isoformParent, noUniprotUpdate, negative, ccNote, locusName, orfName);
    }
}
