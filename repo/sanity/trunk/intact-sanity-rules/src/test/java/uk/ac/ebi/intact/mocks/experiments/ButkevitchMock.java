/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.experiments;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.PrimaryRefMock;
import uk.ac.ebi.intact.mocks.cvTopics.AuthorListMock;
import uk.ac.ebi.intact.mocks.cvTopics.AcceptedMock;
import uk.ac.ebi.intact.mocks.cvInteractions.CoSedimentationMock;
import uk.ac.ebi.intact.mocks.cvIdentifications.PredeterminedMock;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.mocks.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ButkevitchMock {
    private static final String SHORTLABEL = "butkevich-2004-1";
    private static final String FULLNAME = "Drebrin is a novel connexin-43 binding partner that links gap junctions to the submembrane cytoskeleton.";
    private static final String AUTHOR_LIST = "Butkevich E., Hulsmann S., Wenzel D., Shirao T., Duden R., Majoul I.";

    public static Experiment getMock(){
        Experiment experiment = new Experiment(InstitutionMock.getMock(),SHORTLABEL, BioSourceMock.getMock());

        experiment = (Experiment) IntactObjectSetter.setBasicObject(experiment);

        
        ExperimentXref xref = XrefMock.getMock(ExperimentXref.class, PubmedMock.getMock(), PrimaryRefMock.getMock(), "15084279");
        experiment.addXref(xref);

        Annotation authorList = AnnotationMock.getMock(AuthorListMock.getMock(), AUTHOR_LIST);
        experiment.addAnnotation(authorList);

        Annotation accepted = new Annotation(InstitutionMock.getMock(), AcceptedMock.getMock(),"by JYOTI, 09-03-07");
        experiment.addAnnotation(accepted);

        experiment.setCvInteraction(CoSedimentationMock.getMock());
        experiment.setCvIdentification(PredeterminedMock.getMock());

        experiment.setFullName(FULLNAME);

        Interaction interaction = Cja1Dbn1Mock.getMock(experiment);
        experiment.addInteraction(interaction);

        return experiment;
    }
}