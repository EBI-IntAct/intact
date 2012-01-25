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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared;

import psidev.psi.mi.xml.model.DbReference;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.*;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.PsiMiPopulator;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.regex.Matcher;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class XrefConverter<X extends Xref> extends AbstractIntactPsiConverter<X, DbReference> {

    private Class<X> xrefClass;

    public XrefConverter(Institution institution, Class<X> xrefType) {
        super(institution);
        this.xrefClass = xrefType;
    }

    public X psiToIntact(DbReference psiObject) {
        String primaryId = psiObject.getId();
        String secondaryId = psiObject.getSecondary();
        String dbRelease = psiObject.getVersion();

        if (primaryId.length() == 0) {
            throw new PsiConversionException("Id in DbReference is empty: "+psiObject);
        }

        fixPubmedReferenceAsIdentityToPrimaryRef(psiObject);

        PsiMiPopulator psiMiPopulator = new PsiMiPopulator(getInstitution());

        String db = psiObject.getDb();
        String dbAc = psiObject.getDbAc();

        CvDatabase cvDb = new CvDatabase(getInstitution(), db);
        cvDb.setIdentifier(dbAc);

        if (dbAc != null) {
            psiMiPopulator.populateWithPsiMi(cvDb, dbAc);
        }

        String refType = psiObject.getRefType();
        String refTypeAc = psiObject.getRefTypeAc();

        CvXrefQualifier xrefQual = null;

        if (refType != null) {
            xrefQual = new CvXrefQualifier(getInstitution(), refType);
            xrefQual.setIdentifier(refTypeAc);

            if (refTypeAc != null) {
                psiMiPopulator.populateWithPsiMi(xrefQual, refTypeAc);
            }
        }

        X xref = newXrefInstance(xrefClass, cvDb, primaryId, secondaryId, dbRelease, xrefQual);
        psiStartConversion(psiObject);
        xref.setOwner(getInstitution());
        psiEndConversion(psiObject);

        return xref;
    }

    public DbReference intactToPsi(Xref intactObject) {
        // never change the intact object!!!!
        //fixPubmedReferenceAsIdentityToPrimaryRef(intactObject);

        boolean isPubmed = false;

        DbReference dbRef = new DbReference();

        intactStartConversation(intactObject);

        if (intactObject.getCvDatabase() != null) {
            isPubmed = CvDatabase.PUBMED_MI_REF.equals(intactObject.getCvDatabase().getIdentifier());

            if (intactObject.getCvDatabase().getIdentifier() != null){
                String upperId = intactObject.getCvDatabase().getIdentifier().toUpperCase();
                Matcher databaseMatcher = CvObjectConverter.MI_REGEXP.matcher(upperId);

                if (databaseMatcher.find() && databaseMatcher.group().equalsIgnoreCase(upperId)){
                    dbRef.setDbAc(intactObject.getCvDatabase().getIdentifier());
                }
            }

            dbRef.setDb(intactObject.getCvDatabase().getShortLabel());
        }

        dbRef.setId(intactObject.getPrimaryId());
        dbRef.setSecondary(intactObject.getSecondaryId());
        dbRef.setVersion(intactObject.getDbRelease());

        if (intactObject.getCvXrefQualifier() != null) {
            if (isPubmed && CvXrefQualifier.IDENTITY_MI_REF.equals(intactObject.getCvXrefQualifier().getIdentifier())){
                dbRef.setRefType(CvXrefQualifier.PRIMARY_REFERENCE);
                dbRef.setRefTypeAc(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
            }
            else{
                dbRef.setRefType(intactObject.getCvXrefQualifier().getShortLabel());

                if (intactObject.getCvXrefQualifier().getIdentifier() != null){
                    String upperId = intactObject.getCvXrefQualifier().getIdentifier().toUpperCase();
                    Matcher qualifierMatcher = CvObjectConverter.MI_REGEXP.matcher(upperId);

                    if (qualifierMatcher.find() && qualifierMatcher.group().equalsIgnoreCase(upperId)){
                        dbRef.setRefTypeAc(intactObject.getCvXrefQualifier().getIdentifier());
                    }
                }
            }
        }

        intactEndConversion(intactObject);
        return dbRef;
    }

    private static <X extends Xref> X newXrefInstance(Class<X> xrefClass, CvDatabase db, String primaryId, String secondaryId, String dbRelease, CvXrefQualifier cvXrefQual) {
        X xref = null;
        try {
            xref = xrefClass.newInstance();
            xref.setCvDatabase(db);
            xref.setPrimaryId(primaryId);
            xref.setSecondaryId(secondaryId);
            xref.setDbRelease(dbRelease);
            xref.setCvXrefQualifier(cvXrefQual);
        } catch (Exception e) {
            throw new PsiConversionException(e);
        }

        return xref;
    }

    protected void fixPubmedReferenceAsIdentityToPrimaryRef(Xref xref) {
        if (CvDatabase.PUBMED_MI_REF.equals(xref.getCvDatabase().getIdentifier())
                && CvXrefQualifier.IDENTITY_MI_REF.equals(xref.getCvXrefQualifier().getIdentifier())) {
            CvXrefQualifier primaryRef = CvObjectUtils.createCvObject(xref.getOwner(), CvXrefQualifier.class,
                    CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, CvXrefQualifier.PRIMARY_REFERENCE);
            xref.setCvXrefQualifier(primaryRef);

            final ConverterMessage converterMessage = new ConverterMessage(MessageLevel.WARN, "Incorrect cross refernece to Pubmed that had qualifier 'identity'. Changed to 'primary-reference",
                    ConverterContext.getInstance().getLocation().getCurrentLocation());
            converterMessage.setAutoFixed(true);
            ConverterContext.getInstance().getReport().getMessages().add(converterMessage);
        }
    }

    protected void fixPubmedReferenceAsIdentityToPrimaryRef(DbReference dbRef) {
        if (((dbRef.getDbAc() != null && CvDatabase.PUBMED_MI_REF.equals(dbRef.getDbAc()))
                || (dbRef.getDbAc() == null && CvDatabase.PUBMED.equals(dbRef.getDb().toLowerCase())))
                && (( dbRef.getRefTypeAc() != null && CvXrefQualifier.IDENTITY_MI_REF.equals(dbRef.getRefTypeAc()))
        || ( dbRef.getRefTypeAc() == null && CvXrefQualifier.IDENTITY.equals(dbRef.getRefType().toLowerCase())))) {
            dbRef.setRefTypeAc(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
            dbRef.setRefType(CvXrefQualifier.PRIMARY_REFERENCE);

            final ConverterMessage converterMessage = new ConverterMessage(MessageLevel.WARN, "Incorrect cross refernece to Pubmed that had qualifier 'identity'. Changed to 'primary-reference",
                    ConverterContext.getInstance().getLocation().getCurrentLocation());
            converterMessage.setAutoFixed(true);
            ConverterContext.getInstance().getReport().getMessages().add(converterMessage);
        }
    }

}