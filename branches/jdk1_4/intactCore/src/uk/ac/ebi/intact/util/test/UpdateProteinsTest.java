/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.UpdateProteins;
import uk.ac.ebi.intact.util.UpdateProteinsI;
import uk.ac.ebi.intact.util.test.mocks.MockEntryText;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * Test the UpdateProtein process by inserting successively some proteins covering mosts cases
 * and then we check what has been created.
 * <p/>
 * <br>
 * Note: That test is supposed to be performed against an empty database.
 * <br>
 * <p/>
 * The following Entries are tested:
 * <pre>
 * <b>Q03767</b>: no filter, no update to be done, no splice variant
 * <b>O01367</b>: no filter, no update to be done, 1 biosource, 2 splice variants
 * <b>P13953</b>: no filter, no update to be done, 2 biosources (so 2 proteins),
 *         2 splice variants (per biosource, so 4 in all)
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class UpdateProteinsTest extends TestCase {

    private static final String NEW_LINE = System.getProperty( "line.separator" );


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public UpdateProteinsTest( String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( UpdateProteinsTest.class );
    }


    IntactHelper helper;
    UpdateProteinsI proteinBuilder;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        try {

            helper = new IntactHelper();

            loadSharedObject(); // CVs ...

            // TODO: in order to avoid to have to write to the database, we could have a special implementation
            //       of the helper which implements the create, update ... but doesn't do anything ...
            proteinBuilder = new UpdateProteins( helper, false ); // no output
            proteinBuilder.setDebugOnScreen( false );

        } catch ( Exception e ) {
            fail( NEW_LINE +
                  "Could not load the IntAct Controlled Vocabulary." +
                  NEW_LINE +
                  "Reason: " + e.getMessage() );
        }
    }


    /**
     * Xref databases
     */
    private static CvDatabase uniprotDatabase;
    private static CvDatabase intactDatabase;
    private static CvDatabase sgdDatabase;
    private static CvDatabase goDatabase;
    private static CvDatabase interproDatabase;
    private static CvDatabase flybaseDatabase;

    /**
     * Describe wether an Xref is related the primary SPTR AC (identityCrefQualifier)
     * or not (secondaryXrefQualifier)
     */
    private static CvXrefQualifier identityXrefQualifier;
    private static CvXrefQualifier secondaryXrefQualifier;

    private CvXrefQualifier isoFormParentXrefQualifier;

    private CvTopic isoformComment;
    private CvAliasType isoformSynonym;

    private static CvAliasType geneNameAliasType;
    private static CvAliasType geneNameSynonymAliasType;


    private void loadSharedObject() throws IntactException {

        sgdDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "sgd" );
        if( sgdDatabase == null ) {
            fail( "Could not load CvDatabase: sgd" );
        }

        uniprotDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, CvDatabase.UNIPROT );
        if( uniprotDatabase == null ) {
            fail( "Could not load CvDatabase: uniprotkb" );
        }

        intactDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "intact" );
        if( intactDatabase == null ) {
            fail( "Could not load CvDatabase: intact" );
        }

        goDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "go" );
        if( goDatabase == null ) {
            fail( "Could not load CvDatabase: go" );
        }

        interproDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "interpro" );
        if( interproDatabase == null ) {
            fail( "Could not load CvDatabase: interpro" );
        }

        flybaseDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "flybase" );
        if( flybaseDatabase == null ) {
            fail( "Could not load CvDatabase: flybase" );
        }

        identityXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "identity" );
        if( identityXrefQualifier == null ) {
            fail( "Could not load CvXrefQualifier: identity" );
        }

        secondaryXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "secondary-ac" );
        if( secondaryXrefQualifier == null ) {
            fail( "Could not load CvXrefQualifier: secondary-ac" );
        }

        isoFormParentXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "isoform-parent" );
        if( secondaryXrefQualifier == null ) {
            fail( "Could not load CvXrefQualifier: isoform-parent" );
        }

        isoformComment = (CvTopic) helper.getObjectByLabel( CvTopic.class, "isoform-comment" );
        if( isoformComment == null ) {
            fail( "Could not load CvTopic: isoform-comment" );
        }

        isoformSynonym = (CvAliasType) helper.getObjectByLabel( CvAliasType.class, "isoform-synonym" );
        if( isoformSynonym == null ) {
            fail( "Could not load CvAliasType: isoform-synonym" );
        }

        geneNameAliasType = (CvAliasType) helper.getObjectByLabel( CvAliasType.class, "gene-name" );
        if( geneNameAliasType == null ) {
            fail( "Could not load CvAliasType: gene-name" );
        }

        geneNameSynonymAliasType = (CvAliasType) helper.getObjectByLabel( CvAliasType.class, "gene-name-synonym" );
        if( geneNameSynonymAliasType == null ) {
            fail( "Could not load CvAliasType: gene-name-synonym" );
        }
    }


    /**
     * Utility methods - Facilitate the test of the Protein content
     */
    private boolean confirmXref( Protein protein,
                                 String parentAc,
                                 CvDatabase db,
                                 CvXrefQualifier qualifier,
                                 String primaryId,
                                 String secondaryId ) {

        Collection xrefs = protein.getXrefs();
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();

            // check it out
            if( xref.getParentAc().equals( parentAc ) &&
                xref.getPrimaryId().equals( primaryId )
            ) {

                if( db != null ) {
                    if( !db.equals( xref.getCvDatabase() ) ) {
                        continue; // next!
                    }
                }

                if( qualifier != null ) {
                    if( !qualifier.equals( xref.getCvXrefQualifier() ) ) {
                        continue; // next
                    }
                }

                if( secondaryId != null ) {
                    if( !secondaryId.equals( xref.getSecondaryId() ) ) {
                        continue;
                    }
                }

                return true;
            }

        } // for

        fail( NEW_LINE +
              "An Xref which fits the description below should have been created for the protein: " +
              protein.getShortLabel() + "." + NEW_LINE +
              "parent: " + protein.getAc() + NEW_LINE +
              "Database: " + ( db != null ? db.getShortLabel() : "" ) + NEW_LINE +
              "XrefQualifier: " + ( qualifier != null ? qualifier.getShortLabel() : "" ) + NEW_LINE +
              "PrimaryId: " + primaryId + NEW_LINE +
              "SecondaryId; " + ( secondaryId != null ? secondaryId : "" ) );

        return false;
    }

    private boolean confirmAlias( Protein protein,
                                  String parentAc,
                                  CvAliasType type,
                                  String name ) {

        Collection aliases = protein.getAliases();
        for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
            Alias alias = (Alias) iterator.next();

            // check it out
            if( alias.getParentAc().equals( parentAc ) &&
                alias.getCvAliasType().equals( type ) &&
                alias.getName().equals( name )
            ) {
                return true;
            }

        } // for

        fail( NEW_LINE +
              "An Alias which fits the description below should have been createdfor the protein: " +
              protein.getShortLabel() + "." +
              NEW_LINE +
              "parent: " + protein.getAc() + NEW_LINE +
              "Type: " + type.getShortLabel() + NEW_LINE +
              "Name: " + name );

        return false;
    }


    // hasAnnotation


    /**
     * Search for a protein by shortlabel in a given collection.
     *
     * @param proteins   the collection ew are searching in (MUST contains 0..n Proteins object)
     * @param shortlabel the shortlabel of the protein we are looking for
     * @return the found protein or null (if not found)
     */
    private Protein search( Collection proteins, String shortlabel ) {

        // TODO: fail is several occurence of a Protein is found

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            if( protein.getShortLabel().equals( shortlabel ) ) {
                return protein;
            }
        }
        return null;
    }

    private Protein commonProteinTests( Collection proteins,
                                        String shortlabel, String fullname, String sequence,
                                        String taxid,
                                        int xrefCount,
                                        int aliasCount ) {

        Protein protein = search( proteins, shortlabel );

        if( protein == null ) {
            fail( NEW_LINE +
                  "A protein having the shortlabel: " + shortlabel + " should have been created." );
        }

        assertEquals( protein.getFullName(), fullname );

        assertEquals( protein.getSequence(), sequence );

        // Check BioSource
        BioSource bs = protein.getBioSource();
        if( bs == null ) {
            fail( NEW_LINE +
                  "no BioSource affected to the protein created for that entry, protein's shortlabel:" +
                  protein.getShortLabel() +
                  NEW_LINE + " The taxid should have been " + taxid + "." );
        }

        if( !bs.getTaxId().equals( taxid ) ) {
            fail( NEW_LINE +
                  "We were expecting to have the BioSource " + taxid + " affected to the protein having the shortlabel:" +
                  protein.getShortLabel() + NEW_LINE +
                  "Currently affected to " + bs.getTaxId() );
        }

        // check on Xref
        int size = 0;
        if( ( size = protein.getXrefs().size() ) != xrefCount ) {
            fail( NEW_LINE +
                  xrefCount + " Xref(s) were expected for " + protein.getShortLabel() +
                  ", " + size + " have been found." );
        }

        // Check on Alias
        size = 0;
        if( ( size = protein.getAliases().size() ) != aliasCount ) {
            fail( NEW_LINE +
                  aliasCount + " Alias(s) were expected for " + protein.getShortLabel() +
                  ", " + size + " have been found." );
        }

        return protein;
    }

    private Collection buildCollection( String entryText, String entryName, String taxidFilter, boolean update,
                                        int expectedItems )
            throws IllegalArgumentException {

        Collection proteins = null;

        MockInputStream mis = new MockInputStream();
        mis.setBuffer( entryText );


        try {
            proteins = proteinBuilder.insertSPTrProteins( (InputStream) mis, taxidFilter, update );
            mis.close();
            mis.verify();
        } catch ( IllegalArgumentException iae ) {
            throw iae;

        } catch ( Exception e ) {

            // anything else ... fail !
            fail( NEW_LINE +
                  "An exception has been thrown while trying to insert " + entryName + "." +
                  NEW_LINE +
                  "Reason: " + e.getMessage() );
        }

        if( proteins == null || proteins.size() != expectedItems ) {
            int size = 0;
            if( proteins != null ) {
                size = proteins.size();
            }
            fail( NEW_LINE +
                  "The insert of " + entryName + " should have created " + expectedItems + " created, however " + size +
                  " have been created." );
        }

        return proteins;
    }


    ///////////////////////
    // Test methods
    ///////////////////////

