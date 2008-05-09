/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport.drcomparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Aug-2006</pre>
 */
public class DrComparatorReport
{

    private static final Log log = LogFactory.getLog(DrComparatorReport.class);

    private Collection<String> added;
    private Collection<String> removed;

    public DrComparatorReport()
    {
    }

    public DrComparatorReport(Collection<String> added, Collection<String> removed)
    {
        this.added = added;
        this.removed = removed;
    }

    public void addAdded(String str)
    {
        if (added == null)
        {
            this.added = new ArrayList<String>();
        }

        added.add(str);
    }

    public void addRemoved(String str)
    {
        if (removed == null)
        {
           this.removed = new ArrayList<String>();
        }

        removed.add(str);
    }

    public Collection<String> getRemoved()
    {
        return removed;
    }

    public Collection<String> getAdded()
    {
        return added;
    }
}
