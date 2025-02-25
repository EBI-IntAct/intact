/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;

import uk.ac.ebi.intact.business.IntactException;

import java.util.ArrayList;

/**
 * Interface which provides methods specific to check if the multiple alignment command
 * has been well executed, and to retrieve the result list, made of arrays with:
 *              * the accession number
 *              * the percentage identity
 *              * the start of the query fragment
 *              * the start of the subject fragment
 *              * the end of the query fragment
 *              * the end of the subject fragment
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public interface RunSimilaritySearchIF {

    /**
     * keep inform the action whether the command line was executed well or not.
     *
     * @return the boolean answer.
     */
    public boolean GetCommandExecResponse ();

    /**
     * return the file which contains all Blast results from the user's request.
     *
     * @return the results in a list of list.
     */
    public ArrayList RetrieveParseResult () throws IntactException ;

}
