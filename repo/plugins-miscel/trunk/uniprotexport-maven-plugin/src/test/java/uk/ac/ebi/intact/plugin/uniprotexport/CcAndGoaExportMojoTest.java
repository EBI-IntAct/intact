/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;

import java.io.File;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.dataexchange.cvutils.OboUtils;
import uk.ac.ebi.intact.dataexchange.cvutils.CvUpdater;
import uk.ac.ebi.intact.dataexchange.cvutils.CvUtils;
import uk.ac.ebi.intact.dataexchange.cvutils.model.IntactOntology;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class CcAndGoaExportMojoTest extends AbstractMojoTestCase
{

    private static final Log log = LogFactory.getLog(CcAndGoaExportMojoTest.class);

    public void testCCAndGoaMojoExport() throws Exception
    {


        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/cc-goa-config.xml" );
        File hibernateConfig = new File (CcAndGoaExportMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        IntactContext.initStandaloneContext(hibernateConfig);

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // CVs
        IntactOntology ontology = OboUtils.createOntologyFromOboDefault(11332);
        CvUpdater cvUpdater = new CvUpdater();
        cvUpdater.createOrUpdateCVs(ontology);

        CvTopic noUniprotUpdate = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(),
                                                               CvTopic.class, null, CvTopic.NON_UNIPROT);
        CvTopic negative = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(),
                                                               CvTopic.class, null, CvTopic.NEGATIVE);
        CvTopic ccNote = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(),
                                                               CvTopic.class, null, CvTopic.CC_NOTE);
        CvTopic uniprotDrExport = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(),
                                                               CvTopic.class, null, CvTopic.UNIPROT_DR_EXPORT);
        PersisterHelper.saveOrUpdate(noUniprotUpdate, negative, ccNote, uniprotDrExport);

        // some data
        PsiExchange.importIntoIntact(CcAndGoaExportMojoTest.class.getResourceAsStream("/10348744.xml"));

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().close();

        CcAndGoaExportMojo mojo = (CcAndGoaExportMojo) lookupMojo( "cc-goa", pluginXmlFile );
        mojo.hibernateConfig = hibernateConfig;
        mojo.setUniprotLinksFile(new File(CcAndGoaExportMojoTest.class.getResource("/uniprotlinks.dat").getFile()));

        mojo.execute();
    }

}
