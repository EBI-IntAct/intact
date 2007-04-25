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
package uk.ac.ebi.intact.psixml.tools.generator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SourceBuilderHelper {

    private Map<Class,String> modelClassToValidatorName;
    private Map<Class,String> modelClassToValidatorFilename;

    private SourceBuilderContext sbContext;

    public SourceBuilderHelper(List<Class> modelClasses, SourceBuilderContext sbContext)
    {
        this.sbContext = sbContext;

        File outputDir = createOutputDir();
        
        modelClassToValidatorName = new HashMap(modelClasses.size());
        modelClassToValidatorFilename = new HashMap(modelClasses.size());

        for (Class modelClass : modelClasses)
        {
            String validatorClassName = validatorNameForClass(modelClass);
            String validatorClassFile = filenameForClass(modelClass);

            modelClassToValidatorName.put(modelClass, validatorClassName);
            modelClassToValidatorFilename.put(modelClass, validatorClassFile);
        }
    }

    public String getValidatorNameForClass(Class modelClass)
    {
        return modelClassToValidatorName.get(modelClass);
    }

    public String getValidatorFilenameForClass(Class modelClass) {
        return modelClassToValidatorFilename.get(modelClass);
    }

    public File getValidatorFileForClass(Class modelClass)
    {
        return new File(sbContext.getOutputDir(), getValidatorFilenameForClass(modelClass));
    }

    private String validatorNameForClass(Class modelClass)
    {
        return modelClass.getSimpleName()+"Validator";
    }

    private String filenameForClass(Class modelClass)
    {
        return validatorNameForClass(modelClass)+".java";
    }

    private File createOutputDir() {
        sbContext.getOutputDir().mkdirs();

        return sbContext.getOutputDir();
    }
}