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

import psidev.psi.mi.xml.model.Alias;
import psidev.psi.mi.xml.model.*;
import psidev.psi.mi.xml.model.Confidence;
import psidev.psi.mi.xml.model.Feature;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Interactor;
import psidev.psi.mi.xml.model.Parameter;
import psidev.psi.mi.xml.model.Range;
import psidev.psi.mi.xml.model.Xref;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.IdSequenceGenerator;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PsiMockFactory {

    private static int MIN_CHILDREN = 2;
    private static int MAX_CHILDREN = 10;

    public static void setMinChildren(int minChildren) {
        MIN_CHILDREN = minChildren;
    }

    public static void setMaxChildren(int maxChildren) {
        MAX_CHILDREN = maxChildren;
    }

    private PsiMockFactory() {
    }

    public static Entry createMockEntry() {
        Entry entry = new Entry();

        entry.setSource(createMockSource());

        for (int i = 0; i < childRandom(); i++) {
            Interaction interaction = createMockInteraction();
            entry.getInteractions().add(interaction);

            for (ExperimentDescription expDesc : interaction.getExperiments()) {
                entry.getExperiments().add(expDesc);
            }

            for (Participant part : interaction.getParticipants()) {
                entry.getInteractors().add(part.getInteractor());
            }
        }

        return entry;
    }

    public static Source createMockSource() {
        Source source = new Source();

        Names names = new Names();
        source.setNames(names);

        names.setShortLabel("intact");
        names.setFullName("European Bioinformatics Institute");

        names.getAliases().add(createAlias("ebi", CvAliasType.GO_SYNONYM, CvAliasType.GO_SYNONYM_MI_REF));

        Xref miRef = createPsiMiXref();
        miRef.getPrimaryRef().setId(CvDatabase.INTACT_MI_REF);
        source.setXref(miRef);

        source.setBibref(createSourceBibref());

        return source;
    }

    private static Xref createSourceXref(String dbMiRef) {
        DbReference dbReference = new DbReference(dbMiRef, CvDatabase.PSI_MI);
        dbReference.setDbAc(CvDatabase.PSI_MI_MI_REF);
        dbReference.setRefType(CvXrefQualifier.PRIMARY_REFERENCE);
        dbReference.setRefTypeAc(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);

        return new Xref(dbReference);
    }

    private static Bibref createSourceBibref() {
        DbReference dbReference = new DbReference("14681455", CvDatabase.PUBMED);
        dbReference.setDbAc(CvDatabase.PUBMED_MI_REF);
        dbReference.setRefType(CvXrefQualifier.PRIMARY_REFERENCE);
        dbReference.setRefTypeAc(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);

        Xref xref = new Xref(dbReference);

        return new Bibref(xref);
     }

    private static Bibref createExperimentBibref() {
        DbReference dbReference = new DbReference("14681455", CvDatabase.PUBMED);
        dbReference.setDbAc(CvDatabase.PUBMED_MI_REF);
        dbReference.setRefType(CvXrefQualifier.PRIMARY_REFERENCE);
        dbReference.setRefTypeAc(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);

        Xref xref = new Xref(dbReference);

        return new Bibref(xref);
     }

    public static Interaction createMockInteraction() {
        Interaction interaction = new Interaction();
        populate(interaction);
        interaction.getInteractionTypes().add(createCvType(InteractionType.class));

        for (int i = 0; i < childRandom(); i++) {
            interaction.getParticipants().add(createMockParticipant(interaction));
        }

        for (int i = 0; i < childRandom(1, 2); i++) {
            interaction.getExperiments().add(createMockExperiment());
        }

        for (int i=0; i< childRandom( 1, 2); i++){
            interaction.getConfidences().add(createMockConfidence());
        }

        interaction.getParameters().add(createParameter());

        return interaction;
    }

    public static Interaction createMockInteraction(int participantNum) {
        Interaction interaction = new Interaction();
        populate(interaction);
        interaction.getInteractionTypes().add(createCvType(InteractionType.class));

        for (int i = 0; i < participantNum; i++) {
            interaction.getParticipants().add(createMockParticipant(interaction));
        }

        interaction.getExperiments().add(createMockExperiment());

        for (int i=0; i< childRandom( 1, 2); i++){
            interaction.getConfidences().add(createMockConfidence());
        }

        return interaction;
    }

    public static Participant createMockParticipant(Interaction interaction) {
        Participant participant = new Participant();
        populate(participant);
        participant.setInteraction(interaction);
        participant.setInteractor(createMockInteractor());
        participant.setBiologicalRole(createCvType(BiologicalRole.class));
        participant.getExperimentalRoles().add(createCvType(ExperimentalRole.class));

        participant.getExperimentalPreparations().add(createCvType(ExperimentalPreparation.class));

        participant.getParticipantIdentificationMethods().add(createCvType(ParticipantIdentificationMethod.class, CvIdentification.PREDETERMINED_MI_REF, CvIdentification.PREDETERMINED));

        for (int i=0; i<childRandom(); i++) {
            participant.getFeatures().add(createFeature());
        }

        participant.getParameters().add(createParameter());

        return participant;
    }

    public static Interactor createMockInteractor() {
        Interactor interactor = new Interactor();
        populate(interactor);
        interactor.setInteractorType(createInteractorType());

        interactor.setOrganism(createMockOrganism());

        return interactor;
    }


    public static ExperimentDescription createMockExperiment() {
        Bibref bibref = createExperimentBibref();

        InteractionDetectionMethod idm = createCvType(InteractionDetectionMethod.class);

        ExperimentDescription experiment = new ExperimentDescription(bibref, idm);
        populate(experiment);
        experiment.getHostOrganisms().add(createMockOrganism());
        experiment.setBibref(bibref);

        return experiment;
    }

    public static Confidence createMockConfidence(){
        Unit unit = createCvType( Unit.class, "IA:10001","intact conf score");
       return new Confidence(unit, "0.8");
    }   

    public static Organism createMockOrganism() {
        Organism organism = new Organism();
        populate(organism);
        organism.setNcbiTaxId(nextInt());

        return organism;
    }

    private static void populate(Object object) {
        if (object instanceof HasId) {
            populateId((HasId) object);
        }
        if (object instanceof NamesContainer) {
            populateNames((NamesContainer) object);
        }
        if (object instanceof XrefContainer) {
            if (object instanceof CvType) {
                populateCvXrefs((CvType) object);
            } else {
                populateXref((XrefContainer) object);
            }
        }
    }

    private static void populateId(HasId hasId) {
        hasId.setId(nextId());
    }

    private static void populateNames(NamesContainer namesContainer) {
        namesContainer.setNames(createNames());
    }

    private static void populateXref(XrefContainer xrefContainer) {
        xrefContainer.setXref(createXref());
    }

    private static void populateCvXrefs(CvType xrefContainer) {
        xrefContainer.setXref(createPsiMiXref());
    }

    public static Names createNames() {
        Names names = new Names();
        names.setShortLabel(nextString());
        names.setFullName(nextString());

        names.getAliases().add(createAlias());

        return names;
    }

    public static Xref createXref() {
        return createXref(CvXrefQualifier.IDENTITY, CvXrefQualifier.IDENTITY_MI_REF);
    }

    public static Xref createPsiMiXref() {
        Xref xref = createXref(CvXrefQualifier.IDENTITY, CvXrefQualifier.IDENTITY_MI_REF);
        xref.getPrimaryRef().setId("MI:"+nextInt());
        xref.getPrimaryRef().setDb(CvDatabase.PSI_MI);
        xref.getPrimaryRef().setDbAc(CvDatabase.PSI_MI_MI_REF);

        return xref;
    }

    public static Xref createXref(String primaryRefType, String primaryRefTypeAc) {
        Xref xref = new Xref();
        xref.setPrimaryRef(createDbReference(primaryRefType, primaryRefTypeAc));

        for (int i = 0; i < childRandom(0, 4); i++) {
            xref.getSecondaryRef().add(createDbReference());
        }

        return xref;
    }

    public static Alias createAlias(String value, String aliasType, String aliasTypeAc) {
        Alias alias = new Alias();
        alias.setValue(value);
        alias.setType(aliasType);
        alias.setTypeAc(aliasTypeAc);

        return alias;
    }

    public static Alias createAlias() {
        return createAlias(nextString("al_"), CvAliasType.GENE_NAME_MI_REF, CvAliasType.GENE_NAME);
    }

    private static <C extends CvType> C createCvType(Class<C> cvTypeClass) {
        C cv = null;

        try {
            cv = cvTypeClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        populate(cv);

        return cv;
    }

    public static <C extends CvType> C createCvType(Class<C> cvTypeClass, String miRef, String label) {
        C cv = createCvType(cvTypeClass);
        cv.getNames().setShortLabel(label);

        if (miRef != null) {
            cv.getXref().setPrimaryRef(createDbReferencePsiMi(miRef));
        }

        return cv;
    }

    private static InteractorType createInteractorType() {
        InteractorType intType = createCvType(InteractorType.class);
        intType.getNames().setShortLabel(CvInteractorType.PROTEIN);
        intType.getXref().getPrimaryRef().setId("MI:0326");
        return intType;
    }

    public static Attribute createAttribute() {
        Attribute attr = new Attribute("name", nextString());
        return attr;
    }

    public static DbReference createDbReference() {
        return createDbReference(nextString("reftype"), nextString("ac"));
    }

    public static DbReference createDbReference(String refType, String refTypeAc) {
        DbReference dbRef = new DbReference(nextString("id"), nextString("db"));
        dbRef.setDbAc(nextString("ac"));
        dbRef.setRefType(refType);
        dbRef.setRefTypeAc(refTypeAc);
        dbRef.setSecondary(nextString("secondary"));
        dbRef.setVersion(nextString("version"));

        return dbRef;
    }

    public static DbReference createDbReferenceDatabaseOnly(String id, String dbAc, String db) {
        DbReference dbRef = new DbReference(id, db);
        dbRef.setDbAc(dbAc);
        return dbRef;
    }

    public static DbReference createDbReference(String refType, String refTypeAc, String dbType, String dbAc) {
        DbReference dbRef = new DbReference(nextString("id"), dbType);
        dbRef.setDbAc(dbAc);
        dbRef.setRefType(refType);
        dbRef.setRefTypeAc(refTypeAc);
        dbRef.setSecondary(nextString("secondary"));
        dbRef.setVersion(nextString("version"));

        return dbRef;
    }

    public static DbReference createDbReferencePsiMi(String psiMiRef) {
        DbReference ref = createDbReference(CvXrefQualifier.IDENTITY, CvXrefQualifier.IDENTITY_MI_REF, CvDatabase.PSI_MI, CvDatabase.PSI_MI_MI_REF);
        ref.setId(psiMiRef);

        return ref;
    }

    public static DbReference createDbReferencePrimaryRef(String psiMiRef) {
        DbReference ref = createDbReference(CvXrefQualifier.PRIMARY_REFERENCE, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, CvDatabase.PUBMED, CvDatabase.PUBMED_MI_REF);
        ref.setId(psiMiRef);

        return ref;
    }

    public static Feature createFeature() {
        Feature feature = new Feature(nextId(), new ArrayList<Range>(Arrays.asList(createRange())));

        Names names = new Names();
        names.setShortLabel("enzyme");
        names.setFullName("enzyme");
        feature.setNames(names);

        DbReference primaryRef = createDbReferencePsiMi("MI:0501");
        DbReference secondaryRef = createDbReferencePrimaryRef("14755292");

        FeatureType featureType = new FeatureType();
        featureType.setNames(names);
        featureType.setXref(new Xref(primaryRef, new ArrayList(Arrays.asList(secondaryRef))));

        feature.setFeatureType(featureType);

        feature.getRanges().add(createRange());

        return feature;
    }

    public static Parameter createParameter() {
        Parameter param = new Parameter();
        param.setTerm("temperature of interaction");
        param.setTermAc("MI:0836");
        param.setUnit("kelvin");
        param.setUnitAc("MI:0838");
        param.setFactor(275);
        return param;
    }

    public static Range createRange() {
        RangeStatus rangeStatus = new RangeStatus();

        Names names = new Names();
        names.setShortLabel("certain");

        rangeStatus.setNames(names);

        DbReference rangeDbRef = createDbReferencePsiMi("MI:0335");

        rangeStatus.setXref(new Xref(rangeDbRef));

        Position begin = new Position(1);
        Position end = new Position(5);

        Range range = new Range(rangeStatus, begin, rangeStatus, end);
        return range;
    }

    private static String nextString() {
        return new IntactMockBuilder().randomString(childRandom(5,10));
    }

    private static String nextString(String prefix) {
        return prefix + "_" + nextInt();
    }

    private static int nextInt() {
        return new Random().nextInt(10000);
    }

    private static int nextId() {
        return IdSequenceGenerator.getInstance().nextId();
    }

    private static int childRandom() {
        return childRandom(MIN_CHILDREN, MAX_CHILDREN);
    }

    private static int childRandom(int min, int max) {
        if (min == max) return max;

        return new Random().nextInt(Math.max(1,(max - min))) + min;
    }
}