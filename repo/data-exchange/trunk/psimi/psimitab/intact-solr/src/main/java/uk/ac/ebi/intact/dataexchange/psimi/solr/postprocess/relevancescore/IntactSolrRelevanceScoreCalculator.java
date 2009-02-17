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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;

import java.util.*;
import java.io.IOException;

import psidev.psi.mi.tab.model.CrossReference;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.model.ExtendedInteractor;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;

/**
 * /**
 * This Class calculates the RelevanceScore for a given binaryinteraction
 * based on the properties(interactortype, experimentrole, biologicalrole, and name)
 * RSC should be of the format B1B2E1E2T1T2N1N2
 * <p/>
 * BETN, with
 * B=Biological Role (eg: enzyme, inhibitor)
 * E=Experimental Role(eg: bait, prey)
 * T=Molecule Type (eg: protein, smallmolecule)
 * N=Name. N will be abbreviated to a fixed length.
 * <p/>
 * For the given particular role or type, the property file is referred and the corresponding score is fetched.
 * For new roles or unspecified roles a default score N is assigned
 * Then generated overall score as
 * B1B2E1E2T1T2N1N2.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class IntactSolrRelevanceScoreCalculator {

    private static final Log log = LogFactory.getLog( IntactSolrRelevanceScoreCalculator.class );

    private static final String DEFAULT_SCORE = "N";
    Properties rscProperties;


    public IntactSolrRelevanceScoreCalculator() throws IOException {
        this.rscProperties = getDefaultProperties();
    }

    public IntactSolrRelevanceScoreCalculator( Properties rscProperties ) {
        this.rscProperties = rscProperties;
    }

    /**
     * Calculates the relevance score based on the principle
     * explained in the class header
     *
     * @param inputDocument SolrInputDocument
     * @param converter SolrDocumentConverter
     * @return relevance Score of the format B1B2E1E2T1T2N1N2
     */
    public String calculateScore( SolrInputDocument inputDocument,SolrDocumentConverter converter) {
        if ( inputDocument == null ) {
            throw new NullPointerException( "You must give a non null inputDocument" );
        }
        if ( converter == null ) {
            throw new NullPointerException( "You must give a non null converter" );
        }
        final IntactBinaryInteraction binaryInteraction = (IntactBinaryInteraction)converter.toBinaryInteraction( inputDocument );
        return calculateScore( binaryInteraction);
    }


    /**
     *  Calculates the relevance score based on the principle
     * explained in the class header
     * @param binaryInteraction  IntactBinaryInteraction
     * @return relevance Score of the format B1B2E1E2T1T2N1N2
     */
    public String calculateScore( IntactBinaryInteraction binaryInteraction ) {

        final ExtendedInteractor interactorA = binaryInteraction.getInteractorA();
        final ExtendedInteractor interactorB = binaryInteraction.getInteractorB();

        final List<CrossReference> biologicalRoles_A = interactorA.getBiologicalRoles();
        final List<CrossReference> biologicalRoles_B = interactorB.getBiologicalRoles();
        final List<CrossReference> experimentalRoles_A = interactorA.getExperimentalRoles();
        final List<CrossReference> experimentalRoles_B = interactorB.getExperimentalRoles();
        final CrossReference interactorType_A = interactorA.getInteractorType();
        final CrossReference interactorType_B = interactorB.getInteractorType();

        String interactorName_A = getInteractorName( interactorA );
        String interactorName_B = getInteractorName( interactorB );

        return calculateScore(biologicalRoles_A,biologicalRoles_B,experimentalRoles_A,experimentalRoles_B,interactorType_A,interactorType_B,interactorName_A,interactorName_B);
    }

    /**
     * Calculates relevance Score
     * @param biologicalRoles_A list of bioRoles for A
     * @param biologicalRoles_B list of bioRoles for B
     * @param experimentalRoles_A list of expRoles for A
     * @param experimentalRoles_B list of expRoles for B
     * @param interactorType_A  interactorType A
     * @param interactorType_B  interactorType B
     * @param interactorName_A  interactor Name of A
     * @param interactorName_B  interactor Name of B
     * @return score
     */
    public String calculateScore( List<CrossReference> biologicalRoles_A, List<CrossReference> biologicalRoles_B, List<CrossReference> experimentalRoles_A, List<CrossReference> experimentalRoles_B, CrossReference interactorType_A, CrossReference interactorType_B, String interactorName_A, String interactorName_B ) {

        Set<String> uniqueBioRoles_A = getSetOfRoles( biologicalRoles_A );
        String b1 = getScoreForGivenRolesAndTypes( uniqueBioRoles_A );

        Set<String> uniqueBioRoles_B = getSetOfRoles( biologicalRoles_B );
        String b2 = getScoreForGivenRolesAndTypes( uniqueBioRoles_B );

        Set<String> uniqueExpRoles_A = getSetOfRoles( experimentalRoles_A );
        String e1 = getScoreForGivenRolesAndTypes( uniqueExpRoles_A );

        Set<String> uniqueExpRoles_B = getSetOfRoles( experimentalRoles_B );
        String e2 = getScoreForGivenRolesAndTypes( uniqueExpRoles_B );

        String type_A = interactorType_A.getText();
        String t1 = getScoreForGivenRole( type_A );

        String type_B = interactorType_B.getText();
        String t2 = getScoreForGivenRole( type_B );

        String n1 = limitToFixedLength( interactorName_A );
        String n2 = limitToFixedLength( interactorName_B );

        String B1B2E1E2T1T2N1N2 = appendAll( b1, b2, e1, e2, t1, t2, n1, n2 );
        if ( log.isDebugEnabled() ) {
            log.debug( "B1B2E1E2T1T2N1N2->  " + B1B2E1E2T1T2N1N2 );
        }

        return B1B2E1E2T1T2N1N2;

    }


    /**
     * First tries to get the name from the Aliases, as this contains the genename
     * if not, then tries to get from alternative identifiers as this contains
     * all the other alias type and name is returned based on the priority
     * @param interactor ExtendedInteractor
     * @return  interactor Name
     */
    private String getInteractorName( ExtendedInteractor interactor ) {
        String interactorName = null;
        //we add to the tabalias only if the alias type is GENE_NAME, so return the first one
        if(interactor.getAliases()!=null && interactor.getAliases().size()>0){
            interactorName = interactor.getAliases().iterator().next().getName();
        }

        if(interactorName==null){
          final Collection<CrossReference> alternativeIds = interactor.getAlternativeIdentifiers();
          interactorName = getAliasByPriority( alternativeIds,"gene name","gene name synonym","commercial name","go synonym","locus name","orf name","shortlabel" );
         }


        return interactorName;
    }

    /**
     *  In the interactorconver all the aliases other than genename are addeded
     * to alternative identifiers.  So we have to check in alternative ids
     * @param alternativeIds  alternative ids
     * @param aliasTypes      aliastypes by priority
     * @return  InteractorName
     */
    private String getAliasByPriority( Collection<CrossReference> alternativeIds, String... aliasTypes ) {
        String aliasName = null;
        for ( CrossReference altId : alternativeIds ) {
            for ( String aliasType : aliasTypes ) {
                if ( aliasType.equals( altId.getText() ) ) {
                    aliasName = altId.getIdentifier();
                    return aliasName;
                }
            }
        }
        return aliasName;
    }


    private String getScoreForGivenRolesAndTypes( Set<String> roles ) {
        String score = DEFAULT_SCORE;
        if ( roles != null && roles.size() > 0 ) {
            if ( roles.size() > 1 ) {
                score = getMaxScoreForGivenRoles( roles );
            } else if ( roles.size() == 1 ) {
                score = getScoreForGivenRole( roles.iterator().next() );
            }
        }

        return score;
    }

    protected String getMaxScoreForGivenRoles( Set<String> roles ) {
        //read property file and load the role with maximum score
        String maxScore = DEFAULT_SCORE;
        for ( String role : roles ) {
            String tempScore = getScoreForGivenRole( role );
            if ( tempScore.compareTo( maxScore ) < 0 ) {
                maxScore = tempScore;
            }
        }

        return maxScore;
    }

    protected String getScoreForGivenRole( String role ) {
        role = role.replaceAll( "\\s+", "" );
        if ( rscProperties.containsKey( role ) ) {
            return ( String ) rscProperties.get( role );
        } else {
            return DEFAULT_SCORE;
        }

    }


    private Set<String> getSetOfRoles( List<CrossReference> roles ) {

        Set<String> uniqueRoles = new HashSet<String>();
        for ( CrossReference crossReference : roles ) {
            String role = crossReference.getText();
            if(role!=null){
            role = role.trim().replaceAll( "\\s+", "" );
            uniqueRoles.add( role );
            }

        }
        return uniqueRoles;
    }

    private String limitToFixedLength( String name ) {
        if ( name != null ) {
            if ( name.length() > 20 ) {
                name = name.substring( 0, 20 ).toLowerCase();
            }
            return name.toLowerCase();
        }
        return null;
    }

    private String appendAll( String... str ) {
        StringBuilder builder = new StringBuilder();
        for ( String s : str ) {
            builder.append( s );
        }

        return builder.toString();
    }

    public Properties getRscProperties() {
        return rscProperties;
    }

    public void setRscProperties( Properties rscProperties ) {
        this.rscProperties = rscProperties;
    }

    private Properties getDefaultProperties() throws IOException {
        Properties properties = new Properties();
        properties.load( IntactSolrRelevanceScoreCalculator.class.getResourceAsStream( "/relevancescore.properties" ) );
        return properties;
    }
}
