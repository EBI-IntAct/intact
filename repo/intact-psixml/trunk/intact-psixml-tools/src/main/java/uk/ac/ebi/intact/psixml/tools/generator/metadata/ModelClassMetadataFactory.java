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

import psidev.psi.mi.annotations.PsiXmlElement;
import uk.ac.ebi.intact.psixml.tools.generator.SourceGeneratorHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates instances of ModelClassMetadata
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:ModelClassMetadataFactory.java 8272 2007-04-25 10:20:12Z baranda $
 */
public class ModelClassMetadataFactory {

    public static ModelClassMetadata createModelClassMetadata(SourceGeneratorHelper helper, Class modelClass) {
        ModelClassMetadata mcm = new ModelClassMetadata(modelClass);
        mcm.setIndividuals(individualsFrom(helper, mcm));
        mcm.setCollections(collectionsFrom(helper, mcm));
        return mcm;
    }

    private static List<FieldMetadata> individualsFrom(SourceGeneratorHelper helper, ModelClassMetadata modelClassMetadata) {
        List<FieldMetadata> individuals = new ArrayList<FieldMetadata>();

        for (Field field : fieldsWithModelClasses(modelClassMetadata)) {
            Class clazz = field.getType();

            Method getterMethod = getGetterMethodForIndividual(clazz, modelClassMetadata);

            if (getterMethod != null) {
                FieldMetadata fm = new FieldMetadata(clazz, helper.getValidatorNameForClass(clazz), getterMethod.getName());
                individuals.add(fm);
            }
        }

        return individuals;
    }


    /**
     * Using reflection, gets the collections from the model class provided and create CollectionMetaData
     */
    private static List<FieldMetadata> collectionsFrom(SourceGeneratorHelper helper, ModelClassMetadata modelClassMetadata) {
        List<FieldMetadata> collections = new ArrayList<FieldMetadata>();

        for (Field field : fieldsOfType(modelClassMetadata, Collection.class)) {
            Type genType = field.getGenericType();

            if (genType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genType;

                Class typeOfCollection = (Class) pt.getActualTypeArguments()[0];

                if (typeOfCollection.getAnnotation(PsiXmlElement.class) != null) {
                    Method getterMethod = getGetterMethodForCollection(typeOfCollection, modelClassMetadata);

                    if (getterMethod != null) {
                        FieldMetadata cm = new FieldMetadata(typeOfCollection, helper.getValidatorNameForClass(typeOfCollection), getterMethod.getName());
                        collections.add(cm);
                    }
                }
            }
        }

        return collections;
    }

    private static Method getGetterMethodForIndividual(Class type, ModelClassMetadata modelClassMetadata) {
        for (Method m : modelClassMetadata.getModelClass().getMethods()) {
            if (m.getName().startsWith("get")) {

                Type retType = m.getReturnType();

                if (type.equals(retType)) {
                    return m;
                }
            }
        }

        return null;
    }

    private static Method getGetterMethodForCollection(Class genericType, ModelClassMetadata modelClassMetadata) {
        for (Method m : modelClassMetadata.getModelClass().getMethods()) {
            if (m.getName().startsWith("get")) {
                Type genType = m.getGenericReturnType();

                if (genType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genType;

                    Class typeOfCollection = (Class) pt.getActualTypeArguments()[0];

                    if (typeOfCollection.equals(genericType)) {
                        return m;
                    }
                }
            }
        }

        return null;
    }

    private static List<Field> fieldsOfType(ModelClassMetadata modelClassMetadata, Class type) {
        List<Field> fields = new ArrayList<Field>();

        for (Field field : modelClassMetadata.getModelClass().getDeclaredFields()) {
            if (type.equals(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    private static List<Field> fieldsWithModelClasses(ModelClassMetadata modelClassMetadata) {
        List<Field> fields = new ArrayList<Field>();

        for (Field field : modelClassMetadata.getModelClass().getDeclaredFields()) {
            Class clazz = field.getType();

            if (clazz.getAnnotation(PsiXmlElement.class) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

}