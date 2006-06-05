/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.framework;

import org.apache.struts.action.*;
import org.apache.log4j.Logger;

//import uk.ac.ebi.intact.application.search.business.IntactUserIF;
//import uk.ac.ebi.intact.application.search.business.IntactServiceIF;
import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SimilarityResultBean;
import uk.ac.ebi.intact.application.intSeq.business.RunSimilaritySearchIF;
import uk.ac.ebi.intact.application.intSeq.business.RunSimilaritySearch;
import uk.ac.ebi.intact.application.intSeq.business.Constants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.util.SearchReplace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Super class for all Intact related action classes.
 * completed by shuet (shuet@ebi.ac.uk) with the <code>ManageBlastResult</code> method.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBaseAction extends Action {

    public static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    private static Properties properties = PropertyLoader.load (Constants.INTSEQ_PROPERTIES);



    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();

    /**
     * returns a value stored in a properties file.
     *
     * @param propName the property name
     * @return the associated value or null if not exists or file not loaded.
     */
    protected String getProperty (String propName) {
        String value = null;

        if (properties != null) {
           value = properties.getProperty(propName);
        } else {
            logger.warn ("Could not load property " + propName + ", " + Constants.INTSEQ_PROPERTIES + " not loaded.");
        }

        return value;
    }

    /**
     * eturns a value stored in a properties file, eventually a default value.
     *
     * @param propName propName the property name
     * @param defaultValue the default value in case the property is not found
     * @return the associated value or a default value if not found or null if the file is not loaded.
     */
    protected String getProperty (String propName, String defaultValue) {
        String value = getProperty(propName);

        if (value == null) {
            return defaultValue;
        }

        return value;
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
        if (!myErrors.isEmpty()) {
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
     * answer the question : "is the error set empty ?"
     */
    protected boolean isEmptyError () {
        return (myErrors.size() == 0);
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
     * @param commLine - the alignment algorithm is run in a server thanks to this command line
     *                   which is defined in the web.xml file.
     * @param perc_base is the minimum percentage identity required to validate the results.
     * @param param is the maximum E Value allowed to validate the results.
     *
     * @return the reusulting collection.

     *
     */
    protected ArrayList ManageBlastResult (String sequence,
                                           String commLine,
                                           String perc_base,
                                           String param)
            throws IntactException {

        //to empty the arraylist from its old request results.
        ArrayList toTransfer = new ArrayList();

        //Calling the business logic class.
        RunSimilaritySearchIF run = null;
        // object which contains another ArrayList of results.
        ArrayList matchList = new ArrayList();

        //constants defined in the web.xml file
        Integer percBase = new Integer(perc_base);
        Double paramTest = new Double (param);

        run = new RunSimilaritySearch (sequence, commLine, paramTest.doubleValue());
        matchList = run.RetrieveParseResult();
        boolean commandExecution = run.GetCommandExecResponse();

        logger.info ("Blast execution sends back: " + commandExecution);
        logger.info ("#results = " + matchList.size());

        // command well done
        if (commandExecution == true && matchList.size() != 0) {
            for (int i = 0; i < matchList.size(); i++) {

                ArrayList theGet = (ArrayList) matchList.get(i);
                int size = theGet.size();

                if (size != 6) { // we need six items to display the table in a good way
                    break;
                }
                else {
                    String id = (String)theGet.get(0);
                    String perc = (String)theGet.get(1);

                    // to test if the percentage is greater than the percentage allowed.
                    Integer thePerc = new Integer(perc);

                    /*
                     *  For a sequence with 47% of similarity,
                     *  Blast would gives us : 47
                     *  Fasta would gives us : 0.47
                     *
                     *  And we parse to get all after '.' ... so it cvould happen that we get
                     *  the result 0 in case Fasta gives 1.00
                     */
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
        } else {
            // error during execution
            return null;
        }
        return toTransfer;
    }

    /**
     * Perform the similarity search operation.<br>
     * Check the protein sequence and similarity search program parameters.
     * The result is stored in the session in order to be red by a JSP.
     * CAUTION:
     * After calling that method you MUST check the content of ActionErrors like :
     * <pre>
     *      if (false == isEmptyError()) {
     *         // FOrward to an error page.
     *         return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
     *      }
     * </pre>
     *
     * @param sequence the protein sequence to work on.
     * @param request HttpRequest to record Errors and using the session.
     */
    protected void doSimilaritySearch (String sequence,
                                            HttpServletRequest request) {

        if (sequence == null || sequence.equals("")) {
            addError ("error.srs.noseq");
            saveErrors(request);
            return;
        }

        // get similarity program parameters
        String theBlastCommand = getProperty ("intSeq.alignmentProgram");
        logger.info("theBlastCommand = " + theBlastCommand);

        String minIdentityPercentage = getProperty ("intSeq.minIdentityPercentage");
        logger.info("minIdentityPercentage = " + minIdentityPercentage);

        String maxEValue = getProperty ("intSeq.maxEValue");
        logger.info("maxEValue = " + maxEValue);

        if (theBlastCommand == null || minIdentityPercentage == null || maxEValue == null) {
            addError ("error.similaritySearch.parameter");
            saveErrors(request);
            return;
        }

        // through the RunSimilaritySearch class
        ArrayList dataSetTransfered = null;
        try {
            dataSetTransfered = ManageBlastResult (sequence,
                                                   theBlastCommand,
                                                   minIdentityPercentage,
                                                   maxEValue);
        } catch (IntactException e) {
            addError ("error.blast.processing", e.getMessage());
            saveErrors(request);
            return;
        }

        if (dataSetTransfered == null || dataSetTransfered.isEmpty()) {
            addError("error.command.failed");
            saveErrors(request);
            return;
        }

        // store the result in the session "similarityList" will be recognized in the jsp page
        request.getSession().setAttribute("similarityList", dataSetTransfered);
    }


    protected String getProteinsSrsUrl (String searchString) {

        String srsProteinSearchUrl = getProperty("intSeq.SRS.getProteins.url");
        logger.info ("URL: " + srsProteinSearchUrl);
        // replace ${SEARCH_STRING} by the search string
        srsProteinSearchUrl = SearchReplace.replace (srsProteinSearchUrl, "${SEARCH_STRING}", searchString);
        logger.info( "After replacement: URL=" + srsProteinSearchUrl );

        return srsProteinSearchUrl;
    }

    protected String getSequenceSrsUrl (String proteinAc) {

        String srsSequenceSearchUrl = getProperty("intSeq.SRS.getSequence.url");
        logger.info ("URL: " + srsSequenceSearchUrl);
        // replace ${PROTEIN_AC} by the search string
        String srsProteinSearchUrl = SearchReplace.replace (srsSequenceSearchUrl, "${PROTEIN_AC}", proteinAc);
        logger.info( "After replacement: URL=" + srsProteinSearchUrl );

        return srsProteinSearchUrl;
    }

}