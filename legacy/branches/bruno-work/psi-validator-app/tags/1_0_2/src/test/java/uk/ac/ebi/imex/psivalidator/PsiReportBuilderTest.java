/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator;

import junit.framework.TestCase;

import java.io.*;
import java.net.URL;
import java.net.URISyntaxException;


/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jun-2006</pre>
 */
public class PsiReportBuilderTest extends TestCase
{

    private PsiReportBuilder psiReportBuilder;


    protected void setUp() throws Exception
    {


    }

    protected void tearDown() throws Exception
    {
        psiReportBuilder = null;
    }

    public void testPsiFileWithoutErrors() throws IOException, URISyntaxException
    {
        URL url = PsiReportBuilderTest.class.getResource("resource/psiFileOk.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder1", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        assertEquals(0, report.getValidatorMessages().size());

        assertEquals("builder1", report.getName());
        assertEquals("valid", report.getXmlSyntaxStatus());
        assertEquals("valid", report.getSemanticsStatus());
        assertNotNull(report.getHtmlView());

    }

    public void testPsiFileWithErrors() throws IOException, URISyntaxException
    {
        URL url = PsiReportBuilderTest.class.getResource("resource/psiFileWithErrors.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder2", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        assertEquals(5, report.getValidatorMessages().size());

        assertEquals("builder2", report.getName());
        assertEquals("valid", report.getXmlSyntaxStatus());
        assertEquals("invalid", report.getSemanticsStatus());
        assertNotNull(report.getHtmlView());

        //not checked
    }

    public void testWrongTypeFile() throws IOException, URISyntaxException
    {
         URL url = PsiReportBuilderTest.class.getResource("resource/wrong.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder3", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        assertNull(report.getValidatorMessages());

        assertEquals("builder3", report.getName());
        assertEquals("invalid", report.getXmlSyntaxStatus());
        assertTrue(report.getSemanticsStatus().startsWith("not checked"));
        assertNull(report.getHtmlView());
    }
}
