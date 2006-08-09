/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class ExperimentListItem
{

    private static final Log log = LogFactory.getLog(ExperimentListItem.class);

    private String filename;
    private String pattern;


    public ExperimentListItem(String filename, String pattern)
    {
        this.filename = filename;
        this.pattern = pattern;
    }


    public String getFilename()
    {
        return filename;
    }

    public String getPattern()
    {
        return pattern;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        ExperimentListItem eli = (ExperimentListItem) obj;

        return filename.equals(eli.getFilename()) && pattern.equals(eli.getPattern());
    }

    @Override
    public String toString()
    {
        return filename+" "+pattern;
    }

    @Override
    public int hashCode()
    {
        return 47*filename.hashCode()*pattern.hashCode();
    }
}
