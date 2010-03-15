package uk.ac.ebi.intact.core.users.model;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import uk.ac.ebi.intact.core.users.persistence.dao.RoleDao;
import uk.ac.ebi.intact.core.users.persistence.dao.UserDao;
import uk.ac.ebi.intact.core.users.unit.UsersBasicTestCase;

/**
 * User tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class UserTest extends UsersBasicTestCase {

    @Test
    public void persist() throws Exception {

        User john = new User( "john.doe", "john", "doe", "john.doe@gmail.com" );
        final UserDao userDao = getDaoFactory().getUserDao();

        userDao.persist( john );
        Assert.assertEquals( 1, userDao.countAll() );

        final User reloadedJohn = userDao.getAll().get( 0 );
        Assert.assertEquals( "john.doe", reloadedJohn.getLogin() );
        Assert.assertEquals( "john", reloadedJohn.getFirstName() );
        Assert.assertEquals( "doe", reloadedJohn.getLastName() );
        Assert.assertEquals( "john.doe@gmail.com", reloadedJohn.getEmail() );
        Assert.assertEquals( null, reloadedJohn.getLastLogin() );
    }

    @Test
    public void invalidLoginSearch() throws Exception {
        final UserDao userDao = getDaoFactory().getUserDao();
        Assert.assertEquals( 0, userDao.countAll() );
        User user = userDao.getByLogin( "foo" );
        Assert.assertNull( user );
    }

    @Test
    public void invalidEmailSearch() throws Exception {
        final UserDao userDao = getDaoFactory().getUserDao();
        Assert.assertEquals( 0, userDao.countAll() );
        User user = userDao.getByEmail( "foo@bar.com" );
        Assert.assertNull( user );
    }

    @Test
    public void updateEmail() throws Exception {
        User john = new User( "john.doe", "john", "doe", "john.doe@gmail.com" );
        final UserDao userDao = getDaoFactory().getUserDao();

        userDao.persist( john );
        userDao.flush();

        final User reloadedJohn = userDao.getByLogin( "john.doe" );
        reloadedJohn.setEmail( "john.doe@hotmail.com" );
        userDao.update( reloadedJohn );
        userDao.flush();

        final User johnAgain = userDao.getByEmail( "john.doe@hotmail.com" );
        Assert.assertEquals( "john.doe", johnAgain.getLogin() );
        Assert.assertEquals( "john.doe@hotmail.com", johnAgain.getEmail() );
    }

    @Test
    public void getByLogin() throws Exception {
        User john = new User( "john.doe", "john", "doe", "john.doe@gmail.com" );
        final UserDao userDao = getDaoFactory().getUserDao();
        userDao.persist( john );
        
        final User user = userDao.getByLogin( "john.doe" );
        Assert.assertNotNull( user );
    }

    @Test
    public void getByEmail() throws Exception {
        User john = new User( "john.doe", "john", "doe", "john.doe@gmail.com" );
        final UserDao userDao = getDaoFactory().getUserDao();
        userDao.persist( john );
        
        final User user = userDao.getByEmail( "john.doe@gmail.com" );
        Assert.assertNotNull( user );
    }

    @Test( expected = DataIntegrityViolationException.class )
    public void uniqueLogin() throws Exception {

        final UserDao userDao = getDaoFactory().getUserDao();

        User john = new User( "jdoe", "john", "doe", "john.doe@gmail.com" );
        userDao.persist( john );

        User jack = new User( "jdoe", "jack", "doe", "jack.doe@gmail.com" );
        userDao.persist( jack );

        userDao.flush();
    }

    @Test( expected = DataIntegrityViolationException.class )
    public void uniqueEmail() throws Exception {
        final UserDao userDao = getDaoFactory().getUserDao();

        User john = new User( "john.doe", "john", "doe", "jdoe@gmail.com" );
        userDao.persist( john );

        User jack = new User( "jack.doe", "jack", "doe", "jdoe@gmail.com" );
        userDao.persist( jack );

        userDao.flush();
    }

    @Test
    public void addRole() throws Exception {
        final UserDao userDao = getDaoFactory().getUserDao();
        Assert.assertEquals( 0, userDao.countAll() );
        User john = new User( "john.doe", "john", "doe", "john.doe@gmail.com" );
        userDao.persist( john );

        final RoleDao roleDao = getDaoFactory().getRoleDao();
        Role curator = new Role( "CURATOR" );
        roleDao.persist( curator );

        john.addRole( curator );
        userDao.update( john );

        userDao.flush();

        final User reloadedJohn = userDao.getByLogin( "john.doe" );
        Assert.assertNotNull( reloadedJohn );
        Assert.assertEquals( 1, reloadedJohn.getRoles().size() );
        Role role = reloadedJohn.getRoles().iterator().next();
        Assert.assertEquals( "CURATOR", role.getName() );
    }
}
