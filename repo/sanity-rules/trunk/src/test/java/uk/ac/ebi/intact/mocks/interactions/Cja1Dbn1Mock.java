/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.interactions;

import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.mocks.components.P08050ComponentMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.bioSources.InVitroMock;
import uk.ac.ebi.intact.mocks.cvInteractionTypes.PhysicalInteractionMock;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class Cja1Dbn1Mock {
    private static final String SHORTLABEL = "cja1-dbn1-1";
    private static final String FULLNAME = "Pull down of  human  connexin 43 and mouse endogenous drebrin E";
    

    public static Interaction getMock(Experiment experiment){
        Interaction interaction = new InteractionImpl();

        interaction = (Interaction) IntactObjectSetter.setBasicObject(interaction);


        interaction.setOwner(InstitutionMock.getMock());
        interaction.setShortLabel(SHORTLABEL);
        interaction.setFullName(FULLNAME);
        interaction.setCvInteractionType(PhysicalInteractionMock.getMock());
        interaction.setBioSource(InVitroMock.getMock());

        Collection<Component> components = new ArrayList<Component>();
        components.add(P08050ComponentMock.getMock(interaction));
        components.add(Q9QXS6ComponentMock.getMock(interaction));
        interaction.setComponents(components);

        Collection<Experiment> experiments = new ArrayList<Experiment>();
        experiments.add(experiment);
        interaction.setExperiments(experiments);

        return interaction;
    }
}