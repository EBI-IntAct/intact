/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein.mock;

import uk.ac.ebi.intact.uniprot.model.Organism;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version here
 */
public class MockUniprotProtein {

    public static final SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yyyy" );

    public static final Collection<String> NONE = new ArrayList<String>( 0 );

    private static Date buildDate( String d ) {
        try {
            return sdf.parse( d );
        } catch ( ParseException e ) {
            throw new RuntimeException( "Could not parse date: " + d );
        }
    }

    private MockUniprotProtein() {
    }

    //////////////////////////////
    // Mock proteins

    public static UniprotProtein build_CDC42_CANFA() {
        return new UniprotProteinBuilder()
                .setSource( UniprotProteinType.SWISSPROT )
                .setId( "CDC42_CANFA" )
                .setPrimaryAc( "P60952" )
                .setSecondaryAcs( Arrays.asList( "P21181", "P25763" ) )
                .setOrganism( new Organism( 9615, "Dog" ) )
                .setDescription( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)" )
                .setGenes( Arrays.asList( "CDC42" ) )
                .setSynomyms( NONE )
                .setOrfs( NONE )
                .setLocuses( NONE )
                .setLastSequenceUpdate( buildDate( "13-APR-2004" ) )
                .setLastAnnotationUpdate( buildDate( "20-FEB-2007" ) )
                .setDiseases( NONE )
                .setFunctions( NONE )
                .setKeywords( NONE )
                .setCrossReferences(
                        new UniprotProteinXrefBuilder()
                                .add( "IPR003578", "InterPro", "GTPase_Rho" )
                                .add( "IPR013753", "InterPro", "Ras" )
                                .add( "IPR001806", "InterPro", "Ras_trnsfrmng" )
                                .add( "IPR005225", "InterPro", "Small_GTP_bd" )
                                .build()
                )
                .setCrc64( "34B44F9225EC106B" )
                .setSequence( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                              "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                              "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                              "ETQPKRKCCIF" )
                .setFeatureChains( null )
                .setSpliceVariants( Arrays.asList(
                        new UniprotSpliceVariantBuilder()
                                .setPrimaryAc( "P60952-1" )
                                .setSecondaryAcs( Arrays.asList( "P21181-1" ) )
                                .setOrganism( new Organism( 9615, "Dog" ) )
                                .setSynomyms( Arrays.asList( "Brain" ) )
                                .setNote( "Has not been isolated in dog so far" )
                                .setSequence( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                                              "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                                              "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                                              "ETQPKRKCCIF" )
                                .build(),
                        new UniprotSpliceVariantBuilder()
                                .setPrimaryAc( "P60952-2" )
                                .setSecondaryAcs( Arrays.asList( "P21181-4" ) )
                                .setOrganism( new Organism( 9615, "Dog" ) )
                                .setSynomyms( Arrays.asList( "Placental" ) )
                                .setSequence( "MQTIKCVKRKCCIF" ) /* Fake sequence */
                                .build()
                ) )
                .build();
    }

    public static UniprotProtein build_CDC42_HUMAN() {
        return new UniprotProteinBuilder()
                .setSource( UniprotProteinType.SWISSPROT )
                .setId( "CDC42_HUMAN" )
                .setPrimaryAc( "P60953" )
                .setSecondaryAcs( Arrays.asList( "P21181", "P25763", "Q7L8R5" ) )
                .setOrganism( new Organism( 9606, "Human" ) )
                .setDescription( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)" )
                .setGenes( Arrays.asList( "CDC42" ) )
                .setSynomyms( NONE )
                .setOrfs( NONE )
                .setLocuses( NONE )
                .setLastSequenceUpdate( buildDate( "13-APR-2004" ) )
                .setLastAnnotationUpdate( buildDate( "20-FEB-2007" ) )
                .setDiseases( NONE )
                .setFunctions( NONE )
                .setKeywords( Arrays.asList( "3D-structure", "Alternative splicing", "Direct protein sequencing" ) )
                .setCrossReferences(
                        new UniprotProteinXrefBuilder()
                                .add( "IPR003578", "InterPro", "GTPase_Rho" )
                                .add( "IPR013753", "InterPro", "Ras" )
                                .add( "IPR001806", "InterPro", "Ras_trnsfrmng" )
                                .add( "IPR005225", "InterPro", "Small_GTP_bd" )
                                .add( "GO:0005737", "Go", "" )
                                .add( "GO:0030175", "Go", "" )
                                .add( "GO:0005886", "Go", "" )
                                .add( "GO:0003924", "Go", "" )
                                .add( "ENSG00000070831", "Ensembl", "Homo sapiens" )
                                .add( "2NGR", "PDB", "" )
                                .build()
                )
                .setCrc64( "34B44F9225EC106B" )
                .setSequence( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                              "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                              "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                              "ETQPKRKCCIF" )
                .setFeatureChains( null )
                .setSpliceVariants( Arrays.asList(
                        new UniprotSpliceVariantBuilder()
                                .setPrimaryAc( "P60953-1" )
                                .setSecondaryAcs( Arrays.asList( "P21181-1" ) )
                                .setOrganism( new Organism( 9606, "Human" ) )
                                .setSynomyms( Arrays.asList( "Brain" ) )
                                .setNote( null )
                                .setSequence( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                                              "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                                              "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                                              "ETQPKRKCCIF" )
                                .build(),
                        new UniprotSpliceVariantBuilder()
                                .setPrimaryAc( "P60953-2" )
                                .setSecondaryAcs( Arrays.asList( "P21181-4" ) )
                                .setOrganism( new Organism( 9606, "Human" ) )
                                .setSynomyms( Arrays.asList( "Placental" ) )
                                .setSequence( "SYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHH" ) /* Fake sequence */
                                .build()
                ) )
                .build();
    }
}