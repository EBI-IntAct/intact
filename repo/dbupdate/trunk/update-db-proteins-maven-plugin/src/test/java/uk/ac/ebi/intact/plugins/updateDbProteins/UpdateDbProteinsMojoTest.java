package uk.ac.ebi.intact.plugins.updateDbProteins;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

import java.io.File;

/**
 * UpdateDbProteinsMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.1
 */
public class UpdateDbProteinsMojoTest  {         // extends IntactBasicTestCase

    // Trick to work around multiple inheritance
    private class MojoHelper extends AbstractMojoTestCase {

        private MojoHelper() throws Exception {
            super.setUp();
        }

        @Override
        public Mojo lookupMojo( String goal, File pluginPom ) throws Exception {
            return super.lookupMojo( goal, pluginPom );
        }
    }

    @Test
    public void executeIntactMojo() throws Exception {

        File mojoConfig = new File( UpdateDbProteinsMojoTest.class.getResource( "/plugin-configs/updateDbProteins-config.xml" ).getFile() );
        UpdateDbProteinsMojo mojo = (UpdateDbProteinsMojo) new MojoHelper().lookupMojo( "update-proteins", mojoConfig );

        mojo.setSpringConfig( "/META-INF/dev.jpa.spring.xml" );
        mojo.setReportsDir( new File("target/protein-update") );
        mojo.setFixDuplicates( true );
        mojo.setDeleteSpliceVarsWithoutInteractions( false );
        mojo.setBatchSize( 100 );

        mojo.executeIntactMojo();
    }

    @Test
    @Ignore
    public void executeIntactMojo_zdev() throws Exception {

        File mojoConfig = new File( UpdateDbProteinsMojoTest.class.getResource( "/plugin-configs/updateDbProteins-config.xml" ).getFile() );
        UpdateDbProteinsMojo mojo = (UpdateDbProteinsMojo) new MojoHelper().lookupMojo( "update-proteins", mojoConfig );

        mojo.setSpringConfig( "/META-INF/zdev.jpa.spring.xml" );
        mojo.setReportsDir( new File("target/protein-update") );
        mojo.setFixDuplicates( true );
        mojo.setDeleteSpliceVarsWithoutInteractions( false );
        mojo.setBatchSize( 100 );

        mojo.execute();
    }
}
