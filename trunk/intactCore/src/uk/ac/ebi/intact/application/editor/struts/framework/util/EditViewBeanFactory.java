/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import java.util.Map;
import java.util.HashMap;

import uk.ac.ebi.intact.application.editor.struts.view.cvedit.CvEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.biosource.BioSourceEditViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvObject;

/**
 * The factory class to create edit view beans.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditViewBeanFactory {

    /**
     * Maps: name -> views. Cache for view beans. It is safe to reuse the view
     *  beans as only one can be used at any time. We cache them to reuse
     *  rather than creating a new instance for each selection.
     */
    private Map nameToView = new HashMap();

    /**
     * BioSource editor is stored under this key.
     */
    private static final String BIOSRC_EDITOR = "biosource";

    /**
     * CV editor is stored under this key.
     */
    private static final String CV_EDITOR = "cv";

    /**
     * Factory method to return an instance of an Edit view bean.
     * @param annot the Annotated object to initialize the edit view bean.
     * @return an instance of <code>AbstractEditViewBean</code>; the type to return
     * is determined by <code>annot</code>. This method does not create new
     * instances of beans; instead it recyles them.
     */
    public AbstractEditViewBean factory(AnnotatedObject annot) {
        // The view bean to return.
        AbstractEditViewBean viewbean;
        if (annot.getClass().equals(BioSource.class)) {
            viewbean = getBioSourceEditor((BioSource) annot);
//            System.out.println("Returning a biosource instance");
        }
        else {
            // Assume it is an CV object.
            viewbean = getCvEditor((CvObject) annot);
//            System.out.println("Returning a cv instance");
        }
        return viewbean;
    }

    // Helper methods.

    private AbstractEditViewBean getBioSourceEditor(BioSource biosrc) {
        if (!nameToView.containsKey(BIOSRC_EDITOR)) {
            nameToView.put(BIOSRC_EDITOR, new BioSourceEditViewBean());
        }
        BioSourceEditViewBean bioview =
                (BioSourceEditViewBean) nameToView.get(BIOSRC_EDITOR);
        bioview.setAnnotatedObject(biosrc);
        return bioview;
    }

    private AbstractEditViewBean getCvEditor(CvObject cvobj) {
        if (!nameToView.containsKey(CV_EDITOR)) {
            nameToView.put(CV_EDITOR, new CvEditViewBean());
        }
        CvEditViewBean cvview = (CvEditViewBean) nameToView.get(CV_EDITOR);
        cvview.setAnnotatedObject(cvobj);
        return cvview;
    }
}
