/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.exception.validation.ExperimentException;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.tiles.ComponentContext;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Experiment edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentViewBean extends AbstractEditViewBean {

    /**
     * The host organism.
     */
    private String myOrganism;

    /**
     * The interaction with the experiment.
     */
    private String myInteraction;

    /**
     * The experiment identification.
     */
    private String myIdentification;

    public void setAnnotatedObject(Experiment exp) {
        super.setAnnotatedObject(exp);
        // Only set the short labels if the experiment has the objects.
        BioSource biosrc = exp.getBioSource();
        if (biosrc != null) {
            setOrganism(biosrc.getShortLabel());
        }
        CvInteraction inter = exp.getCvInteraction();
        if (inter != null) {
            setInteraction(exp.getCvInteraction().getShortLabel());
        }
        CvIdentification ident = exp.getCvIdentification();
        if (ident != null) {
            setIdentification(exp.getCvIdentification().getShortLabel());
        }
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // The order is important! update super last as it does
        // the update of the object.
        Experiment exp = (Experiment) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInteraction);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdentification);

        // Update the experiment object.
        exp.setBioSource(biosource);
        exp.setCvInteraction(interaction);
        exp.setCvIdentification(ident);
        super.persist(user);
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.exp.layout");
    }

    // Override to provide Experiment help tag.
    public String getHelpTag() {
        return "experiment";
    }

    // Null for any of these values will throw an exception.
    public void validate(EditUserI user) throws ValidationException,
            SearchException {
        super.validate(user);
        if (myOrganism == null) {
            throw new ExperimentException("error.exp.bio.validation");
        }
        else if (myInteraction == null) {
            throw new ExperimentException("error.exp.cvint.validation");
        }
        else if (myIdentification == null) {
            throw new ExperimentException("error.exp.ident.validation");
        }
    }

    public Map getEditorMenus() throws SearchException {
        // The map to return.
        Map map = null;
        // The object we are editing at the moment.
        Experiment exp = (Experiment) getAnnotatedObject();
        if (exp.getBioSource() == null) {
            // Adding a new Experiment.
            map = getMenuFactory().getExperimentMenus(1);
        }
        else {
            // Editing an existing experiment.
            map = getMenuFactory().getExperimentMenus(0);
        }
        return map;
    }

    /**
     * The organism menu list.
     * @return the organism menu consisting of organism short labels. The first
     * item in the menu may contain '---Select---' if the current organism is
     * not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getOrganismMenu() throws SearchException {
        int mode = (myOrganism == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.ORGANISMS, mode);
    }

    /**
     * The interaction menu list.
     * @return the interaction menu consisting of interaction short labels.
     * The first item in the menu may contain '---Select---' if the current
     * interaction is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getInteractionMenu() throws SearchException {
        int mode = (myInteraction == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.INTERACTIONS, mode);
    }

    /**
     * The idetification menu list.
     * @return the idetification menu consisting of idetification short labels.
     * The first item in the menu may contain '---Select---' if the current
     * idetification is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getIdentificationMenu() throws SearchException {
        int mode = (myIdentification == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.IDENTIFICATIONS, mode);
    }

    // Getter/Setter methods for Organism.
    public String getOrganism() {
        return myOrganism;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    // Getter/Setter methods for Interaction.
    public String getInteraction() {
        return myInteraction;
    }

    public void setInteraction(String interaction) {
        myInteraction = interaction;
    }

    // Getter/Setter methods for Identification.
    public String getIdentification() {
        return myIdentification;
    }

    public void setIdentification(String identification) {
        myIdentification = identification;
    }

    public String getSelectedInteraction() {
        if (myInteraction == null) {
            return "";
        }
        // The interaction menu.
        List list;
        try {
            list = (List) getEditorMenus().get(EditorMenuFactory.INTERACTIONS);
        }
        catch (SearchException e) {
            e.printStackTrace();
            return "";
        }
        // Get the normalize version of the interaction list.
        List normalList = normalize(list);
        // The position where the current interaction ocurrs.
        int pos = normalList.indexOf(myInteraction);
//        System.out.println("pos is " + pos + " Interaction is " + myInteraction);
        if (pos != -1) {
//            String s = (String) list.get(pos);
//            System.out.println("Found " + s);
            return (String) list.get(pos);
        }
//        System.out.println("NOT FOUND");
        return "";
    }

    private List normalize(List list) {
        List newList = new ArrayList();
        Pattern pat = Pattern.compile("\\.+");
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String listItem = iter.next().toString();
            Matcher match = pat.matcher(listItem);
            if (match.find()) {
                newList.add(match.replaceAll(""));
            }
            else {
                // No need to do any replacements.
                newList.add(listItem);
            }
        }
        return newList;
    }
}
