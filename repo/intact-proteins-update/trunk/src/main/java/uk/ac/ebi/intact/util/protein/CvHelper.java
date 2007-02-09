/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.protein;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * Controlled Vocabulary Helper, provides methods for retreiving CV terms.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Feb-2007</pre>
 */
public class CvHelper {

    public CvDatabase getDatabaseByMi( String miRef ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvDatabase> cvObjectDao = daoFactory.getCvObjectDao( CvDatabase.class );
        return cvObjectDao.getByPsiMiRef( miRef );
    }

    public CvXrefQualifier getQualifierByMi( String miRef ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvXrefQualifier> cvObjectDao = daoFactory.getCvObjectDao( CvXrefQualifier.class );
        return cvObjectDao.getByPsiMiRef( miRef );
    }

    public CvAliasType getAliasTypeByMi( String miRef ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvAliasType> cvObjectDao = daoFactory.getCvObjectDao( CvAliasType.class );
        return cvObjectDao.getByPsiMiRef( miRef );
    }

    public CvTopic getTopicByMi( String miRef ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvTopic> cvObjectDao = daoFactory.getCvObjectDao( CvTopic.class );
        return cvObjectDao.getByPsiMiRef( miRef );
    }

    public CvInteractorType getInteractorTypeByMi( String miRef ) {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvObjectDao<CvInteractorType> cvObjectDao = daoFactory.getCvObjectDao( CvInteractorType.class );
        return cvObjectDao.getByPsiMiRef( miRef );
    }

    public Institution getInstitution() {
        return IntactContext.getCurrentInstance().getInstitution();
    }
}