/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.Date;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
public class IntactObjectEventListener implements PreInsertEventListener, PreUpdateEventListener
{

    private static final Log log = LogFactory.getLog(IntactObjectEventListener.class);

    public boolean onPreInsert(PreInsertEvent preInsertEvent)
    {
        log.debug("Inserting audit info for: "+preInsertEvent.getId());

        IntactObject intactObject = (IntactObject) preInsertEvent.getEntity();
        intactObject.setCreated(new Date());
        intactObject.setUpdated(new Date());

        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();
        intactObject.setCreator(currentUser);
        intactObject.setUpdator(currentUser);

        return false;
    }

    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent)
    {
        log.debug("Updating audit info for: "+preUpdateEvent.getId());

        IntactObject intactObject = (IntactObject) preUpdateEvent.getEntity();
        intactObject.setUpdated(new Date());

        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();
        intactObject.setCreator(currentUser);
        intactObject.setUpdator(currentUser);

        return false;
    }
}
