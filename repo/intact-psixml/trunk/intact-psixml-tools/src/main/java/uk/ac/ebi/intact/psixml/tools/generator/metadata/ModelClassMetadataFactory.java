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
package uk.ac.ebi.intact.psixml.tools.generator.metadata;

import uk.ac.ebi.intact.psixml.tools.generator.SourceGeneratorHelper;
import uk.ac.ebi.intact.psixml.tools.generator.metadata.util.PsiReflectionUtils;

/**
 * Creates instances of ModelClassMetadata
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:ModelClassMetadataFactory.java 8272 2007-04-25 10:20:12Z baranda $
 */
public class ModelClassMetadataFactory {

    public static ModelClassMetadata createModelClassMetadata(SourceGeneratorHelper helper, Class modelClass) {
        ModelClassMetadata mcm = new ModelClassMetadata(modelClass);
        mcm.setBooleansWithMetadata(PsiReflectionUtils.booleanFieldsFrom(mcm));
        mcm.setIndividuals(PsiReflectionUtils.individualsFrom(helper, mcm));
        mcm.setCollections(PsiReflectionUtils.collectionsFrom(helper, mcm));
        return mcm;
    }


}