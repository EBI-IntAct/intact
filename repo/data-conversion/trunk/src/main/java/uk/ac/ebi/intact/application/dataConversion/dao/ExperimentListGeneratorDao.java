/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.dao;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projection;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
@SuppressWarnings("unchecked")
public class ExperimentListGeneratorDao
{
 
    /**
     * Query to get at the Experiment ACs containing negative interaction annotations
     */
    public static List<Experiment> getExpWithInteractionsContainingAnnotation(String cvshortLabel, String labelLike)
    {

        return getSession().createCriteria(Experiment.class)
                .add(Restrictions.like("shortLabel", labelLike))
                .createCriteria("interactions")
                .createCriteria("annotations")
                .createCriteria("cvTopic")
                .add(Restrictions.eq("shortLabel", cvshortLabel)).list();
    }

    /**
     * Query to obtain annotated objects by searching for an Annotation
     * with the cvTopic label provided
     */
    public static <T extends AnnotatedObjectImpl> List<T> getContainingAnnotation(Class<T> annObject, String cvshortLabel, String labelLike)
    {
         return getSession().createCriteria(annObject.getClass())
                .add(Restrictions.like("shortLabel", labelLike))
                .createCriteria("annotations")
                .createCriteria("cvTopic")
                .add(Restrictions.eq("shortLabel", cvshortLabel)).list();
    }

    public static Map<String,String> getExperimentAcAndLabelWithoutPubmedId(String labelLike)
    {
        List<Object[]> allExps = getSession().createCriteria(Experiment.class)
                .add(Restrictions.like("shortLabel", labelLike))
                .setProjection(Projections.projectionList()
                        .add(Projections.distinct(Projections.property("ac")))
                        .add(Projections.property("shortLabel"))).list();

        Map<String,String> filteredExpsMap = new HashMap<String,String>();

        for (Object[] exp : allExps)
        {
            filteredExpsMap.put((String)exp[0], (String)exp[1]);
        }

        Map<String,String> expsAndPmid = getExperimentAcAndPmid(labelLike);
        
        for (String expWithPmid : expsAndPmid.keySet())
        {
            filteredExpsMap.remove(expWithPmid);
        }

        return filteredExpsMap;
    }

    public static Map<String,String> getExperimentAcAndPmid(String shortLabelLike)
    {
         List<Object[]> expsAndPmidResults = getSession().createCriteria(Experiment.class)
                 .add(Restrictions.like("shortLabel", shortLabelLike))
                 .createAlias("xrefs", "xref")
                 .createAlias("xref.cvDatabase", "cvDb")
                .createAlias("xref.cvXrefQualifier", "cvXrefQual")
                .add(Restrictions.eq("cvDb.shortLabel", CvDatabase.PUBMED))
                .add(Restrictions.eq("cvXrefQual.shortLabel", CvXrefQualifier.PRIMARY_REFERENCE))
                 .setProjection(Projections.projectionList()
                         .add(Projections.distinct(Projections.property("ac")))
                         .add(Projections.property("xref.primaryId"))).list();

        Map<String,String> expAndPmids = new HashMap<String,String>();

        for (Object[] expAndPmid : expsAndPmidResults)
        {
            String pmid = (String)expAndPmid[1];

            if (pmid != null)
            {
                expAndPmids.put((String)expAndPmid[0], pmid);
            }
        }

        return expAndPmids;

    }

    public static Map<String,List<String>> getExperimentAcAndTaxids(String shortLabelLike)
    {
         List<Object[]> expsAndTaxidResults = getSession().createCriteria(Experiment.class)
                 .add(Restrictions.like("shortLabel", shortLabelLike))
                 .createAlias("xrefs", "xref")
                .createAlias("xref.cvXrefQualifier", "cvXrefQual")
                .add(Restrictions.eq("cvXrefQual.shortLabel", CvXrefQualifier.TARGET_SPECIES))
                 .setProjection(Projections.projectionList()
                         .add(Projections.distinct(Projections.property("ac")))
                         .add(Projections.property("xref.primaryId"))).list();

        Map<String,List<String>> expAndTaxid = new HashMap<String,List<String>>();

        for (Object[] expAndTaxidResult : expsAndTaxidResults)
        {
            String expAc = (String)expAndTaxidResult[0];
            String taxId = (String)expAndTaxidResult[1];

            if (expAndTaxid.containsKey(expAc))
            {
                expAndTaxid.get(expAc).add(taxId);
            }
            else
            {
                List<String> taxIds = new ArrayList<String>();
                taxIds.add(taxId);
                expAndTaxid.put(expAc, taxIds);
            }
        }

        return expAndTaxid;
    }

    public static Map<String,Integer> countInteractionCountsForExperiments(String shortLabelLike)
    {
         List<Object[]> expWithInteractionsCount = getSession().createCriteria(Experiment.class)
                 .add(Restrictions.like("shortLabel", shortLabelLike))
                 .createAlias("interactions", "int")
                 .setProjection(Projections.projectionList()
                         .add(Projections.distinct(Projections.property("ac")))
                         .add(Projections.count("int.ac"))
                         .add(Projections.groupProperty("ac"))).list();

       Map<String,Integer> interactionCountByExpAc = new HashMap<String,Integer>();

       for (Object[] expAndIntCount : expWithInteractionsCount)
        {
            interactionCountByExpAc.put((String)expAndIntCount[0], (Integer)expAndIntCount[1]);
        }

        return interactionCountByExpAc;

    }

    public static List<Interaction> getInteractionByExperimentShortLabel(String[] experimentLabels, Integer firstResult, Integer maxResults)
    {
        Criteria criteria = getSession().createCriteria(Interaction.class)
                 .createCriteria("experiments")
                 .add(Restrictions.in("shortLabel", experimentLabels));

        if (firstResult != null && firstResult >=0)
        {
            criteria.setFirstResult(firstResult);
        }

        if (maxResults != null && maxResults > 0)
        {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }

    private static Session getSession()
    {
        StandardCoreDataConfig dataConfig = (StandardCoreDataConfig) IntactContext.getCurrentInstance().getConfig().getDataConfig(StandardCoreDataConfig.NAME);
        return dataConfig.initialize().getCurrentSession(); 
    }

}
