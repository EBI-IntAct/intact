/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport.drcomparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import uk.ac.ebi.intact.util.uniprotExport.CCLineExport;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Aug-2006</pre>
 */
public class DrComparator
{

    private static final Log log = LogFactory.getLog(DrComparator.class);

    private DrComparator()
    {
    }

    public static DrComparatorReport compareFiles(File drFile1, File drFile2) throws IOException
    {
        Collection<String> dr1 = CCLineExport.getEligibleProteinsFromFile(drFile1.toString());
        Collection<String> dr2 = CCLineExport.getEligibleProteinsFromFile(drFile2.toString());

        List<String> added = (List<String>) CollectionUtils.subtract(dr1,dr2);
        List<String> removed = (List<String>) CollectionUtils.subtract(dr2,dr1);
        
        Collections.sort(added);
        Collections.sort(removed);

        return new DrComparatorReport(added, removed);
    }

}
