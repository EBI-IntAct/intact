package uk.ac.ebi.intact.curationTools.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */

public class CurationToolsDaoFactory implements Serializable{
    private EntityManager currentEntityManager;

    //private UserDao userDao;

    //private RoleDao roleDao;

    public CurationToolsDaoFactory() {
    }

    //@Autowired
    //public UserDao getUserDao() {
       // return userDao;
    //}

   // @Autowired
   // public RoleDao getRoleDao() {
     //   return roleDao;
   // }

    @PersistenceContext( unitName = "intact-curationTools-default" )
    public EntityManager getEntityManager() {
        return currentEntityManager;
    }

    public void setEntityManager( EntityManager entityManager ) {
        currentEntityManager = entityManager;
    }
}
