/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.viewx.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.ValidationException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.SearchException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.tiles.ComponentContext;

import java.util.*;

/**
 * Interaction edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionViewBean extends AbstractEditViewBean {

    /**
     * The KD.
     */
    private Float myKD;

    /**
     * The host organism.
     */
    private String myOrganism;

    /**
     * The interaction with the experiment.
     */
    private String myInteractionType;

    /**
     * The experiment.
     */
    private String myExperiment;

    /**
     * The collection of Proteins.
     */
    private List myProteins = new ArrayList();

    /**
     * Holds Proteins to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myProteinsToAdd = new ArrayList();

    /**
     * Holds Proteins to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myProteinsToDel = new ArrayList();

    /**
     * Holds Proteins to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myProteinsToUpdate = new ArrayList();


    // Override the super method to set the tax id.
    public void setAnnotatedObject(Interaction intact) {
        super.setAnnotatedObject(intact);
        myKD = intact.getKD();
        // Only set the short labels if the interaction has non null values.
        BioSource biosrc = intact.getBioSource();
        if (biosrc != null) {
            setOrganism(biosrc.getShortLabel());
        }
        CvInteractionType inter = intact.getCvInteractionType();
        if (inter != null) {
            setInteractionType(intact.getCvInteractionType().getShortLabel());
        }
//        Experiment exp = intact.get;
        // Clear previous Proteins.
        myProteins.clear();
        makeProteinBeans(intact.getComponent());
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException {
        // The order is important! update super last as it does
        // the update of the object.
        Interaction intact = (Interaction) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteractionType type = (CvInteractionType) user.getObjectByLabel(
                CvInteractionType.class, myInteractionType);

        intact.setBioSource(biosource);
        intact.setBioSourceAc(biosource.getAc());
        intact.setCvInteractionType(type);
        intact.setCvInteractionTypeAc(type.getAc());

        // Need to update Proteins here.

        super.persist(user);
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.interaction.layout");
    }

    // Null for any of these values will throw an exception.
    public void validate() throws ValidationException {
        if ((myOrganism == null) || (myInteractionType == null)) {
//            throw new ExperimentValidationException();
        }
    }

    // Override to provide Experiment info.
    public void fillEditorSpecificInfo(DynaBean form) {
        form.set("kD", myKD);
        form.set("organism", myOrganism);
        form.set("interactionType", myInteractionType);
        form.set("experiment", myExperiment);
        form.set("searchInput", "");
    }

    // Override to provde menus needed for this editor.
    public Map getEditorMenus() throws SearchException {
        // The object we are editing at the moment.
        Interaction exp = (Interaction) getAnnotatedObject();
        Map map = (exp.getBioSource() == null)
                ? getMenuFactory().getInteractionMenus(1)
                : getMenuFactory().getInteractionMenus(0);
        return map;
    }

    /**
     * Returns the edit menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getEditProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLES, 0);
    }

    /**
     * Returns the add menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getAddProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLES, 0);
    }

    public void setKD(Float kd) {
        myKD = kd;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    public void setInteractionType(String interaction) {
        myInteractionType = interaction;
    }

    public String setExperiment(String experiment) {
        return myExperiment;
    }

    /**
     * Adds an Protein.
     * @param protein the Protein to add.
     *
     * <pre>
     * post: myProteinsToAdd = myProteinsToAdd@pre + 1
     * post: myProteins = myProteins@pre + 1
     * </pre>
     */
    public void addProtein(Protein protein) {
        ProteinBean pb = new ProteinBean(protein);
        // Protein to add.
        myProteinsToAdd.add(pb);
        // Add to the view as well.
        myProteins.add(pb);
    }

    /**
     * Removes a Protein.
     * @param pb the Protein bean to remove.
     *
     * <pre>
     * post: myProteinsToDel = myProteinsToDel@pre - 1
     * post: myProteins = myProteins@pre - 1
     * </pre>
     */
    public void delProtein(ProteinBean pb) {
        // Add to the container to delete proteins.
        myProteinsToDel.add(pb);
        // Remove from the view as well.
        myProteins.remove(pb);
    }

    /**
     * Adds a Protein bean to update.
     * @param pb a <code>ProteinBean</code> object to update.
     *
     * <pre>
     * post: myProteinsToUpdate = myProteinsToUpdate@pre + 1
     * post: myProteins = myProteins@pre
     * </pre>
     */
    public void addProteinToUpdate(ProteinBean pb) {
        myProteinsToUpdate.add(pb);
    }

    /**
     * Returns a collection of <code>ProteinBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(ProteinBean))
     * </pre>
     */
    public List getProteins() {
        return myProteins;
    }

    /**
     * Returns a <code>ProteinBean</code> at given location.
     * @param index the position to return <code>ProteinBean</code>.
     * @return <code>ProteinBean</code> at <code>index</code>.
     *
     * <pre>
     * pre: index >=0 and index < myProteins->size
     * post: return != null
     * post: return = myProteins->at(index)
     * </pre>
     */
    public ProteinBean getProtein(int index) {
        return (ProteinBean) myProteins.get(index);
    }

    /**
     * Fills the given form with Protein data.
     * @param form the form to fill with.
     */
    public void fillProteins(EditForm form) {
        form.setItems(myProteins);
    }
    // Helper methods

    private void makeProteinBeans(Collection components) {
        for (Iterator iter = components.iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            myProteins.add(new ProteinBean(comp));
        }
    }
}
