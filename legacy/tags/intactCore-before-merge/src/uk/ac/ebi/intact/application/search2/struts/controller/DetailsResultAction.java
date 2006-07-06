/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.ViewBeanFactory;

import java.util.Collection;

/**
 * Performs the details view for Proteins.
 *
 * @author IntAct Team
 * @version $Id$
 */
public class DetailsResultAction extends AbstractResulltAction {

    ///////////////////////////////////
    // Abstract methods implementation
    ///////////////////////////////////

    protected AbstractViewBean getAbstractViewBean ( Collection results, IntactUserIF user, String contextPath ) {

        logger.info( "details action: building view beans..." );
        return ViewBeanFactory.getInstance().getDetailsViewBean ( results, user.getHelpLink(), contextPath );
    }
}
