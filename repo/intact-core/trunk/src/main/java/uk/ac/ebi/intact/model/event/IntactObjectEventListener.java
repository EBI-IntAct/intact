/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Auditable;
import uk.ac.ebi.intact.model.IntactObject;

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
        if (!(preInsertEvent.getEntity() instanceof Auditable))
        {
            log.debug("No auditable object: "+preInsertEvent.getId());
            return false;
        }

        log.debug("Inserting audit info for: "+preInsertEvent.getId());

        Date now = new Date();

        Auditable auditable = (Auditable) preInsertEvent.getEntity();
        auditable.setCreated(now);
        auditable.setUpdated(now);

        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId().toUpperCase();
        auditable.setCreator(currentUser);
        auditable.setUpdator(currentUser);

        String[] names = preInsertEvent.getPersister().getPropertyNames();
        Object[] values = preInsertEvent.getState();
        for (int i = 0; i < names.length; i++)
        {
            if (names[i].equals("created") || names[i].equals("updated"))
            {
                values[i] = now;
            }

            if (names[i].equals("creator") || names[i].equals("updator"))
            {
                log.debug("Current user " + currentUser);
                values[i] = currentUser;
            }
        }

        return false;
    }

    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent)
    {
        log.debug("Updating audit info for: "+preUpdateEvent.getId());

        Date now = new Date();

        IntactObject intactObject = (IntactObject) preUpdateEvent.getEntity();
        intactObject.setUpdated(now);

        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId().toUpperCase();
        intactObject.setUpdator(currentUser);

        String[] names = preUpdateEvent.getPersister().getPropertyNames();
        Object[] values = preUpdateEvent.getState();
        for (int i = 0; i < names.length; i++)
        {
            if (names[i].equals("updated"))
            {
                values[i] = now;
            }
            if (names[i].equals("updator"))
            {
                log.debug("Current user is " + currentUser);
                values[i] = currentUser;
            }
            if (names[i].equals("creator"))
            {
                if(values[i] == null){
                    values[i] = intactObject.getCreator();
                }
            }
             if (names[i].equals("created"))
            {
                if(values[i] == null){
                    values[i] = intactObject.getCreated();
                }
            }
        }

        return false;
    }
}
