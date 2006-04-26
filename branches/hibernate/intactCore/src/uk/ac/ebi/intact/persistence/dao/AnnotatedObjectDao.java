/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.SimpleExpression;

import java.util.Collection;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class AnnotatedObjectDao<T extends AnnotatedObject> extends IntactObjectDao<T>
{

    public AnnotatedObjectDao(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }

    public T getByShortLabel(String value)
    {
        return getByShortLabel(value, true);
    }

    public T getByShortLabel(String value, boolean ignoreCase)
    {
        return getByShortLabel(value, ignoreCase);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, MatchMode matchMode)
    {
       return getByPropertyNameLike("shortLabel", value, ignoreCase, matchMode);
    }

   

}
