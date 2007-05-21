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
package uk.ac.ebi.intact.psixml.converter.util;

import psidev.psi.mi.xml.model.DbReference;
import psidev.psi.mi.xml.model.Names;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.psixml.converter.shared.XrefConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ConverterUtils {

    private ConverterUtils() {
    }

    public static void populateNames(Names names, AnnotatedObject<?, ?> annotatedObject) {
        annotatedObject.setShortLabel(names.getShortLabel());
        annotatedObject.setFullName(names.getFullName());
    }

    public static <X extends Xref> void populateXref(psidev.psi.mi.xml.model.Xref psiXref, AnnotatedObject<X, ?> annotatedObject, XrefConverter<X> xrefConverter) {
        if (psiXref.getPrimaryRef() != null) {
            addXref(psiXref.getPrimaryRef(), annotatedObject, xrefConverter);
        }

        for (DbReference secondaryRef : psiXref.getSecondaryRef()) {
            addXref(secondaryRef, annotatedObject, xrefConverter);
        }
    }

    private static <X extends Xref> void addXref(DbReference dbReference, AnnotatedObject<X, ?> annotatedObject, XrefConverter<X> xrefConverter) {
        X xref = xrefConverter.psiToIntact(dbReference);
        annotatedObject.addXref(xref);
    }

}