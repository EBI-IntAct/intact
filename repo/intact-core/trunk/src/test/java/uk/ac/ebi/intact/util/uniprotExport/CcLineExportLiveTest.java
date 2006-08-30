/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Aug-2006</pre>
 */
public class CcLineExportLiveTest extends TestCase
{

    private static final Log log = LogFactory.getLog(CcLineExportLiveTest.class);


    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }
      
    public void testGenerateCCLines() throws Exception
    {
        Collection<String> uniprotIds =
                CCLineExport.getEligibleProteinsFromFile(CcLineExportLiveTest.class.getResource("uniprotlinks.dat").getFile());

        Writer ccWriter = new StringWriter();
        Writer goaWriter = new StringWriter();

        CCLineExport ccLineExport = new CCLineExport(ccWriter, goaWriter);

        new CcLineExportProgressThread(ccLineExport, uniprotIds.size()).start();

        ccLineExport.generateCCLines(uniprotIds);

        //assertEquals(3, ccLineExport.getCcLineCount());
        //assertEquals(4, ccLineExport.getGoaLineCount());

        System.out.println(ccWriter.toString());
    }

}
