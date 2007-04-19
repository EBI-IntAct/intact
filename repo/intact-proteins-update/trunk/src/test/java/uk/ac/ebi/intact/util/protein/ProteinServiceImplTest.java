package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.util.protein.mock.*;
import uk.ac.ebi.intact.util.taxonomy.DummyTaxonomyService;

import java.util.Collection;
import java.util.List;

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

    /////////////////////
    // Instance variable

    private final MockAlarmProcessor alarmProcessor = new MockAlarmProcessor();

    //////////////////////
    // Helper methods

    private ProteinService buildProteinService() {
        UniprotService uniprotService = new MockUniprotService();
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        service.setAlarmProcessor( alarmProcessor );
        return service;
    }

    private ProteinService buildProteinService( UniprotService uniprotService ) {
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        service.setAlarmProcessor( alarmProcessor );
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

        ProteinService service = buildProteinService();
        Collection<Protein> proteins = service.retrieve( "P60952" ); /* CDC42_CANFA */

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

        proteins = service.retrieve( "P60952" ); /* CDC42_CANFA */

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
    }

    public void testRetrieve_update_CDC42_CANFA() throws Exception {

        FlexibleMockUniprotService service = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        service.add( "P60952", canfa );

        ProteinService proteinService = buildProteinService( service );
        Collection<Protein> proteins = proteinService.retrieve( "P60952" );
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

        proteins = proteinService.retrieve( "P60952" );
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
    }

    public void testRetrieve_sequenceUpdate() throws ProteinServiceException, IntactTransactionException {

        // clear database content.
        clearProteinsFromDatabase();
        
        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        uniprotService.add( "P60952", canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        service.setAlarmProcessor( alarmProcessor );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Collection<Protein> proteins = service.retrieve( "P60952" );
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

        proteins = service.retrieve( "P60952" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        protein = proteins.iterator().next();

        // check that we have retrieved the exact same protein.
        assertEquals( newSequence, protein.getSequence() );
        assertEquals( Crc64.getCrc64( newSequence ), protein.getCrc64() );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testRetrieve_TrEMBL_to_SP() throws Exception {
        // checks that protein moving from TrEMBL to SP are detected and updated accordingly.
        // Essentially, that means having a new Primary AC and the current on in the databse becoming secondary.

        // clear database content.
        clearProteinsFromDatabase();

        // TODO spelling of 'retrieve' in UniprotService is wrong ! Fix it.

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        FlexibleMockUniprotService uniprotService = new FlexibleMockUniprotService();
        UniprotProtein canfa = MockUniprotProtein.build_CDC42_CANFA();
        //  ACs are P60952, P21181, P25763
        uniprotService.add( "P60952", canfa );

        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new DummyTaxonomyService() ) );
        service.setAlarmProcessor( alarmProcessor );

        Collection<Protein> proteins = service.retrieve( "P60952" );
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

        proteins = service.retrieve( "P12345" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        protein = proteins.iterator().next();

        // check that we have retrieved the exact same protein.
        assertEquals( proteinAc, protein.getAc() );
        assertEquals( "XXXX", protein.getSequence() );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public void testX() {

    }
}
