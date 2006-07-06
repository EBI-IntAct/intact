package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SingleResultViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.TooLargeViewBean;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.application.commons.search.SearchClass;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Provides the actions to handle the case if the retriving resultset from the searchaction is too big. The Action
 * creates a basic statistic which gives an overview over the resultsset. this statistics will be shown in the
 * web-interface.
 *
 * @author Michael Kleen (mkleen@ebi.ac.uk)
 * @version $Id$
 */
public class TooLargeAction extends IntactBaseAction {

    /**
     * Counts the complete result information in 4 different categories this is necessary because all controlled
     * vocabulary terms should count in the same category.
     *
     * @param mapping  The ActionMapping used to select this instance
     * @param form     The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @return an ActionForward object
     */
    public ActionForward execute( ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) {

        logger.info( "tooLarge action: the resultset contains to many objects" );

        //TODO use here a iteratable map instead

        // get the resultinfo from the initial request from the search action
        final Map<String,Integer> resultInfo = ( (Map<String,Integer>) request.getAttribute( SearchConstants.RESULT_INFO ) );
        logger.info( resultInfo );
        final Collection<String> someKeys = resultInfo.keySet();

        int cvCount = 0;
        int proteinCount = 0;
        int nucleicAcidCount = 0;
        int experimentCount = 0;
        int interactionCount = 0;

        // count for any type of searchable objects in the resultset to generate the statistic
        // this is done by creating from the classname a class and check then for classtype

        for (String className : someKeys)
        {
            logger.info("tooLarge action: searching for class" + className);

            try
            {
                if (className.equals("uk.ac.ebi.intact.model.SmallMoleculeImpl"))
                {
                    // TODO remove this check when the new type is implemented
                    logger.warn("Found SmallMolecule/s. This is not implemented yet, so ignoring it");
                    continue;
                }

                Class clazz = Class.forName(className);

                SearchClass searchClass = SearchClass.valueOfMappedClass(clazz);

                if (searchClass == SearchClass.PROTEIN)
                {
                    proteinCount += resultInfo.get(className);
                }
                 if (searchClass == SearchClass.NUCLEIC_ACID)
                {
                    nucleicAcidCount += resultInfo.get(className);
                }
                else if (searchClass == SearchClass.EXPERIMENT)
                {
                    experimentCount += resultInfo.get(className);
                }
                else if (searchClass == SearchClass.INTERACTION)
                {
                    interactionCount += resultInfo.get(className);
                }
                else if (searchClass.isCvObjectSubclass())
                {
                    cvCount += resultInfo.get(className);
                }
            }
            catch (ClassNotFoundException e)
            {

                // we got a class which is not part of the the searchable classes.
                logger.info("tooLarge action: the resultset contains to an object which is no " +
                        "assignable from an intactType: "+className);
                logger.info("tooLarge action: forward to an errorpage");
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);

            }
        } // for

        // get the helplink count the results and create with them  a couple of viewbeans for the jsp

        final String relativeHelpLink = getServlet().getServletContext().getInitParameter( "helpLink" );
        final String relativePath = UrlUtil.absolutePathWithoutContext(request);
        final String helpLink = relativePath.concat( relativeHelpLink );

        final String appPath = getServlet().getServletContext().getInitParameter( "searchLink" );
        final String searchURL = request.getContextPath().concat( appPath );
        HttpSession session = super.getSession( request );
        String query = (String) session.getAttribute( SearchConstants.SEARCH_CRITERIA );


        TooLargeViewBean tooLargeViewBean = new TooLargeViewBean();

        if ( experimentCount > 0 ) {
            tooLargeViewBean.add( new SingleResultViewBean( SearchClass.EXPERIMENT.getShortName(),
                                                            experimentCount,
                                                            helpLink, searchURL, query ) );
        }

        if ( interactionCount > 0 ) {
            tooLargeViewBean.add( new SingleResultViewBean( SearchClass.INTERACTION.getShortName(),
                                                            interactionCount,
                                                            helpLink, searchURL, query ) );
        }

        if ( proteinCount > 0 ) {
            tooLargeViewBean.add( new SingleResultViewBean( SearchClass.PROTEIN.getShortName(),
                                                            proteinCount,
                                                            helpLink, searchURL, query ) );
        }

        if ( nucleicAcidCount > 0 ) {
            tooLargeViewBean.add( new SingleResultViewBean( SearchClass.NUCLEIC_ACID.getShortName(),
                                                            nucleicAcidCount,
                                                            helpLink, searchURL, query ) );
        }

        if ( cvCount > 0 ) {
            tooLargeViewBean.add( new SingleResultViewBean( SearchClass.CV_OBJECT.getShortName(), cvCount,
                                                            helpLink, searchURL, query ) );
        }

        // add the viewbean to the request and forward to the jsp
        request.setAttribute( SearchConstants.VIEW_BEAN, tooLargeViewBean );
        return mapping.findForward( SearchConstants.FORWARD_RESULTS );
    }
}