/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseForm;

/**
 * The form which captures user action (Submit, Cancel or Delete).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditForm extends IntactBaseForm {

    /**
     * True if the delete option is selected.
     *
     * <pre>
     * post: return = true if super.getAction() = 'Delete'
     * </pre>
     */
    public boolean isDeleted() {
        return super.getAction().equals("Delete");
    }
}
