/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.id.SequenceGenerator;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jul-2006</pre>
 */
public class IntactIdGenerator extends SequenceGenerator
{

    private static final Log log = LogFactory.getLog(IntactIdGenerator.class);


    public String getSequenceName()
    {

        Institution institution = IntactContext.getCurrentInstance().getInstitution();
        String prefix = institution.getShortLabel();

        String id = prefix+"-"+super.getSequenceName();

        log.trace("Assigning Id: "+id);

        return id;
    }
}
