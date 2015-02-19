/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.data;

import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.description.NameType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VarSeqFeature;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.kraken.model.factories.DefaultCommentFactory;
import uk.ac.ebi.kraken.model.factories.DefaultFeatureFactory;
import uk.ac.ebi.kraken.model.factories.DefaultUniProtFactory;
import uk.ac.ebi.kraken.model.factories.DefaultXRefFactory;
import uk.ac.ebi.kraken.model.uniprot.dbx.DatabaseCrossReferenceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility for building Mock Uniprot entries.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class MockUniProtEntries {

    //////////////////////
    // Constants

    private static final String[] NONE = new String[]{};

    private static final String UNIPROT_DATE_FORMAT = "dd-MMM-yyyy";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat( UNIPROT_DATE_FORMAT );

    ///////////////////////
    // Helper methods

    private static ProteinDescription buildDescription( String description ) {
        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        ProteinDescription d = factory.buildProteinDescription();

        final Field fullField = factory.buildField();
        fullField.setType(FieldType.FULL);
        fullField.setValue(description);
              
        final Name recName = factory.buildName();
        recName.getFields().add(fullField);
        recName.setNameType(NameType.RECNAME);

        d.setRecommendedName(recName);

        return d;
    }

    private static Gene buildGene( String geneName, String[] synonyms, String[] orfs, String[] orderedLocus ) {
        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        Gene gene = factory.buildGene();

        gene.setGeneName( factory.buildGeneName( geneName ) );

        List<GeneNameSynonym> synList = new ArrayList<GeneNameSynonym>( synonyms.length );
        for ( int i = 0; i < synonyms.length; i++ ) {
            String syn = synonyms[i];
            synList.add( factory.buildGeneNameSynonym( syn ) );
        }
        gene.setGeneNameSynonyms( synList );

        List<ORFName> orfList = new ArrayList<ORFName>( orfs.length );
        for ( int i = 0; i < orfs.length; i++ ) {
            String orf = orfs[i];
            orfList.add( factory.buildORFName( orf ) );
        }
        gene.setORFNames( orfList );

        List<OrderedLocusName> locusList = new ArrayList<OrderedLocusName>( orderedLocus.length );
        for ( int i = 0; i < orderedLocus.length; i++ ) {
            String locus = synonyms[i];
            locusList.add( factory.buildOrderedLocusName( locus ) );
        }
        gene.setOrderedLocusNames( locusList );

        return gene;
    }

    private static List<SecondaryUniProtAccession> buildSecondaryAcs( String[] acs ) {
        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        List<SecondaryUniProtAccession> accessions = new ArrayList<SecondaryUniProtAccession>( acs.length );
        for ( int i = 0; i < acs.length; i++ ) {
            String ac = acs[i];
            accessions.add( factory.buildSecondaryUniProtAccession( ac ) );
        }
        return accessions;
    }

    private static Organism buildOrganism( String commonName, String scientificName ) {
        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        Organism organism = factory.buildOrganism();
        organism.setCommonName( factory.buildOrganismCommonName( commonName ) );
        organism.setScientificName( factory.buildOrganismScientificName( scientificName ) );
        return organism;
    }

    private static NcbiTaxonomyId buildTaxid( String taxonId ) {
        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        NcbiTaxonomyId taxid = factory.buildNcbiTaxonomyId( taxonId );
        return taxid;
    }

    private static DatabaseCrossReference buildGo( String id ) {
        DefaultXRefFactory factory = DefaultXRefFactory.getInstance();

        Go go = factory.buildGo(new DatabaseCrossReferenceImpl(DatabaseType.GO));
        go.setPrimaryId(factory.buildXDBAttribute(id));

        return go;
    }

    private static DatabaseCrossReference buildFlybase( String id ) {
        DefaultXRefFactory factory = DefaultXRefFactory.getInstance();

        DatabaseCrossReference fb = factory.buildDatabaseCrossReference(DatabaseType.FLYBASE);
        fb.setPrimaryId(factory.buildXDBAttribute(id));

        return fb;
    }

    private static DatabaseCrossReference buildEnsembl( String id ) {
        DefaultXRefFactory factory = DefaultXRefFactory.getInstance();

        DatabaseCrossReference fb = factory.buildDatabaseCrossReference(DatabaseType.ENSEMBL);
        fb.setPrimaryId(factory.buildXDBAttribute(id));

        return fb;
    }

    private static DatabaseCrossReference buildInterpro( String id ) {
        DefaultXRefFactory factory = DefaultXRefFactory.getInstance();

        DatabaseCrossReference fb = factory.buildDatabaseCrossReference(DatabaseType.INTERPRO);
        fb.setPrimaryId(factory.buildXDBAttribute(id));

        return fb;
    }

    private static DatabaseCrossReference buildSmart( String id ) {
        DefaultXRefFactory factory = DefaultXRefFactory.getInstance();

        DatabaseCrossReference fb = factory.buildDatabaseCrossReference(DatabaseType.SMART);
        fb.setPrimaryId(factory.buildXDBAttribute(id));

        return fb;
    }

    private static EntryAudit buildEntryAudit( int version,
                                               String lastAnnotationUpdateDate,
                                               String lastSequenceUpdateDate
    ) {

        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();
        EntryAudit ea = factory.buildEntryAudit();

        ea.setEntryVersion( version );

        try {
            ea.setLastAnnotationUpdateDate( FORMAT.parse( lastAnnotationUpdateDate ) );
        } catch ( ParseException e ) {
            throw new IllegalArgumentException( "lastAnnotationUpdateDate( " + lastAnnotationUpdateDate +
                                                " ) could not parse to format: " + UNIPROT_DATE_FORMAT );
        }

        try {
            ea.setLastSequenceUpdateDate( FORMAT.parse( lastSequenceUpdateDate ) );
        } catch ( ParseException e ) {
            throw new IllegalArgumentException( "lastSequenceUpdateDate( " + lastSequenceUpdateDate +
                                                " ) could not parse to format: " + UNIPROT_DATE_FORMAT );
        }
        return ea;
    }


    private static Feature buildVariantFeature( String id, int from, int to, String seqFrom, String seqTo ) {
        DefaultFeatureFactory ff = DefaultFeatureFactory.getInstance();

        VarSeqFeature vf = ff.buildFeature( FeatureType.VAR_SEQ );

        vf.setFeatureLocation( ff.buildFeatureLocation( from, to ) );
        vf.setFeatureId( ff.buildFeatureId( id ) );

        if ( seqTo != null && seqTo.trim().length() > 0 ) {
            vf.setAlternativeSequences( Arrays.asList( ff.buildFeatureSequence( seqTo ) ) );
        }

        if ( seqFrom != null && seqFrom.trim().length() > 0 ) {
            vf.setOriginalSequence( ff.buildFeatureSequence( seqFrom ) );
        }

        return vf;
    }

    private static AlternativeProductsIsoform buildIsoform( String[] acs, String syn, String note, String[] sequenceIds ) {
        DefaultCommentFactory factory = DefaultCommentFactory.getInstance();

        AlternativeProductsIsoform isoform = factory.buildAlternativeProductsIsoform();

        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < acs.length; i++ ) {
            String ac = acs[i];
            sb.append( ac );
            if ( i < acs.length ) {
                sb.append( ',' );
            }
        }

        isoform.setIds( Arrays.asList( factory.buildIsoformId( sb.toString() ) ) );

        if ( note != null && note.trim().length() > 0 ) {
            isoform.setNote( factory.buildIsoformNote( note ) );
        }

        if ( syn != null && syn.trim().length() > 0 ) {
            isoform.setSynonyms( Arrays.asList( factory.buildIsoformSynonym( syn ) ) );
        }

        if ( sequenceIds == null || sequenceIds.length == 0 ) {
            isoform.setIsoformSequenceStatus( IsoformSequenceStatus.DISPLAYED );
        } else {
            isoform.setIsoformSequenceStatus( IsoformSequenceStatus.DESCRIBED );
            ArrayList<IsoformSequenceId> mySequenceIds = new ArrayList<IsoformSequenceId>();
            for ( String sequenceId : sequenceIds ) {
                mySequenceIds.add( factory.buildIsoformSequenceId( sequenceId ) );
            }
            isoform.setSequenceIds( mySequenceIds );
        }

        return isoform;
    }

    private static AlternativeProductsComment buildSpliceVariant( List<AlternativeProductsIsoform> isoforms ) {
        DefaultCommentFactory factory = DefaultCommentFactory.getInstance();

        AlternativeProductsComment comment = factory.buildComment( CommentType.ALTERNATIVE_PRODUCTS );
        comment.setIsoforms( isoforms );

        return comment;
    }

    /////////////////////////////////////
    // Specific UniProt Protein Mockups

    /*
    * from http://www.ebi.uniprot.org/entry/Q9VGX3?format=text&ascii
    */

    public static UniProtEntry build_Q9VGX3() {

        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();

        UniProtEntry entry = factory.buildEntry();

        entry.setType( UniProtEntryType.SWISSPROT );
        entry.setUniProtId( factory.buildUniProtId( "FAU_DROME" ) );
        entry.setEntryAudit( buildEntryAudit( 41, "23-JAN-2007", "19-JUL-2004" ) );
        entry.setPrimaryUniProtAccession( factory.buildPrimaryUniProtAccession( "Q9VGX3" ) );
        entry.setSecondaryUniProtAccessions( buildSecondaryAcs( new String[]{"Q95S18", "Q9VGX1", "Q9VGX2", "Q9Y0F9"} ) );
        entry.setProteinDescription( buildDescription( "Protein anoxia up-regulated" ) );
        entry.setOrganism( buildOrganism( "Fruit fly", "Drosophila melanogaster" ) );
        entry.setNcbiTaxonomyIds( Arrays.asList( buildTaxid( "7227" ) ) );
        entry.setGenes( Arrays.asList( buildGene( "fau", NONE, new String[]{"CG6544"}, NONE ) ) );

        ArrayList<DatabaseCrossReference> crs = new ArrayList<DatabaseCrossReference>();
        crs.add( buildGo( "GO:0005515" ) );
        crs.add( buildGo( "GO:0006979" ) );
        crs.add( buildFlybase( "FBgn0020439" ) );
        crs.add( buildEnsembl( "CG6544" ) );
        entry.setDatabaseCrossReferences( crs );

        entry.setSequence( factory.buildSequence( "MVYESGFTTRRTYSSRPVTTSYAVTYPSVEKVTRVYKSSYPIYSSYSVPRRVYGATRVVT" +
                                                  "SPIRVVTSPARVVSRVIHSPSPVRVVRTTTRVISSPERTTYSYTTPSTYYSPSYLPSTYT" +
                                                  "STYIPTSYTTYTPSYAYSPTTVTRVYAPRSSLSPLRITPSPVRVITSPVRSVPSYLKRLP" +
                                                  "PGYGARALTNYLNTEPFTTFSEETSRIRNRAQSLIRDLHTPVVRRARSCTPFPVTGYTYE" +
                                                  "PASQLALDAYVARVTNPVRHIAKEVHNISHYPRPAVKYVDAELDPNRPSRKFSAPRPLED" +
                                                  "PLDVEAKEKQRLRQERLLTVNEEALDEVDLEKKRAQKADEAKRREERALKEERDRLTAEA" +
                                                  "EKQAAAKAKKAAEEAAKIAAEEALLAEAAAQKAAEEAKALKAAEDAAQKAAEEARLAEEA" +
                                                  "AAQKVAEEAAQKAAEEARLAEEAAAQKAAEEAAQKAAEEAALKAAEEARLAEEAAQKAAE" +
                                                  "EAALKAVEEARAAEEAAQKAAEEARVAEEARLEEEQRVREQELERLAEIEKESEGELARQ" +
                                                  "AAELAEIARQESELAAQELQAIQKNENETSEPVVEEPVTPVEEQEPIIELGSNVTPTGGN" +
                                                  "SYEEDLDAEEEEDEEEEEE" ) );
        entry.getSequence().setCRC64( "7DDCB26AD1AB9CEE" );

        //////////////////////
        // Splice variants

        // create features
        entry.setFeatures( Arrays.asList(
                buildVariantFeature( "VSP_004048", 26, 163,
                                     "YPSVEKVTRVYKSSYPIYSSYSVPRRVYGATRVVTSPIRVVTSPARVVSRVIHSPSPVRVVRTTTRVISSPERTTYSYTTPSTYYSPSYLPSTYTSTYIPTSYTTYTPSYAYSPTTVTRVYAPRSSLSPLRITPSPVR",
                                     "RTKRTPIDWEKVPFVPRPSLISDPVTAFGVRRPDLERRQRSILDPINRASIKPDYKLAYEPIEPYVSTRDKNRTRILGMVRQHIDTVEAGGNTAGRTFRDSLDAQLPRLHRAVSESLPVRRETYRNERSGAMVTKYSY" ),
                buildVariantFeature( "VSP_004049", 164, 619, null, null )
        ) );

        // create comments
        List<AlternativeProductsComment> comments = new ArrayList<AlternativeProductsComment>();
        comments.add( buildSpliceVariant( Arrays.asList(
                buildIsoform( new String[]{"Q9VGX3-5"}, "", "No experimental confirmation available", null )
                , buildIsoform( new String[]{"Q9VGX3-2"}, "", "No experimental confirmation available", new String[]{"VSP_004048", "VSP_004049"} )
        ) ) );
        entry.setComments( new ArrayList<Comment>() );
        entry.getComments().addAll( comments );

        return entry;
    }


    /*
    * from http://www.ebi.uniprot.org/entry/Q9VGX3?format=text&ascii
    */
    public static UniProtEntry build_P60952() {

        DefaultUniProtFactory factory = DefaultUniProtFactory.getInstance();

        UniProtEntry entry = factory.buildEntry();

        entry.setType( UniProtEntryType.SWISSPROT );
        entry.setUniProtId( factory.buildUniProtId( "CDC42_CANFA" ) );
        entry.setEntryAudit( buildEntryAudit( 35, "20-FEB-2007", "13-APR-2004" ) );
        entry.setPrimaryUniProtAccession( factory.buildPrimaryUniProtAccession( "P60952" ) );
        entry.setSecondaryUniProtAccessions( buildSecondaryAcs( new String[]{"P21181", "P25763"} ) );
        entry.setProteinDescription( buildDescription( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)" ) );
        entry.setOrganism( buildOrganism( "Dog", "Canis familiaris" ) );
        entry.setNcbiTaxonomyIds( Arrays.asList( buildTaxid( "9615" ) ) );
        entry.setGenes( Arrays.asList( buildGene( "CDC42", NONE, NONE, NONE ) ) );

        // TODO KeyWords

        // TODO Function

        ArrayList<DatabaseCrossReference> crs = new ArrayList<DatabaseCrossReference>();
        crs.add( buildInterpro( "IPR003578" ) );
        crs.add( buildInterpro( "IPR013753" ) );
        crs.add( buildInterpro( "IPR001806" ) );
        crs.add( buildInterpro( "IPR005225" ) );
        crs.add( buildEnsembl( "ENSCAFG00000014707" ) );
        crs.add( buildSmart( "SM00174" ) );
        entry.setDatabaseCrossReferences( crs );

        entry.setSequence( factory.buildSequence( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                                                  "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                                                  "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                                                  "ETQPKRKCCIF" ) );
        entry.getSequence().setCRC64( "34B44F9225EC106B" );

        //////////////////////
        // Splice variants

        // create features
        entry.setFeatures( Arrays.asList(
                buildVariantFeature( "VSP_010079", 182, 191, "TQPKRKCCIF", "PKKSRRCVLL"),
                buildVariantFeature( "VSP_010078", 163, 163, "R", "K" )
        ) );

        // create comments
        List<AlternativeProductsComment> comments = new ArrayList<AlternativeProductsComment>();
        comments.add( buildSpliceVariant( Arrays.asList(
                buildIsoform( new String[]{"P60952-1", "P21181-1"}, "Brain", "Has not been isolated in dog so far", null )
                , buildIsoform( new String[]{"P60952-2", "P21181-4"}, "Placental", "", new String[]{"VSP_010078", "VSP_010079"} )
        ) ) );
        entry.setComments( new ArrayList<Comment>() );
        entry.getComments().addAll( comments );

        return entry;
    }
}