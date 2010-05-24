package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.HibernatePersistent;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateBaseDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Transactional(readOnly = true)
public class UpdateBaseDaoImpl<T extends HibernatePersistent> implements UpdateBaseDao<T>{

    @PersistenceContext( unitName = "intact-curationTools-default" )
    private EntityManager entityManager;

    private Class<T> entityClass;

    public UpdateBaseDaoImpl( Class<T> entityClass ) {
        this.entityClass = entityClass;
    }

     public UpdateBaseDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        this.entityClass = entityClass;
    }

    public int countAll() {
        final Query query = entityManager.createQuery( "select count(*) from " + entityClass.getSimpleName() + " e" );
        return ((Long) query.getSingleResult()).intValue();
    }

    public List<T> getAll() {
        return entityManager.createQuery( "select e from " + entityClass.getSimpleName() + " e" ).getResultList();
    }

    public void persist( T entity ) {
        getEntityManager().persist( entity );
    }

    public void delete( T entity ) {
        getEntityManager().remove( entity );
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
        return ( (HibernateEntityManager) entityManager ).getSession();
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
