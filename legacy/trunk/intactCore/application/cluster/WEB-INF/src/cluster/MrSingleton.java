/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package cluster;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.io.Serializable;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Mar-2006</pre>
 */
public class MrSingleton implements Serializable
{
    private static final String SINGLETON_ATT = MrSingleton.class.getName();

    private Date creationDate;

    private MrSingleton()
    {
       creationDate = new Date();
    }

    public static MrSingleton getInstance(HttpServletRequest request)
    {
        if (request.getSession().getAttribute(SINGLETON_ATT) == null)
        {
            request.getSession().setAttribute(SINGLETON_ATT, new MrSingleton());
        }

        return (MrSingleton) request.getSession().getAttribute(SINGLETON_ATT);
    }

    public Date getCreationDate()
    {
        return creationDate;
    }


}
