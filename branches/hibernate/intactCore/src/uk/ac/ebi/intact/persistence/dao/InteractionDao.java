/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.model.InteractionImpl;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
public class InteractionDao extends InteractorDao<InteractionImpl>
{
    public InteractionDao(Session session)
    {
        super(InteractionImpl.class, session);
    }
}
