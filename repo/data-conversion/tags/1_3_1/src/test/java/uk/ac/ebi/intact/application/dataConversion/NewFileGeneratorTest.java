/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class NewFileGeneratorTest extends TestCase
{

    private static final Log log = LogFactory.getLog(NewFileGeneratorTest.class);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }
    
    public void testGenerateListGavin() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);


        ExperimentListGenerator gen = new ExperimentListGenerator("mahajan-2000-1");
        //gen.setLargeScaleChunkSize(150);

        List<ExperimentListItem> eliSpecies = gen.generateClassificationBySpecies();

        ExperimentListItem eli = eliSpecies.get(0);
        log.info("Experiment List Item: "+eli);

        Document doc = NewFileGenerator.generatePsiData(eliSpecies.get(0), PsiVersion.getVersion1(), mapping);

        Writer writer = new StringWriter();
        DisplayXML.write(doc, writer, "   ");

        String xmlDoc = writer.toString();
        assertEquals(25939, xmlDoc.length());

        // 2.5
        doc = NewFileGenerator.generatePsiData(eliSpecies.get(0), PsiVersion.getVersion25(), mapping);

        writer = new StringWriter();
        DisplayXML.write(doc, writer, "   ");

        xmlDoc = writer.toString();
        assertEquals(128792, xmlDoc.length());

    }

    public void testGenerateXmlFiles_Psi25() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);

        ExperimentListGenerator gen = new ExperimentListGenerator("ab%");

        List<ExperimentListItem> allItems = gen.generateAllClassifications();

        for (ExperimentListItem item : allItems)
        {
            NewFileGenerator.writePsiData(item, PsiVersion.VERSION_25, mapping, new File("target/psi25"), true);
        }

        // check if the files exist and are not empty
        for (ExperimentListItem item : allItems)
        {
            File xmlFile = new File("target/psi25", item.getFilename());

            assertTrue(xmlFile.exists());
            assertTrue(xmlFile.length() > 0);
        }
    }

    public void testGenerateXmlFiles_Psi1() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);

        ExperimentListGenerator gen = new ExperimentListGenerator("ab%");

        List<ExperimentListItem> allItems = gen.generateAllClassifications();

        for (ExperimentListItem item : allItems)
        {
            NewFileGenerator.writePsiData(item, PsiVersion.VERSION_1, mapping, new File("target/psi1"), false);
        }

        // check if the files exist and are not empty
        for (ExperimentListItem item : allItems)
        {
            File xmlFile = new File("target/psi1", item.getFilename());

            assertTrue(xmlFile.exists());
            assertTrue(xmlFile.length() > 0);
        }
    }

    public void testGenerateXmlFilesWithSmallMolecule_Psi1() throws Exception
    {
        File reverseMappingFile = new File(NewFileGeneratorTest.class.getResource("/reverseMapping.txt").getFile());

        CvMapping mapping = new CvMapping();
        mapping.loadFile(reverseMappingFile);

        ExperimentListGenerator gen = new ExperimentListGenerator("gonzalez-2003-1");

        List<ExperimentListItem> allItems = gen.generateAllClassifications();

        for (ExperimentListItem item : allItems)
        {
            NewFileGenerator.writePsiData(item, PsiVersion.VERSION_1, mapping, new File("target/psi1"), false);
        }

        // check if the files exist and are not empty
        for (ExperimentListItem item : allItems)
        {
            File xmlFile = new File("target/psi1", item.getFilename());

            assertTrue(xmlFile.exists());
            assertTrue(xmlFile.length() > 0);
        }
    }
     
}
