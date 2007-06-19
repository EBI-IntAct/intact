package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.persistence.dao.*;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.util.protein.mock.*;
import uk.ac.ebi.intact.util.protein.utils.UniprotServiceResult;
import uk.ac.ebi.intact.util.taxonomy.DummyTaxonomyService;

import java.util.*;

/**
 * ProteinServiceImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version
 */
public class ProteinServiceImplTest extends TestCase {

    public ProteinServiceImplTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
    }

    public void tearDown() throws Exception {
        super.tearDown();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public static Test suite() {
        return new TestSuite( ProteinServiceImplTest.class );
    }

    //////////////////////
    // Helper methods

    private ProteinService buildProteinService() {
        UniprotService uniprotService = new MockUniprotService();
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        return service;
    }

    private ProteinService buildProteinService( UniprotService uniprotService ) {
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        return service;
    }

    private Protein searchByShortlabel( List<ProteinImpl> proteins, String shortlabel ) {
        for ( ProteinImpl protein : proteins ) {
            if ( protein.getShortLabel().equals( shortlabel ) ) {
                return protein;
            }
        }
        return null;
    }

    private void clearProteinsFromDatabase() throws IntactTransactionException {

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // delete all interactors
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractorDao idao = daoFactory.getInteractorDao();
        List all = idao.getAll();
        System.out.println( "Searching for objects to delete, found " + all.size() + " interactor(s)." );

        if ( !all.isEmpty() ) {
            System.out.println( "Now deleting them all..." );
            idao.deleteAll( all );

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();

            // check that interactor count is 0
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            idao = daoFactory.getInteractorDao();

            List list = idao.getAll();
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            assertNotNull( list );
            assertTrue( list.isEmpty() );
            list = null;
        } else {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            System.out.println( "Database was already cleared of any interactor." );
        }
    }

    ////////////////////
    // Tests

    public void testRetrieve_CDC42_CANFA() throws Exception {


        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        ProteinService service = buildProteinService();
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC); /* CDC42_CANFA */
        Collection<Protein> proteins = uniprotServiceResult.getProteins();

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        Protein protein = proteins.iterator().next();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvObject> cvDao = daoFactory.getCvObjectDao();

        Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();

        CvAliasType isoformSynonym = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.ISOFORM_SYNONYM_MI_REF );
        CvAliasType gene = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.GENE_NAME_MI_REF );
        CvAliasType synonym = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.GENE_NAME_SYNONYM_MI_REF );
        CvAliasType orf = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.ORF_NAME_MI_REF );
        CvAliasType locus = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.LOCUS_NAME_MI_REF );

        CvXrefQualifier identity = ( CvXrefQualifier ) cvDao.getByPsiMiRef( CvXrefQualifier.IDENTITY_MI_REF );
        CvXrefQualifier secondaryAc = ( CvXrefQualifier ) cvDao.getByPsiMiRef( CvXrefQualifier.SECONDARY_AC_MI_REF );
        CvXrefQualifier isoformParent = ( CvXrefQualifier ) cvDao.getByPsiMiRef( CvXrefQualifier.ISOFORM_PARENT_MI_REF );

        CvDatabase uniprot = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.UNIPROT_MI_REF );
        CvDatabase go = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.GO_MI_REF );
        CvDatabase interpro = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.INTERPRO_MI_REF );
        CvDatabase intact = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.INTACT_MI_REF );
        CvDatabase pdb = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.PDB_MI_REF );

        CvTopic isoformComment = ( CvTopic ) cvDao.getByShortLabel( CvTopic.ISOFORM_COMMENT );

        assertNotNull( protein.getAc() );
        assertEquals( "cdc42_canfa", protein.getShortLabel() );
        assertEquals( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)", protein.getFullName() );
        assertEquals( "34B44F9225EC106B", protein.getCrc64() );
        assertEquals( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                "ETQPKRKCCIF", protein.getSequence() );

        assertNotNull( protein.getBioSource() );
        assertNotNull( protein.getBioSource().getAc() );
        assertEquals( "9615", protein.getBioSource().getTaxId() );

        assertEquals( 7, protein.getXrefs().size() );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, uniprot, "P60952", identity ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, uniprot, "P21181", secondaryAc ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, uniprot, "P25763", secondaryAc ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR003578", null ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR013753", null ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR001806", null ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR005225", null ) ) );

        assertEquals( 1, protein.getAliases().size() );
        assertTrue( protein.getAliases().contains( new InteractorAlias( owner, protein, gene, "CDC42" ) ) );

        assertEquals( 0, protein.getAnnotations().size() );

        //////////////////////
        // Splice variants

        ProteinDao pdao = daoFactory.getProteinDao();
        List<ProteinImpl> variants = pdao.getSpliceVariants( protein );

        assertEquals( 2, variants.size() );

        Protein sv1 = searchByShortlabel( variants, "P60952-1" );
        assertNotNull( sv1 );
        assertEquals( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)", sv1.getFullName() );
        assertTrue( sv1.getXrefs().contains( new InteractorXref( owner, intact, protein.getAc(), isoformParent ) ) );
        assertTrue( sv1.getXrefs().contains( new InteractorXref( owner, uniprot, "P60952-1", identity ) ) );
        assertTrue( sv1.getXrefs().contains( new InteractorXref( owner, uniprot, "P21181-1", secondaryAc ) ) );
        assertEquals( "34B44F9225EC106B", sv1.getCrc64() );
        assertEquals( "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" +
                "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" +
                "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" +
                "ETQPKRKCCIF", sv1.getSequence() );
        assertTrue( sv1.getAliases().contains( new InteractorAlias( owner, sv1, isoformSynonym, "Brain" ) ) );
        assertTrue( sv1.getAnnotations().contains(
                new Annotation( owner, isoformComment, "Has not been isolated in dog so far" ) ) );

        Protein sv2 = searchByShortlabel( variants, "P60952-2" );
        assertNotNull( sv2 );
        assertEquals( "Cell division control protein 42 homolog precursor (G25K GTP-binding protein)", sv2.getFullName() );
        assertTrue( sv2.getXrefs().contains( new InteractorXref( owner, intact, protein.getAc(), isoformParent ) ) );
        assertTrue( sv2.getXrefs().contains( new InteractorXref( owner, uniprot, "P60952-2", identity ) ) );
        assertTrue( sv2.getXrefs().contains( new InteractorXref( owner, uniprot, "P21181-4", secondaryAc ) ) );
        assertEquals( Crc64.getCrc64( "MQTIKCVKRKCCIF" ), sv2.getCrc64() );
        assertEquals( "MQTIKCVKRKCCIF", sv2.getSequence() );
        assertTrue( sv2.getAliases().contains( new InteractorAlias( owner, sv2, isoformSynonym, "Placental" ) ) );

        // check that a second call to the service bring the same protein
        String ac = protein.getAc();
        String sv1ac = sv1.getAc();
        String sv2ac = sv2.getAc();


        uniprotServiceResult =  service.retrieve( "P60952" ); /* CDC42_CANFA */
        proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        protein = proteins.iterator().next();
        assertEquals( ac, protein.getAc() );

        variants = pdao.getSpliceVariants( protein );
        assertEquals( 2, variants.size() );

        sv1 = searchByShortlabel( variants, "P60952-1" );
        assertEquals( sv1ac, sv1.getAc() );

        sv2 = searchByShortlabel( variants, "P60952-2" );
        assertEquals( sv2ac, sv2.getAc() );
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }

    public void testRetrieve_update_CDC42_CANFA() throws Exception {
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService service = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        service.add( "P60952", canfa );

        ProteinService proteinService = buildProteinService( service );
        UniprotServiceResult uniprotServiceResult = proteinService.retrieve( "P60952" );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        // update shortlabel
        canfa.setId( "FOO_BAR" );
        canfa.setDescription( "LALALA" );
        canfa.setSequence( "LLLLLLLLLLLLL" );
        canfa.setCrc64( "XXXXXXXXXXXXX" );

        // provoking recycling and deletion of Xrefs
        canfa.getCrossReferences().addAll( new UniprotProteinXrefBuilder()
                .add( "IPR00000", "InterPro", "he he" )
                .build() );

        canfa.getCrossReferences().removeAll( new UniprotProteinXrefBuilder()
                .add( "IPR003578", "InterPro", "GTPase_Rho" )
                .add( "IPR005225", "InterPro", "Small_GTP_bd" )
                .build() );

        canfa.getSynomyms().add( "s" );
        canfa.getOrfs().add( "o" );
        canfa.getLocuses().add( "l" );
        canfa.getGenes().add( "foo" );
        canfa.getGenes().remove( "CDC42" );

        uniprotServiceResult = proteinService.retrieve( "P60952" );
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        for(String errorType : keySet){
            String error = errors.get(errorType);
            System.out.println("error message is : " + error);
        }
        proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        Protein protein = proteins.iterator().next();

        assertEquals( "foo_bar", protein.getShortLabel() );
        assertEquals( "LALALA", protein.getFullName() );
        assertEquals( "LLLLLLLLLLLLL", protein.getSequence() );
        assertEquals( "XXXXXXXXXXXXX", protein.getCrc64() );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvObject> cvDao = daoFactory.getCvObjectDao();

        Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();
        CvDatabase interpro = ( CvDatabase ) cvDao.getByPsiMiRef( CvDatabase.INTERPRO_MI_REF );
        CvAliasType gene = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.GENE_NAME_MI_REF );
        CvAliasType synonym = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.GENE_NAME_SYNONYM_MI_REF );
        CvAliasType orf = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.ORF_NAME_MI_REF );
        CvAliasType locus = ( CvAliasType ) cvDao.getByPsiMiRef( CvAliasType.LOCUS_NAME_MI_REF );

        assertEquals( 6, protein.getXrefs().size() );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR00000", null ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR013753", null ) ) );
        assertTrue( protein.getXrefs().contains( new InteractorXref( owner, interpro, "IPR001806", null ) ) );

        assertEquals( 4, protein.getAliases().size() );
        assertTrue( protein.getAliases().contains( new InteractorAlias( owner, protein, gene, "foo" ) ) );
        assertTrue( protein.getAliases().contains( new InteractorAlias( owner, protein, synonym, "s" ) ) );
        assertTrue( protein.getAliases().contains( new InteractorAlias( owner, protein, orf, "o" ) ) );
        assertTrue( protein.getAliases().contains( new InteractorAlias( owner, protein, locus, "l" ) ) );


        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testRetrieve_sequenceUpdate() throws ProteinServiceException, IntactTransactionException {

        // clear database content.
        clearProteinsFromDatabase();

        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( "P60952", canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        UniprotServiceResult uniprotServiceResult =  service.retrieve( "P60952" );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        Protein protein = proteins.iterator().next();
        String proteinSeq = protein.getSequence();
        String proteinCrc = protein.getCrc64();

        // Update the seqence/CRC of the protein
        assertTrue( proteinSeq.length() > 20 );
        String newSequence = proteinSeq.substring( 2, 20 );
        canfa.setSequence( newSequence );
        canfa.setSequenceLength( canfa.getSequence().length() );
        canfa.setCrc64( Crc64.getCrc64( canfa.getSequence() ) );

        protein = null;

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        uniprotServiceResult = service.retrieve( "P60952" );
        proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        protein = proteins.iterator().next();

        // check that we have retrieved the exact same protein.
        assertEquals( newSequence, protein.getSequence() );
        assertEquals( Crc64.getCrc64( newSequence ), protein.getCrc64() );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testRetrieve_intact1_uniprot0() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        assertEquals(1,uniprotServiceResult.getProteins().size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        // Re-initialize the uniprot service so that it does not contain any more the CANFA entry.
        uniprotService = new FlexibleMockUniprotService();
        canfa = MockUniprotProtein.build_CDC42_HUMAN();
        uniprotService.add( MockUniprotProtein.CDC42_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_2, canfa );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_3, canfa );

        service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );


        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertEquals("Couldn't update protein with uniprot id = " + uniprotServiceResult.getQuerySentToService() + ". It was found" +
                    " in IntAct but was not found in Uniprot.", error);
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }

    public void testRetrieve_intact0_uniprot0() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CDC42_PRIMARY_AC );
        assertEquals(0, uniprotServiceResult.getProteins().size());
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertEquals("Could not udpate protein with uniprot id = " + uniprotServiceResult.getQuerySentToService() + ". No " +
                    "corresponding entry found in uniprot.",error);
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }

    /**
     * Test that the protein xref and the protein are udpated when : countPrimary == 0 && countSecondary == 1
     */
    public void testRetrieve_primaryCount0_secondaryCount1() throws Exception{

        // clear database content.
        clearProteinsFromDatabase();

        /*----------------------------------------------------------
        Create in the db, the CANFA protein with primary Ac P60952
         -----------------------------------------------------------*/

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        //Do some settings
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertEquals( 1,proteins.size() );

        String proteinAc = "";
        for(Protein protein : proteins){
            proteinAc = protein.getAc();
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        /*--------------------------------------------------------------------------------------------------------------
        In the db, modify the sequence of P60952, and set it's xref identity to uniprot to P21181 which -in uniprot-
        is a secondary ac of P60952
         -------------------------------------------------------------------------------------------------------------*/
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        //Make sure that no protein in the database have an xref identity to uniprot with primaryAc = P21181
        List<ProteinImpl> P21181 = proteinDao.getByXrefLike(uniprot,identity,MockUniprotProtein.CANFA_SECONDARY_AC_1);
        assertEquals(0,P21181.size());
        //Get the protein we created earlier, with xref identity to uniprot and primary Ac P60952
        ProteinImpl P60952 = proteinDao.getByXref(MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(proteinAc, P60952.getAc());
        //Change it's identity xref to P21181 which is the secondary ac of the CANFA protein in Uniprot
        InteractorXref uniprotXref = ProteinUtils.getUniprotXref(P60952);
        assertNotNull(uniprotXref);
        uniprotXref.setPrimaryId(MockUniprotProtein.CANFA_SECONDARY_AC_1);
        //Change the sequence
        P60952.setSequence("TRALALA");
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        //Retrieve (= update P60952)
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        proteins = uniprotServiceResult.getProteins();
        assertNotNull(proteins);
        assertEquals(1, proteins.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        // As the protein has been update, we shouldn't get anything searching by xref identity to uniprot with P22181.
        // We make sure that this is the case.
        P21181 = proteinDao.getByXrefLike(uniprot,identity,MockUniprotProtein.CANFA_SECONDARY_AC_1);
        assertEquals(0,P21181.size());
        // Make sure that there is in the db one prot corresponding to P60952
        List protP60952 = proteinDao.getByXrefLike( uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC );
        assertNotNull(protP60952);
        assertEquals(1, protP60952.size());
        assertTrue(protP60952.iterator().hasNext());
        P60952 = (ProteinImpl) protP60952.iterator().next();
        assertEquals(proteinAc, P60952.getAc());
        uniprotXref = ProteinUtils.getUniprotXref(P60952);
        assertNotNull(uniprotXref);
        assertEquals(uniprotXref.getPrimaryId(), MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(P60952.getSequence(),MockUniprotProtein.CANFA_SEQUENCE);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }

    /**
     * Check that nothing is update if more then 1 proteins are found in uniprot.
     */
    public void testRetrieve_uniprotAcReturningMoreThen1EntryWithDifferentSpecies() throws Exception{

        // clear database content.
        clearProteinsFromDatabase();

        /*----------------------------------------------------------
       Create in the db, the CANFA protein with primary Ac P60952
        -----------------------------------------------------------*/

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        //Do some settings
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        UniprotProtein human = MockUniprotProtein.build_CDC42_HUMAN();
        uniprotService.add( MockUniprotProtein.CDC42_PRIMARY_AC, human );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_1, human );
        Collection<UniprotProtein> uniprotProteins = new ArrayList(2);
        uniprotProteins.add(human);
        uniprotProteins.add(canfa);
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_2, uniprotProteins );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_3, human );


        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CDC42_SECONDARY_AC_2 );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertEquals( 0,proteins.size() );
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(("Trying to update "+ uniprotServiceResult.getQuerySentToService() +" returned a set of proteins belonging to different organisms.").equals(error));
        }

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        Collection<ProteinImpl> intactProteins = proteinDao.getByUniprotId(canfa.getPrimaryAc());
        assertEquals(0, intactProteins.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }

    /**
     * Check that nothing is update if more then 1 proteins are found in uniprot.
     */
    public void testRetrieve_uniprotAcReturningMoreThen1EntryWithSameSpecies() throws Exception{

        // clear database content.
        clearProteinsFromDatabase();

        /*----------------------------------------------------------
       Create in the db, the CANFA protein with primary Ac P60952
        -----------------------------------------------------------*/

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        //Do some settings
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        UniprotProtein human = MockUniprotProtein.build_CDC42_HUMAN();
        uniprotService.add( MockUniprotProtein.CDC42_PRIMARY_AC, human );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_1, human );
        canfa.setOrganism(human.getOrganism());
        Collection<UniprotProtein> uniprotProteins = new ArrayList(2);
        uniprotProteins.add(human);
        uniprotProteins.add(canfa);
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_2, uniprotProteins );
        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_3, human );


        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CDC42_SECONDARY_AC_2 );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertEquals( 0,proteins.size() );
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(("Trying to update "+ uniprotServiceResult.getQuerySentToService() +" returned a set of proteins belonging to the same organism.").equals(error));
        }
    }

