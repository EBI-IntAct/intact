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

import psidev.psi.mi.xml.model.*;
import psidev.psi.mi.xml.model.Xref;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.psixml.converter.shared.AbstractCvConverter;
import uk.ac.ebi.intact.psixml.converter.shared.XrefConverter;

import java.util.Collection;
import java.util.HashSet;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PsiConverterUtils {

    private PsiConverterUtils() {
    }

    public static void populate(AnnotatedObject<?, ?> annotatedObject, Object objectToPopulate) {
        if (objectToPopulate instanceof HasId) {
            populateId((HasId) objectToPopulate);
        }

        if (objectToPopulate instanceof NamesContainer) {
            populateNames(annotatedObject, (NamesContainer) objectToPopulate);
        }

        if (objectToPopulate instanceof XrefContainer) {
            populateXref(annotatedObject, (XrefContainer) objectToPopulate);
        }
    }

    private static void populateNames(AnnotatedObject<?, ?> annotatedObject, NamesContainer namesContainer) {
        Names names = namesContainer.getNames();

        if (names == null) {
            names = new Names();
        }

        names.setShortLabel(annotatedObject.getShortLabel());
        names.setFullName(annotatedObject.getFullName());

        namesContainer.setNames(names);
    }

    private static void populateXref(AnnotatedObject<?, ?> annotatedObject, XrefContainer xrefContainer) {
        if (annotatedObject.getXrefs().isEmpty()) {
            return;
        }

        Xref xref = xrefContainer.getXref();

        if (xref == null) {
            xref = new Xref();
        }

        Collection<DbReference> dbRefs = toDbReferences(annotatedObject.getXrefs());

        // normally the primary reference is the identity reference, but for bibliographic references
        // it is the primary-reference and it does not contain secondary refs
        if (xrefContainer instanceof Bibref) {
            DbReference primaryRef = getPrimaryReference(dbRefs);
            xref.setPrimaryRef(primaryRef);
        } else {
            DbReference primaryRef = getIdentity(dbRefs);
            xref.setPrimaryRef(primaryRef);

            // remove the primary ref and the bibref (primary-ref) in case of being an experiment
            // from the collection and add the rest as secondary refs
            dbRefs.remove(primaryRef);

            if (annotatedObject instanceof Experiment) {
                dbRefs.remove(getPrimaryReference(dbRefs));
            }

            xref.getSecondaryRef().addAll(dbRefs);
        }


        xrefContainer.setXref(xref);
    }

    private static int populateId(HasId hasIdElement) {
        int id = IdSequenceGenerator.getInstance().nextId();
        hasIdElement.setId(id);

        return id;
    }

    public static CvType toCvType(CvObject cvObject, AbstractCvConverter converter) {
        if (cvObject == null) {
            throw new NullPointerException("cvObject");
        }

        CvType cvType = converter.intactToPsi(cvObject);
        populate(cvObject, cvType);

        return cvType;
    }

    private static Collection<DbReference> toDbReferences(Collection<? extends uk.ac.ebi.intact.model.Xref> intactXrefs) {
        Collection<DbReference> dbRefs = new HashSet<DbReference>(intactXrefs.size());

        for (uk.ac.ebi.intact.model.Xref intactXref : intactXrefs) {
            XrefConverter xrefConverter = new XrefConverter(null, intactXref.getClass());

            DbReference dbRef = xrefConverter.intactToPsi(intactXref);
            dbRefs.add(dbRef);
        }

        return dbRefs;
    }

    private static DbReference getIdentity(Collection<DbReference> dbRefs) {
        Collection<DbReference> identityRefs = new HashSet<DbReference>();

        for (DbReference dbRef : dbRefs) {
            if (dbRef.getRefTypeAc() != null && dbRef.getRefTypeAc().equals(CvXrefQualifier.IDENTITY_MI_REF)) {

                if (dbRef.getDbAc() != null && dbRef.getDbAc().equals(CvDatabase.PSI_MI_MI_REF)) {
                    return dbRef;
                }

                identityRefs.add(dbRef);
            }
        }

        if (!identityRefs.isEmpty()) {
            return identityRefs.iterator().next();
        }

        if (!dbRefs.isEmpty()) {
            return dbRefs.iterator().next();
        }

        return null;
    }

    private static DbReference getPrimaryReference(Collection<DbReference> dbRefs) {
        for (DbReference dbRef : dbRefs) {
            if (dbRef.getRefTypeAc() != null && dbRef.getRefTypeAc().equals(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF)) {
                return dbRef;
            }
        }

        if (!dbRefs.isEmpty()) {
            return dbRefs.iterator().next();
        }

        return null;
    }

}