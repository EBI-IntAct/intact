/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;

/**
 * This form captures the user action of the similaritySearch.jsp.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public final class SequenceSimilarityForm extends ActionForm {


    /**
     * The protein sequence captured by the user in the provided textarea of similaritySearch.jsp
     * This sequence must be in Fasta format.
     */
    private String sequence = "";

    public void setSequence (String sequence) {
        this.sequence = sequence;
    }

    public String getSequence (){
         return (this.sequence);
    }

            //--------- PUBLIC METHODS --------------//

    /* validate the whole request
        * except if the sequence textarea is empty.
        * except if the sequence is smaller than 20 amino acids long
        * except if the sequence isn't in the Fasta format.
        In these cases, this form should return an <code>ActionErrors<code>
        with the appropriate messages in the jsp page*/

    public ActionErrors validate (ActionMapping map, HttpServletRequest query)  {

        ActionErrors errors = new ActionErrors();

        if (sequence == null || sequence.length() < 1) {
            errors.add("idseqerror", new ActionError("error.sequence.required"));
        }
        else if (sequence.length() < SeqIdConstants.SMALLEST_LENGHT_PROTEIN) {
            errors.add("idseqerror", new ActionError("error.sequence.longer"));
        }
        else if ( (sequence.startsWith(">") == false) || (sequence.startsWith(">>") == true)) {
            errors.add("idseqerror", new ActionError("error.sequence.format"));
        }

        /*if (VerifProtSeq(sequence) == false) {
            errors.add("errors", new ActionError("error.sequence.aa"));
        } */

        if (errors.size() > 0) {
	            // delete properties of the bean, so can't be saved in the session.
	        reset(map, query);
	    }

        return errors;

    }

    /*
    */
    public void reset (ActionMapping map, HttpServletRequest query) {
        this.sequence = null;
    }



    /*

        Buffer only contains admitted characters in an amino acid sequence, i.e 20 letters corresponding
        to the 20 amino acid symbols
    */
    public boolean VerifProtSeq (String sequence) {

        StringBuffer sb = new StringBuffer ("ACDEFGHIKLMNPQRSTVYW");
        char a, b;
        boolean egal = true;
        int i, j;
        for (i = 0; i < sequence.length(); i++) {
            b = sequence.charAt(i);


            for (j = 0; j < sb.capacity(); j++) {
                a = sb.charAt(j);
                if (b != a) {
                    continue;
                }
                else if ((b == a) && (j < 20)) {
                    break;
                }
                else if ((b != a) && (j >= 20)) {
                    egal = false;
                    return egal;
                }

            }
        }
        return egal;
    }
}

