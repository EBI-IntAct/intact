/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionServlet;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.util.LockManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.MissingResourceException;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * This is Intact editor specific action servlet class. This class is
 * responsible for initializing application wide resources.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorActionServlet extends ActionServlet {

    public void init() throws ServletException {
        // Make sure to call super;s init().
        super.init();

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServletContext();

        // Create an instance of EditorService.
        EditorService service = null;
        try {
            // Load resources.
            service = EditorService.getInstance(ctx.getInitParameter("resources"));
        }
        catch (MissingResourceException mre) {
            // Unable to find the resource file.
            log(ExceptionUtils.getStackTrace(mre));
            throw new ServletException();
        }
        catch (EmptyTopicsException mite) {
            // An empty topic resource file.
            log(ExceptionUtils.getStackTrace(mite));
            throw new ServletException();
        }
        // Make them accessible for any servlets within the server.
        ctx.setAttribute(EditorConstants.EDITOR_SERVICE, service);
        ctx.setAttribute(EditorConstants.EDITOR_TOPICS, service.getIntactTypes());
        ctx.setAttribute(EditorConstants.LOCK_MGR, LockManager.getInstance());
        ctx.setAttribute(EditorConstants.ANCHOR_MAP, getAnchorMap());
    }

    private Map getAnchorMap() {
        // The map to return (map key -> anchor name).
        Map map = new HashMap();

        // Resource bundle to access the message resources to set keys.
        ResourceBundle rb = ResourceBundle.getBundle(
                "uk.ac.ebi.intact.application.editor.MessageResources");

        // Editing short label.
        map.put("error.cvinfo.label", "info");

        // Protein search in the Interaction editor.
        map.put("error.int.protein.edit.role", "int.protein");
        map.put(rb.getString("int.proteins.button.search"), "int.protein");

        // Adding annotation.
        map.put("error.annotation.topic", "annotation");
        map.put(rb.getString("annotations.button.add"), "annotation");

        // Adding Xrefs.
        map.put("error.xref.database", "xref");
        map.put("error.xref.pid", "xref");
        map.put(rb.getString("xrefs.button.add"), "xref");


        return map;
    }
}