//    private Protein getProtein(String uniprotId, ProteinDao proteinDao){
//        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
//        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
//                // As the protein has been update, we shouldn't get anything searching by xref identity to uniprot with P22181.
//                // We make sure that this is the case.
//        List proteins = proteinDao.getByXrefLike(uniprot,identity,uniprotId);
//        if(proteins.size() == 0){
//            return null;
//        }else if(proteins.size()>1){
//
//        }
//    }

    public void testRetrieve_primaryCount0_secondaryCount2() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins();
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        List<ProteinImpl> proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(1, proteinsList.size());
        ProteinImpl protein = proteinsList.get(0);
        InteractorXref uniprotXref = ProteinUtils.getUniprotXref(protein);
        uniprotXref.setPrimaryId(MockUniprotProtein.CANFA_SECONDARY_AC_1);
        protein.setSequence("BLABLA");
        proteinDao.saveOrUpdate(protein);
        uniprotXref = ProteinUtils.getUniprotXref(protein);
        System.out.println("uniprotXref.getPrimaryId() = " + uniprotXref.getPrimaryId());
        String ac = protein.getAc();
        Protein duplicatedProtein = new ProteinImpl(IntactContext.getCurrentInstance().getInstitution(),
                protein.getBioSource(),
                protein.getShortLabel(),
                protein.getCvInteractorType());
        proteinDao.saveOrUpdate((ProteinImpl) duplicatedProtein);
        duplicatedProtein.setSequence("BLABLA");
        InteractorXref newXref = new InteractorXref(IntactContext.getCurrentInstance().getInstitution(),
                uniprot,
                MockUniprotProtein.CANFA_SECONDARY_AC_1,
                identity);
        newXref.setParent(duplicatedProtein);
        XrefDao xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao();
        xrefDao.saveOrUpdate(newXref);
        duplicatedProtein.addXref(newXref);
        proteinDao.saveOrUpdate((ProteinImpl) duplicatedProtein);
        System.out.println("ac = " + ac);
        System.out.println("duplicatedProtein.getAc() = " + duplicatedProtein.getAc());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_SECONDARY_AC_1);
        assertEquals(2, proteinsList.size());
        proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(0, proteinsList.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        proteinsColl = uniprotServiceResult.getProteins();
        assertEquals(0,proteinsColl.size());
        System.out.println("proteinsColl.size() = " + proteinsColl.size());
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(error.contains("More than one IntAct protein is matching secondary AC(s):"));
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testRetrieve_primaryCount1_secondaryCount1() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins() ;
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        List<ProteinImpl> proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(1, proteinsList.size());
        ProteinImpl protein = proteinsList.get(0);

        Protein secondaryProt = new ProteinImpl(IntactContext.getCurrentInstance().getInstitution(),
                protein.getBioSource(),
                protein.getShortLabel(),
                protein.getCvInteractorType());
        proteinDao.saveOrUpdate((ProteinImpl) secondaryProt);
        secondaryProt.setSequence("BLABLA");
        InteractorXref newXref = new InteractorXref(IntactContext.getCurrentInstance().getInstitution(),
                uniprot,
                MockUniprotProtein.CANFA_SECONDARY_AC_1,
                identity);
        newXref.setParent(secondaryProt);
        XrefDao xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao();
        xrefDao.saveOrUpdate(newXref);
        secondaryProt.addXref(newXref);
        proteinDao.saveOrUpdate((ProteinImpl) secondaryProt);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        proteinsColl = uniprotServiceResult.getProteins();
        assertEquals(0,proteinsColl.size());
        System.out.println("proteinsColl.size() = " + proteinsColl.size());
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(error.contains("Unexpected number of protein found in IntAct for UniprotEntry(P60952) Count of " +
                    "protein in Intact for the Uniprot entry primary ac(1) for the Uniprot entry secondary ac(s)(1)"));
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testRetrieve_throwException() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        try{
            UniprotServiceResult uniprotServiceResult = service.retrieve((String) null);
            fail("Should have thrown a NullPointerException");
        }catch(IllegalArgumentException e){

        }
        try{
            UniprotServiceResult uniprotServiceResult = service.retrieve("");
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e){

        }
    }