//    public void testInsertSPTrProteins_WithoutValidEntry() {
//        try {
//            buildCollection(null, null, null, false, 0 );
//            fail( "Not giving an Entry should not be allowed (entryAc=null)" );
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//
//        try {
//            buildCollection(null, "", null, false, 0 );
//            fail( "Not giving an Entry should not be allowed (entryAc=empty string)" );
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//
//        try {
//            buildCollection(null, "    ", null, false, 0 );
//            fail( "Not giving an Entry should not be allowed (entryAc=blank spaces)" );
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//    }

    /**
     * Insert of a simple protein (Q03767).
     * ie. no filter, no update to be done, no splice variant
     * <p/>
     * Below is the Entry
     * <p/>
     * <pre>
     * ID   NUM1_YEAST     STANDARD;      PRT;  2748 AA.
     * AC   Q00402; Q03767;
     * DT   01-DEC-1992 (Rel. 24, Created)
     * DT   10-OCT-2003 (Rel. 42, Last sequence update)
     * DT   10-OCT-2003 (Rel. 42, Last annotation update)
     * DE   Nuclear migration protein NUM1.
     * GN   NUM1 OR YDR150W OR YD8358.06.
     * OS   Saccharomyces cerevisiae (Baker's yeast).
     * OC   Eukaryota; Fungi; Ascomycota; Saccharomycotina; Saccharomycetes;
     * OC   Saccharomycetales; Saccharomycetaceae; Saccharomyces.
     * OX   NCBI_TaxID=4932;
     * RN   [1]
     * RP   SEQUENCE FROM N.A.
     * RC   STRAIN=ATCC 28383 / FL100;
     * RX   MEDLINE=92079907; PubMed=1745235;
     * RA   Kormanec J., Schaaff-Gerstenschlaeger I., Zimmermann F.K.,
     * RA   Perecko D., Kuentzel H.;
     * RT   "Nuclear migration in Saccharomyces cerevisiae is controlled by the
     * RT   highly repetitive 313 kDa NUM1 protein.";
     * RL   Mol. Gen. Genet. 230:277-287(1991).
     * RN   [2]
     * RP   SEQUENCE FROM N.A.
     * RC   STRAIN=S288c / FY1678;
     * RX   MEDLINE=97313263; PubMed=9169867;
     * RA   Jacq C., Alt-Moerbe J., Andre B., Arnold W., Bahr A., Ballesta J.P.G.,
     * RA   Bargues M., Baron L., Becker A., Biteau N., Bloecker H., Blugeon C.,
     * RA   Boskovic J., Brandt P., Brueckner M., Buitrago M.J., Coster F.,
     * RA   Delaveau T., del Rey F., Dujon B., Eide L.G., Garcia-Cantalejo J.M.,
     * RA   Goffeau A., Gomez-Peris A., Granotier C., Hanemann V., Hankeln T.,
     * RA   Hoheisel J.D., Jaeger W., Jimenez A., Jonniaux J.-L., Kraemer C.,
     * RA   Kuester H., Laamanen P., Legros Y., Louis E.J., Moeller-Rieker S.,
     * RA   Monnet A., Moro M., Mueller-Auer S., Nussbaumer B., Paricio N.,
     * RA   Paulin L., Perea J., Perez-Alonso M., Perez-Ortin J.E., Pohl T.M.,
     * RA   Prydz H., Purnelle B., Rasmussen S.W., Remacha M., Revuelta J.L.,
     * RA   Rieger M., Salom D., Saluz H.P., Saiz J.E., Saren A.-M., Schaefer M.,
     * RA   Scharfe M., Schmidt E.R., Schneider C., Scholler P., Schwarz S.,
     * RA   Urrestarazu L.A., Verhasselt P., Vissers S., Voet M., Volckaert G.,
     * RA   Wagner G., Wambutt R., Wedler E., Wedler H., Woelfl S., Harris D.E.,
     * RA   Bowman S., Brown D., Churcher C.M., Connor R., Dedman K., Gentles S.,
     * RA   Hamlin N., Hunt S., Jones L., McDonald S., Murphy L., Niblett D.,
     * RA   Odell C., Oliver K., Rajandream M.A., Richards C., Shore L.,
     * RA   Walsh S.V., Barrell B.G., Dietrich F.S., Mulligan J.T., Allen E.,
     * RA   Araujo R., Aviles E., Berno A., Carpenter J., Chen E., Cherry J.M.,
     * RA   Chung E., Duncan M., Hunicke-Smith S., Hyman R.W., Komp C.,
     * RA   Lashkari D., Lew H., Lin D., Mosedale D., Nakahara K., Namath A.,
     * RA   Oefner P., Oh C., Petel F.X., Roberts D., Schramm S., Schroeder M.,
     * RA   Shogren T., Shroff N., Winant A., Yelton M.A., Botstein D.,
     * RA   Davis R.W., Johnston M., Andrews S., Brinkman R., Cooper J., Ding H.,
     * RA   Du Z., Favello A., Fulton L., Gattung S., Greco T., Hallsworth K.,
     * RA   Hawkins J., Hillier L.W., Jier M., Johnson D., Johnston L.,
     * RA   Kirsten J., Kucaba T., Langston Y., Latreille P., Le T., Mardis E.,
     * RA   Menezes S., Miller N., Nhan M., Pauley A., Peluso D., Rifkin L.,
     * RA   Riles L., Taich A., Trevaskis E., Vignati D., Wilcox L., Wohldman P.,
     * RA   Vaudin M., Wilson R., Waterston R., Albermann K., Hani J., Heumann K.,
     * RA   Kleine K., Mewes H.-W., Zollner A., Zaccaria P.;
     * RT   "The nucleotide sequence of Saccharomyces cerevisiae chromosome IV.";
     * RL   Nature 387:75-78(1997).
     * CC   -!- FUNCTION: Controls nuclear migration. NUM1 specifically controls
     * CC       the interaction of the bud neck cytoskeleton with the pre-
     * CC       divisional G2 nucleus perhaps by recognizing G2-specific
     * CC       cytoplasmic microtubuli or other components of the nuclear
     * CC       envelope.
     * CC   -!- MISCELLANEOUS: Additional regions of lower homology to the repeat
     * CC       consensus (always starting with proline) are found in both
     * CC       flanking domains of the tandem repeats.
     * CC   -!- SIMILARITY: Contains 1 PH domain.
     * CC   --------------------------------------------------------------------------
     * CC   This SWISS-PROT entry is copyright. It is produced through a collaboration
     * CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -
     * CC   the European Bioinformatics Institute.  There are no  restrictions on  its
     * CC   use  by  non-profit  institutions as long  as its content  is  in  no  way
     * CC   modified and this statement is not removed.  Usage  by  and for commercial
     * CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/
     * CC   or send an email to license@isb-sib.ch).
     * CC   --------------------------------------------------------------------------
     * DR   EMBL; X61236; CAA43554.1; -.
     * DR   EMBL; Z50046; CAA90372.1; -.
     * DR   GermOnline; 140641; -.
     * DR   SGD; S0002557; NUM1.
     * DR   GO; GO:0005934; C:bud tip; IDA.
     * DR   GO; GO:0005938; C:cell cortex; IDA.
     * DR   GO; GO:0015631; F:tubulin binding; IPI.
     * DR   GO; GO:0000226; P:microtubule cytoskeleton organization and b...; IMP.
     * DR   GO; GO:0000065; P:nuclear migration (sensu Saccharomyces); IMP.
     * DR   InterPro; IPR005529; DUF321.
     * DR   InterPro; IPR001849; PH.
     * DR   Pfam; PF03778; DUF321; 13.
     * DR   Pfam; PF00169; PH; 1.
     * DR   SMART; SM00233; PH; 1.
     * DR   PROSITE; PS50003; PH_DOMAIN; 1.
     * KW   Repeat.
     * FT   DOMAIN      593   1384       12.5 X TANDEM REPEATS.
     * FT   REPEAT      593    656       1.
     * FT   REPEAT      657    727       2.
     * FT   REPEAT      728    798       3.
     * FT   REPEAT      799    862       4.
     * FT   REPEAT      863    926       5.
     * FT   REPEAT      927    990       6.
     * FT   REPEAT      991   1054       7.
     * FT   REPEAT     1055   1118       8.
     * FT   REPEAT     1119   1182       9.
     * FT   REPEAT     1183   1246       10.
     * FT   REPEAT     1247   1310       11.
     * FT   REPEAT     1311   1374       12.
     * FT   REPEAT     1375   1384       13 (INCOMPLETE).
     * FT   DOMAIN     2573   2683       PH.
     * FT   CONFLICT   1570   1570       A -> V (in Ref. 1).
     * FT   CONFLICT   1822   1822       E -> K (in Ref. 1).
     * FT   CONFLICT   1960   1962       KAS -> RHL (in Ref. 1).
     * FT   CONFLICT   1971   1972       KD -> RN (in Ref. 1).
     * FT   CONFLICT   2049   2049       S -> N (in Ref. 1).
     * FT   CONFLICT   2637   2637       V -> A (in Ref. 1).
     * SQ   SEQUENCE   2748 AA;  313030 MW;  EB4E48F950621142 CRC64;
     * MSHNNRHKKN NDKDSSAGQY ANSIDNSLSQ ESVSTNGVTR MANLKADECG SGDEGDKTKR
     * FSISSILSKR ETKDVLPEFA GSSSHNGVLT ANSSKDMNFT LELSENLLVE CRKLQSSNEA
     * KNEQIKSLKQ IKESLSDKIE ELTNQKKSFM KELDSTKDLN WDLESKLTNL SMECRQLKEL
     * KKKTEKSWND EKESLKLLKT DLEILTLTKN GMENDLSSQK LHYDKEISEL KERILDLNNE
     * NDRLLISVSD LTSEINSLQS NRTERIKIQK QLDDAKASIS SLKRKVQKKY YQKQHTSDTT
     * VTSDPDSEGT TSEEDIFDIV IEIDHMIETG PSVEDISEDL VKKYSEKNNM ILLSNDSYKN
     * LLQKSESASK PKDDELMTKE VAENLNMIAL PNDDNYSKKE FSLESHIKYL EASGYKVLPL
     * EEFENLNESL SNPSYNYLKE KLQALKKIPI DQSTFNLLKE PTIDFLLPLT SKIDCLIIPT
     * KDYNDLFESV KNPSIEQMKK CLEAKNDLQS NICKWLEERN GCKWLSNDLY FSMVNKIETP
     * SKQYLSDKAK EYDQVLIDTK ALEGLKNPTI DFLREKASAS DYLLLKKEDY VSPSLEYLVE
     * HAKATNHHLL SDSAYEDLVK CKENPDMEFL KEKSAKLGHT VVSNEAYSEL EKKLEQPSLE
     * YLVEHAKATN HHLLSDSAYE DLVKCKENPD MEFLKEKSAK LGHTVVSNEA YSELQRKYSE
     * LEKEVEQPSL AYLVEHAKAT DHHLLSDSAY EDLVKCKENP DVEFLKEKSA KLGHTVVSSE
     * EYSELQRKYS ELEKEVEQPS LAYLVEHAKA TDHHLLSDSA YEELVKCKEN PDMEFLKEKS
     * AKLGHTVVSN EAYSELEKKL EQPSLAYLVE HAKATDHHLL SDSAYEDLVK CKENSDVEFL
     * KEKSAKLGHT VVSNEAYSEL EKKLEQPSLA YLVEHAKATD HHLLSDSAYE DLVKCKENPD
     * MEFLKEKSAK LGHTVVSNEA YSELEKKLEQ PSLEYLVEHA KATNHHLLSD SAYEDLVKCK
     * ENPDMEFLKE KSAKLGHTVV SNEAYSELEK KLEQPSLEYL VEHAKATNHH LLSDSAYEEL
     * VKCKENPDVE FLKEKSAKLG HTVVSNEAYS ELEKKLEQPS LEYLVEHAKA TNHHLLSDSA
     * YEELVKCKEN PDVEFLKEKS AKLGHTVVSN EAYSELEKKL EQPSLAYLVE HAKATDHHLL
     * SDSAYEDLVK CKENPDVEFL KEKSAKLGHT VVSNEAYSEL EKKLEQPSLA YLVEHAKATD
     * HHLLSDSAYE DLVKCKENPD MEFLKEKSAK LGHTVVSNEA YSELEKKLEQ PSLEYLVEHA
     * KATNHHLLSD SAYEDLVKCK ENPDMEFLKE KSAKLGHTVV SNKEYSELEK KLEQPSLEYL
     * VKHAEQIQSK IISISDFNTL ANPSMEDMAS KLQKLEYQIV SNDEYIALKN TMEKPDVELL
     * RSKLKGYHII DTTTYNELVS NFNSPTLKFI EEKAKSKGYR LIEPNEYLDL NRIATTPSKE
     * EIDNFCKQIG CYALDSKEYE RLKNSLENPS KKFIEENAAL LDLVLVDKTE YQAMKDNASN
     * KKSLIPSTKA LDFVTMPAPQ LASAEKSSLQ KRTLSDIENE LKALGYVAIR KENLPNLEKP
     * IVDNASKNDV LNLCSKFSLV PLSTEEYDNM RKEHTKILNI LGDPSIDFLK EKCEKYQMLI
     * ISKHDYEEKQ EAIENPGYEF ILEKASALGY ELVSEVELDR MKQMIDSPDI DYMQEKAARN
     * EMVLLRNEEK EALQKKIEYP SLTFLIEKAA GMNKILVDQI EYDETIRKCN HPTRMELEES
     * CHHLNLVLLD QNEYSTLREP LENRNVEDLI NTLSKLNYIA IPNTIYQDLI GKYENPNFDY
     * LKDSLNKMDY VAISRQDYEL MVAKYEKPQL DYLKISSEKI DHIVVPLSEY NLMVTNYRNP
     * SLSYLKEKAV LNNHILIKED DYKNILAVSE HPTVIHLSEK ASLLNKVLVD KDDFATMSRS
     * IEKPTIDFLS TKALSMGKIL VNESTHKRNE KLLSEPDSEF LTMKAKEQGL IIISEKEYSE
     * LRDQIDRPSL DVLKEKAAIF DSIIVENIEY QQLVNTTSPC PPITYEDLKV YAHQFGMELC
     * LQKPNKLSGA ERAERIDEQS INTTSSNSTT TSSMFTDALD DNIEELNRVE LQNNEDYTDI
     * ISKSSTVKDA TIFIPAYENI KNSAEKLGYK LVPFEKSNIN LKNIEAPLFS KDNDDTSVAS
     * SIDLDHLSRK AEKYGMTLIS DQEFEEYHIL KDNAVNLNGG MEEMNNPLSE NQNLAAKTTN
     * TAQEGAFQNT VPHNDMDNEE VEYGPDDPTF TVRQLKKPAG DRNLILTSRE KTLLSRDDNI
     * MSQNEAVYGD DISDSFVDES QEIKNDVDII KTQAMKYGML CIPESNFVGA SYASAQDMSD
     * IVVLSASYYH NLMSPEDMKW NCVSNEELQA EVKKRGLQIA LTTKEDKKGQ ATASKHEYVS
     * HKLNNKTSTV STKSGAKKGL AEAAATTAYE DSESHPQIEE QSHRTNHHKH HKRQQSLNSN
     * STSKTTHSSR NTPASRRDIV ASFMSRAGSA SRTASLQTLA SLNEPSIIPA LTQTVIGEYL
     * FKYYPRLGPF GFESRHERFF WVHPYTLTLY WSASNPILEN PANTKTKGVA ILGVESVTDP
     * NPYPTGLYHK SIVVTTETRT IKFTCPTRQR HNIWYNSLRY LLQRNMQGIS LEDIADDPTD
     * NMYSGKIFPL PGENTKSSSK RLSASRRSVS TRSLRHRVPQ SRSFGNLR
     * //
     * </pre>
     */
    public void testInsertSPTrProteins_Q03767() {

        insertSPTrProteins_Q03767_with_BioSourceFilter();
        insertSPTrProteins_Q03767_without_filter();
    }

    private void insertSPTrProteins_Q03767_without_filter() {

        Collection proteins = buildCollection( MockEntryText.Q03767, "Q03767", null, false, 1 );

        String sequence =
                "MSHNNRHKKNNDKDSSAGQYANSIDNSLSQESVSTNGVTRMANLKADECGSGDEGDKTKRFSISSILSKRETKDVLPEFAGSSSHNGVLTA" +
                "NSSKDMNFTLELSENLLVECRKLQSSNEAKNEQIKSLKQIKESLSDKIEELTNQKKSFMKELDSTKDLNWDLESKLTNLSMECRQLKELKKKTEKSWNDEK" +
                "ESLKLLKTDLEILTLTKNGMENDLSSQKLHYDKEISELKERILDLNNENDRLLISVSDLTSEINSLQSNRTERIKIQKQLDDAKASISSLKRKVQKKYYQK" +
                "QHTSDTTVTSDPDSEGTTSEEDIFDIVIEIDHMIETGPSVEDISEDLVKKYSEKNNMILLSNDSYKNLLQKSESASKPKDDELMTKEVAENLNMIALPNDD" +
                "NYSKKEFSLESHIKYLEASGYKVLPLEEFENLNESLSNPSYNYLKEKLQALKKIPIDQSTFNLLKEPTIDFLLPLTSKIDCLIIPTKDYNDLFESVKNPSI" +
                "EQMKKCLEAKNDLQSNICKWLEERNGCKWLSNDLYFSMVNKIETPSKQYLSDKAKEYDQVLIDTKALEGLKNPTIDFLREKASASDYLLLKKEDYVSPSLE" +
                "YLVEHAKATNHHLLSDSAYEDLVKCKENPDMEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLEYLVEHAKATNHHLLSDSAYEDLVKCKENPDMEFLKEK" +
                "SAKLGHTVVSNEAYSELQRKYSELEKEVEQPSLAYLVEHAKATDHHLLSDSAYEDLVKCKENPDVEFLKEKSAKLGHTVVSSEEYSELQRKYSELEKEVEQ" +
                "PSLAYLVEHAKATDHHLLSDSAYEELVKCKENPDMEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLAYLVEHAKATDHHLLSDSAYEDLVKCKENSDVEF" +
                "LKEKSAKLGHTVVSNEAYSELEKKLEQPSLAYLVEHAKATDHHLLSDSAYEDLVKCKENPDMEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLEYLVEHA" +
                "KATNHHLLSDSAYEDLVKCKENPDMEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLEYLVEHAKATNHHLLSDSAYEELVKCKENPDVEFLKEKSAKLGH" +
                "TVVSNEAYSELEKKLEQPSLEYLVEHAKATNHHLLSDSAYEELVKCKENPDVEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLAYLVEHAKATDHHLLSD" +
                "SAYEDLVKCKENPDVEFLKEKSAKLGHTVVSNEAYSELEKKLEQPSLAYLVEHAKATDHHLLSDSAYEDLVKCKENPDMEFLKEKSAKLGHTVVSNEAYSE" +
                "LEKKLEQPSLEYLVEHAKATNHHLLSDSAYEDLVKCKENPDMEFLKEKSAKLGHTVVSNKEYSELEKKLEQPSLEYLVKHAEQIQSKIISISDFNTLANPS" +
                "MEDMASKLQKLEYQIVSNDEYIALKNTMEKPDVELLRSKLKGYHIIDTTTYNELVSNFNSPTLKFIEEKAKSKGYRLIEPNEYLDLNRIATTPSKEEIDNF" +
                "CKQIGCYALDSKEYERLKNSLENPSKKFIEENAALLDLVLVDKTEYQAMKDNASNKKSLIPSTKALDFVTMPAPQLASAEKSSLQKRTLSDIENELKALGY" +
                "VAIRKENLPNLEKPIVDNASKNDVLNLCSKFSLVPLSTEEYDNMRKEHTKILNILGDPSIDFLKEKCEKYQMLIISKHDYEEKQEAIENPGYEFILEKASA" +
                "LGYELVSEVELDRMKQMIDSPDIDYMQEKAARNEMVLLRNEEKEALQKKIEYPSLTFLIEKAAGMNKILVDQIEYDETIRKCNHPTRMELEESCHHLNLVL" +
                "LDQNEYSTLREPLENRNVEDLINTLSKLNYIAIPNTIYQDLIGKYENPNFDYLKDSLNKMDYVAISRQDYELMVAKYEKPQLDYLKISSEKIDHIVVPLSE" +
                "YNLMVTNYRNPSLSYLKEKAVLNNHILIKEDDYKNILAVSEHPTVIHLSEKASLLNKVLVDKDDFATMSRSIEKPTIDFLSTKALSMGKILVNESTHKRNE" +
                "KLLSEPDSEFLTMKAKEQGLIIISEKEYSELRDQIDRPSLDVLKEKAAIFDSIIVENIEYQQLVNTTSPCPPITYEDLKVYAHQFGMELCLQKPNKLSGAE" +
                "RAERIDEQSINTTSSNSTTTSSMFTDALDDNIEELNRVELQNNEDYTDIISKSSTVKDATIFIPAYENIKNSAEKLGYKLVPFEKSNINLKNIEAPLFSKD" +
                "NDDTSVASSIDLDHLSRKAEKYGMTLISDQEFEEYHILKDNAVNLNGGMEEMNNPLSENQNLAAKTTNTAQEGAFQNTVPHNDMDNEEVEYGPDDPTFTVR" +
                "QLKKPAGDRNLILTSREKTLLSRDDNIMSQNEAVYGDDISDSFVDESQEIKNDVDIIKTQAMKYGMLCIPESNFVGASYASAQDMSDIVVLSASYYHNLMS" +
                "PEDMKWNCVSNEELQAEVKKRGLQIALTTKEDKKGQATASKHEYVSHKLNNKTSTVSTKSGAKKGLAEAAATTAYEDSESHPQIEEQSHRTNHHKHHKRQQ" +
                "SLNSNSTSKTTHSSRNTPASRRDIVASFMSRAGSASRTASLQTLASLNEPSIIPALTQTVIGEYLFKYYPRLGPFGFESRHERFFWVHPYTLTLYWSASNP" +
                "ILENPANTKTKGVAILGVESVTDPNPYPTGLYHKSIVVTTETRTIKFTCPTRQRHNIWYNSLRYLLQRNMQGISLEDIADDPTDNMYSGKIFPLPGENTKS" +
                "SSKRLSASRRSVSTRSLRHRVPQSRSFGNLR";

        Protein protein = commonProteinTests( proteins,
                                              "num1_yeast", "Nuclear migration protein NUM1", sequence,
                                              "4932", // this is yeast
                                              10, 3 );

        String ac = protein.getAc();

        // Check Xrefs
        confirmXref( protein, ac, uniprotDatabase, identityXrefQualifier, "Q00402", "num1_yeast" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "Q03767", "num1_yeast" );
        confirmXref( protein, ac, goDatabase, null, "GO:0005934", "C:bud tip" );
        confirmXref( protein, ac, goDatabase, null, "GO:0005938", "C:cell cortex" );
        confirmXref( protein, ac, goDatabase, null, "GO:0015631", "F:tubulin binding" );
        confirmXref( protein, ac, goDatabase, null, "GO:0000226", "P:microtubule cytoskeleton org" );
        confirmXref( protein, ac, goDatabase, null, "GO:0000065", "P:nuclear migration (sensu Sac" );
        confirmXref( protein, ac, sgdDatabase, null, "S0002557", "NUM1" );
        confirmXref( protein, ac, interproDatabase, null, "IPR005529", "DUF321" );
        confirmXref( protein, ac, interproDatabase, null, "IPR001849", "PH" );

        // Check Alias
        confirmAlias( protein, ac, geneNameAliasType, "num1" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "ydr150w" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "yd8358.06" );

    } // Q03776


    /**
     * Insert of a simple protein with a specific filter that prevent any Protein from being created.
     * ie. SPTREntry contains 'yeast', the given filter is 'human'
     */
    private void insertSPTrProteins_Q03767_with_BioSourceFilter() {
        //TODO: try to be sure that this one is ran before the creation of the protein happen
        // we don't want the builder to retreive it from the database !!
        buildCollection( MockEntryText.Q03767, "Q03767", "9606", false, 0 );
    }


    /**
     * Insert of a slightly more complex protein (O01367).
     * ie. no filter, no update to be done, 1 biosource, 2 splice variants
     * <p/>
     * Below is the ENtry
     * <pre>
     * ID   HOW_DROME      STANDARD;      PRT;   405 AA.
     * AC   O01367; O02392; P91680; Q8IN11; Q8T999; Q94539;
     * DT   10-OCT-2003 (Rel. 42, Created)
     * DT   10-OCT-2003 (Rel. 42, Last sequence update)
     * DT   10-OCT-2003 (Rel. 42, Last annotation update)
     * DE   Held out wings protein (KH-domain protein KH93F) (Putative RNA-binding
     * DE   protein) (Muscle-specific protein) (Wings held out protein) (Struthio
     * DE   protein) (Quaking-related 93F).
     * GN   HOW OR WHO OR STRU OR KH93F OR QKR93F OR CG10293.
     * OS   Drosophila melanogaster (Fruit fly).
     * OC   Eukaryota; Metazoa; Arthropoda; Hexapoda; Insecta; Pterygota;
     * OC   Neoptera; Endopterygota; Diptera; Brachycera; Muscomorpha;
     * OC   Ephydroidea; Drosophilidae; Drosophila.
     * OX   NCBI_TaxID=7227;
     * RN   [1]
     * RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), FUNCTION, INDUCTION, TISSUE
     * RP   SPECIFICITY, AND MUTAGENESIS OF ARG-185.
     * RX   MEDLINE=97236479; PubMed=9118803;
     * RA   Baehrecke E.H.;
     * RT   "who encodes a KH RNA binding protein that functions in muscle
     * RT   development.";
     * RL   Development 124:1323-1332(1997).
     * RN   [2]
     * RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), FUNCTION, AND SUBCELLULAR
     * RP   LOCATION.
     * RC   STRAIN=Oregon-R;
     * RX   MEDLINE=97313250; PubMed=9169854;
     * RA   Zaffran S., Astier M., Gratecos D., Semeriva M.;
     * RT   "The held out wings (how) Drosophila gene encodes a putative RNA-
     * RT   binding protein involved in the control of muscular and cardiac
     * RT   activity.";
     * RL   Development 124:2087-2098(1997).
     * RN   [3]
     * RP   SEQUENCE FROM N.A. (ISOFORMS MATERNAL AND ZYGOTIC), FUNCTION, TISSUE
     * RP   SPECIFICITY, AND DEVELOPMENTAL STAGE.
     * RC   TISSUE=Embryo;
     * RX   MEDLINE=98008900; PubMed=9344542;
     * RA   Lo P.C.H., Frasch M.;
     * RT   "A novel KH-domain protein mediates cell adhesion processes in
     * RT   Drosophila.";
     * RL   Dev. Biol. 190:241-256(1997).
     * RN   [4]
     * RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), TISSUE SPECIFICITY, AND
     * RP   DEVELOPMENTAL STAGE.
     * RC   STRAIN=Canton-S; TISSUE=Embryo;
     * RX   MEDLINE=97473527; PubMed=9332381;
     * RA   Fyrberg C., Becker J., Barthmaier P., Mahaffey J., Fyrberg E.;
     * RT   "A Drosophila muscle-specific gene related to the mouse quaking
     * RT   locus.";
     * RL   Gene 197:315-323(1997).
     * RN   [5]
     * RP   SEQUENCE FROM N.A.
     * RC   STRAIN=Berkeley;
     * RX   MEDLINE=20196006; PubMed=10731132;
     * RA   Adams M.D., Celniker S.E., Holt R.A., Evans C.A., Gocayne J.D.,
     * RA   Amanatides P.G., Scherer S.E., Li P.W., Hoskins R.A., Galle R.F.,
     * RA   George R.A., Lewis S.E., Richards S., Ashburner M., Henderson S.N.,
     * RA   Sutton G.G., Wortman J.R., Yandell M.D., Zhang Q., Chen L.X.,
     * RA   Brandon R.C., Rogers Y.-H.C., Blazej R.G., Champe M., Pfeiffer B.D.,
     * RA   Wan K.H., Doyle C., Baxter E.G., Helt G., Nelson C.R., Miklos G.L.G.,
     * RA   Abril J.F., Agbayani A., An H.-J., Andrews-Pfannkoch C., Baldwin D.,
     * RA   Ballew R.M., Basu A., Baxendale J., Bayraktaroglu L., Beasley E.M.,
     * RA   Beeson K.Y., Benos P.V., Berman B.P., Bhandari D., Bolshakov S.,
     * RA   Borkova D., Botchan M.R., Bouck J., Brokstein P., Brottier P.,
     * RA   Burtis K.C., Busam D.A., Butler H., Cadieu E., Center A., Chandra I.,
     * RA   Cherry J.M., Cawley S., Dahlke C., Davenport L.B., Davies P.,
     * RA   de Pablos B., Delcher A., Deng Z., Mays A.D., Dew I., Dietz S.M.,
     * RA   Dodson K., Doup L.E., Downes M., Dugan-Rocha S., Dunkov B.C., Dunn P.,
     * RA   Durbin K.J., Evangelista C.C., Ferraz C., Ferriera S., Fleischmann W.,
     * RA   Fosler C., Gabrielian A.E., Garg N.S., Gelbart W.M., Glasser K.,
     * RA   Glodek A., Gong F., Gorrell J.H., Gu Z., Guan P., Harris M.,
     * RA   Harris N.L., Harvey D.A., Heiman T.J., Hernandez J.R., Houck J.,
     * RA   Hostin D., Houston K.A., Howland T.J., Wei M.-H., Ibegwam C.,
     * RA   Jalali M., Kalush F., Karpen G.H., Ke Z., Kennison J.A., Ketchum K.A.,
     * RA   Kimmel B.E., Kodira C.D., Kraft C., Kravitz S., Kulp D., Lai Z.,
     * RA   Lasko P., Lei Y., Levitsky A.A., Li J.H., Li Z., Liang Y., Lin X.,
     * RA   Liu X., Mattei B., McIntosh T.C., McLeod M.P., McPherson D.,
     * RA   Merkulov G., Milshina N.V., Mobarry C., Morris J., Moshrefi A.,
     * RA   Mount S.M., Moy M., Murphy B., Murphy L., Muzny D.M., Nelson D.L.,
     * RA   Nelson D.R., Nelson K.A., Nixon K., Nusskern D.R., Pacleb J.M.,
     * RA   Palazzolo M., Pittman G.S., Pan S., Pollard J., Puri V., Reese M.G.,
     * RA   Reinert K., Remington K., Saunders R.D.C., Scheeler F., Shen H.,
     * RA   Shue B.C., Siden-Kiamos I., Simpson M., Skupski M.P., Smith T.,
     * RA   Spier E., Spradling A.C., Stapleton M., Strong R., Sun E.,
     * RA   Svirskas R., Tector C., Turner R., Venter E., Wang A.H., Wang X.,
     * RA   Wang Z.-Y., Wassarman D.A., Weinstock G.M., Weissenbach J.,
     * RA   Williams S.M., Woodage T., Worley K.C., Wu D., Yang S., Yao Q.A.,
     * RA   Ye J., Yeh R.-F., Zaveri J.S., Zhan M., Zhang G., Zhao Q., Zheng L.,
     * RA   Zheng X.H., Zhong F.N., Zhong W., Zhou X., Zhu S., Zhu X., Smith H.O.,
     * RA   Gibbs R.A., Myers E.W., Rubin G.M., Venter J.C.;
     * RT   "The genome sequence of Drosophila melanogaster.";
     * RL   Science 287:2185-2195(2000).
     * RN   [6]
     * RP   REVISIONS, AND ALTERNATIVE SPLICING.
     * RX   MEDLINE=22426069; PubMed=12537572;
     * RA   Misra S., Crosby M.A., Mungall C.J., Matthews B.B., Campbell K.S.,
     * RA   Hradecky P., Huang Y., Kaminker J.S., Millburn G.H., Prochnik S.E.,
     * RA   Smith C.D., Tupy J.L., Whitfield E.J., Bayraktaroglu L., Berman B.P.,
     * RA   Bettencourt B.R., Celniker S.E., de Grey A.D.N.J., Drysdale R.A.,
     * RA   Harris N.L., Richter J., Russo S., Schroeder A.J., Shu S.Q.,
     * RA   Stapleton M., Yamada C., Ashburner M., Gelbart W.M., Rubin G.M.,
     * RA   Lewis S.E.;
     * RT   "Annotation of the Drosophila melanogaster euchromatic genome: a
     * RT   systematic review.";
     * RL   Genome Biol. 3:RESEARCH0083.1-RESEARCH0083.22(2002).
     * RN   [7]
     * RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC).
     * RC   STRAIN=Berkeley; TISSUE=Embryo;
     * RX   MEDLINE=22426066; PubMed=12537569;
     * RA   Stapleton M., Carlson J.W., Brokstein P., Yu C., Champe M.,
     * RA   George R.A., Guarin H., Kronmiller B., Pacleb J.M., Park S., Wan K.H.,
     * RA   Rubin G.M., Celniker S.E.;
     * RT   "A Drosophila full-length cDNA resource.";
     * RL   Genome Biol. 3:RESEARCH0080.1-RESEARCH0080.8(2002).
     * CC   -!- FUNCTION: Required for integrin-mediated cell-adhesion in wing
     * CC       blade. Vital role in steroid regulation of muscle development and
     * CC       to control heart rate. Required during embryogenesis, in late
     * CC       stages of somatic muscle development, for myotube migration and
     * CC       during metamorphosis for muscle reorganization.
     * CC   -!- SUBCELLULAR LOCATION: Nuclear.
     * CC   -!- ALTERNATIVE PRODUCTS:
     * CC       Event=Alternative splicing; Named isoforms=2;
     * CC       Name=Zygotic; Synonyms=A;
     * CC         IsoId=O01367-1; Sequence=Displayed;
     * CC       Name=Maternal; Synonyms=B;
     * CC         IsoId=O01367-2; Sequence=VSP_050197;
     * CC   -!- TISSUE SPECIFICITY: During embryogenesis, expression is seen in
     * CC       mesodermal precursors of somatic, visceral and pharyngeal muscle.
     * CC       Later in embryogenesis, expression is restricted to heart and
     * CC       muscle attachment sites of the epidermis. During onset of
     * CC       metamorphosis, expression is seen in muscle and muscle attachment
     * CC       cells.
     * CC   -!- DEVELOPMENTAL STAGE: Expressed both maternally and zygotically
     * CC       during embryonic, larval and pupal development.
     * CC   -!- INDUCTION: By 20-hydroxyecdysone at the onset of metamorphosis.
     * CC   -!- MISCELLANEOUS: Mutants exhibit wing blisters and flight
     * CC       impairment.
     * CC   -!- SIMILARITY: Contains 1 KH domain.
     * CC   --------------------------------------------------------------------------
     * CC   This SWISS-PROT entry is copyright. It is produced through a collaboration
     * CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -
     * CC   the European Bioinformatics Institute.  There are no  restrictions on  its
     * CC   use  by  non-profit  institutions as long  as its content  is  in  no  way
     * CC   modified and this statement is not removed.  Usage  by  and for commercial
     * CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/
     * CC   or send an email to license@isb-sib.ch).
     * CC   --------------------------------------------------------------------------
     * DR   EMBL; U85651; AAB51251.1; -.
     * DR   EMBL; U72331; AAB17350.1; -.
     * DR   EMBL; AF003106; AAB60946.1; -.
     * DR   EMBL; AF003107; AAB60947.1; -.
     * DR   EMBL; U87150; AAB47553.1; -.
     * DR   EMBL; AE003737; AAF55952.1; -.
     * DR   EMBL; AE003737; AAN13901.1; -.
     * DR   EMBL; AY069862; AAL40007.1; -.
     * DR   FlyBase; FBgn0017397; how.
     * DR   GO; GO:0005634; C:nucleus; IDA.
     * DR   GO; GO:0003723; F:RNA binding; IDA.
     * DR   GO; GO:0007155; P:cell adhesion; IMP.
     * DR   GO; GO:0016477; P:cell migration; IMP.
     * DR   GO; GO:0016203; P:muscle attachment; IMP.
     * DR   GO; GO:0007521; P:muscle cell fate determination; IDA.
     * DR   GO; GO:0008016; P:regulation of heart rate; IMP.
     * DR   InterPro; IPR004087; KH_dom.
     * DR   InterPro; IPR004088; KH_type_1.
     * DR   Pfam; PF00013; KH; 1.
     * DR   SMART; SM00322; KH; 1.
     * DR   PROSITE; PS50084; KH_TYPE_1; 1.
     * KW   Developmental protein; Nuclear protein; RNA-binding;
     * KW   Alternative splicing.
     * FT   DOMAIN       11     73       Gln-rich.
     * FT   DOMAIN      142    210       KH.
     * FT   VARSPLIC    370    405       VGAIKQQRRLATNREHPYQRATVGVPAKPAGFIEIQ -> G
     * FT                                GLFAR (in isoform Maternal).
     * FT                                /FTId=VSP_050197.
     * FT   MUTAGEN     185    185       R->C: In allele how-E44; embryonic
     * FT                                lethal.
     * FT   CONFLICT     33     33       Q -> QQA (in Ref. 3).
     * FT   CONFLICT     46     46       Q -> QAQ (in Ref. 7).
     * FT   CONFLICT     52     52       P -> S (in Ref. 7).
     * FT   CONFLICT    338    339       QT -> RA (in Ref. 4).
     * FT   CONFLICT    370    370       Missing (in Ref. 2, 4 and 7).
     * FT   CONFLICT    384    384       E -> A (in Ref. 4).
     * SQ   SEQUENCE   405 AA;  44325 MW;  DCA3A29A12D1A55E CRC64;
     * MSVCESKAVV QQQLQQHLQQ QAAAAVVAVA QQQQAQAQAQ AQAQAQQQQQ APQVVVPMTP
     * QHLTPQQQQQ STQSIADYLA QLLKDRKQLA AFPNVFTHVE RLLDEEIARV RASLFQINGV
     * KKEPLTLPEP EGSVVTMNEK VYVPVREHPD FNFVGRILGP RGMTAKQLEQ ETGCKIMVRG
     * KGSMRDKKKE DANRGKPNWE HLSDDLHVLI TVEDTENRAT VKLAQAVAEV QKLLVPQAEG
     * EDELKKRQLM ELAIINGTYR DTTAKSVAVC DEEWRRLVAA SDSRLLTSTG LPGLAAQIRA
     * PAAAPLGAPL ILNPRMTVPT TAASILSAQA APTAAFDQTG HGMIFAPYDY ANYAALAGNP
     * LLTEYADHSV GAIKQQRRLA TNREHPYQRA TVGVPAKPAG FIEIQ
     * //
     * </pre>
     */
    public void testInsertSPTrProteins_O01367() {

        Collection proteins = buildCollection( MockEntryText.O01367, "O01367", null, false, 3 );

        /////////////////////////
        // Check Protein (1)
        /////////////////////////

        String sequence =
                "MSVCESKAVVQQQLQQHLQQQAAAAVVAVAQQQQAQAQAQAQAQAQQQQQAPQVVVPMTP" +
                "QHLTPQQQQQSTQSIADYLAQLLKDRKQLAAFPNVFTHVERLLDEEIARVRASLFQINGV" +
                "KKEPLTLPEPEGSVVTMNEKVYVPVREHPDFNFVGRILGPRGMTAKQLEQETGCKIMVRG" +
                "KGSMRDKKKEDANRGKPNWEHLSDDLHVLITVEDTENRATVKLAQAVAEVQKLLVPQAEG" +
                "EDELKKRQLMELAIINGTYRDTTAKSVAVCDEEWRRLVAASDSRLLTSTGLPGLAAQIRA" +
                "PAAAPLGAPLILNPRMTVPTTAASILSAQAAPTAAFDQTGHGMIFAPYDYANYAALAGNP" +
                "LLTEYADHSVGAIKQQRRLATNREHPYQRATVGVPAKPAGFIEIQ";

        Protein protein = commonProteinTests( proteins,
                                              "how_drome", "Held out wings protein", sequence,
                                              "7227", // this is drome
                                              16, 6 );

        String ac = protein.getAc();

        // Check Xrefs
        confirmXref( protein, ac, uniprotDatabase, identityXrefQualifier, "O01367", "how_drome" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "O02392", "how_drome" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "P91680", "how_drome" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "Q8IN11", "how_drome" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "Q8T999", "how_drome" );
        confirmXref( protein, ac, uniprotDatabase, secondaryXrefQualifier, "Q94539", "how_drome" );
        confirmXref( protein, ac, goDatabase, null, "GO:0005634", "C:nucleus" );
        confirmXref( protein, ac, goDatabase, null, "GO:0003723", "F:RNA binding" );
        confirmXref( protein, ac, goDatabase, null, "GO:0007155", "P:cell adhesion" );
        confirmXref( protein, ac, goDatabase, null, "GO:0016477", "P:cell migration" );
        confirmXref( protein, ac, goDatabase, null, "GO:0016203", "P:muscle attachment" );
        confirmXref( protein, ac, goDatabase, null, "GO:0007521", "P:muscle cell fate determinati" );
        confirmXref( protein, ac, goDatabase, null, "GO:0008016", "P:regulation of heart rate" );
        confirmXref( protein, ac, interproDatabase, null, "IPR004087", "KH_dom" );
        confirmXref( protein, ac, interproDatabase, null, "IPR004088", "KH_type_1" );
        confirmXref( protein, ac, flybaseDatabase, null, "FBgn0017397", "how" );

        // Check Alias
        confirmAlias( protein, ac, geneNameAliasType, "how" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "who" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "stru" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "kh93f" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "qkr93f" );
        confirmAlias( protein, ac, geneNameSynonymAliasType, "cg10293" );


        //////////////////////////////
        // Check Splice variant (1/2)
        //////////////////////////////

        sequence =
        "MSVCESKAVVQQQLQQHLQQQAAAAVVAVAQQQQAQAQAQAQAQAQQQQQAPQVVVPMTPQHLTPQQQQQSTQSIADYLAQLLKDRKQLAAFPNVF" +
        "THVERLLDEEIARVRASLFQINGVKKEPLTLPEPEGSVVTMNEKVYVPVREHPDFNFVGRILGPRGMTAKQLEQETGCKIMVRGKGSMRDKKKEDA" +
        "NRGKPNWEHLSDDLHVLITVEDTENRATVKLAQAVAEVQKLLVPQAEGEDELKKRQLMELAIINGTYRDTTAKSVAVCDEEWRRLVAASDSRLLTS" +
        "TGLPGLAAQIRAPAAAPLGAPLILNPRMTVPTTAASILSAQAAPTAAFDQTGHGMIFAPYDYANYAALAGNPLLTEYADHSVGAIKQQRRLATNRE" +
        "HPYQRATVGVPAKPAGFIEIQ";

        Protein sv = commonProteinTests( proteins,
                                         "o01367-1", "Held out wings protein", sequence,
                                         "7227", // this is drome
                                         2, 1 );
        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, protein.getAc(), "how_drome" );
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "O01367-1", "HOW_DROME" );

        // Check Alias
        confirmAlias( sv, sv.getAc(), isoformSynonym, "a" );


        //////////////////////////////
        // Check Splice variant (2/2)
        //////////////////////////////

        sequence =
        "MSVCESKAVVQQQLQQHLQQQAAAAVVAVAQQQQAQAQAQAQAQAQQQQQAPQVVVPMTPQHLTPQQQQQSTQSIADYLAQLLKDRKQLAAFPNVF" +
        "THVERLLDEEIARVRASLFQINGVKKEPLTLPEPEGSVVTMNEKVYVPVREHPDFNFVGRILGPRGMTAKQLEQETGCKIMVRGKGSMRDKKKEDA" +
        "NRGKPNWEHLSDDLHVLITVEDTENRATVKLAQAVAEVQKLLVPQAEGEDELKKRQLMELAIINGTYRDTTAKSVAVCDEEWRRLVAASDSRLLTS" +
        "TGLPGLAAQIRAPAAAPLGAPLILNPRMTVPTTAASILSAQAAPTAAFDQTGHGMIFAPYDYANYAALAGNPLLTEYADHSGGLFAR";

        sv = commonProteinTests( proteins,
                                 "o01367-2", "Held out wings protein", sequence,
                                 "7227", // this is drome
                                 2, 1 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, protein.getAc(), "how_drome" );
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "O01367-2", "HOW_DROME" );

        // Check Alias
        confirmAlias( sv, sv.getAc(), isoformSynonym, "b" );

    } // O01367


    /**
     * Insert of a slightly more complex protein (P13953).
     * ie. no filter, no update to be done, 2 biosources (so 2 proteins),
     * 2 splice variants (per biosource, so 4 in all)
     * <p/>
     * Below is the Entry
     * <pre>
     * ID   D2DR_MOUSE     STANDARD;      PRT;   444 AA.
     * AC   P13953;
     * DT   01-JAN-1990 (Rel. 13, Created)
     * DT   01-JAN-1990 (Rel. 13, Last sequence update)
     * DT   28-FEB-2003 (Rel. 41, Last annotation update)
     * DE   D(2) dopamine receptor.
     * GN   DRD2.
     * OS   Mus musculus (Mouse), and
     * OS   Rattus norvegicus (Rat).
     * OC   Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi;
     * OC   Mammalia; Eutheria; Rodentia; Sciurognathi; Muridae; Murinae; Mus.
     * OX   NCBI_TaxID=10090, 10116;
     * RN   [1]
     * RP   SEQUENCE OF 1-241 AND 271-444 FROM N.A.
     * RC   SPECIES=Rat; TISSUE=Brain;
     * RX   MEDLINE=89082643; PubMed=2974511;
     * RA   Bunzow J.R., van Tol H.H.M., Grandy D.K., Albert P., Salon J.,
     * RA   Christie M., Machida C.A., Neve K.A., Civelli O.;
     * RT   "Cloning and expression of a rat D2 dopamine receptor cDNA.";
     * RL   Nature 336:783-787(1988).
     * RN   [2]
     * RP   SEQUENCE FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90081866; PubMed=2531846;
     * RA   Eidne K.A., Taylor P.L., Zabavnik J., Saunders P.T.K., Inglis J.D.;
     * RT   "D2 receptor, a missing exon.";
     * RL   Nature 342:865-865(1989).
     * RN   [3]
     * RP   SEQUENCE FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90081872; PubMed=2531847;
     * RA   Giros B., Sokoloff P., Martres M.-P., Riou J.-F., Emorine L.J.,
     * RA   Schwartz J.-C.;
     * RT   "Alternative splicing directs the expression of two D2 dopamine
     * RT   receptor isoforms.";
     * RL   Nature 342:923-926(1989).
     * RN   [4]
     * RP   SEQUENCE FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90081873; PubMed=2480527;
     * RA   Monsma F.J. Jr., McVittie L.D., Gerfen C.R., Mahan L.C., Sibley D.R.;
     * RT   "Multiple D2 dopamine receptors produced by alternative RNA
     * RT   splicing.";
     * RL   Nature 342:926-929(1989).
     * RN   [5]
     * RP   SEQUENCE FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90235966; PubMed=2139615;
     * RA   Rao D.D., McKelvy J., Kebabian J., MacKenzie R.G.;
     * RT   "Two forms of the rat D2 dopamine receptor as revealed by the
     * RT   polymerase chain reaction.";
     * RL   FEBS Lett. 263:18-22(1990).
     * RN   [6]
     * RP   SEQUENCE FROM N.A.
     * RC   SPECIES=Rat; STRAIN=Sprague-Dawley;
     * RA   Taylor P.L., Inglis J.D., Eidne K.A.;
     * RL   Submitted (OCT-1990) to the EMBL/GenBank/DDBJ databases.
     * RN   [7]
     * RP   SEQUENCE OF 242-270 FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90147685; PubMed=2137336;
     * RA   Miller J.C., Wang Y., Filer D.;
     * RT   "Identification by sequence analysis of a second rat brain cDNA
     * RT   encoding the dopamine (D2) receptor.";
     * RL   Biochem. Biophys. Res. Commun. 166:109-112(1990).
     * RN   [8]
     * RP   SEQUENCE OF 242-270 FROM N.A.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=90201380; PubMed=2138567;
     * RA   O'Dowd B.F., Nguyen T., Tirpak A., Jarvie K.R., Israel Y., Seeman P.,
     * RA   Niznik H.B.;
     * RT   "Cloning of two additional catecholamine receptors from rat brain.";
     * RL   FEBS Lett. 262:8-12(1990).
     * RN   [9]
     * RP   SEQUENCE FROM N.A. (ISOFORM A).
     * RC   SPECIES=Mouse;
     * RX   MEDLINE=91122293; PubMed=1991517;
     * RA   Montmayeur J.P., Bausero P., Amlaiky N., Maroteaux L., Hen R.,
     * RA   Borrelli E.;
     * RT   "Differential expression of the mouse D2 dopamine receptor isoforms.";
     * RL   FEBS Lett. 278:239-243(1991).
     * RN   [10]
     * RP   MUTAGENESIS OF SER-193; SER-194; SER-197 AND SER-420.
     * RC   SPECIES=Rat;
     * RX   MEDLINE=92333328; PubMed=1321233;
     * RA   Cox B.A., Henningsen R.A., Spanoyannis A., Neve R.L., Neve K.A.;
     * RT   "Contributions of conserved serine residues to the interactions of
     * RT   ligands with dopamine D2 receptors.";
     * RL   J. Neurochem. 59:627-635(1992).
     * RN   [11]
     * RP   INTERACTION WITH NEURABIN II.
     * RX   MEDLINE=99321921; PubMed=10391935;
     * RA   Smith F.D., Oxford G.S., Milgram S.L.;
     * RT   "Association of the D2 dopamine receptor third cytoplasmic loop with
     * RT   spinophilin, a protein phosphatase-1-interacting protein.";
     * RL   J. Biol. Chem. 274:19894-19900(1999).
     * RN   [12]
     * RP   3D-STRUCTURE MODELING.
     * RX   MEDLINE=93038566; PubMed=1358063;
     * RA   Livingstone C.D., Strange P.G., Naylor L.H.;
     * RT   "Molecular modelling of D2-like dopamine receptors.";
     * RL   Biochem. J. 287:277-282(1992).
     * CC   -!- FUNCTION: This is one of the five types (D1 to D5) of receptors
     * CC       for dopamine. The activity of this receptor is mediated by G
     * CC       proteins which inhibit adenylyl cyclase.
     * CC   -!- SUBUNIT: INTERACTS WITH NEURABIN II.
     * CC   -!- SUBCELLULAR LOCATION: Integral membrane protein.
     * CC   -!- ALTERNATIVE PRODUCTS:
     * CC       Event=Alternative splicing; Named isoforms=2;
     * CC       Name=Long;
     * CC         IsoId=P13953-1; Sequence=Displayed;
     * CC       Name=Short;
     * CC         IsoId=P13953-2; Sequence=VSP_001871;
     * CC   -!- SIMILARITY: Belongs to family 1 of G-protein coupled receptors.
     * CC   --------------------------------------------------------------------------
     * CC   This SWISS-PROT entry is copyright. It is produced through a collaboration
     * CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -
     * CC   the European Bioinformatics Institute.  There are no  restrictions on  its
     * CC   use  by  non-profit  institutions as long  as its content  is  in  no  way
     * CC   modified and this statement is not removed.  Usage  by  and for commercial
     * CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/
     * CC   or send an email to license@isb-sib.ch).
     * CC   --------------------------------------------------------------------------
     * DR   EMBL; X53278; CAA37373.1; -.
     * DR   EMBL; M32241; AAA41074.1; -.
     * DR   EMBL; M36831; AAA41075.1; -.
     * DR   EMBL; X56065; CAA39543.1; -.
     * DR   EMBL; X55674; CAA39209.1; -.
     * DR   PIR; S08146; S08146.
     * DR   PIR; S13921; DYMSD2.
     * DR   MGD; MGI:94924; Drd2.
     * DR   InterPro; IPR000276; GPCR_Rhodpsn.
     * DR   Pfam; PF00001; 7tm_1; 1.
     * DR   PRINTS; PR00237; GPCRRHODOPSN.
     * DR   PROSITE; PS00237; G_PROTEIN_RECEP_F1_1; 1.
     * DR   PROSITE; PS50262; G_PROTEIN_RECEP_F1_2; 1.
     * KW   G-protein coupled receptor; Transmembrane; Glycoprotein;
     * KW   Multigene family; Alternative splicing.
     * FT   DOMAIN        1     37       Extracellular (Potential).
     * FT   TRANSMEM     38     60       1 (Potential).
     * FT   DOMAIN       61     71       Cytoplasmic (Potential).
     * FT   TRANSMEM     72     97       2 (Potential).
     * FT   DOMAIN       98    108       Extracellular (Potential).
     * FT   TRANSMEM    109    130       3 (Potential).
     * FT   DOMAIN      131    151       Cytoplasmic (Potential).
     * FT   TRANSMEM    152    174       4 (Potential).
     * FT   DOMAIN      175    186       Extracellular (Potential).
     * FT   TRANSMEM    187    210       5 (Potential).
     * FT   DOMAIN      211    374       Cytoplasmic (Potential).
     * FT   TRANSMEM    375    398       6 (Potential).
     * FT   DOMAIN      399    406       Extracellular (Potential).
     * FT   TRANSMEM    407    430       7 (Potential).
     * FT   DOMAIN      431    444       Cytoplasmic (Potential).
     * FT   DOMAIN      211    374       INTERACTS WITH NEURABIN II.
     * FT   SITE        193    193       IMPLICATED IN CATECHOL AGONIST BINDING.
     * FT   SITE        194    194       IMPLICATED IN RECEPTOR ACTIVATION.
     * FT   SITE        197    197       IMPLICATED IN RECEPTOR ACTIVATION.
     * FT   CARBOHYD      5      5       N-linked (GlcNAc...) (Potential).
     * FT   CARBOHYD     17     17       N-linked (GlcNAc...) (Potential).
     * FT   CARBOHYD     23     23       N-linked (GlcNAc...) (Potential).
     * FT   DISULFID    107    182       By similarity.
     * FT   VARSPLIC    242    270       Missing (in isoform Short).
     * FT                                /FTId=VSP_001871.
     * FT   MUTAGEN     193    193       S->A: MODERATE DECREASE IN LIGAND
     * FT                                BINDING. 200-FOLD REDUCTION OF AGONIST-
     * FT                                MEDIATED CYCLIC AMP INHIBITION.
     * FT   MUTAGEN     194    194       S->A: SMALL DECREASE IN AGONIST BINDING.
     * FT                                COMPLETE LOSS OF AGONIST-MEDIATED CYCLIC
     * FT                                AMP INHIBITION.
     * FT   MUTAGEN     197    197       S->A: SMALL DECREASE IN AGONIST BINDING.
     * FT                                18-FOLD REDUCTION OF AGONIST-MEDIATED
     * FT                                CYCLIC AMP INHIBITION.
     * FT   MUTAGEN     420    420       S->A: MODERATE DECREASE IN LIGAND
     * FT                                BINDING.
     * FT   CONFLICT     99     99       E -> D (in Ref. 5).
     * FT   CONFLICT    173    173       G -> R (in Ref. 5).
     * FT   CONFLICT    180    180       N -> G (in Ref. 5).
     * SQ   SEQUENCE   444 AA;  50903 MW;  216E56CEE5CA32FB CRC64;
     * MDPLNLSWYD DDLERQNWSR PFNGSEGKAD RPHYNYYAML LTLLIFIIVF GNVLVCMAVS
     * REKALQTTTN YLIVSLAVAD LLVATLVMPW VVYLEVVGEW KFSRIHCDIF VTLDVMMCTA
     * SILNLCAISI DRYTAVAMPM LYNTRYSSKR RVTVMIAIVW VLSFTISCPL LFGLNNTDQN
     * ECIIANPAFV VYSSIVSFYV PFIVTLLVYI KIYIVLRKRR KRVNTKRSSR AFRANLKTPL
     * KGNCTHPEDM KLCTVIMKSN GSFPVNRRRM DAARRAQELE MEMLSSTSPP ERTRYSPIPP
     * SHHQLTLPDP SHHGLHSNPD SPAKPEKNGH AKIVNPRIAK FFEIQTMPNG KTRTSLKTMS
     * RRKLSQQKEK KATQMLAIVL GVFIICWLPF FITHILNIHC DCNIPPVLYS AFTWLGYVNS
     * AVNPIIYTTF NIEFRKAFMK ILHC
     * //
     * </pre>
     */
    public void testInsertSPTrProteins_P13953() {

        insertSPTrProteins_P13953_WithRatFilter();
        insertSPTrProteins_P13953_WithoutFilter();
    }

    /**
     * Ask for a subset of the created protein (Rat)
     */
    private void insertSPTrProteins_P13953_WithRatFilter() {

        Collection proteins = buildCollection( MockEntryText.P13953, "P13953", "10116", false, 3 );

        Protein proteinRat = null;
        String ac = null;

        String sequence =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKGNCTHPEDMKLCTVIMKSNGSFPVNRRRMDAARRAQELEMEMLSSTSPPE" +
                "RTRYSPIPPSHHQLTLPDPSHHGLHSNPDSPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWL" +
                "PFFITHILNIHCDCNIPPVLYSAFTWLGYVNSAVNPIIYTTFNIEFRKAFMKILHC";

        /////////////////////////
        // Check Protein (1/1)
        /////////////////////////

        proteinRat = commonProteinTests( proteins,
                                         "drd2_rat", "D(2) dopamine receptor", sequence,
                                         "10116", // this is rat
                                         2, 1 );

        ac = proteinRat.getAc();

        // Check Xrefs
        confirmXref( proteinRat, ac, uniprotDatabase, identityXrefQualifier, "P13953", "drd2_rat" );
        confirmXref( proteinRat, ac, interproDatabase, null, "IPR000276", "GPCR_Rhodpsn" );

        // Check Alias
        confirmAlias( proteinRat, ac, geneNameAliasType, "drd2" );

        // Sequence shared by the splice variant,
        //  sequence1 is own by p13953-1_rat
        //  sequence2 is own by p13953-2_rat
        String sequence1 =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKGNCTHPEDMKLCTVIMKSNGSFPVNRRRMDAARRAQELEMEMLSSTSPPE" +
                "RTRYSPIPPSHHQLTLPDPSHHGLHSNPDSPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWL" +
                "PFFITHILNIHCDCNIPPVLYSAFTWLGYVNSAVNPIIYTTFNIEFRKAFMKILHC";


        String sequence2 =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKDAARRAQELEMEMLSSTSPPERTRYSPIPPSHHQLTLPDPSHHGLHSNPD" +
                "SPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWLPFFITHILNIHCDCNIPPVLYSAFTWLGY" +
                "VNSAVNPIIYTTFNIEFRKAFMKILHC";

        Protein sv = null;

        //////////////////////////////
        // Check Splice variant (1/2)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-1_rat", "D(2) dopamine receptor", sequence1,
                                 "10116", // this is rat
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-1", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinRat.getAc(), "drd2_rat" );


        //////////////////////////////
        // Check Splice variant (2/2)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-2_rat", "D(2) dopamine receptor", sequence2,
                                 "10116", // this is rat
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-2", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinRat.getAc(), "drd2_rat" );

    }


    /**
     * Parse the entry (update activated) and get all proteins
     */
    private void insertSPTrProteins_P13953_WithoutFilter() {

        Collection proteins = buildCollection( MockEntryText.P13953, "P13953", null, true, 6 );

        Protein proteinRat = null;
        Protein proteinMouse = null;
        String ac = null;


        /////////////////////////
        // Check Protein (1/2)
        /////////////////////////

        String sequence =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKGNCTHPEDMKLCTVIMKSNGSFPVNRRRMDAARRAQELEMEMLSSTSPPE" +
                "RTRYSPIPPSHHQLTLPDPSHHGLHSNPDSPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWL" +
                "PFFITHILNIHCDCNIPPVLYSAFTWLGYVNSAVNPIIYTTFNIEFRKAFMKILHC";

        proteinMouse = commonProteinTests( proteins,
                                           "drd2_mouse", "D(2) dopamine receptor", sequence,
                                           "10090", // this is mouse
                                           2, 1 );

        ac = proteinMouse.getAc();

        // Check Xrefs
        confirmXref( proteinMouse, ac, uniprotDatabase, identityXrefQualifier, "P13953", "drd2_mouse" );
        confirmXref( proteinMouse, ac, interproDatabase, null, "IPR000276", "GPCR_Rhodpsn" );

        // Check Alias
        confirmAlias( proteinMouse, ac, geneNameAliasType, "drd2" );


        /////////////////////////
        // Check Protein (2/2)
        /////////////////////////

        proteinRat = commonProteinTests( proteins,
                                         "drd2_rat", "D(2) dopamine receptor", sequence,
                                         "10116", // this is rat
                                         2, 1 );

        ac = proteinRat.getAc();

        // Check Xrefs
        confirmXref( proteinRat, ac, uniprotDatabase, identityXrefQualifier, "P13953", "drd2_rat" );
        confirmXref( proteinRat, ac, interproDatabase, null, "IPR000276", "GPCR_Rhodpsn" );

        // Check Alias
        confirmAlias( proteinRat, ac, geneNameAliasType, "drd2" );



        // Sequence shared by the splice variant,
        //  sequence1 is own by either p13953-1_mouse and p13953-1_rat
        //  sequence2 is own by either p13953-2_mouse and p13953-2_rat
        String sequence1 =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKGNCTHPEDMKLCTVIMKSNGSFPVNRRRMDAARRAQELEMEMLSSTSPPE" +
                "RTRYSPIPPSHHQLTLPDPSHHGLHSNPDSPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWL" +
                "PFFITHILNIHCDCNIPPVLYSAFTWLGYVNSAVNPIIYTTFNIEFRKAFMKILHC";


        String sequence2 =
                "MDPLNLSWYDDDLERQNWSRPFNGSEGKADRPHYNYYAMLLTLLIFIIVFGNVLVCMAVSREKALQTTTNYLIVSLAVADLLVATLVMPWVVYLEVV" +
                "GEWKFSRIHCDIFVTLDVMMCTASILNLCAISIDRYTAVAMPMLYNTRYSSKRRVTVMIAIVWVLSFTISCPLLFGLNNTDQNECIIANPAFVVYSS" +
                "IVSFYVPFIVTLLVYIKIYIVLRKRRKRVNTKRSSRAFRANLKTPLKDAARRAQELEMEMLSSTSPPERTRYSPIPPSHHQLTLPDPSHHGLHSNPD" +
                "SPAKPEKNGHAKIVNPRIAKFFEIQTMPNGKTRTSLKTMSRRKLSQQKEKKATQMLAIVLGVFIICWLPFFITHILNIHCDCNIPPVLYSAFTWLGY" +
                "VNSAVNPIIYTTFNIEFRKAFMKILHC";

        Protein sv = null;

        //////////////////////////////
        // Check Splice variant (1/4)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-1_mouse", "D(2) dopamine receptor", sequence1,
                                 "10090", // this is mouse
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-1", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinMouse.getAc(), "drd2_mouse" );


        //////////////////////////////
        // Check Splice variant (2/4)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-2_mouse", "D(2) dopamine receptor", sequence2,
                                 "10090", // this is mouse
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-2", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinMouse.getAc(), "drd2_mouse" );


        //////////////////////////////
        // Check Splice variant (3/4)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-1_rat", "D(2) dopamine receptor", sequence1,
                                 "10116", // this is rat
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-1", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinRat.getAc(), "drd2_rat" );


        //////////////////////////////
        // Check Splice variant (4/4)
        //////////////////////////////

        sv = commonProteinTests( proteins,
                                 "p13953-2_rat", "D(2) dopamine receptor", sequence2,
                                 "10116", // this is rat
                                 2, 0 );

        ac = sv.getAc();

        // Check Xrefs
        confirmXref( sv, ac, uniprotDatabase, identityXrefQualifier, "P13953-2", "D2DR_MOUSE" );
        confirmXref( sv, ac, intactDatabase, isoFormParentXrefQualifier, proteinRat.getAc(), "drd2_rat" );
    } // P13953
}