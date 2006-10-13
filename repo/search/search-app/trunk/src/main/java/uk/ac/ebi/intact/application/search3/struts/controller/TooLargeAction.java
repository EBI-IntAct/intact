package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SingleResultViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.TooLargeViewBean;
import uk.ac.ebi.intact.model.SmallMoleculeImpl;
import uk.ac.ebi.intact.searchengine.SearchClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
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

    private static final Log logger = LogFactory.getLog(TooLargeAction.class);

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

        logger.debug( "tooLarge action: the resultset contains to many objects" );

        //TODO use here a iteratable map instead

        // get the resultinfo from the initial request from the search action
        final Map<?,Integer> resultInfo = ( Map ) request.getAttribute( SearchConstants.RESULT_INFO );
        logger.debug( "Result info: "+ resultInfo );

        final Collection someKeys = resultInfo.keySet();

        int cvCount = 0;
        int proteinCount = 0;
        int nucleicAcidCount = 0;
        int experimentCount = 0;
        int interactionCount = 0;

        // count for any type of searchable objects in the resultset to generate the statistic
        // this is done by creating from the classname a class and check then for classtype

        for (Object objClass : someKeys)
        {
            Class clazz;

            if (objClass instanceof Class)
            {
                clazz = (Class)objClass;
            }
            else
            {
                try
                {
                    clazz = Class.forName(objClass.toString());
                }
                catch (ClassNotFoundException e)
                {
                    logger.error("tooLarge action: the resultset contains to an object which is no " +
                        "assignable from an intactType: "+objClass.toString());
                    return mapping.findForward(SearchConstants.FORWARD_FAILURE);
                }
            }

            String className = clazz.getName();

            logger.debug("tooLarge action: searching for class " + className);

            if (className.equals(SmallMoleculeImpl.class.getName()))
            {
                // TODO remove this check when the new type is implemented
                logger.warn("Found SmallMolecule/s. This is not implemented yet, so ignoring it");
                continue;
            }

            SearchClass searchClass = SearchClass.valueOfMappedClass(clazz);

            if (searchClass == SearchClass.PROTEIN)
            {
                proteinCount += resultInfo.get(objClass);
            }
            if (searchClass == SearchClass.NUCLEIC_ACID)
            {
                nucleicAcidCount += resultInfo.get(objClass);
            }
            else if (searchClass == SearchClass.EXPERIMENT)
            {
                experimentCount += resultInfo.get(objClass);
            }
            else if (searchClass == SearchClass.INTERACTION)
            {
                interactionCount += resultInfo.get(objClass);
            }
            else if (searchClass.isCvObjectSubclass())
            {
                cvCount += resultInfo.get(className);
            }
        } // for

        // get the helplink count the results and create with them  a couple of viewbeans for the jsp

        final String relativeHelpLink = getServlet().getServletContext().getInitParameter( "helpLink" );
        final String relativePath = UrlUtil.absolutePathWithoutContext(request);
        final String helpLink = relativePath.concat( relativeHelpLink );

        final String appPath = getServlet().getServletContext().getInitParameter( "searchLink" );
        final String searchURL = request.getContextPath().concat( appPath );
        HttpSession session = super.getSession( request );

        Object objQuery = session.getAttribute( SearchConstants.SEARCH_CRITERIA );

        if (objQuery == null) throw new NullPointerException("Attribute: searchCriteria");

        String query = objQuery.toString();


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