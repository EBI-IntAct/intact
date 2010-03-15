/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.users.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.core.users.persistence.dao.RoleDao;
import uk.ac.ebi.intact.core.users.persistence.dao.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: DaoFactory.java 13934 2009-12-17 11:46:41Z brunoaranda $
 * @since <pre>24-Apr-2006</pre>
 */
@Component
public class DaoFactory implements Serializable {

    @PersistenceContext( unitName = "intact-users-default" )
    private EntityManager currentEntityManager;

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    public DaoFactory() {
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public RoleDao getRoleDao() {
        return roleDao;
    }

    public EntityManager getEntityManager() {
        return currentEntityManager;
    }

    public void setEntityManager( EntityManager entityManager ) {
        currentEntityManager = entityManager;
    }
}