//    public void testRetrieve_withSecondaryAcSharedBy2UniprotEntry() throws Exception{
//        // clear database content.
//        clearProteinsFromDatabase();
//
//        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
//        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
//        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
//        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
//        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
//        UniprotProtein cdc42 = MockUniprotProtein.build_CDC42_HUMAN();
//        uniprotService.add( MockUniprotProtein.CDC42_PRIMARY_AC, cdc42 );
//        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_1, cdc42 );
//        uniprotService.add( MockUniprotProtein.CDC42_SECONDARY_AC_3, cdc42 );
//        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>();
//        proteins.add(cdc42);
//        proteins.add(canfa);
//        uniprotService.add(MockUniprotProtein.CDC42_SECONDARY_AC_2, proteins);
//
//        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
//        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
//
//        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CDC42_PRIMARY_AC );
//        /*UniprotServiceResult*/ uniprotServiceResult = service.retrieve( MockUniprotProtein.CDC42_SECONDARY_AC_2 );
//        assertEquals(2, uniprotServiceResult.getProteins().size());
//        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
//
//
//    }

    public void testRetrieve_primaryCount2_secondaryCount1() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins();
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        List<ProteinImpl> proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(1, proteinsList.size());
        ProteinImpl protein = proteinsList.get(0);

        /**Create in intact db a second protein with primary identity xref to uniprot equal to the primary id of the uniprot entry.**/
        Protein duplicatedPrimaryProt = new ProteinImpl(IntactContext.getCurrentInstance().getInstitution(),
                protein.getBioSource(),
                protein.getShortLabel(),
                protein.getCvInteractorType());
        proteinDao.saveOrUpdate((ProteinImpl) duplicatedPrimaryProt);
        duplicatedPrimaryProt.setSequence("BLABLA");
        InteractorXref newXref = new InteractorXref(IntactContext.getCurrentInstance().getInstitution(),
                uniprot,
                MockUniprotProtein.CANFA_PRIMARY_AC,
                identity);
        newXref.setParent(duplicatedPrimaryProt);
        XrefDao xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao();
        xrefDao.saveOrUpdate(newXref);
        duplicatedPrimaryProt.addXref(newXref);
        proteinDao.saveOrUpdate((ProteinImpl) duplicatedPrimaryProt);



        /**Create in intact db a protein with primary identity xref to uniprot equal to the secondary id of the uniprot entry.**/
        Protein secondaryProt = new ProteinImpl(IntactContext.getCurrentInstance().getInstitution(),
                protein.getBioSource(),
                protein.getShortLabel(),
                protein.getCvInteractorType());
        proteinDao.saveOrUpdate((ProteinImpl) secondaryProt);
        secondaryProt.setSequence("BLABLA");
        newXref = new InteractorXref(IntactContext.getCurrentInstance().getInstitution(),
                uniprot,
                MockUniprotProtein.CANFA_SECONDARY_AC_1,
                identity);
        newXref.setParent(secondaryProt);
        xrefDao.saveOrUpdate(newXref);
        secondaryProt.addXref(newXref);
        proteinDao.saveOrUpdate((ProteinImpl) secondaryProt);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // Make sure that we have set the database so that there is 2 protein in Intact for the uniprot primary Ac and
        // one corresponding to the uniprot secondary ac.
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        proteinsList =  proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(2,proteinsList.size());
        proteinsList =  proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_SECONDARY_AC_1);
        assertEquals(1,proteinsList.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        proteinsColl = uniprotServiceResult.getProteins();
        assertEquals(0,proteinsColl.size());
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_SECONDARY_AC_1 );
        proteinsColl = uniprotServiceResult.getProteins();
        assertEquals(0,proteinsColl.size());

        System.out.println("proteinsColl.size() = " + proteinsColl.size());
