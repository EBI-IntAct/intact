/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;

/**
 * This class holds the data from the searching a CV object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SearchForm extends ActionForm {

    /**
     * The AcNumber number.
     */
    private String myAcNumber;

    /**
     * The short label.
     */
    private String myLabel;

    /**
     * The action (search by AcNumber or Label). Empty if no action was taken.
     */
    private String myAction = "";

    /**
     * Sets the AcNumber number.
     * @param ac the accession number.
     */
    public void setAcNumber(String ac) {
        myAcNumber = ac;
    }

    /**
     * Returns the Accession Number.
     * @return the accession number.
     */
    public String getAcNumber() {
        return myAcNumber;
    }

    /**
     * Sets the short label.
     * @param label the short label.
     */
    public void setLabel(String label) {
        myLabel = label;
    }

    /**
     * Return the label.
     * @return the short label.
     */
    public String getLabel() {
        return myLabel;
    }

    /**
     * Sets the action.
     * @param action the action for the form. If this contains the word
     * 'AC' then the search is by AC otherwise the search is by label.
     */
    public void setAction(String action) {
        //myAction = action;
        if (action.indexOf("AC") != -1) {
            myAction = WebIntactConstants.SEARCH_BY_AC;
        }
        else if (action.indexOf("Label") != -1) {
            myAction = WebIntactConstants.SEARCH_BY_LABEL;
        }
        else if (action.indexOf("Create") != -1) {
            myAction = WebIntactConstants.CREATE_NEW;
        }
    }

    /**
     * True if searching by AC.
     */
    public boolean searchByAc() {
        return myAction.equals(WebIntactConstants.SEARCH_BY_AC);
    }


    /**
     * True if searching by Short Label.
     */
    public boolean searchByLabel() {
        return myAction.equals(WebIntactConstants.SEARCH_BY_LABEL);
    }

    /**
     * True if create new button is pressed.
     */
    public boolean createNew() {
        return myAction.equals(WebIntactConstants.CREATE_NEW);
    }

    /**
     * Return the action.
     * @return the action.
     */
    public String getAction(){
        return myAction;
    }

    /**
     *  this method of the <code>Actionform</code> class should be overriden
     * in subclasses to ensure Bean properties can be reset before they are re-populated
     * in subsequent requests
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        myAcNumber = null;
        myLabel = null;
        myAction = null;
    }
}
