package uk.ac.ebi.intact.application.hierarchView.highlightment.source;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.view.LabelValueBean;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.simpleGraph.Node;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import org.apache.log4j.Logger;


/**
 * Interface allowing to wrap an highlightment source.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class GoHighlightmentSource extends HighlightmentSource {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * separator of keys, use to create and parse key string.
     */
    private static String KEY_SEPARATOR = ",";
    private String ATTRIBUTE_OPTION_CHILDREN = "CHILDREN";
    private String PROMPT_OPTION_CHILDREN = "With children of the selected GO term";


    /**
     * Return the html code for specific options of the source to integrate int the highlighting form.
     * if the method return null, the source hasn't options.
     *
     * @return the html code for specific options of the source.
     */
    public String getHtmlCodeOption(HttpSession aSession) {
        String htmlCode;
        String check = (String)  aSession.getAttribute (ATTRIBUTE_OPTION_CHILDREN);

        if (check == null) {
            check = "";
        }

        htmlCode = "<INPUT TYPE=\"checkbox\" NAME=\"" + ATTRIBUTE_OPTION_CHILDREN +"\" " +
                   check + " VALUE=\"checked\">" + PROMPT_OPTION_CHILDREN;

        return htmlCode;
    }


    /**
     * Return a collection of keys specific to the selected protein and the current source.
     * e.g. If the source is GO, we will send the collection of GO term owned by the given protein.
     * Those informations are retreived from the Intact database
     *
     * @param aProteinAC a protein identifier (AC)
     * @return a set of keys (this keys are a String) (this Keys are a String[] which contains the GOterm and a description)
     */
    public Collection getKeysFromIntAct (String aProteinAC, HttpSession aSession) {

        Collection result = null;
        Iterator iterator;
        Collection listGOTerm = new ArrayList();
        IntactUserI user = (IntactUserI) aSession.getAttribute (uk.ac.ebi.intact.application.hierarchView.business.Constants.USER_KEY);

        if (null == user) {
            logger.error("No user found in the session, unable to search for GO terms");
            return null;
        }

        try {
            logger.info ("Try to get a list of GO term (from protein AC=" + aProteinAC + ")");
            result = user.getHelper().search ("uk.ac.ebi.intact.model.Protein","ac", aProteinAC);
        } catch (IntactException ie) {
            logger.error ("When trying to get a list of GO", ie);
            return null;
        }

        // no object
        if (result.isEmpty()) return null;

        iterator = result.iterator();
        Interactor interactor = (Interactor) iterator.next();

        // get Xref collection
        Collection xRef = interactor.getXref();
        logger.info(result.size() + "Xref found");
        Iterator xRefIterator = xRef.iterator() ;

        while (xRefIterator.hasNext() ) {
            String[] goterm = new String[2];
            Xref xref = (Xref) xRefIterator.next();

            if ((xref.getCvDatabase().getShortLabel()).equals("GO")) {
                goterm[0] = xref.getPrimaryId();
                goterm[1] = xref.getSecondaryId();
                listGOTerm.add(goterm);
                logger.info (xref.getPrimaryId());
            }
        }

        return listGOTerm;
    } // getKeysFromIntAct


    /**
     * Create a set of protein we must highlight in the graph given in parameter.
     * The protein selection is done according to the source keys stored in the IntactUser.
     * Keys are GO terms, so we select (and highlight) every protein which awned that GO term.
     * If the children option is activated, all proteins which owned a children of the selected
     * GO term are selected.
     *
     * @param aSession the session where to find selected keys.
     * @param aGraph the graph we want to highlight
     * @return a collection of node to highlight
     */
    public Collection proteinToHightlight (HttpSession aSession, InteractionNetwork aGraph) {
        Collection nodeList = new Vector ();

        IntactUserI user = (IntactUserI) aSession.getAttribute(Constants.USER_KEY);
        Collection keys   = user.getKeys();

        // get source option
        String  check = (String) user.getHighlightOption (ATTRIBUTE_OPTION_CHILDREN);

        ArrayList listOfNode = aGraph.getOrderedNodes();
        int size = listOfNode.size();

        for (int i=0 ; i<size ; i++) {
            Node node = (Node) listOfNode.get(i);
            String ac = node.getAc();

            // Search all GoTerm for this ac number
            Collection listGOTerm = this.getKeysFromIntAct (ac, aSession);

            if (listGOTerm!= null && !listGOTerm.isEmpty()) {
                String[] goTermInfo;
                String goTerm;
                Iterator list = listGOTerm.iterator();

                while (list.hasNext()) {
                    Iterator it = keys.iterator();
                    String selectedGOTerm = null;

                    if (it.hasNext()) {
                        selectedGOTerm = (String) it.next();
                    }

                    goTermInfo = new String[2];
                    goTerm = new String();
                    goTermInfo = (String[]) list.next();
                    goTerm = goTermInfo[0];

                    if (selectedGOTerm.equals(goTerm)) {
                        nodeList.add(node);
                        break;
                    }

                    if ((check != null) && (check.equals("checked"))) {
                        while (it.hasNext()) {
                            String newGOTerm = (String) it.next();

                            if (newGOTerm.equals(goTerm)) {
                                nodeList.add(node);
                                break;
                            }
                        }
                        // goterm.isChildrenOf(keys)?? -> if it'OK nodeList.add(node) et break
                    }
                } // while
            }
        } // for

        return nodeList;
    } // proteinToHightlight


    /**
     * Allows to update the session object with parameters' request.
     * These parameters are specific of the implementation.
     *
     * @param aRequest request in which we have to get parameters to save in the session
     * @param aSession session in which we have to save the parameter
     */
    public void saveOptions (HttpServletRequest aRequest, HttpSession aSession) {

        IntactUserI user = (IntactUserI) aSession.getAttribute(Constants.USER_KEY);
        String[] result = aRequest.getParameterValues(ATTRIBUTE_OPTION_CHILDREN);

        if (result != null)
            user.addHighlightOption (ATTRIBUTE_OPTION_CHILDREN, result[0]);
    } // saveOptions


    /**
     * Return a collection of URL corresponding to the selected protein and source
     * Here it produce a list of GO terms and format URLs according to the InGO specification.<br>
     *
     * @param aProteinAC a protein identifier (AC)
     * @return a set of URL pointing on the highlightment source
     */
    public Collection getSourceUrls (String aProteinAC, HttpSession aSession)
         throws IntactException {
        Collection urls = new Vector();

        // get in the Highlightment properties file where is hosted interpro
        Properties props = PropertyLoader.load (StrutsConstants.PROPERTY_FILE_HIGHLIGHTING);
        if (null == props) {
            String msg = "Unable to find the interpro hostname. "+
                         "The properties file '" + StrutsConstants.PROPERTY_FILE_HIGHLIGHTING + "' couldn't be loaded.";
            logger.error (msg);
            throw new IntactException ();
        }

        String hostname = props.getProperty("highlightment.source.GO.hostname");

        if (null == hostname) {
            String msg = "Unable to find the interpro hostname. "+
                         "Check the 'highlightment.source.GO.hostname' property in the '" +
                         StrutsConstants.PROPERTY_FILE_HIGHLIGHTING + "' properties file";
            logger.error (msg);
            throw new IntactException ();
        }

        // Create a collection of label-value object (GOterm, URL to access a nice display in interpro)
        String[] goTermInfo;
        String goTerm, goTermDescription;

        Collection listGOTerm = this.getKeysFromIntAct (aProteinAC, aSession);

        if (listGOTerm != null && !listGOTerm.isEmpty()) {
            Iterator list = listGOTerm.iterator();
            while (list.hasNext()) {
                goTermInfo        = (String[]) list.next();
                goTerm            = goTermInfo[0];
                goTermDescription = goTermInfo[1];
                String url = hostname + "/ingo/ego/DisplayGoTerm?selected=" + goTerm + "&intact=true&format=contentonly";

                // http://web7-node1.ebi.ac.uk:8110/ingo/ego/DisplayGoTerm?selected=GO:0005635,GO:0005637&intact=true&format=contentonly

                urls.add ( new LabelValueBean (goTerm, url, goTermDescription) );
            }
        }

        return urls;
    } // getSourceUrls


    /**
     * Parse the set of key generate by the source and give back a collection of keys.
     *
     * @param someKeys a string which contains some key separates by a character.
     * @return the splitted version of the key string as a collection of String.
     */
    public Collection parseKeys (String someKeys) {
        Collection keys = new Vector ();

        if ((null == someKeys) || (someKeys.length() < 1)) {
            return null;
        }

        StringTokenizer st = new StringTokenizer (someKeys, KEY_SEPARATOR);

        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            keys.add(key);
        }

        return  keys;
    } // parseKeys

} // GoHighlightmentSource

























































































