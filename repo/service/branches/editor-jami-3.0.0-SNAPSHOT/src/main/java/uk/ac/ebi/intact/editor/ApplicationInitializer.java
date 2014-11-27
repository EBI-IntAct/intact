package uk.ac.ebi.intact.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.List;

/**
 * Application initializer.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
@Service
public class ApplicationInitializer extends AbstractEditorService implements InitializingBean {

    private static final Log log = LogFactory.getLog( ApplicationInitializer.class );

    @Override
    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void afterPropertiesSet() throws Exception {
        // attach current dao to clear it after commit
        attachDaoToTransactionManager();

        createDefaultRoles();
        createDefaultUsers();
    }

    private void createDefaultUsers() throws SynchronizerException, FinderException, PersisterException {
        User admin = getIntactDao().getUserDao().getByLogin( "admin" );
        if ( admin == null ) {
            admin = new User( "admin", "Admin", "N/A", "intact-admin@ebi.ac.uk" );
            admin.setPassword( "d033e22ae348aeb5660fc2140aec35850c4da997" );

            Role adminRole = getIntactDao().getRoleDao().getByName("ADMIN");
            if (adminRole == null){
                adminRole = new Role("ADMIN");
                persistIntactObject(adminRole, getIntactDao().getRoleDao());
            }
            admin.addRole( adminRole );

            persistIntactObject(admin, getIntactDao().getUserDao());
        }
    }

    private void createDefaultRoles() throws SynchronizerException, FinderException, PersisterException {
        final List<Role> allRoles = getIntactDao().getRoleDao().getAll();
        addMissingRole( allRoles, "ADMIN" );
        addMissingRole( allRoles, "CURATOR" );
        addMissingRole( allRoles, "REVIEWER" );
        addMissingRole( allRoles, "COMPLEX_CURATOR" );
        addMissingRole( allRoles, "COMPLEX_REVIEWER" );

        log.info( "After loadParticipants: found " + getIntactDao().getRoleDao().getAll().size() + " role(s) in the database." );
    }

    private void addMissingRole( List<Role> allRoles, String roleName ) throws SynchronizerException, FinderException, PersisterException {
        boolean found = false;
        for ( Role role : allRoles ) {
            if ( role.getName().equals( roleName ) ) {
                found = true;
            }
        }

        if ( !found ) {
            Role role = new Role( roleName );
            persistIntactObject(role, getIntactDao().getRoleDao());
            if ( log.isInfoEnabled() ) {
                log.info( "Created user role: " + roleName );
            }
        }
    }
}