//        Collection<String> errors =  uniprotServiceResult.getErrors();
//        //        assertEquals(1,messages.size());
//        for (String message : errors){
//            System.out.println("MESSAGE : ");
//            System.out.println("message = " + message);
////            assertTrue(message.contains("Unexpected number of protein found in IntAct for UniprotEntry(P60952) Count of " +
////                    "protein in Intact for the Uniprot entry primary ac(1) for the Uniprot entry secondary ac(s)(1)"));
//        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // Make sure that we still have in the database, 2 proteins for the uniprot primary Ac and
        // one corresponding to the uniprot secondary ac (check that nothing as been updated).
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        proteinsList =  proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(2,proteinsList.size());
        proteinsList =  proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_SECONDARY_AC_1);
        assertEquals(1,proteinsList.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

    }


    public void testRetrieve_spliceVariantWith2UniprotIdentity() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins();
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
        List<ProteinImpl> proteinsList = proteinDao.getByXrefLike(uniprot, identity,MockUniprotProtein.CANFA_PRIMARY_AC);
        assertEquals(1, proteinsList.size());
        ProteinImpl protein = proteinsList.get(0);
        List<ProteinImpl> spliceVariants = proteinDao.getSpliceVariants(protein);
        ProteinImpl spliceVariant = spliceVariants.get(0);
        assertNotNull(ProteinUtils.getUniprotXref(spliceVariant));
        InteractorXref newXref = new InteractorXref(IntactContext.getCurrentInstance().getInstitution(),
                uniprot,
                "P12345",
                identity);
        newXref.setParent(spliceVariant);
        XrefDao xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao();
        xrefDao.saveOrUpdate(newXref);
        spliceVariant.addXref(newXref);
        proteinDao.saveOrUpdate(spliceVariant);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(error.contains("Could not find a unique UniProt identity for splice variant:"));
        }
        assertEquals(0,uniprotServiceResult.getProteins().size());
        assertEquals(1,uniprotServiceResult.getErrors().size());

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }


    public void testRetrieve_spliceVariantFoundInIntactNotInUniprot() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins();
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        //In the uniprotService replace canfa with splice variants by canfa without, so that when will try to retrieve
        // canfa, we can test the case where the intact protein has splice variants but not the uniprot entry.
        UniprotProtein canfaWithNoSpliceVariant = MockUniprotProtein.build_CDC42_CANFA_WITH_NO_SPLICE_VARIANT();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfaWithNoSpliceVariant );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfaWithNoSpliceVariant );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfaWithNoSpliceVariant );
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        assertEquals(0,uniprotServiceResult.getProteins().size());
        Map<String ,String> errors = uniprotServiceResult.getErrors();
        Set<String> keySet = errors.keySet();
        assertEquals(1,errors.size());
        for(String errorType : keySet){
            String error = errors.get(errorType);
            assertTrue(error.contains("cdc42_canfa,P60952] but in Uniprot it is not the case"));
        }
        // Assert that the message found it the write one.
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }

    public void testRetrieve_1spliceVariantFoundInIntact2InUniprot() throws Exception{
        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( MockUniprotProtein.CANFA_PRIMARY_AC, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_1, canfa );
        uniprotService.add( MockUniprotProtein.CANFA_SECONDARY_AC_2, canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        //Create the CANFA protein in the empty database, assert it has been created And commit.
        UniprotServiceResult uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        Collection<Protein> proteinsColl = uniprotServiceResult.getProteins();
        assertEquals( 1,proteinsColl.size() );
        String proteinAc = "";
        for(Protein protein : proteinsColl){
            proteinAc = protein.getAc();
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class,CvXrefQualifier.IDENTITY_MI_REF);
        List<ProteinImpl> proteins = proteinDao.getByXrefLike(uniprot, identity, MockUniprotProtein.CANFA_PRIMARY_AC);
        assertNotNull(proteins);
        assertEquals(1,proteins.size());
        Protein intactCanfa = proteins.get(0);
        List<ProteinImpl> spliceVariants = proteinDao.getSpliceVariants(intactCanfa);
        assertEquals(2,spliceVariants.size());
        //We delete one of the splice variants so that the intact canfa has only 1 splice variant when the uniprot canfa
        //got one.
        ProteinImpl splice2delete = spliceVariants.get(1);
        proteinDao.delete(splice2delete);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        //Check that canfa has 1 splice variant and update it in Intact
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class,CvXrefQualifier.IDENTITY_MI_REF);
        proteins = proteinDao.getByXrefLike(uniprot, identity, MockUniprotProtein.CANFA_PRIMARY_AC);
        assertNotNull(proteins);
        assertEquals(1,proteins.size());
        intactCanfa = proteins.get(0);
        spliceVariants = proteinDao.getSpliceVariants(intactCanfa);
        assertEquals(1,spliceVariants.size());
        uniprotServiceResult = service.retrieve( MockUniprotProtein.CANFA_PRIMARY_AC );
        assertEquals(1, uniprotServiceResult.getProteins().size());
        assertEquals(0, uniprotServiceResult.getErrors().size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        //Check now that canfa has 2 splice variants now.
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);
        identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class,CvXrefQualifier.IDENTITY_MI_REF);
        proteins = proteinDao.getByXrefLike(uniprot, identity, MockUniprotProtein.CANFA_PRIMARY_AC);
        assertNotNull(proteins);
        assertEquals(1,proteins.size());
        intactCanfa = proteins.get(0);
        spliceVariants = proteinDao.getSpliceVariants(intactCanfa);
        assertEquals(2,spliceVariants.size());
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }


    public void testRetrieve_TrEMBL_to_SP() throws Exception {
        // checks that protein moving from TrEMBL to SP are detected and updated accordingly.
        // Essentially, that means having a new Primary AC and the current on in the databse becoming secondary.

        // clear database content.
        clearProteinsFromDatabase();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        //  ACs are P60952, P21181, P25763
        uniprotService.add( "P60952", canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );

        UniprotServiceResult uniprotServiceResult = service.retrieve( "P60952" );
        Collection<Protein> proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        Protein protein = proteins.iterator().next();
        String proteinAc = protein.getAc();
        String proteinSeq = protein.getSequence();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        // Set a new primary Id
        canfa.getSecondaryAcs().add( 0, "P60952" );
        canfa.setPrimaryAc( "P12345" );
        canfa.setSequence( "XXXX" );
        canfa.setSequenceLength( canfa.getSequence().length() );
        canfa.setCrc64( "YYYYYYYYYYYYYY" );

        uniprotService.clear();
        uniprotService.add( "P12345", canfa );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        uniprotServiceResult = service.retrieve( "P12345" );
        proteins = uniprotServiceResult.getProteins();
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        protein = proteins.iterator().next();

        // check that we have retrieved the exact same protein.
        assertEquals( proteinAc, protein.getAc() );
        assertEquals( "XXXX", protein.getSequence() );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testConstructor() throws Exception{
        try{
            ProteinService proteinService = new ProteinServiceImpl(null);
            fail("Should have thrown and IllegalArgumentExcpetion.");
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }

    public void testSetBioSource() throws Exception{
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        try{
            service.setBioSourceService(null );
            fail("Should have thrown and IllegalArgumentExcpetion.");
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }

}
