/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.messages;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2007</pre>
 */
public class GeneralMessage {

    public static final int HIGH_LEVEL = 2;
    public static final int AVERAGE_LEVEL = 1;
    public static final int LOW_LEVEL = 0;

    public static final String NO_IDEA = "";
    /*
    The description of the rule.
    ex : An experiment should always be linked to an interaction.
     */
    private String description;
    /*
    The level of gravity of the rule.
     */
    private int level;
    /*
    The proposed solution if the rule is transgressed
     */
    private String proposedSolution;
    /*
    The out-low object
     */
    private IntactObject outLow;

    public GeneralMessage(String description, int level, String proposedSolution, IntactObject outLow) {
        this.description = description;
        this.level = level;
        this.proposedSolution = proposedSolution;
        this.outLow = outLow;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProposedSolution() {
        return proposedSolution;
    }

    public void setProposedSolution(String proposedSolution) {
        this.proposedSolution = proposedSolution;
    }


    public IntactObject getOutLow() {
        return outLow;
    }

    public void setOutLow(IntactObject outLow) {
        this.outLow = outLow;
    }
}