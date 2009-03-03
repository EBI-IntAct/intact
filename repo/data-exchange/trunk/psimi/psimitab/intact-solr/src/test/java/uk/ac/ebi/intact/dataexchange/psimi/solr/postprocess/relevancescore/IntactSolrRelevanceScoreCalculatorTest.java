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

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.mock.IntactPsimiTabMockBuilder;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;

import java.util.ArrayList;
import java.util.List;


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

        IntactRelevanceScoreCalculator rsc = new IntactRelevanceScoreCalculator();

        String score1 = rsc.calculateScore( interaction );
        /**
         * NN-unspecified-role B1,B2
         * BC - scores for bait,prey
         * DD - scores for protein, protein
         */
//         Assert.assertEquals("NNBCDDblablklakl",score1);


        //Again modify the interactors

        //add biological roles    B1, B2
        CrossReference enzyme = new CrossReferenceImpl("psi-mi","MI:0501","enzyme");
        interactorA.setBiologicalRoles( getAsCollection(enzyme ));

        CrossReference enzymetarget = new CrossReferenceImpl("psi-mi","MI:0502","enzyme target");
        interactorB.setBiologicalRoles( getAsCollection(enzymetarget ));

        CrossReference dna = new CrossReferenceImpl("psi-mi","MI:0318","nucleicacid");
        interactorB.setInteractorType( dna );

        String score2 =  rsc.calculateScore( interaction );
        //BB: enzyme, enzyme target
        Assert.assertEquals("BBBCDEblablklakl",score2);

        //Test with SolrInputDocument
        SolrDocumentConverter converter = new SolrDocumentConverter( );
        final SolrInputDocument inputDocument = converter.toSolrDocument( interaction );
        final String score3 = rsc.calculateScore( inputDocument, converter );
        Assert.assertEquals("BBBCDEblablklakl",score3);

    }

    @Test
    public void convertToFloatTest() throws Exception {
        IntactRelevanceScoreCalculator rsc = new IntactRelevanceScoreCalculator( );

        //first case normal
        String relevanceScore = "BBBCDEblablklakl";
        //convert Score to Float
        float boostScore = rsc.convertScoreToFloat( relevanceScore );
        String boostScoreString = String.valueOf( boostScore );
        Assert.assertEquals("6.666667E31",boostScoreString);

        //second case with name less than 10
        relevanceScore = "BBBCDEblablk";
        //convert Score to Float
        boostScore = rsc.convertScoreToFloat( relevanceScore );
        boostScoreString = String.valueOf( boostScore );
        Assert.assertEquals("6.666667E31",boostScoreString);

         //third case with special character in name
        relevanceScore = "BBBCDEbla-bla";
        //convert Score to Float
        boostScore = rsc.convertScoreToFloat( relevanceScore );
        boostScoreString = String.valueOf( boostScore );
        Assert.assertEquals("6.666667E31",boostScoreString);

    }

    @Test
    public void convertToAsciiTest() throws Exception {
        IntactRelevanceScoreCalculator rsc = new IntactRelevanceScoreCalculator( );
        CharSequence sequence = "BBBCDE";
        final String ascii = rsc.getAsciiString( sequence );
        Assert.assertEquals( "666666676869", ascii );
    }

    private List<CrossReference> getAsCollection( CrossReference role ) {
        List<CrossReference> roles = new ArrayList<CrossReference>();
        roles.add( role );
        return roles;
    }


}
