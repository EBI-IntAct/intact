package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.SearchItem;
import uk.ac.ebi.intact.persistence.dao.ComponentDao;

import java.util.List;

/**
 * DAO for components
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class ComponentDaoImpl extends IntactObjectDaoImpl<Component> implements ComponentDao
{

    public ComponentDaoImpl(Session session)
    {
        super(Component.class, session);
    }


    public List<Component> getByInteractorAc(String interactorAc)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("interactor")
                .add(Restrictions.idEq(interactorAc)).list();
    }
}
