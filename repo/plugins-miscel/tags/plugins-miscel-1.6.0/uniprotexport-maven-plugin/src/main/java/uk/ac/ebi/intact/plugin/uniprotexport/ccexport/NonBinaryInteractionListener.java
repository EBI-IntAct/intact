/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport.ccexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;
import java.io.IOException;
import java.util.Date;

import uk.ac.ebi.intact.util.uniprotExport.event.CcLineEventListener;
import uk.ac.ebi.intact.util.uniprotExport.event.NonBinaryInteractionFoundEvent;
import uk.ac.ebi.intact.util.uniprotExport.event.DrLineProcessedEvent;
import uk.ac.ebi.intact.util.uniprotExport.event.CcLineCreatedEvent;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Sep-2006</pre>
 */
public class NonBinaryInteractionListener implements CcLineEventListener
{

    private static final Log log = LogFactory.getLog(NonBinaryInteractionListener.class);

    private static final String NEW_LINE = System.getProperty("line.separator");

    private Writer writer;

    public NonBinaryInteractionListener(Writer writer)
    {
        this.writer = writer;

        try
        {
            writer.write(header());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void processNonBinaryInteraction(NonBinaryInteractionFoundEvent evt)
    {
        try
        {
            writer.write(evt.getInteraction().getAc()+" - "+evt.getInteraction().getShortLabel()+NEW_LINE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void drLineProcessed(DrLineProcessedEvent evt)
    {
        // nothing
    }

    public void ccLineCreated(CcLineCreatedEvent evt)
    {
        // nothing
    }

    private String header()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("# Non-binary interactions - "+new Date()+NEW_LINE);
        sb.append("#######################################################"+NEW_LINE);
        sb.append(NEW_LINE);

        return sb.toString();
    }
}
