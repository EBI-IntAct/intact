/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context.impl;

import uk.ac.ebi.intact.context.UserContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class IntactContextWrapper extends IntactContext
{
        public IntactContextWrapper(UserContext userContext, IntactSession session)
        {
            super(userContext, session);
            setCurrentInstance(this);
        }

}
