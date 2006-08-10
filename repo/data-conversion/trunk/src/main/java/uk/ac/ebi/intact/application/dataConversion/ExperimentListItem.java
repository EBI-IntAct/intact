/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Date;

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

    private Collection<String> experimentLabels;
    private String name;
    private boolean negative;
    private Integer chunkNumber;


    public ExperimentListItem(Collection<String> experimentLabels, String name, boolean negative, Integer chunkNumber)
    {
        this.experimentLabels = experimentLabels;
        this.name = name;
        this.negative = negative;
        this.chunkNumber = chunkNumber;
    }

    public String getFilename()
    {
        String strNegative = "";
        if (negative)
        {
            strNegative = "_negative";
        }

        String fileNumber = "";
        if (chunkNumber != null)
        {
            String indexPrefix = "-";
            if (chunkNumber < 10)
            {
                indexPrefix = "-0";
            }
            fileNumber = indexPrefix+chunkNumber;
        }

        return name + fileNumber + strNegative + FileHelper.XML_FILE_EXTENSION;
    }

    public String getPattern()
    {
        StringBuffer sb = new StringBuffer();

        int i=0;
        for (String experimentLabel : experimentLabels)
        {
            if (i>0)
            {
               sb.append(",");
            }

            sb.append(experimentLabel);

            i++;
        }

        return sb.toString();
    }

    public Integer getChunkNumber()
    {
        return chunkNumber;
    }

    public boolean isNegative()
    {
        return negative;
    }

    public String getName()
    {
        return name;
    }

    public Collection<String> getExperimentLabels()
    {
        return experimentLabels;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        ExperimentListItem eli = (ExperimentListItem) obj;

        return getFilename().equals(eli.getFilename()) && getPattern().equals(eli.getPattern());
    }

    @Override
    public String toString()
    {
        return getFilename()+" "+getPattern();
    }

    @Override
    public int hashCode()
    {
        return 47*getFilename().hashCode()*getPattern().hashCode();
    }
}
