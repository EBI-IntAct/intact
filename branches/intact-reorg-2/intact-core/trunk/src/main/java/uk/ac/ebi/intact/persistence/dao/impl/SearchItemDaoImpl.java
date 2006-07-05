/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import uk.ac.ebi.intact.model.SearchItem;
import uk.ac.ebi.intact.persistence.dao.impl.IntactObjectDaoImpl;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * DAO for search items
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class SearchItemDaoImpl extends IntactObjectDaoImpl<SearchItem> implements uk.ac.ebi.intact.persistence.dao.SearchItemDao
{

    public SearchItemDaoImpl(Session session)
    {
        super(SearchItem.class, session);
    }

    public Map<String,Integer> countGroupsByValuesLike(String[] values, String[] objClasses, String type)
    {
        Map<String,Integer> results = new HashMap<String,Integer>();

        List<Object[]> critRes = criteriaByValues(values, objClasses, type)
                                     .setProjection(Projections.projectionList()
                                             .add(Projections.countDistinct("ac"))
                                             .add(Projections.groupProperty("objClass"))).list();

        for (Object[] res : critRes)
        {
             results.put((String)res[1], (Integer)res[0]);
        }

        return results;
    }

    public List<String> getDistinctAc(String[] values, String[] objClasses, String type, int firstResult, int maxResults){

         return criteriaByValues(values, objClasses, type)
                                    .setFirstResult(firstResult)
                                    .setMaxResults(maxResults)
                                    .setProjection(
                                        Projections.distinct(Projections.property("ac"))).list();

    }


    public Map<String, String> getDistinctAcGroupingByObjClass(String[] values, String[] objClasses, String type, int firstResult, int maxResults){

        Map<String,String> results = new HashMap<String,String>();

        List<Object[]> critRes = criteriaByValues(values, objClasses, type)
                                    .setFirstResult(firstResult)
                                    .setMaxResults(maxResults)
                                    .setProjection(Projections.projectionList()
                                        .add(Projections.distinct(Projections.property("ac")))
                                        .add(Projections.property("objClass"))).list();

        for (Object[] res : critRes)
        {
             results.put((String)res[0], (String)res[1]);
        }

        return results;
    }

    private Criteria criteriaByValues(String[] values, String[] objClasses, String type)
    {
        Criteria crit = getSession().createCriteria(SearchItem.class);

        // no restriction (WHERE) necessary if only one value is passed and it is a wildcard
        if (!(values.length == 1 && values[0].equals("%")))
        {
            crit.add(disjunctionForArray("value", values));
        }

        if (objClasses != null)
        {
            if (objClasses.length > 1)
            {
                crit.add(disjunctionForArray("objClass", objClasses));
            }
            else
            {
                if (objClasses[0] != null)
                    crit.add(Restrictions.eq("objClass", objClasses[0]));
            }
        }

        if (type != null)
        {
            crit.add(Restrictions.eq("type", type));
        }

        return crit;
    }

}
