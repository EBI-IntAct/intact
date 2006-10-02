/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class AnnotatedObjectDaoImpl<T extends AnnotatedObject> extends IntactObjectDaoImpl<T> implements AnnotatedObjectDao<T>
{

    private static final Log log = LogFactory.getLog(AnnotatedObjectDaoImpl.class);

    public AnnotatedObjectDaoImpl(Class<T> entityClass, Session session, IntactSession intactSession)
    {
        super(entityClass, session, intactSession);
    }

    public T getByShortLabel(String value)
    {
        return getByShortLabel(value, true);
    }

    public T getByShortLabel(String value, boolean ignoreCase)
    {
        return getByPropertyName("shortLabel", value, ignoreCase);
    }

    public Collection<T> getByShortLabelLike(String value)
    {
       return getByPropertyNameLike("shortLabel", value);
    }

    public Collection<T> getByShortLabelLike(String value, int firstResult, int maxResults)
    {
        return getByPropertyNameLike("shortLabel", value, true, firstResult, maxResults);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase)
    {
       return getByPropertyNameLike("shortLabel", value, ignoreCase, -1, -1);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, int firstResult, int maxResults)
    {
        return getByPropertyNameLike("shortLabel", value, ignoreCase, firstResult, maxResults);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc)
    {
        return getByPropertyNameLike("shortLabel", value, ignoreCase, firstResult, maxResults, orderAsc);
    }

    public T getByXref(String primaryId)
    {
        return (T) getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.eq("xref.primaryId", primaryId)).uniqueResult();
    }

    public List<T> getByXrefLike(String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId)).list();
    }

    public List<T> getByXrefLike(CvDatabase database, String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId))
                .add(Restrictions.eq("xref.cvDatabase", database)).list();
    }

    public List<T> getByXrefLike(CvDatabase database, CvXrefQualifier qualifier, String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId))
                .add(Restrictions.eq("xref.cvDatabase", database))
                .add(Restrictions.eq("xref.cvXrefQualifier", qualifier)).list();

    }

    public String getPrimaryIdByAc(String ac, String cvDatabaseShortLabel)
    {
       return (String) getSession().createCriteria(getEntityClass())
               .add(Restrictions.idEq(ac))
               .createAlias("xrefs", "xref")
               .createAlias("xref.cvDatabase", "cvDatabase")
               .add(Restrictions.like("cvDatabase.shortLabel", cvDatabaseShortLabel))
               .setProjection(Property.forName("xref.primaryId")).uniqueResult();

    }

    public List<T> getByAnnotationAc(String ac)
    {
        return getSession().createCriteria(getEntityClass())
                .createAlias("annotations", "annot")
                .add(Restrictions.eq("annot.ac", ac)).list();
    }

    /**
     * Return a collection of annotated object of type <T> being annotated with an annotation having
     * a topic equal to the topic given in parameter and the description equal to the description given
     * in parameter.
     * @param topic
     * @param description
     * @return  a list of annotated objects.
     */
    public List<T> getByAnnotationTopicAndDescription(CvTopic topic, String description){
        return getSession().createCriteria(getEntityClass()).createAlias("annotations","annot")
                .add(Restrictions.eq("annot.cvTopic",topic))
                .add(Restrictions.eq("annot.annotationText",description )).list();
    }

    /**
     * Gets all the CVs for the current entity
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden if true exclude the hidden CVs
     * @return the list of CVs
     */
    public List<T> getAll(boolean excludeObsolete, boolean excludeHidden)
    {

        Criteria crit = getSession().createCriteria(getEntityClass()).addOrder(Order.asc("shortLabel"));
        List<T> listTotal = crit.list();
        Collection<T> subList = Collections.EMPTY_LIST;
        if (excludeObsolete || excludeHidden)
        {
            crit.createAlias("annotations", "annot")
                .createAlias("annot.cvTopic", "annotTopic");
        }

        if (excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.or(
                    Restrictions.eq("annotTopic.shortLabel", CvTopic.OBSOLETE),
                    Restrictions.eq("annotTopic.shortLabel",CvTopic.HIDDEN))
                    );
            subList = crit.list();
        }
        else if (excludeObsolete && !excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.OBSOLETE));
            subList = crit.list();
        }
        else if (!excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.HIDDEN));
            subList = crit.list();
        }

        listTotal.removeAll(subList);
        return listTotal;
    }
    /**
     * This method will search in the database an AnnotatedObject of type T having it's shortlabel or it's
     * ac like the searchString given in argument.
     * @param searchString (ex : "butkevitch-2006-%", "butkevitch-%-%", "EBI-12345%"
     * @return a List of AnnotatedObject having their ac or shortlabel like the searchString
     */
    public List<T> getByShortlabelOrAcLike(String searchString)
    {
        return getSession().createCriteria(getEntityClass()).addOrder(Order.asc("shortLabel"))
                .add(Restrictions.or(
                        Restrictions.like("ac",searchString).ignoreCase(),
                        Restrictions.like("shortLabel",searchString).ignoreCase())).list();
    }

    /**
     * Persists the annotated object, creating the search items
     * @param objToPersist
     */
    @Override
    public void persist(T objToPersist)
    {
        super.persist(objToPersist);
        saveSearchItemsForAnnotatedObject(objToPersist);
    }

    @Override
    public void delete(T objToDelete)
    {
        super.delete(objToDelete);
        deleteSearchItemsForAnnotatedbject(objToDelete);
    }

    @Override
    public void saveOrUpdate(T objToPersist)
    {
        super.saveOrUpdate(objToPersist);
        saveSearchItemsForAnnotatedObject(objToPersist);
    }

    @Override
    public void update(T objToUpdate)
    {
        super.update(objToUpdate);
        updateSearchItemsForAnnotatedbject(objToUpdate);
    }

    private void saveSearchItemsForAnnotatedObject(AnnotatedObject<? extends Xref, ? extends Alias> ao)
    {
        if (!RuntimeConfig.getCurrentInstance(getIntactSession()).isSynchronizedSearchItems())
        {
            return;
        }

        for (SearchItem searchItem : searchItemsForAnnotatedObject(ao))
        {
            SearchItemPk pk = new SearchItemPk(searchItem.getAc(), searchItem.getValue(), searchItem.getObjClass(), searchItem.getType());
            if (null == getSession().get(SearchItem.class, pk))
            {
               getSession().saveOrUpdate(searchItem);
            }
        }
    }

    private void updateSearchItemsForAnnotatedbject(AnnotatedObject<? extends Xref, ? extends Alias> ao)
    {
        if (!RuntimeConfig.getCurrentInstance(getIntactSession()).isSynchronizedSearchItems())
        {
            return;
        }

        Criteria crit = getSession().createCriteria(SearchItem.class)
                .add(Restrictions.eq("ac", ao.getAc()));

        List<SearchItem> searchItems = searchItemsForAnnotatedObject(ao);
        List<SearchItem> oldSearchItems = crit.list();

        Collection<SearchItem> itemsToRemove = CollectionUtils.subtract(searchItems,  oldSearchItems);

        for (SearchItem itemToRemove : itemsToRemove)
        {
            SearchItemPk pk = new SearchItemPk(itemToRemove.getAc(), itemToRemove.getValue(), itemToRemove.getObjClass(), itemToRemove.getType());
            SearchItem existingSearchItem = (SearchItem) getSession().get(SearchItem.class, pk);
            if (existingSearchItem != null)
            {
               getSession().delete(existingSearchItem);
            }
        }

        Collection<SearchItem> itemsToSave = CollectionUtils.subtract(oldSearchItems, searchItems);

        for (SearchItem itemToSave : itemsToSave)
        {
            SearchItemPk pk = new SearchItemPk(itemToSave.getAc(), itemToSave.getValue(), itemToSave.getObjClass(), itemToSave.getType());
            SearchItem existingSearchItem = (SearchItem) getSession().get(SearchItem.class, pk);
            if (existingSearchItem != null)
            {
               getSession().saveOrUpdate(existingSearchItem);
            }
        }
    }

    private void deleteSearchItemsForAnnotatedbject(AnnotatedObject<? extends Xref, ? extends Alias> ao)
    {
        if (!RuntimeConfig.getCurrentInstance(getIntactSession()).isSynchronizedSearchItems())
        {
            return;
        }
        
        Query deleteQuery = getSession().createQuery("delete SearchItem searchItem where searchItem.ac = :ac");
        deleteQuery.setParameter("ac", ao.getAc());

        int count = deleteQuery.executeUpdate();

        if (log.isDebugEnabled())
        {
            log.debug(count + " SearchItems removed for: "+ao.getAc());
        }

    }

    private static List<SearchItem> searchItemsForAnnotatedObject(AnnotatedObject<? extends Xref, ? extends Alias> ao)
    {
        List<SearchItem> searchItems = new ArrayList<SearchItem>();
        searchItems.add(new SearchItem(ao.getAc(), ao.getAc(), ao.getClass().getName(), "ac"));
        searchItems.add(new SearchItem(ao.getAc(), ao.getShortLabel(), ao.getClass().getName(), "shortlabel"));

        if (ao.getFullName() != null)
        {
            searchItems.add(new SearchItem(ao.getAc(), ao.getFullName(), ao.getClass().getName(), "fullname"));
        }

        for (Alias alias : ao.getAliases())
        {
            searchItems.add(new SearchItem(ao.getAc(), alias.getName(), ao.getClass().getName(), alias.getCvAliasType().getShortLabel()));
        }

        return searchItems;
    }
}
