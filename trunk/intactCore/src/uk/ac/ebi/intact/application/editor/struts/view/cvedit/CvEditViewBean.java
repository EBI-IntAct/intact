/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.cvedit;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;

/**
 * The CV edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditViewBean extends AbstractEditViewBean {

    // Override to provide the editory type.
    public String getEditorType() {
        return EditViewBeanFactory.CV_EDITOR;
    }
}
