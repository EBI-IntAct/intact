/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.util.Collection;

/**
 * Performs the single view for Proteins.
 *
 * @author IntAct Team
 * @version $Id$
 */
public class SingleResultAction extends AbstractResulltAction {

    ///////////////////////////////////
    // Abstract methods implementation
    ///////////////////////////////////

    protected AbstractViewBean getAbstractViewBean ( Collection results, IntactUserIF user, String contextPath ) {

        super.log( "single action: building view beans..." );
        AnnotatedObject firstItem = (AnnotatedObject) results.iterator().next();
        return ViewBeanFactory.getInstance().getSingleViewBean ( firstItem, user.getHelpLink(), contextPath );
    }
}
