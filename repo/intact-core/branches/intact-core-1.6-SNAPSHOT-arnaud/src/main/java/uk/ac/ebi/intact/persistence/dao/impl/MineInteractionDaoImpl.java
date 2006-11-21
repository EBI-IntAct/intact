/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.MineInteraction;
import uk.ac.ebi.intact.persistence.dao.MineInteractionDao;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class MineInteractionDaoImpl extends HibernateBaseDaoImpl<MineInteraction>
        implements MineInteractionDao
{

    public MineInteractionDaoImpl(Session session, IntactSession intactSession)
    {
        super(MineInteraction.class, session, intactSession);
    }

    public void persist(MineInteraction mineInteraction)
    {
        getSession().persist(mineInteraction);
    }

    public int deleteAll()
    {
        return getSession().createQuery("DELETE MineInteraction").executeUpdate();
    }

    public MineInteraction get(String proteinAc1, String proteinAc2)
    {
        return (MineInteraction) getSession().createCriteria(getEntityClass())
                .add(Restrictions.or(
                        Restrictions.and(
                                Restrictions.eq("protein1Ac", proteinAc1),
                                Restrictions.eq("protein2Ac", proteinAc2)
                        ),
                        Restrictions.and(
                                Restrictions.eq("protein2Ac", proteinAc1),
                                Restrictions.eq("protein1Ac", proteinAc2)
                        )
                )).uniqueResult();
    }
}
