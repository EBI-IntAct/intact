package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.persistence.dao.AnnotationDao;
import uk.ac.ebi.intact.persistence.dao.FeatureDao;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO for features
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-jul-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class FeatureDaoImpl extends AnnotatedObjectDaoImpl<Feature> implements FeatureDao
{

    public FeatureDaoImpl(Session session)
    {
        super(Feature.class, session);
    }
}
