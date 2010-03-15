package uk.ac.ebi.intact.core.users.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.users.persistence.dao.UsersBaseDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Base operations for persistence of user management objects.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class UsersBaseDaoImpl<T> implements UsersBaseDao<T> {

    @PersistenceContext( unitName = "intact-users-default" )
    private EntityManager entityManager;

    private Class<T> entityClass;

    public UsersBaseDaoImpl( Class<T> entityClass ) {
        this.entityClass = entityClass;
    }

    @Transactional( readOnly = true )
    public int countAll() {
        return ( Integer ) getSession()
                .createCriteria( entityClass )
                .setProjection( Projections.rowCount() )
                .uniqueResult();
    }

    @Transactional( readOnly = true )
    public List<T> getAll() {
        return entityManager.createQuery( "select u from User As u" ).getResultList();
    }

    public void persist( T entity ) {
        entityManager.persist( entity );
    }

    public void delete( T entity ) {
        entityManager.remove( entity );
    }

    public void update( T entity ) {
        getSession().update( entity );
    }

    public void saveOrUpdate( T entity ) {
        getSession().saveOrUpdate( entity );
    }

    public void flush() {
        entityManager.flush();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Session getSession() {
        return ( ( HibernateEntityManager ) entityManager ).getSession();
    }
}
