package uk.ac.ebi.intact.plugin.myplugin;

/*
 * Copyright 2006 The European Bioinformatics Institute.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import uk.ac.ebi.intact.plugin.IntactAbstractMojo;

import java.io.File;

/**
 * Test goal
 *
 * @goal mygoal
 * 
 * @phase process-resources
 */
public class MyMojo
    extends IntactAbstractMojo
{

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        File outputDir = super.getDirectory();

        // TODO: implement plugin
    }
}
