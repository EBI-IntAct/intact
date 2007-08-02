package uk.ac.ebi.intact.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import psidev.psi.mi.tab.expansion.SpokeWithoutBaitExpansion;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.psimitab.ConvertXml2Tab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Goal which converts a list of PSI XML 2.5 files into a single PSIMITAB file.
 *
 * @goal xml2tab
 * @phase process-sources
 */
public class ConvertXmlToTabMojo extends IntactAbstractMojo {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(ConvertXmlToTabMojo.class);

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Location of the output PSIMITAB file.
     *
     * @parameter
     * @required
     */
    private String tabFile;

    /**
     * A list of file/directories to convert into a single PSIMITAB file.
     *
     * @parameter
     * @required
     */
    private List files;

    public void execute() throws MojoExecutionException {

        enableLogging();

        ConvertXml2Tab converter = new ConvertXml2Tab();

        // config
        converter.setOverwriteOutputFile( true );
        converter.setExpansionStrategy(new SpokeWithoutBaitExpansion() );
        converter.setInteractorPairCluctering(true);

        Collection<File> inputFiles = new ArrayList<File>();
        if (files != null) {
            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                String filepath = (String) iterator.next();
                inputFiles.add(new File(filepath));
            }
        }
        converter.setXmlFilesToConvert(inputFiles);
        converter.setOutputFile( new File(tabFile) );

        try {
            converter.setLogWriter(getOutputWriter());
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException("Error getting outputWriter", e);
        }

        // run it
        try {
            converter.convert();
        } catch (Exception e) {
            throw new MojoExecutionException("Error while converting files to PSIMITAB... see nested exception.", e);
        }
    }

    public MavenProject getProject() {
        return project;
    }
}
