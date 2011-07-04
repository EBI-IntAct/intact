package uk.ac.ebi.intact.core.users.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.users.model.Role;
import uk.ac.ebi.intact.core.users.persistence.dao.RoleDao;

import javax.persistence.Query;
import java.util.List;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class RoleDaoImpl extends UsersBaseDaoImpl<Role> implements RoleDao {

    public RoleDaoImpl() {
        super( Role.class );
    }

    public Role getRoleByName( String name ) {
        if ( name == null ) {
            throw new IllegalArgumentException( "You must give a non null name" );
        }
        final Query query = getEntityManager().createQuery( "select r from Role r where upper(r.name) = :name" );
        query.setParameter( "name", name.toUpperCase() );
        List<Role> roles = query.getResultList();
        if ( roles.isEmpty() ) {
            return null;
        }
        return roles.get( 0 );
    }
}
