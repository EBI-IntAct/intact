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
package uk.ac.ebi.intact.kickstart;

import psidev.psi.mi.xml.model.*;
import psidev.psi.mi.xml.model.Organism;
import psidev.psi.mi.xml.model.Interactor;
import psidev.psi.mi.xml.PsimiXmlWriter;

import java.io.FileWriter;
import java.util.Date;

/**
 * Example on how to create a PSI-XML file using the API
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CreatePsiXmlFromScratch {

    private static int idCount = 0;

    public static void main(String[] args) throws Exception {
        // An EntrySet contains all the information
        EntrySet entrySet = createEntrySet();

        // write the EntrySet to the console (or it could to a file by changing the parameters in the write method)
        PsimiXmlWriter writer = new PsimiXmlWriter();

        writer.write(entrySet, new FileWriter("f:/projectes/temp/psi.xml"));
    }

    private static EntrySet createEntrySet() {
        // create EntrySet for the schema 2.5.3
        EntrySet entrySet = new EntrySet(2,5,3);

        // create an entry
        Entry entry = new Entry();
        entrySet.getEntries().add(entry);

        // source details for the entry
        Source source = new Source();
        source.setNames(createNames("intact", "European Bioinformatics Institute"));
        source.setReleaseDate(new Date());
        entry.setSource(source);

        // create an interaction
        Interaction interaction = createAnInteraction();
        entry.getInteractions().add(interaction);

        return entrySet;
    }

    private static Interaction createAnInteraction() {
        Interaction interaction = new Interaction();
        interaction.setId(nextId());

        // interaction type
        InteractionType interactionType = new InteractionType();
        interactionType.setNames(createNames("physical interaction", null));
        Xref interactionTypeXref = new Xref(createDbReference("MI:0218", "MI:0356", "identity", "MI:0488", "psi-mi"));
        interactionType.setXref(interactionTypeXref);

        interaction.getInteractionTypes().add(interactionType);

        // create some participants of the interaction
        Participant participantA = new Participant();
        participantA.setId(nextId());
        participantA.setInteractor(createInteractor("elne_human", "Leukocyte elastase precursor", "P08246"));

        Participant participantB = new Participant();
        participantB.setId(nextId());
        participantB.setInteractor(createInteractor("grab_human", "Granzyme B precursor", "P10144"));

        interaction.getParticipants().add(participantA);
        interaction.getParticipants().add(participantB);

        // Experiment details for the interaction
        ExperimentDescription exp = createExperiment("myexp-2007-1", "Study of this and that", "1234567");
        interaction.getExperiments().add(exp);

        return interaction;
    }

    private static ExperimentDescription createExperiment(String label, String fullName, String pmid) {
        ExperimentDescription exp = new ExperimentDescription();
        exp.setId(nextId());
        exp.setNames(createNames(label, fullName));

        // cross reference to pubmed, for instance
        Xref xref = new Xref(createDbReference(pmid, "MI:0358", "primary-reference", "MI:0446", "pubmed"));
        Bibref bibref = new Bibref(xref);
        exp.setBibref(bibref);

        // host organism
        Organism organism = createOrganism(9606, "human");
        exp.getHostOrganisms().add(organism);

        // interaction detection method
        InteractionDetectionMethod detMethod = new InteractionDetectionMethod();
        detMethod.setNames(createNames("x-ray crystallography", null));
        Xref detMethodXref = new Xref(createDbReference("MI:0114", "MI:0356", "identity", "MI:0488", "psi-mi"));
        detMethod.setXref(detMethodXref);

        exp.setInteractionDetectionMethod(detMethod);

        // participant identification method
        ParticipantIdentificationMethod partMethod = new ParticipantIdentificationMethod();
        partMethod.setNames(createNames("mass spectrometry", null));
        Xref partMethodXref = new Xref(createDbReference("MI:0114", "MI:0356", "identity", "MI:0488", "psi-mi"));
        partMethod.setXref(partMethodXref);

        exp.setParticipantIdentificationMethod(partMethod);

        return exp;
    }

    private static Interactor createInteractor(String shortLabel, String fullName, String uniprotId) {
        Interactor prot = new Interactor();
        prot.setId(nextId());
        prot.setNames(createNames(shortLabel, fullName));

        // xref - primary ref to uniprot, for instance
        Xref xref = new Xref();
        xref.setPrimaryRef(createDbReference(uniprotId, "MI:0356", "identity", "MI:0486", "uniprotkb"));
        prot.setXref(xref);

        // interactor type - protein for this example - use the PSI MI term as the identity
        InteractorType interactorType = new InteractorType();
        interactorType.setNames(createNames("protein", "protein"));
        Xref intTypeXref = new Xref();
        intTypeXref.setPrimaryRef(createDbReference("MI:0326", "MI:0356", "identity", "MI:0488", "psi-mi"));
        interactorType.setXref(intTypeXref);

        prot.setInteractorType(interactorType);

        // assign an organism
        Organism organism = createOrganism(9606, "human");
        prot.setOrganism(organism);

        return prot;
    }

    private static Organism createOrganism(int taxId, String shortLabel) {
        Organism organism = new Organism();
        organism.setNames(createNames(shortLabel, shortLabel));
        organism.setNcbiTaxId(taxId);
        return organism;
    }

    private static Names createNames(String shortLabel, String fullName) {
        Names names = new Names();
        names.setShortLabel(shortLabel);
        names.setFullName(fullName);
        return names;
    }

    private static DbReference createDbReference(String id, String refTypeAc, String refType, String dbAc, String db) {
        DbReference dbRef = new DbReference();
        dbRef.setId(id);
        dbRef.setRefTypeAc(refTypeAc);
        dbRef.setRefType(refType);
        dbRef.setDbAc(dbAc);
        dbRef.setDb(db);
        return dbRef;
    }

    private static int nextId() {
        idCount++;
        return idCount;
    }
}
