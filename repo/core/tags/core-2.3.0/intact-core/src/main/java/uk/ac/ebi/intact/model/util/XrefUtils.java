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
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.util.ClassUtils;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utils with xrefs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class XrefUtils {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(XrefUtils.class);

    public static <X extends Xref> X createIdentityXrefPsiMi(AnnotatedObject<X,?> parent, String primaryId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        return createIdentityXref(parent, primaryId, builder.createIdentityCvXrefQualifier(parent.getOwner()), builder.createPsiMiCvDatabase(parent.getOwner()));
    }

    public static <X extends Xref> X createIdentityXrefIntact(AnnotatedObject<X,?> parent, String intactId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(parent.getOwner(), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        return createIdentityXref(parent, intactId, builder.createIdentityCvXrefQualifier(parent.getOwner()), cvDatabase);
    }

    public static <X extends Xref> X createIdentityXrefChebi(AnnotatedObject<X,?> parent, String chebiId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(parent.getOwner(), CvDatabase.class, CvDatabase.CHEBI_MI_REF, CvDatabase.CHEBI);

        return createIdentityXref(parent, chebiId, builder.createIdentityCvXrefQualifier(parent.getOwner()), cvDatabase);
    }

    public static <X extends Xref> X createIdentityXrefEmblGenbankDdbj(AnnotatedObject<X,?> parent, String emblGenbankDdbjId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(parent.getOwner(),
                                                             CvDatabase.class,
                                                             CvDatabase.DDBG_MI_REF,
                                                             CvDatabase.DDBG);
        return createIdentityXref(parent, emblGenbankDdbjId,
                                  builder.createIdentityCvXrefQualifier(parent.getOwner()),
                                  cvDatabase);
    }

    public static <X extends Xref> X createIdentityXrefUniprot(AnnotatedObject<X,?> parent, String primaryId) {
        CvObjectBuilder builder = new CvObjectBuilder();
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(parent.getOwner(), CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);

        return createIdentityXref(parent, primaryId, builder.createIdentityCvXrefQualifier(parent.getOwner()), cvDatabase);
    }

    public static <X extends Xref> X createIdentityXref(AnnotatedObject<X,?> parent, String primaryId, CvDatabase cvDatabase) {
        CvObjectBuilder builder = new CvObjectBuilder();
        return createIdentityXref(parent, primaryId, builder.createIdentityCvXrefQualifier(parent.getOwner()), cvDatabase);
    }

    public static <X extends Xref> X createIdentityXref(AnnotatedObject<X,?> parent, String primaryId, CvXrefQualifier identityQual, CvDatabase cvDatabase) {
        X xref = (X) newXrefInstanceFor(parent.getClass());
        Institution owner = parent.getOwner();

        if (owner == null) {
            owner = IntactContext.getCurrentInstance().getInstitution();
        }

        xref.setOwner(owner);
        xref.setCvDatabase(cvDatabase);
        xref.setCvXrefQualifier(identityQual);
        xref.setPrimaryId(primaryId);
        xref.setParent(parent);

        return xref;
    }

    public static <X extends Xref> X newXrefInstanceFor(Class<? extends AnnotatedObject> aoClass) {
        Class<X> xrefClass = (Class<X>) AnnotatedObjectUtils.getXrefClassType(aoClass);
        return ClassUtils.newInstance(xrefClass);
    }

    public static <X extends Xref> Collection<X> getIdentityXrefs(AnnotatedObject<X,?> annotatedObject) {
        Collection<X> xrefs = new ArrayList<X>();

        Collection<X> allXrefs = annotatedObject.getXrefs();

        if (!Hibernate.isInitialized(annotatedObject.getXrefs()) && IntactContext.currentInstanceExists()) {
            Class xrefClass = AnnotatedObjectUtils.getXrefClassType(annotatedObject.getClass());
            allXrefs = IntactContext.getCurrentInstance().getDaoFactory().getXrefDao(xrefClass).getByParentAc(annotatedObject.getAc());
        }

        for (X xref : allXrefs) {
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            String qualifierMi = null;
            if (qualifier != null && ((qualifierMi = qualifier.getIdentifier()) != null &&
                qualifierMi.equals(CvXrefQualifier.IDENTITY_MI_REF))) {
                xrefs.add(xref);
            }
        }

        return xrefs;
    }

    public static <X extends Xref> X getIdentityXref(AnnotatedObject<X,?> annotatedObject, CvDatabase cvDatabase) {
        String dbMi = cvDatabase.getIdentifier();

        return getIdentityXref(annotatedObject, dbMi);
    }

    public static <X extends Xref> X getIdentityXref(AnnotatedObject<X,?> annotatedObject, String databaseMi) {
        for (X xref : annotatedObject.getXrefs()) {
            CvXrefQualifier qualifier = xref.getCvXrefQualifier();
            CvDatabase database = xref.getCvDatabase();
            String qualMi;
            String dbMi;
            if (qualifier != null && database != null &&
                (qualMi = qualifier.getIdentifier()) != null &&
                (dbMi = database.getIdentifier()) != null &&
                qualMi.equals(CvXrefQualifier.IDENTITY_MI_REF) &&
                dbMi.equals(databaseMi)) {

                return xref;
            }
        }

        return null;
    }

    public static <X extends Xref> X getPsiMiIdentityXref(AnnotatedObject<X,?> annotatedObject) {
        if (annotatedObject == null) {
            throw new NullPointerException("annotatedObject should not be null");
        }

        X psiMiXref = null;

        Collection<X> identityXrefs = AnnotatedObjectUtils.searchXrefs(annotatedObject, CvDatabase.PSI_MI_MI_REF, CvXrefQualifier.IDENTITY_MI_REF);

        if (!identityXrefs.isEmpty()) {
            psiMiXref = identityXrefs.iterator().next();
        }

        return psiMiXref;
    }

    // ex1 : annotatedObject is supposibly the CvDatabase psi-mi, psiMi is CvDatabase.PSI_MI_MI_REF
    // ex2: annotatedObject is supposibly the CvXrefQualifier identity , psiMi is  CvXrefQualifier.IDENTITY_MI_REF
    public static <X extends Xref> boolean hasIdentity(AnnotatedObject<X,?> annotatedObject, String psiMi) {
        if (annotatedObject == null) {
            throw new NullPointerException("annotatedObject should not be null");
        }
        if (psiMi == null) {
            throw new NullPointerException("psiMi should not be null");
        }
        Collection<X> annotatedObjectXrefs = annotatedObject.getXrefs();
        for (X xref : annotatedObjectXrefs) {
            if (psiMi.equals(xref.getPrimaryId())) {
                if (CvXrefQualifier.IDENTITY_MI_REF.equals(psiMi)) {
                    return true;
                }
                if (xref.getCvXrefQualifier() != null && hasIdentity(xref.getCvXrefQualifier(), CvXrefQualifier.IDENTITY_MI_REF)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets all the Xrefs for the CvDatabase with the passed mi identifier
     * @param annotatedObject the Object with the xrefs
     * @param databaseMi the database MI to look for
     * @return list of Xrefs
     *
     * @deprecated use AnnotatedObjectUtils.searchXrefsByDatabase instead
     */
    @Deprecated
    public static <X extends Xref> List<X> getXrefsFilteredByDatabase(AnnotatedObject<X,?> annotatedObject, String databaseMi) {
        if (annotatedObject == null) throw new NullPointerException("Null annotatedObject");
        if (databaseMi == null) throw new NullPointerException("Database MI Identifier is mandatory");

        return new ArrayList<X>(AnnotatedObjectUtils.searchXrefsByDatabase(annotatedObject, databaseMi));
    }
}