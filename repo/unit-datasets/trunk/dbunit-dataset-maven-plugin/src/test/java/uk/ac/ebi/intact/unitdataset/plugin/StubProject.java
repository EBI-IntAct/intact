/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
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
package uk.ac.ebi.intact.unitdataset.plugin;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: StubProject.java 8642 2007-06-11 15:57:16Z baranda $
 */
public class StubProject extends MavenProject {



    @Override
    public Build getBuild() {
        Build b = new Build();
        b.setDirectory("target");

        return b;
    }


}