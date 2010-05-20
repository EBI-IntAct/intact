package uk.ac.ebi.intact.curationTools.persistence.dao;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */

public interface UpdateBaseDao<T> {

    int countAll();

    List<T> getAll();

    void persist( T entity );

    void delete( T entity );

    void update( T entity );

    void saveOrUpdate( T entity );

    void flush();

    EntityManager getEntityManager();

    Session getSession();
}
