/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.relevancescore;

import org.junit.Test;
import org.junit.Assert;
import org.apache.solr.common.SolrInputDocument;

import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;
import uk.ac.ebi.intact.psimitab.mock.IntactPsimiTabMockBuilder;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;


/**
 * Test class for IntactSolrRelevanceScoreCalculator.java
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class IntactSolrRelevanceScoreCalculatorTest {

    @Test
    public void testRelevanceScore() throws Exception {

        IntactPsimiTabMockBuilder mockBuilder = new IntactPsimiTabMockBuilder();
        final IntactBinaryInteraction interaction = ( IntactBinaryInteraction ) mockBuilder.createInteractionRandom();

        final ExtendedInteractor interactorA = interaction.getInteractorA();
        final ExtendedInteractor interactorB = interaction.getInteractorB();

        //add biological roles    B1, B2
        CrossReference bioRole_A = new CrossReferenceImpl("psi-mi","MI:0499","unspecified role");
        interactorA.setBiologicalRoles( getAsCollection(bioRole_A ));

        CrossReference bioRole_B = new CrossReferenceImpl("psi-mi","MI:0499","unspecified role");
        interactorB.setBiologicalRoles( getAsCollection(bioRole_B ));

        //add experimental roles  E1, E2
        CrossReference expRole_A = new CrossReferenceImpl("psi-mi","MI:0498","prey");
        interactorA.setExperimentalRoles(getAsCollection( expRole_A ));

        CrossReference expRole_B = new CrossReferenceImpl("psi-mi","MI:0496","bait");
        interactorB.setExperimentalRoles( getAsCollection(expRole_B ));

        //add interactor type    T1, T2
        CrossReference protein1 = new CrossReferenceImpl("psi-mi","MI:0326","protein");
        interactorA.setInteractorType( protein1 );
        CrossReference protein2 = new CrossReferenceImpl("psi-mi","MI:0326","protein");
        interactorB.setInteractorType( protein2 );

        //add Names           N1, N2
        CrossReference name_A = new CrossReferenceImpl("intact","blabla","gene name synonym");
        interactorA.setAliases( null );
        interactorA.setAlternativeIdentifiers( getAsCollection(name_A ));

        CrossReference name_B = new CrossReferenceImpl("intact","klakla","go synonym");
        interactorB.setAliases( null );
        interactorB.setAlternativeIdentifiers( getAsCollection(name_B ));

        //Important: Properties has to be set and passed to IntactSolrRelevanceScoreCalculator constructor
        Properties rscProperties = getTestProperties();
        IntactSolrRelevanceScoreCalculator rscSolr = new IntactSolrRelevanceScoreCalculator( rscProperties);

        String score1 = rscSolr.calculateScore( interaction );
        /**
         * NN-unspecified-role B1,B2
         * BC - scores for bait,prey
         * DD - scores for protein, protein
         */
         Assert.assertEquals("NNBCDDblablaklakla",score1);


        //Again modify the interactors

        //add biological roles    B1, B2
        CrossReference enzyme = new CrossReferenceImpl("psi-mi","MI:0501","enzyme");
        interactorA.setBiologicalRoles( getAsCollection(enzyme ));

        CrossReference enzymetarget = new CrossReferenceImpl("psi-mi","MI:0502","enzyme target");
        interactorB.setBiologicalRoles( getAsCollection(enzymetarget ));

        CrossReference dna = new CrossReferenceImpl("psi-mi","MI:0318","nucleicacid");
        interactorB.setInteractorType( dna );

        String score2 =  rscSolr.calculateScore( interaction );
        //BB: enzyme, enzyme target
        Assert.assertEquals("BBBCDEblablaklakla",score2);

        //Test with SolrInputDocument
        SolrDocumentConverter converter = new SolrDocumentConverter( );
        final SolrInputDocument inputDocument = converter.toSolrDocument( interaction );
        final String score3 = rscSolr.calculateScore( inputDocument, converter );
        Assert.assertEquals("BBBCDEblablaklakla",score3);
    }

    private List<CrossReference> getAsCollection( CrossReference role ) {
        List<CrossReference> roles = new ArrayList<CrossReference>();
        roles.add( role );
        return roles;
    }

    public static Properties getTestProperties() throws Exception {
        Properties properties = new Properties();
        properties.load( IntactSolrRelevanceScoreCalculatorTest.class.getResourceAsStream( "/relevancescore/relevancescoretest.properties" ) );
        return properties;
    }


}
