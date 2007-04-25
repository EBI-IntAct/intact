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

import java.lang.reflect.*;
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
        mcm.setCollections(collectionsFrom(helper, mcm));

        return mcm;
    }

    /**
     * Using reflection, gets the collections from the model class provided and create CollectionMetaData
     */
    private static List<CollectionMetadata> collectionsFrom(SourceGeneratorHelper helper, ModelClassMetadata modelClassMetadata) {
        List<CollectionMetadata> collections = new ArrayList<CollectionMetadata>();

        for (Field field : modelClassMetadata.getModelClass().getDeclaredFields()) {
            Class type = field.getType();

            if (type.isAssignableFrom(Collection.class)) {
                Type genType = field.getGenericType();

                if (genType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genType;

                    Class typeOfCollection = (Class) pt.getActualTypeArguments()[0];

                    if (typeOfCollection.getAnnotation(PsiXmlElement.class) != null) {
                        String getterMethodName = getGetterMethodNameForCollection(typeOfCollection, modelClassMetadata);

                        CollectionMetadata cm = new CollectionMetadata(typeOfCollection, helper.getValidatorNameForClass(typeOfCollection), getterMethodName);
                        collections.add(cm);
                    }
                }
            }
        }

        return collections;
    }

    private static String getGetterMethodNameForCollection(Class type, ModelClassMetadata modelClassMetadata) {
        for (Method m : modelClassMetadata.getModelClass().getMethods()) {
            if (m.getName().startsWith("get")) {

                Type genType = m.getGenericReturnType();

                if (genType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genType;

                    Class typeOfCollection = (Class) pt.getActualTypeArguments()[0];

                    if (typeOfCollection.equals(type)) {
                        return m.getName();
                    }
                }
            }
        }

        return null;
    }

    private static void print
            (TypeVariable
                    v
            ) {
        System.out.println("Type variable");
        System.out.println("Name: " + v.getName());
        System.out.println("Declaration: " +
                           v.getGenericDeclaration());
        System.out.println("Bounds:");
        for (Type t : v.getBounds()) {
            print(t);
        }
    }

    // Prints information about a wildcard type
    private static void print
            (WildcardType
                    wt
            ) {
        System.out.println("Wildcard type");
        System.out.println("Lower bounds:");
        for (Type b : wt.getLowerBounds()) {
            print(b);
        }

        System.out.println("Upper bounds:");
        for (Type b : wt.getUpperBounds()) {
            print(b);
        }
    }

    // Prints information about a parameterized type
    private static void print
            (ParameterizedType
                    pt
            ) {
        System.out.println("Parameterized type");
        System.out.println("Owner: " + pt.getOwnerType());
        System.out.println("Raw type: " + pt.getRawType());

        for (Type actualType : pt.getActualTypeArguments()) {
            print(actualType);
        }
    }

    // Prints information about a generic array type
    private static void print
            (GenericArrayType
                    gat
            ) {
        System.out.println("Generic array type");
        System.out.println("Type of array: ");
        print(gat.getGenericComponentType());
    }

    /**
     * Prints information about a type. The nested
     * if/else-if chain calls the
     * appropriate overloaded print method for the
     * type. If t is just a Class,
     * we print it directly.
     */

    private static void print
            (Type
                    t
            ) {
        if (t instanceof TypeVariable) {
            print((TypeVariable) t);
        } else if (t instanceof WildcardType) {
            print((WildcardType) t);
        } else if (t instanceof ParameterizedType) {
            print((ParameterizedType) t);
        } else if (t instanceof GenericArrayType) {
            print((GenericArrayType) t);
        } else {
            System.out.println(t);
        }
    }
}