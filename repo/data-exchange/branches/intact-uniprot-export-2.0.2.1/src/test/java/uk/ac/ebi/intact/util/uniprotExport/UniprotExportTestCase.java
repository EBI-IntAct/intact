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
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.config.impl.EssentialCvPrimer;

/**
 * Abstract test that sets up the database for the uniprot export tests.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class UniprotExportTestCase extends IntactBasicTestCase {

    @After
    public void tearDown() throws Exception {
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }

    @Before
    public void setUp() throws Exception {
        SchemaUtils.createSchema( true );
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        EssentialCvPrimer cvPrimer = new EssentialCvPrimer( daoFactory ){
            @Override
            public void createCVs() {
                super.createCVs();

                IntactMockBuilder builder = new IntactMockBuilder( );
                final CvDatabase uniprotkb = builder.createCvObject( CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT );
                final CvXrefQualifier isoformParent = builder.createCvObject( CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF, CvXrefQualifier.ISOFORM_PARENT );
                final CvTopic noUniprotUpdate = builder.createCvObject( CvTopic.class, null, CvTopic.NON_UNIPROT );
                final CvTopic negative = builder.createCvObject( CvTopic.class, null, CvTopic.NEGATIVE );
                final CvTopic ccNote = builder.createCvObject( CvTopic.class, null, "uniprot-cc-note" );
                final CvAliasType locusName = builder.createCvObject( CvAliasType.class, CvAliasType.LOCUS_NAME_MI_REF, "locus name" );
                final CvAliasType orfName = builder.createCvObject( CvAliasType.class, CvAliasType.ORF_NAME_MI_REF, "orf name" );

                PersisterHelper.saveOrUpdate( uniprotkb, isoformParent, noUniprotUpdate, negative, ccNote, locusName, orfName );
            }
        };
        cvPrimer.createCVs();
    }

    protected Protein createProteinChain( Protein masterProt, String uniprotId, String shortLabel ) {

        Protein chain = getMockBuilder().createProtein(uniprotId, shortLabel);

        if (masterProt.getAc() == null) {
            throw new IllegalArgumentException("Cannot create an chain if the master protein does not have an AC: "+masterProt.getShortLabel());
        }

        CvXrefQualifier isoformParent = getMockBuilder().createCvObject(CvXrefQualifier.class, "MI:0951", "chain-parent" );
        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        InteractorXref isoformXref = getMockBuilder().createXref(chain, masterProt.getAc(), isoformParent, intact);
        chain.addXref(isoformXref);

        return chain;
    }
}
