/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.statisticView.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.io.FilenameFilter;
import java.io.File;

/**
 * Filter to select the files generated in the temp folder that start with the provided sessionID.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Aug-2006</pre>
 */
public class SessionFilenameFilter implements FilenameFilter
{
    private static final Log log = LogFactory.getLog(SessionFilenameFilter.class);

    private HttpSession session;

    public SessionFilenameFilter(HttpSession session)
    {
        this.session = session;
    }

    public boolean accept(File dir, String name)
    {
        String sessionId = session.getId();

        return name.startsWith(sessionId);
    }
}
