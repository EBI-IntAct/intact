/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.framework;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
//import org.apache.commons.lang.exception.ExceptionUtils;

import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.business.IntactServiceIF;
import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SimilarityResultBean;
import uk.ac.ebi.intact.application.intSeq.business.RunSimilaritySearchIF;
import uk.ac.ebi.intact.application.intSeq.business.RunSimilaritySearch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Super class for all Intact related action classes.
 * completed by shuet (shuet@ebi.ac.uk) with the <code>ManageBlastResult</code> method.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBaseAction extends Action {

    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();

    private ArrayList toTransfer = new ArrayList();

    /**
     * Returns the only instance of Intact Service instance.
     * @return only instance of the <code>IntactServiceImpl</code> class.
     */
    protected IntactServiceIF getIntactService() {
        IntactServiceIF service = (IntactServiceIF)
            getApplicationObject(SeqIdConstants.INTACT_SERVICE);
        return service;
    }

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in
     * <code>session</code>
     */
    protected IntactUserIF getIntactUser(HttpSession session) {
        IntactUserIF service = (IntactUserIF)
            session.getAttribute(SeqIdConstants.INTACT_USER);
        return service;
    }

    /**
     * Convenience method that logs for agiven message.
     * @param message string that describes the error or exception
     */
    protected void log(String message) {
       if (super.servlet.getDebug() >= 1)
           super.servlet.log(message);
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request. Null is returned if there
     * is no session associated with <code>request</code>.
     */
    protected HttpSession getSession(HttpServletRequest request) {
        // Don't create a new session.
        return request.getSession(false);
    }

    /**
     * Clear error container.
     */
    protected void clearErrors() {
        if (!myErrors.empty()) {
            myErrors.clear();
        }
    }

    /**
     * Adds an error with given key.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key) {
        myErrors.add(INTACT_ERROR, new ActionError(key));
    }

    /**
     * Adds an error with given key and value.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     * @param value the value to substitute for the first place holder in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key, String value) {
        myErrors.add(INTACT_ERROR, new ActionError(key, value));
    }

    /**
     * Saves the errors in given request for <struts:errors> tag.
     *
     * @param request the request to save errors.
     */
    protected void saveErrors(HttpServletRequest request) {
        super.saveErrors(request, myErrors);
    }


    /**
     *
     * ManageBlastResult can be called from different action classes, to manage the execution
     * of the command line provided and to retrieve alignment results from the parsing.
     *
     * @param sequence which is aligned against sequences of the whole IntAct Database.
     *        commLine - the alignment algorithm is run in a server thanks to this command line
     *                   which is defined in the web.xml file.
     *        perc_base is the minimum percentage identity required to validate the results.
     *        param is the maximum E Value allowed to validate the results.
     *
     * @return boolean to know if the command is executed or not.

     *
     */
    protected boolean ManageBlastResult (String sequence, String commLine, String perc_base, String param) {

                //to empty the arraylist from its old request results.
            toTransfer.clear();
                 //Calling the business logic class.
            RunSimilaritySearchIF run = null;
               // object which contains another ArrayList of results.
            ArrayList matchList = new ArrayList();

                //constants defined in the web.xml file
            Integer percBase = new Integer(perc_base);
            Double paramTest = new Double (param);

           //try {
               run = new RunSimilaritySearch(sequence, commLine, paramTest.doubleValue());
               matchList = run.RetrieveParseResult();
               boolean commandExecution = run.GetCommandExecResponse();

                    // command well done
               if (commandExecution == true && matchList.size() != 0) {
                   for (int i = 0; i < matchList.size(); i++) {

                       ArrayList theGet = (ArrayList)matchList.get(i);
                       int size = theGet.size();

                       if (size != 6) { // we need six items to display the table in a good way
                           break;
                       }
                       else {
                           String id = (String)theGet.get(0);
                           String perc = (String)theGet.get(1);

                                // to test if the percentage is greater than the percentage allowed.
                           Integer thePerc = new Integer(perc);

                                /* the percentage identity can't be 0 %. If it is, that's means we are in
                                the Fasta program with 1.00 = 100 % !*/
                           if (thePerc.intValue() == 0) {
                                perc = "100";
                           }
                                // the percentage identity must be greater than a value choose by the biologist.
                           if ( (thePerc.intValue() >= percBase.intValue()) ) {

                                Integer theBegQuery = new Integer((String)theGet.get(2));
                                int begQ = theBegQuery.intValue();
                                Integer theEndQuery = new Integer((String)theGet.get(3));
                                int endQ = theEndQuery.intValue();

                                Integer theBegSubj = new Integer((String)theGet.get(4));
                                int begSub = theBegSubj.intValue();
                                Integer theEndSubj = new Integer((String)theGet.get(5));
                                int endSub = theEndSubj.intValue();

                                    // store these result in a Bean, one for each session
                                toTransfer.add(new SimilarityResultBean (id, perc, begQ, endQ, begSub, endSub));
                           }

                           theGet.clear();
                       }
                   }

                   if (toTransfer.isEmpty() == true) {
                        toTransfer.add(new SimilarityResultBean ("",
                               "no match greater than " + percBase.toString() + " % for an available EValue",
                                0, 0, 0, 0));
                   }

               }
               return commandExecution;

           //}
           //catch (BlastException e) {
           //    this.log(ExceptionUtils.getStackTrace(e));
           //    return false;
           //}
    }



    /**
     * A method which retrieves the similarity table full by the fasta program results
     * @return the interesting arraylist
     */
    protected ArrayList getToTransfer () {
        return (this.toTransfer);
    }


    // Helper methods.

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    private Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }


}