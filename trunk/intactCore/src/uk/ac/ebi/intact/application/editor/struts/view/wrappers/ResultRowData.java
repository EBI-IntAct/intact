/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.wrappers;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * An instance of this class contains information for the display library to display
 * results from a search page.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultRowData {

    private String myAc;
    private String myShortLabel;
    private String myFullName;
    private String myType;

    public ResultRowData(AnnotatedObject annobj) {
        myAc = annobj.getAc();
        myShortLabel = annobj.getShortLabel();
        myFullName = annobj.getFullName();
        myType = IntactHelper.getDisplayableClassName(annobj);
    }

    // Getter methods.

    public String getAc() {
        return myAc;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getFullName() {
        return myFullName;
    }

    public String getType() {
        return myType;
    }
}
