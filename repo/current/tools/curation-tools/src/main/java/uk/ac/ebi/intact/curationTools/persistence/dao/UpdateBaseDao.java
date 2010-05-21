package uk.ac.ebi.intact.curationTools.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.annotation.Mockable;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Mockable
public interface UpdateBaseDao<T> {

    public int countAll();

    public List<T> getAll();

    public void persist( T entity );

    public void delete( T entity );

    public void update( T entity );

    public void saveOrUpdate( T entity );

    public void flush();

    public EntityManager getEntityManager();

    public Session getSession();

    public void setEntityClass(Class<T> entityClass);
}
