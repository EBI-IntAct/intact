package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.DatabaseTestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.util.*;

/**
 * IntactObjectDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version
 */
public class IntactObjectDaoImplTest extends DatabaseTestCase {

    public IntactObjectDaoImplTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( IntactObjectDaoImplTest.class );
    }

    ////////////////////
    // Tests

//    public void testGetByAcLike() throws Exception {
//        fail( "Not yet implemented." );
//    }

//    public void testGetByAcLike1() throws Exception {
//        fail( "Not yet implemented." );
//    }

    public void testGetByAc_String() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        InteractionImpl i = idao.getByAc( "TEST-5195" );
        assertNotNull( i );
        assertEquals( "cara-7", i.getShortLabel() );

        assertNull( idao.getByAc( "xxx" ) );
    }

    public void testGetByAc_array() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();

        // retreive a single interaction
        List<InteractionImpl> is = idao.getByAc( new String[]{"TEST-5195"} );
        assertNotNull( is );
        assertEquals( 1, is.size() );
        InteractionImpl i = is.iterator().next();
        assertNotNull( i );
        assertEquals( "cara-7", i.getShortLabel() );

        // retreive multiple interactions
        is = null;
        is = idao.getByAc( new String[]{"TEST-5195", "TEST-5317"} );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( "TEST-5195" ) ) {
                assertEquals( "cara-7", ii.getShortLabel() );
            } else if ( ii.getAc().equals( "TEST-5317" ) ) {
                assertEquals( "ubx-dip1", ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC 'TEST-5195' or 'TEST-5317'." );
            }
        }

        // retreive multiple non existing interactions
        is = null;
        is = idao.getByAc( new String[]{"xxx", "yyy"} );
        assertNotNull( is );
        assertTrue( is.isEmpty() );

        // retreive a mixture of true and false potitive
        is = null;
        is = idao.getByAc( new String[]{"TEST-5195", "yyy", "TEST-5317", "zzz"} );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( "TEST-5195" ) ) {
                assertEquals( "cara-7", ii.getShortLabel() );
            } else if ( ii.getAc().equals( "TEST-5317" ) ) {
                assertEquals( "ubx-dip1", ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC 'TEST-5195' or 'TEST-5317'." );
            }
        }
    }

    public void testGetByAc_collection() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();

        // retreive a single interaction
        List<InteractionImpl> is = idao.getByAc( Arrays.asList( new String[]{"TEST-5195"} ) );
        assertNotNull( is );
        assertEquals( 1, is.size() );
        InteractionImpl i = is.iterator().next();
        assertNotNull( i );
        assertEquals( "cara-7", i.getShortLabel() );

        // retreive multiple interactions
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{"TEST-5195", "TEST-5317"} ) );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( "TEST-5195" ) ) {
                assertEquals( "cara-7", ii.getShortLabel() );
            } else if ( ii.getAc().equals( "TEST-5317" ) ) {
                assertEquals( "ubx-dip1", ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC 'TEST-5195' or 'TEST-5317'." );
            }
        }

        // retreive multiple non existing interactions
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{"xxx", "yyy"} ) );
        assertNotNull( is );
        assertTrue( is.isEmpty() );

        // retreive a mixture of true and false potitive
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{"TEST-5195", "yyy", "TEST-5317", "zzz"} ) );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( "TEST-5195" ) ) {
                assertEquals( "cara-7", ii.getShortLabel() );
            } else if ( ii.getAc().equals( "TEST-5317" ) ) {
                assertEquals( "ubx-dip1", ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC 'TEST-5195' or 'TEST-5317'." );
            }
        }
    }

    public void testGetAll() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll();
        assertEquals( 9, list.size() );
    }

    public void testGetAllIterator() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        Iterator<InteractionImpl> ii = idao.getAllIterator();
        int count = 0;
        if ( ii.hasNext() ) {
            count++;
            assertNotNull( ii.next() );
        }
        assertEquals( 9, count );
    }

    public void testGetAll_int_int() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll( 0, 3 );
        assertNotNull( list );
        assertEquals( 3, list.size() );
    }

    public void testExists() {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll();
        assertNotNull( list );
        assertEquals( 9, list.size() );

        InteractionImpl interaction = list.get( 3 );
        assertTrue( idao.exists( interaction ) );

        // build mock and check that it doesn't exist in the database
        Institution owner = new Institution( "test" );
        CvInteractionType type = new CvInteractionType( owner, "type" );
        CvInteractorType intType = new CvInteractorType( owner, "interaction" );
        Collection<Experiment> exps = new ArrayList<Experiment>();
        InteractionImpl i = new InteractionImpl( exps, type, intType, "sl", owner );

        assertFalse( idao.exists( i ) );

        i.setAc( "lala" );
        assertFalse( idao.exists( i ) );
    }
}