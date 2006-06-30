/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.persistence.dao.BioSourceDao;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Jun-2006</pre>
 */
public class BioSourceDaoImpl extends AnnotatedObjectDaoImpl<BioSource> implements BioSourceDao 
{

    private static final Log log = LogFactory.getLog(BioSourceDaoImpl.class);

    public BioSourceDaoImpl(Session session) {
        super(BioSource.class, session);
    }


    public Collection<BioSource> getByTaxonId(String taxonId)
    {
        return getSession().createCriteria(BioSource.class)
                .add(Restrictions.eq("taxId", taxonId)).list();
    }
}
