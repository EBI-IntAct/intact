/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.cv;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Institution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The CV edit view bean. Currently, this class does not provide any additional
 * functionalities (simply extend the super abstract class to allow to create
 * an instance of this class).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvViewBean extends AbstractEditViewBean {

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // The current CV object.
        CvObject cvobj = (CvObject) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (cvobj == null) {
            // Not persisted; create a new cv object.
            try {
                Constructor ctr = getEditClass().getDeclaredConstructor(
                        new Class[]{Institution.class, String.class});
                cvobj = (CvObject) ctr.newInstance(
                        new Object[]{user.getInstitution(), getShortLabel()});
            }
            catch (NoSuchMethodException ne) {
                // Shouldn't happen.
                throw new SearchException(ne.getMessage());
            }
            catch (SecurityException se) {
                throw new SearchException(se.getMessage());
            }
            catch (InstantiationException ie) {
                throw new SearchException(ie.getMessage());
            }
            catch (IllegalAccessException le) {
                throw new SearchException(le.getMessage());
            }
            catch (InvocationTargetException te) {
                throw new SearchException(te.getMessage());
            }
            setAnnotatedObject(cvobj);
        }
    }
}
