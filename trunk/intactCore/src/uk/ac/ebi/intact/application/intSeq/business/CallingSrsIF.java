/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;


import java.util.ArrayList;


/**
 * Interface which provides methods specific to a user request,
 * to return SRS results from this query.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public interface CallingSrsIF {

    /**
     * check if the protein topic still corresponds to an Intact entry.
     * @return the boolean answer.
     */
    public boolean GetBooleanIntactId ();

    /**
     * request result: return a list of accession numbers and its own description.
     * @return arraylist of arraylist (size 2).
     */
    public ArrayList RetrieveAccDes ();

    /**
     * return the sequence, which allows to run a multi-alignment algorithm (like Fasta or Blast).
     * @return string sequence in the Fasta format.
     */
    public String GetSequenceFasta();
}

