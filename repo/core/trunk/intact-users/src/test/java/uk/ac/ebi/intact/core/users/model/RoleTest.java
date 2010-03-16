package uk.ac.ebi.intact.core.users.model;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import uk.ac.ebi.intact.core.users.persistence.dao.RoleDao;
import uk.ac.ebi.intact.core.users.unit.UsersBasicTestCase;

/**
 * Role Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class RoleTest extends UsersBasicTestCase {

    @Test
    public void search() throws Exception {
        final RoleDao roleDao = getDaoFactory().getRoleDao();
        Assert.assertEquals( 0, roleDao.countAll() );

        Role role = new Role( "CURATOR" );
        roleDao.persist( role );
        roleDao.flush();
        Assert.assertEquals( 1, roleDao.countAll() );

        Assert.assertNotNull( roleDao.getRoleByName( "CURATOR" ) );
        Assert.assertNotNull( roleDao.getRoleByName( "cuRAtor" ) );
        Assert.assertNotNull( roleDao.getRoleByName( "curator" ) );

        Assert.assertNull( roleDao.getRoleByName( "foo" ) );
    }

    @Test( expected = DataIntegrityViolationException.class )
    public void uniqueRole() throws Exception {
        final RoleDao roleDao = getDaoFactory().getRoleDao();
        Assert.assertEquals( 0, roleDao.countAll() );

        Role role1 = new Role( "CURATOR" );
        roleDao.persist( role1 );

        Role role2 = new Role( "CURATOR" );
        roleDao.persist( role2 );

        roleDao.flush();
    }
}
