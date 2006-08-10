/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

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
    private Integer largeScaleChunkSize;


    public ExperimentListItem(Collection<String> experimentLabels, String name, boolean negative, Integer chunkNumber, Integer largeScaleSize)
    {
        this.experimentLabels = experimentLabels;
        this.name = name;
        this.negative = negative;
        this.chunkNumber = chunkNumber;
        this.largeScaleChunkSize = largeScaleSize;
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
            fileNumber = "-"+twoDigitNumber(chunkNumber);
        }

        String strLargeScale = "";

        if (largeScaleChunkSize != null)
        {
            if (experimentLabels.size() > 1)
            {
                throw new RuntimeException("On large scale items, only one experiment label is allowed");
            }

            strLargeScale = "_"+experimentLabels.iterator().next()+"_"+twoDigitNumber(chunkNumber);
            fileNumber = "";
        }


        return name + strLargeScale +fileNumber + strNegative + FileHelper.XML_FILE_EXTENSION;
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

    public String getInteractionRange()
    {
        if (largeScaleChunkSize == null)
        {
            return "";
        }

        int first = ((chunkNumber-1)*largeScaleChunkSize)+1;
        int last = chunkNumber*largeScaleChunkSize;

        return " ["+first+","+last+"]";
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


    public Integer getLargeScaleChunkSize()
    {
        return largeScaleChunkSize;
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
        return getFilename()+" "+getPattern()+getInteractionRange();
    }

    @Override
    public int hashCode()
    {
        return 47*getFilename().hashCode()*getPattern().hashCode();
    }

    private static String twoDigitNumber(Integer number)
    {
        String strNum = "";

        if (number < 10)
        {
            strNum = "0";
        }
        strNum = strNum + number;

        return strNum;
    }
}
