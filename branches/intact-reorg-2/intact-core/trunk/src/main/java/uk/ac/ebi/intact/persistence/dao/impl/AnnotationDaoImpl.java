package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.persistence.dao.AnnotationDao;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO for annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class AnnotationDaoImpl extends IntactObjectDaoImpl<Annotation> implements AnnotationDao
{

    public AnnotationDaoImpl(Session session)
    {
        super(Annotation.class, session);
    }


    public List<Annotation> getByTextLike(String text)
    {
        return getSession().createCriteria(getEntityClass())
                .add(Restrictions.like("annotationText", text)).list();
    }

    public List<Annotation> getByDescriptionLike(String description)
    {
        return getSession().createCriteria(getEntityClass())
                .add(Restrictions.like("description", description)).list();
    }
}
