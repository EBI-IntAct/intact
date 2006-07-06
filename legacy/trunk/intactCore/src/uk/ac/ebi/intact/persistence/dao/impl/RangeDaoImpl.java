package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.persistence.dao.RangeDao;
import uk.ac.ebi.intact.model.Range;
import org.hibernate.Session;

/**
 * DAO for ranges
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
public class RangeDaoImpl extends IntactObjectDaoImpl<Range> implements RangeDao
{
    public RangeDaoImpl(Session session)
    {
        super(Range.class, session);
    }
}
