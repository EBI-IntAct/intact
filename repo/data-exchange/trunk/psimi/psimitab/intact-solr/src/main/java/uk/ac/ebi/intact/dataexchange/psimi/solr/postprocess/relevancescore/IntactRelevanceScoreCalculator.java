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
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;

import java.util.*;
import java.io.IOException;

import psidev.psi.mi.tab.model.builder.Row;
import psidev.psi.mi.tab.model.builder.Column;
import psidev.psi.mi.tab.model.builder.Field;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;
import uk.ac.ebi.intact.psimitab.IntactInteractionRowConverter;
import uk.ac.ebi.intact.psimitab.util.IntactPsimitabUtils;
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
public class IntactRelevanceScoreCalculator {

    private static final Log log = LogFactory.getLog( IntactRelevanceScoreCalculator.class );

    private static final String DEFAULT_SCORE = "N";
    Properties rscProperties;


    public IntactRelevanceScoreCalculator() throws IOException {
        this.rscProperties = getDefaultProperties();
    }

    public IntactRelevanceScoreCalculator( Properties rscProperties ) {
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
        IntactInteractionRowConverter converter = new IntactInteractionRowConverter();
        return calculateScore(converter.createRow(binaryInteraction));
    }


    /**
     *  Calculates the relevance score based on the principle
     * explained in the class header
     * @param binaryInteraction  IntactBinaryInteraction
     * @return relevance Score of the format B1B2E1E2T1T2N1N2
     */
    public float calculateScoreFloat( IntactBinaryInteraction binaryInteraction ) {
        String scoreStr = calculateScore(binaryInteraction);
        return convertScoreToFloat(scoreStr);
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
    protected String calculateScore( Column biologicalRoles_A, Column biologicalRoles_B, Column experimentalRoles_A, Column experimentalRoles_B, Column interactorType_A, Column interactorType_B, String interactorName_A, String interactorName_B ) {

        Set<String> uniqueBioRoles_A = getValues( biologicalRoles_A );
        String b1 = getScoreForGivenCol( uniqueBioRoles_A );

        Set<String> uniqueBioRoles_B = getValues( biologicalRoles_B );
        String b2 = getScoreForGivenCol( uniqueBioRoles_B );

        Set<String> uniqueExpRoles_A = getValues( experimentalRoles_A );
        String e1 = getScoreForGivenCol( uniqueExpRoles_A );

        Set<String> uniqueExpRoles_B = getValues( experimentalRoles_B );
        String e2 = getScoreForGivenCol( uniqueExpRoles_B );

        Set<String> uniqueInteractor_A = getValues( interactorType_A );
        String t1 = getScoreForGivenCol( uniqueInteractor_A );

        Set<String> uniqueInteractor_B = getValues( interactorType_B );
        String t2 = getScoreForGivenCol( uniqueInteractor_B );

        String n1 = limitToFixedLength( interactorName_A );
        String n2 = limitToFixedLength( interactorName_B );

        String B1B2E1E2T1T2N1N2 = appendAll( b1, b2, e1, e2, t1, t2, n1, n2 );
        if ( log.isDebugEnabled() ) {
            log.debug( "B1B2E1E2T1T2N1N2->  " + B1B2E1E2T1T2N1N2 );
        }

        return B1B2E1E2T1T2N1N2;

    }

    public float calculateScoreFloat(Row row) {
        String strScore = calculateScore(row);
        return convertScoreToFloat(strScore);
    }

    public String calculateScore(Row row) {
        if (row.getColumnCount() < IntactDocumentDefinition.BIOLOGICAL_ROLE_A) {
           return DEFAULT_SCORE;
        }

        final Column biologicalRoles_A = row.getColumnByIndex(IntactDocumentDefinition.BIOLOGICAL_ROLE_A);
        final Column biologicalRoles_B = row.getColumnByIndex(IntactDocumentDefinition.BIOLOGICAL_ROLE_B);
        final Column experimentalRoles_A = row.getColumnByIndex(IntactDocumentDefinition.EXPERIMENTAL_ROLE_A);
        final Column experimentalRoles_B = row.getColumnByIndex(IntactDocumentDefinition.EXPERIMENTAL_ROLE_B);
        final Column interactorType_A = row.getColumnByIndex(IntactDocumentDefinition.INTERACTOR_TYPE_A);
        final Column interactorType_B = row.getColumnByIndex(IntactDocumentDefinition.INTERACTOR_TYPE_B);

        String interactorName_A = IntactPsimitabUtils.getInteractorANameField( row ).getValue();
        String interactorName_B = IntactPsimitabUtils.getInteractorBNameField( row ).getValue();

        return calculateScore(biologicalRoles_A,biologicalRoles_B,experimentalRoles_A,experimentalRoles_B,interactorType_A,interactorType_B,interactorName_A,interactorName_B);
    }

    /**
     * converts the relevancescore string to float
     * @param relevanceScore as String
     * @return score as float
     */
    protected float convertScoreToFloat( String relevanceScore ) {
        if ( relevanceScore == null ) {
            throw new NullPointerException( "You must give a non null relevanceScore" );
        }
        //B1B2E1E2T1T2N1N2  -the first 6 characters are role and the remaining characters from index 6(0 based) is name
        relevanceScore = relevanceScore.toUpperCase().replaceAll( "[^A-Z]","Z" );

        //pad if name is less than 6+10=16 pad with @ (Ascii value 64)
        if ( relevanceScore.length() < 16 ) {
            relevanceScore = StringUtils.rightPad( relevanceScore, 16, '@' );
        }

        final CharSequence rolesCharSequence = relevanceScore;
        StringBuilder asciiString = getAsciiString( rolesCharSequence );

        if ( log.isDebugEnabled()) {
            log.debug( "String <-> AsciiString =>  " + rolesCharSequence + " <-> "+asciiString);
        }

        return Float.valueOf( asciiString.toString() );
    }


    protected StringBuilder getAsciiString( CharSequence rolesCharSequence ) {
        //iterate thru the characters and convert to int and append (A-65 Z-90)
        StringBuilder scoreBuilder = new StringBuilder();
        for(int i =0;i<rolesCharSequence.length();i++){
            int ascii = rolesCharSequence.charAt( i );
            scoreBuilder.append( ascii );
        }
        return scoreBuilder;
    }



    private String getScoreForGivenCol( Set<String> roles ) {
        String score = DEFAULT_SCORE;
        if ( roles != null) {
            score = getMaxScoreForGivenRoles(roles);
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
        if ( rscProperties.containsKey( role ) ) {
            return ( String ) rscProperties.get( role );
        } else {
            return DEFAULT_SCORE;
        }

    }


    private Set<String> getValues( Column column ) {

        Set<String> values = new HashSet<String>();

        for (Field crossReference : column.getFields()) {
            String role = crossReference.getValue();

            if (role != null) {
                values.add(role);
            }

        }
        return values;
    }

    private String limitToFixedLength( String name ) {
        if ( name != null ) {
            if ( name.length() > 5 ) {
                name = name.substring( 0, 5 ).toLowerCase();
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
        properties.load( IntactRelevanceScoreCalculator.class.getResourceAsStream( "/relevancescore.properties" ) );
        return properties;
    }
}
