/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.ExperimentException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvIdentification;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.Experiment;

import java.util.List;

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
    private String myInter;

    /**
     * The experiment identification.
     */
    private String myIdent;

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    public void setAnnotatedObject(Experiment exp) {
        super.setAnnotatedObject(exp);
        // Only set the short labels if the experiment has the objects.
        BioSource biosrc = exp.getBioSource();
        if (biosrc != null) {
            myOrganism = biosrc.getShortLabel();
        }
        else {
            myOrganism = null;
        }
        CvInteraction inter = exp.getCvInteraction();
        if (inter != null) {
            myInter = inter.getShortLabel();
        }
        else {
            myInter = null;
        }
        CvIdentification ident = exp.getCvIdentification();
        if (ident != null) {
            myIdent = ident.getShortLabel();
        }
        else {
            myIdent = null;
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
                CvInteraction.class, myInter);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdent);

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
        else if (myInter == null) {
            throw new ExperimentException("error.exp.cvint.validation");
        }
        else if (myIdent == null) {
            throw new ExperimentException("error.exp.ident.validation");
        }
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
    public List getInterMenu() throws SearchException {
        int mode = (myInter == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.INTERACTIONS, mode);
    }

    /**
     * The idetification menu list.
     * @return the idetification menu consisting of idetification short labels.
     * The first item in the menu may contain '---Select---' if the current
     * idetification is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getIdentMenu() throws SearchException {
        int mode = (myIdent == null) ? 1 : 0;
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
    public String getInter() {
        return myInter;
    }

    public void setInter(String interaction) {
        myInter = interaction;
    }

    // Getter/Setter methods for Identification.
    public String getIdent() {
        return myIdent;
    }

    public void setIdent(String identification) {
        myIdent = identification;
    }

    /**
     * Returns the selected interaction. It is necessary to macth the
     * current interaction to what is given in the drop down list. For example,
     * the match for current interaction 'xyz' could be '...xyz'. If we don't
     * peform this mapping, the hightlighted menu always defaults to the first
     * item in the list.
     * @return the mapped menu item as it appears in the drop down list. If there
     * is no Interaction for this experiment (i.e, null) or the current inteaction
     * is not found (highly unlikely), the selected menu defaults to the first
     * item in the list (doesn't mean that the first item is the selected one).
     * @throws SearchException for errors in constructing the menu.
     */
    public String getSelectedInter() throws SearchException {
        return getSelectedMenuItem(myInter, EditorMenuFactory.INTERACTIONS);
    }

    /**
     * Returns the selected identification
     * @return the mapped menu item as it appears in the drop down list.
     * @throws SearchException for errors in constructing the menu.
     * @see #getSelectedInter()
     */
    public String getSelectedIdent() throws SearchException {
        return getSelectedMenuItem(myIdent,
                EditorMenuFactory.IDENTIFICATIONS);
    }

    /**
     * Returns the normalized interaction. This is the opposite of
     * {@link #getSelectedInter()} method. Given an item from the drop
     * sown list, this method returns the normalized version of it. For example,
     * the match for current interaction '..xyz' this method returns 'xyz'.
     * @param item the menu item to normalize.
     * @return the normalized menu item as without menu level indicator
     * characters.
     * @throws SearchException for errors in constructing the menu.
     */
    public String getNormalizedInter(String item) throws SearchException {
        return getNormalizedMenuItem(item, EditorMenuFactory.INTERACTIONS);
    }

    /**
     * Returns the normalized identification.
     * @param item the menu item to normalize.
     * @return the normalized menu item as without menu level indicator
     * characters.
     * @throws SearchException for errors in constructing the menu.
     * @see #getNormalizedInter(String)
     */
    public String getNormalizedIdent(String item) throws SearchException {
        return getNormalizedMenuItem(item, EditorMenuFactory.IDENTIFICATIONS);
    }

    /**
     * Returns the selected menu item after mapping the current item to the
     * normalized menu.
     * @param item the selected item
     * @param type the menu type e.g., INTERACTIONS or IDENTIFICATIONS
     * @return the displaued menu item for given <code>item</code>.
     * @throws SearchException for errors in constructing the menu.
     */
    private String getSelectedMenuItem(String item, String type)
            throws SearchException {
        if (item == null) {
            return "";
        }
        // The factory to get access to menus.
        EditorMenuFactory factory = getMenuFactory();

        // The menu for the type.
        List list = factory.getMenu(type, 0);

        // Get the normalized version of the interaction list.
        List normalList = factory.getDagMenu(type, 0);

        // The position where the current interaction ocurrs.
        int pos = normalList.indexOf(item);
        if (pos != -1) {
            return (String) list.get(pos);
        }
        return "";
    }

    /**
     * Returns the normalized menu item after stripping off menu level characters
     * from gievn menu item.
     * @param item the selected item from the menu.
     * @param type the menu type e.g., INTERACTIONS or IDENTIFICATIONS
     * @return the normalized menu item for given <code>item</code>.
     * @throws SearchException for errors in constructing the menu.
     */
    private String getNormalizedMenuItem(String item, String type)
            throws SearchException {
        // The factory to get access to menus.
        EditorMenuFactory factory = getMenuFactory();

        // Get the normalized version of the interaction list.
        List normalList = factory.getDagMenu(type, 0);

        // The menu for the type.
        List list = factory.getMenu(type, 0);

        // The position where the current interaction ocurrs.
        int pos = list.indexOf(item);
        if (pos != -1) {
            return (String) normalList.get(pos);
        }
        return "";
    }
}
