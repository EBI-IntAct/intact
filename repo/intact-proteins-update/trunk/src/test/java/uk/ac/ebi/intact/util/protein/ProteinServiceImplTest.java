package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.util.protein.mock.MockAlarmProcessor;
import uk.ac.ebi.intact.util.protein.mock.MockUniprotService;
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

    MockAlarmProcessor alarmProcessor = new MockAlarmProcessor();

    private ProteinService buildProteinService() {
        UniprotService uniprotService = new MockUniprotService();
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

    ////////////////////
    // Tests

    public void testUpdate() throws Exception {
        ProteinService service = buildProteinService();
        Collection<Protein> proteins = service.retrieve( "P60952" ); /* CDC42_CANFA */

        assertNotNull( proteins );
        for ( Protein protein : proteins ) {
            System.out.println( protein );
        }
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
    }
}
