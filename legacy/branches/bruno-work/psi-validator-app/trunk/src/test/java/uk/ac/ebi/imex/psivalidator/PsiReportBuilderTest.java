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

import org.junit.Test;
import org.junit.Assert;


/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jun-2006</pre>
 */
public class PsiReportBuilderTest
{

    private PsiReportBuilder psiReportBuilder;

    @Test
    public void testPsiFileWithoutErrors() throws IOException, URISyntaxException
    {
        URL url = PsiReportBuilderTest.class.getResource("resource/psiFileOk.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder1", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        Assert.assertEquals(0, report.getValidatorMessages().size());

        Assert.assertEquals("builder1", report.getName());
        Assert.assertEquals("valid", report.getXmlSyntaxStatus());
        Assert.assertEquals("valid", report.getSemanticsStatus());
        Assert.assertNotNull(report.getHtmlView());

    }

    @Test
    public void testPsiFileWithErrors() throws IOException, URISyntaxException
    {
        URL url = PsiReportBuilderTest.class.getResource("resource/psiFileWithErrors.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder2", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        Assert.assertEquals(5, report.getValidatorMessages().size());

        Assert.assertEquals("builder2", report.getName());
        Assert.assertEquals("valid", report.getXmlSyntaxStatus());
        Assert.assertEquals("invalid", report.getSemanticsStatus());
        Assert.assertNotNull(report.getHtmlView());

        //not checked
    }

    @Test
    public void testWrongTypeFile() throws IOException, URISyntaxException
    {
         URL url = PsiReportBuilderTest.class.getResource("resource/wrong.xml").toURI().toURL();

        psiReportBuilder = new PsiReportBuilder("builder3", url);

        PsiReport report = psiReportBuilder.createPsiReport();

        Assert.assertNull(report.getValidatorMessages());

        Assert.assertEquals("builder3", report.getName());
        Assert.assertEquals("invalid", report.getXmlSyntaxStatus());
        Assert.assertTrue(report.getSemanticsStatus().startsWith("not checked"));
        Assert.assertNull(report.getHtmlView());
    }
}
