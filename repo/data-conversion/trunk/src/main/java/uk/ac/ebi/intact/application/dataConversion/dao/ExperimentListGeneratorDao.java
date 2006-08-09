/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.AnnotatedObjectImpl;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<Experiment> getExpWithInteractionsContainingAnnotation(String cvShortLabel)
    {

        return getSession().createCriteria(Experiment.class)
                .createCriteria("interactions")
                .createCriteria("annotations")
                .createCriteria("cvTopic")
                .add(Restrictions.eq("shortLabel", cvShortLabel)).list();
    }

    /**
     * Query to obtain annotated objects by searching for an Annotation
     * with the cvTopic label provided
     */
    public static <T extends AnnotatedObjectImpl> List<T> getContainingAnnotation(Class<T> annObject, String cvShortLabel)
    {
         return getSession().createCriteria(annObject.getClass())
                .createCriteria("annotations")
                .createCriteria("cvTopic")
                .add(Restrictions.eq("shortLabel", cvShortLabel)).list();
    }

    public static Map<String,String> getExperimentAcAndLabelWithoutPubmedId()
    {
        List<Object[]> allExps = getSession().createCriteria(Experiment.class)
                 .setProjection(Projections.projectionList()
                        .add(Projections.distinct(Projections.property("ac")))
                        .add(Projections.property("shortLabel"))).list();

        Map<String,String> filteredExpsMap = new HashMap<String,String>();

        for (Object[] exp : allExps)
        {
            filteredExpsMap.put((String)exp[0], (String)exp[1]);
        }

        List<String> expsWithPmid = getSession().createCriteria(Experiment.class, "exp")
                .createCriteria("xrefs")
                .createAlias("cvDatabase", "cvDb")
                .createAlias("cvXrefQualifier", "cvXrefQual")
                .add(Restrictions.eq("cvDb.shortLabel", CvDatabase.PUBMED))
                .add(Restrictions.eq("cvXrefQual.shortLabel", CvXrefQualifier.PRIMARY_REFERENCE))
                .setProjection(Projections.distinct(Projections.property("exp.ac")))
                .list();
        
        for (String expWithPmid : expsWithPmid)
        {
            filteredExpsMap.remove(expWithPmid);
        }

        return filteredExpsMap;
    }

    private static Session getSession()
    {
        StandardCoreDataConfig dataConfig = (StandardCoreDataConfig) IntactContext.getCurrentInstance().getConfig().getDataConfig(StandardCoreDataConfig.NAME);
        return dataConfig.initialize().getCurrentSession(); 
    }

}
