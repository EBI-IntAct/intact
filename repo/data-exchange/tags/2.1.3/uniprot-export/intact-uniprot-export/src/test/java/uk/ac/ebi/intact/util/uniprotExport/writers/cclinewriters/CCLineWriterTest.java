package uk.ac.ebi.intact.util.uniprotExport.writers.cclinewriters;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.util.uniprotExport.UniprotExportBase;
import uk.ac.ebi.intact.util.uniprotExport.parameters.cclineparameters.CCParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Tester of the CCLine writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/02/11</pre>
 */

public class CCLineWriterTest extends UniprotExportBase{

    @Test
    public void test_cc_export(){
        List<CCParameters> parameters = createCCParameters();

        try {
            File testFile = new File("CcTest.txt");
            FileWriter test = new FileWriter(testFile);
            CCLineWriter writer = new DefaultCCLineWriter2(test);

            writer.writeCCLines(parameters);

            writer.close();

            File template = new File(CCLineWriterTest.class.getResource("/cc_file_test.txt").getFile());

            Assert.assertTrue(areFilesEqual(testFile, template));

            testFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
