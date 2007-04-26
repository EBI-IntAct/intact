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
package uk.ac.ebi.intact.psixml.tools.generator.metadata.field;

import org.apache.commons.beanutils.BeanUtils;
import psidev.psi.mi.annotations.PsiBooleanField;
import psidev.psi.mi.annotations.PsiCollectionField;
import uk.ac.ebi.intact.psixml.tools.generator.SourceGeneratorHelper;
import uk.ac.ebi.intact.psixml.tools.generator.metadata.ModelClassMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationFieldMetadataFactory {

    public static BooleanFieldMetadata newBooleanFieldMetadata(Field booleanField, ModelClassMetadata modelClassMd) throws MetadataException {
        BooleanFieldMetadata fieldMetadata = new BooleanFieldMetadata(booleanField);

        Annotation annot = booleanField.getAnnotation(PsiBooleanField.class);
        if (annot != null) {
            populateFieldMetadataWithAnnotation(fieldMetadata, annot);
        }

        return fieldMetadata;
    }

    public static CollectionFieldMetadata newCollectionFieldMetadata(Class type, Field colField, SourceGeneratorHelper helper, ModelClassMetadata modelClassMd) throws MetadataException {
        String validatorName = helper.getValidatorNameForClass(type);

        CollectionFieldMetadata fieldMetadata = new CollectionFieldMetadata(type, colField, validatorName);

        Annotation annot = colField.getAnnotation(PsiCollectionField.class);
        if (annot != null) {
            populateFieldMetadataWithAnnotation(fieldMetadata, annot);
        }

        return fieldMetadata;
    }

    private static void populateFieldMetadataWithAnnotation(FieldMetadata fieldMeta, Annotation annotation) throws MetadataException {
        for (Method annotMethod : annotation.annotationType().getDeclaredMethods()) {
            // the name of the property to set and the annotation method must be the same
            String propName = annotMethod.getName();
            Object propValue = null;
            try {
                propValue = annotMethod.invoke(annotation, null);
            } catch (Exception e) {
                throw new MetadataException("Problem invoking method " + annotMethod.getName() + " in annotation " + annotation.annotationType(), e);
            }

            if (propValue != null) {
                try {
                    BeanUtils.setProperty(fieldMeta, propName, propValue);
                } catch (Exception e) {
                    throw new MetadataException("Exception setting property " + propName + " in bean of type " + fieldMeta.getClass().getName() + ". Value: " + propValue);
                }
            }
        }
    }
}