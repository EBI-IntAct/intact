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
import org.apache.maven.plugin.MojoExecutionException;
import psidev.psi.mi.tab.converter.xml2tab.ColumnHandler;
import psidev.psi.mi.tab.expansion.SpokeWithoutBaitExpansion;
import uk.ac.ebi.intact.psimitab.ConvertXml2Tab;
import uk.ac.ebi.intact.psimitab.IntActColumnHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
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
public class ConvertXmlToTabMojo extends AbstractPsimitabConverterMojo {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ConvertXmlToTabMojo.class );

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

    /**
     * Whether to complete the GO terms
     *
     * @parameter
     */
    private boolean goTermAutocompletion = false;

    /**
     * Whether to complete the interpro terms
     *
     * @parameter
     */
    private boolean interproTermAutocompletion = false;

    ///////////////////////////////
    // Implements abstract method

    public void execute() throws MojoExecutionException {

        enableLogging();

        ConvertXml2Tab converter = new ConvertXml2Tab();

        // config
        converter.setOverwriteOutputFile( true );
        converter.setExpansionStrategy( new SpokeWithoutBaitExpansion() );
        converter.setInteractorPairClustering(true);

        if (hasBinaryInteractionClass()) {

            if (getLog().isWarnEnabled()) {
                getLog().warn("Using BinaryInteraction class: " + getBinaryInteractionClass());
                getLog().warn("Using ColumnHandler class: " + getColumnHandler());
            }

            try {
                converter.setBinaryInteractionClass(Class.forName(getBinaryInteractionClass()));

                if (getColumnHandler() != null) {
                    Constructor constructor = Class.forName(getColumnHandler()).getConstructor(new Class[]{});
                    ColumnHandler columnHandler = (ColumnHandler) constructor.newInstance(new Object[]{});

                    if (columnHandler instanceof IntActColumnHandler) {
                        IntActColumnHandler iaColumnHandler = (IntActColumnHandler) columnHandler;
                        iaColumnHandler.setGoTermNameAutoCompletion(goTermAutocompletion);
                        iaColumnHandler.setInterproNameAutoCompletion(interproTermAutocompletion);
                    }

                    converter.setColumnHandler(columnHandler);
                }
            } catch (Exception e) {
                throw new MojoExecutionException("Could not instanciate provided classes, see nested exception", e);
            }
        }

        Collection<File> inputFiles = new ArrayList<File>();
        if ( files != null ) {
            for ( Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                String filepath = ( String ) iterator.next();
                inputFiles.add( new File( filepath ) );
            }
        }
        converter.setXmlFilesToConvert( inputFiles );
        converter.setOutputFile( new File( tabFile ) );


        try {
            converter.setLogWriter( getOutputWriter() );
        } catch ( IOException e ) {
            getLog().error( e );
            throw new MojoExecutionException( "Error getting outputWriter", e );
        }

        // run it
        try {
            converter.convert();
        } catch ( Exception e ) {
            throw new MojoExecutionException( "Error while converting files to PSIMITAB... see nested exception.", e );
        }
    }

    public boolean isInterproTermAutocompletion() {
        return interproTermAutocompletion;
    }

    public void setInterproTermAutocompletion(boolean interproTermAutocompletion) {
        this.interproTermAutocompletion = interproTermAutocompletion;
    }

    public boolean isGoTermAutocompletion() {
        return goTermAutocompletion;
    }

    public void setGoTermAutocompletion(boolean goTermAutocompletion) {
        this.goTermAutocompletion = goTermAutocompletion;
    }
}