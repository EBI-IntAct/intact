/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.util.controlledVocab.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.controlledVocab.PSILoader;
import uk.ac.ebi.intact.util.controlledVocab.PsiLoaderException;
import uk.ac.ebi.intact.util.controlledVocab.model.*;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * PSILoader Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/17/2006</pre>
 */
public class PSILoaderTest extends TestCase {
    public PSILoaderTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( PSILoaderTest.class );
    }

    /////////////////////
    // Tests

    public void testCvTermXrefs() {

        PSILoader loader = new PSILoader();

        try {

            /**
             * Structure of that simple DAG
             *
             * <pre>
             *                             IA:0000
             *                         -------|-------------
             *                         |          |        |
             *                      IA:0001    IA:0002   IA:0003
             * </pre>
             */


            IntactOntology ontology = loader.parseOboFile( new File( "data/test/obo/multiple.xrefs.obo" ) );
            assertNotNull( ontology );
            Collection cvTerms = ontology.getCvTerms();

            for ( Iterator iterator = cvTerms.iterator(); iterator.hasNext(); ) {
                CvTerm cvTerm = (CvTerm) iterator.next();

                if ( cvTerm.getId().equals( "IA:0000" ) ) {

                    assertEquals( "ia", cvTerm.getShortName() );
                    assertEquals( "intact cv test", cvTerm.getFullName() );
                    assertEquals( 1, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "go synonym", "test data" ) ) );
                    assertEquals( 1, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "99999999", "PMID", "primary-reference" ) ) );

                } else if ( cvTerm.getId().equals( "IA:0001" ) ) {

                    assertEquals( "first", cvTerm.getShortName() );
                    assertEquals( "first term", cvTerm.getFullName() );
                    assertEquals( 2, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "99999999", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA00000", "resid", "identity" ) ) );

                } else if ( cvTerm.getId().equals( "IA:0002" ) ) {

                    assertEquals( "second", cvTerm.getShortName() );
                    assertEquals( "second term", cvTerm.getFullName() );
                    assertEquals( 2, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "99999999", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA00000", "resid", "see-also" ) ) );

                } else if ( cvTerm.getId().equals( "IA:0003" ) ) {

                    assertEquals( "third", cvTerm.getShortName() );
                    assertEquals( "third term", cvTerm.getFullName() );
                    assertEquals( 3, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "11111111111", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "99999999", "PMID", "method-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA333333", "resid", "see-also" ) ) );

                } else {
                    fail( "Expercted term: IA:0000, IA:0001, IA:0002, IA:0003: found " + cvTerm.getId() );
                }
            }

        } catch ( PsiLoaderException e ) {
            e.printStackTrace();
            fail( "Could not parse the file successfuly" );
        }
    }


    public void testAllCvTerm() {

        PSILoader loader = new PSILoader();

        try {

            /**
             * Structure of that simple DAG
             *
             * <pre>
             *                             MI:0000                     IA:0001
             *            --------------------|-------------
             *          MI:0001        |          |        |
             *         ---|---      MI:0123    MI:0478   MI:0531
             *        |      |
             *   MI:0004   MI:0398
             *
             * </pre>
             */

            IntactOntology ontology = loader.parseOboFile( new File( "data/test/obo/mock-psi25.small.fixed.obo" ) );
            assertNotNull( ontology );
            Collection cvTerms = ontology.getCvTerms();

            assertNotNull( cvTerms );
            assertEquals( 9, cvTerms.size() );

            for ( Iterator iterator = cvTerms.iterator(); iterator.hasNext(); ) {
                CvTerm cvTerm = (CvTerm) iterator.next();

                if ( cvTerm.getId().equals( "MI:0000" ) ) {

                    assertEquals( "mi", cvTerm.getShortName() );
                    assertEquals( "molecular interaction", cvTerm.getFullName() );
                    assertEquals( "Controlled vocabularies originally created for protein protein interactions, " +
                                  "extended to other molecules interactions.",
                                  cvTerm.getDefinition() );
                    assertEquals( 1, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "go synonym", "sam you will have to transform all existing synonyms in this format alias before the loading" ) ) );
                    assertEquals( 1, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "14755292", "PMID", "primary-reference" ) ) );
                    assertEquals( 0, cvTerm.getAnnotations().size() );
                    assertEquals( 0, cvTerm.getParents().size() );
                    assertEquals( 4, cvTerm.getChildren().size() );
                    for ( Iterator iterator1 = cvTerm.getChildren().iterator(); iterator1.hasNext(); ) {
                        CvTerm child = (CvTerm) iterator1.next();
                        if ( "MI:0001".equals( child.getId() ) ) {
                            // ok
                        } else if ( "MI:0123".equals( child.getId() ) ) {
                            // ok
                        } else if ( "MI:0478".equals( child.getId() ) ) {
                            // ok
                        } else if ( "MI:0531".equals( child.getId() ) ) {
                            // ok
                        } else {
                            fail( "Expected terms: MI:0001, MI:0123, MI:0478, MI:0531. Found instead: " + child.getId() );
                        }
                        assertFalse( cvTerm.isObsolete() );
                    }


                } else if ( cvTerm.getId().equals( "MI:0213" ) ) {

                    /**
                     [Term]
                     id: MI:0213
                     name: methylation reaction
                     def: "" [pubmed:14755292 "primary-reference", resid:AA0061 "see-also", resid:AA0062 "see-also",
                     resid:AA0063 "see-also", resid:AA0064 "see-also", resid:AA0234 "see-also", resid:AA0065 "see-also",
                     resid:AA0066 "see-also", resid:AA0272 "see-also", resid:AA0067 "see-also", resid:AA0068 "see-also",
                     resid:AA0069 "see-also", resid:AA0070 "see-also", resid:AA0071 "see-also", resid:AA0072 "see-also",
                     resid:AA0073 "see-also", resid:AA0074 "see-also", resid:AA0075 "see-also", resid:AA0076 "see-also",
                     go:GO:0043414 "identity"]
                     exact_synonym: "methylation" []
                     is_a: MI:0414 ! enzymatic reaction
                     */

                    assertEquals( "methylation", cvTerm.getShortName() );
                    assertEquals( "methylation reaction", cvTerm.getFullName() );
                    assertEquals( "The covalent attachment of a methyl residue to one or more monomeric units in a " +
                                  "polypeptide, polynucleotide, polysaccharide, or other biological polymer. " +
                                  "Irreversible reaction that can affect A,G,M,F,P,C,R,N,Q,E,H,or K residues.", cvTerm.getDefinition() );
                    assertEquals( 0, cvTerm.getSynonyms().size() );
                    assertEquals( 20, cvTerm.getXrefs().size() );

                    for ( Iterator iterator1 = cvTerm.getXrefs().iterator(); iterator1.hasNext(); ) {
                        CvTermXref xref = (CvTermXref) iterator1.next();
                        System.out.println( xref );
                    }

                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "14755292", "pubmed", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0061", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0062", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0063", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0064", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0065", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0066", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0272", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0067", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0068", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0069", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0070", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0071", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0072", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0073", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0074", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0075", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0076", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "GO:0043414", "go", "identity" ) ) );
                    assertEquals( 0, cvTerm.getAnnotations().size() );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0000", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 2, cvTerm.getChildren().size() );
                    for ( Iterator iterator1 = cvTerm.getChildren().iterator(); iterator1.hasNext(); ) {
                        CvTerm child = (CvTerm) iterator1.next();
                        if ( "MI:0004".equals( child.getId() ) ) {
                            // ok
                        } else if ( "MI:0398".equals( child.getId() ) ) {
                            // ok
                        } else {
                            fail( "Expected terms: MI:0004, MI:0398. Found instead: " + child.getId() );
                        }
                    }
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0001" ) ) {

                    assertEquals( "interaction detect", cvTerm.getShortName() );
                    assertEquals( "interaction detection method", cvTerm.getFullName() );
                    assertEquals( "Method to determine the interaction.", cvTerm.getDefinition() );
                    assertEquals( 0, cvTerm.getSynonyms().size() );
                    assertEquals( 2, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "14755292", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "11125109", "PMID", "method reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0061", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0062", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0063", "resid", "see-also" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "GO:00000001", "go", "identity" ) ) );
                    assertEquals( 0, cvTerm.getAnnotations().size() );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0000", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 2, cvTerm.getChildren().size() );
                    for ( Iterator iterator1 = cvTerm.getChildren().iterator(); iterator1.hasNext(); ) {
                        CvTerm child = (CvTerm) iterator1.next();
                        if ( "MI:0004".equals( child.getId() ) ) {
                            // ok
                        } else if ( "MI:0398".equals( child.getId() ) ) {
                            // ok
                        } else {
                            fail( "Expected terms: MI:0004, MI:0398. Found instead: " + child.getId() );
                        }
                    }
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0004" ) ) {

                    assertEquals( "affinity chrom", cvTerm.getShortName() );
                    assertEquals( "affinity chromatography technologies", cvTerm.getFullName() );
                    assertEquals( "This class of approaches is characterised by the use of affinity resins as tools " +
                                  "to purify molecule of interest  (baits) and their binding partners. The baits can " +
                                  "be captured by a variety of high affinity ligands linked to a resin - for example," +
                                  " antibodies specific for the bait itself, antibodies for specific tags engineered " +
                                  "to be expressed as part of the bait or other high affinity binders such as " +
                                  "glutathione resins for GST fusion proteins, metal resins for histidine-tagged " +
                                  "proteins.",
                                  cvTerm.getDefinition() );
                    assertEquals( 1, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "Affinity purification" ) ) );
                    assertEquals( 1, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "7708014", "PMID", "primary-reference" ) ) );
                    assertEquals( 0, cvTerm.getAnnotations().size() );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0001", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0123" ) ) {

                    assertEquals( "acetylarginine", cvTerm.getShortName() );
                    assertEquals( "n2-acetyl-arginine", cvTerm.getFullName() );
                    assertEquals( "residue modification", cvTerm.getDefinition() );
                    assertEquals( 5, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "N2-acetyl-L-arginine" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "RAC" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "[R:ac]" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "acetylarginine" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "alpha-acetylamino-delta-guanidinovaleric acid" ) ) );
                    assertEquals( 2, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "11125103", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0354", "RESID", "identity" ) ) );
                    assertEquals( 1, cvTerm.getAnnotations().size() );
                    assertTrue( cvTerm.getAnnotations().contains( new CvTermAnnotation( "caution", "look how awfull are the synonym play attention to the parsing" ) ) );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0000", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0398" ) ) {

                    assertEquals( "two hybrid pooling", cvTerm.getShortName() );
                    assertEquals( "two hybrid pooling approach", cvTerm.getFullName() );
                    assertEquals( "In the pooling strategy the sets of bait and prey hybrid vectors are mated all " +
                                  "together and then selected. The positives double hybrid clones are sequenced to " +
                                  "identify the interacting partners.",
                                  cvTerm.getDefinition() );
                    assertEquals( 1, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "go synonym", "2h" ) ) );
                    assertEquals( 3, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "12634794", "PMID", "primary-reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "11125109", "PMID", "method reference" ) ) );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "11283351", "PMID", "see-also" ) ) );
                    assertEquals( 1, cvTerm.getAnnotations().size() );
                    assertTrue( cvTerm.getAnnotations().contains( new CvTermAnnotation( "exp-modification", "text attached to annotation topic exp-modification" ) ) );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0001", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0478" ) ) {

                    assertEquals( "flybase", cvTerm.getShortName() ); // no exact_synonym available, fall back on name.
                    assertEquals( "flybase", cvTerm.getFullName() );
                    assertEquals( "FlyBase is a comprehensive database for information on the genetics and molecular " +
                                  "biology of Drosophila. http://fbserver.gen.cam.ac.uk:7081/",
                                  cvTerm.getDefinition() );
                    assertEquals( 2, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "FlyBase" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "go synonym", "text with the name of the alias" ) ) );
                    assertEquals( 1, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "14755292", "PMID", "primary-reference" ) ) );
                    assertEquals( 2, cvTerm.getAnnotations().size() );
                    assertTrue( cvTerm.getAnnotations().contains( new CvTermAnnotation( "kinetics", "sample text attached to annotation topic kinetics" ) ) );
                    assertTrue( cvTerm.getAnnotations().contains( new CvTermAnnotation( "id-validation-regexp", "FBgn[0-9]{7}" ) ) );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0000", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "MI:0531" ) ) {

                    assertEquals( "adp-ribosylserine", cvTerm.getShortName() );
                    assertEquals( "o-(adp-ribosyl)-serine", cvTerm.getFullName() );
                    assertEquals( "residue modification", cvTerm.getDefinition() );
                    assertEquals( 6, cvTerm.getSynonyms().size() );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "(S)-2-amino-3-([adenosine 5'-(trihydrogen diphosphate) 5'->5'-ester with alpha-D-ribofuranosyl]oxy)-propanoic acid Formula" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "O-(ADP-ribosyl)-L-serine" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "O3-(ADP-ribosyl)-L-serine" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "O3-[alpha-D-ribofuranoside 5'->5'-ester with adenosine 5'-(trihydrogen diphosphate)]-L-serine" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "O3-alpha-D-ribofuranosyl-L-serine 5'->5'-ester with adenosine 5'-(trihydrogen diphosphate)" ) ) );
                    assertTrue( cvTerm.getSynonyms().contains( new CvTermSynonym( "go synonym", "awfull modification" ) ) );
                    assertEquals( 1, cvTerm.getXrefs().size() );
                    assertTrue( cvTerm.getXrefs().contains( new CvTermXref( "AA0237", "RESID", "identity" ) ) );
                    assertEquals( 1, cvTerm.getParents().size() );
                    assertEquals( "MI:0000", ( (CvTerm) cvTerm.getParents().iterator().next() ).getId() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertFalse( cvTerm.isObsolete() );

                } else if ( cvTerm.getId().equals( "IA:0001" ) ) {

                    assertEquals( "obsolete", cvTerm.getShortName() );
                    assertEquals( "obsolete term", cvTerm.getFullName() );
                    assertEquals( "an obsolete term", cvTerm.getDefinition() );
                    assertEquals( 0, cvTerm.getSynonyms().size() );
                    assertEquals( 0, cvTerm.getXrefs().size() );
                    assertEquals( 0, cvTerm.getParents().size() );
                    assertEquals( 0, cvTerm.getChildren().size() );
                    assertTrue( cvTerm.isObsolete() );

                } else {

                    fail( "Expected terms: MI:0000, MI:0001, MI:0004, MI:0123, MI:0398, MI:0478, MI:0531. found instead: " + cvTerm.getId() );
                }
            }

        } catch ( PsiLoaderException e ) {
            fail( "failed parsing" );
            e.printStackTrace();
        }
    }
}