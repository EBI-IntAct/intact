/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.action.DynaActionForm;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.model.*;

import java.util.*;

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

    /**
     * True if the maximum interactions allowed for the experiment has exceeded.
     */
    private boolean myHasLargeInts;

    /**
     * The collection of Interactions. Transient as it is only valid for the
     * current display.
     */
    private transient List myInteractions = new ArrayList();

    /**
     * Holds Interactions to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myInteractionsToDel = new ArrayList();

    /**
     * Holds Interaction to not yet added. Only valid for the current session.
     */
    private transient List myInteractionsToHold = new ArrayList();

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);
        // Set fields to null.
        setOrganism(null);
        setInter(null);
        setIdent(null);

        // Clear any left overs from previous transaction.
        clearTransactions();

        // Clear Interaction beans.
        makeInteractionBeans(Collections.EMPTY_LIST);
    }

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be an experiment.
        Experiment exp = (Experiment) annobj;

        // Only set the short labels if the experiment has the objects.
        BioSource biosrc = exp.getBioSource();
        setOrganism(biosrc != null ? biosrc.getShortLabel() : null);

        CvInteraction inter = exp.getCvInteraction();
        setInter(inter != null ? inter.getShortLabel() : null);

        CvIdentification ident = exp.getCvIdentification();
        setIdent(ident != null ? ident.getShortLabel() : null);

        // Clear any left overs from previous transaction.
        clearTransactions();

        // Check the limit for interactions.
        int maxLimit = EditorService.getInstance().getInteractionLimit();
        if (exp.getInteractions().size() > maxLimit) {
            // Reached the maximum limit.
            myHasLargeInts = true;
        }
        else {
            // Prepare for Interactions for display.
            makeInteractionBeans(exp.getInteractions());
            myHasLargeInts = false;
        }
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInter);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdent);

        // The current experiment.
        Experiment exp = (Experiment) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (exp == null) {
            // Can't read from the persistent system. Create a new Experiment.
            exp = new Experiment(user.getInstitution(), getShortLabel(), biosource);
            setAnnotatedObject(exp);
        }
        else {
            // No need to set the biosource for a new experiment as it is done
            // in the constructor.
            exp.setBioSource(biosource);
        }
        exp.setCvInteraction(interaction);
        exp.setCvIdentification(ident);

        // Delete interactions from the experiment. Do this block of code before
        // clearing interactions or else 'this' experiment wouln't be removed
        // from interactions.
        for (Iterator iter = myInteractionsToDel.iterator(); iter.hasNext();) {
            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
            exp.removeInteraction(intact);
        }

        // --------------------------------------------------------------------
        // Need this fix to get around the proxies.
        // 1. Clear all the interaction proxies first.
        exp.getInteractions().clear();

        // 2. Now add the interaction as real objects.
        for (Iterator iter = myInteractions.iterator(); iter.hasNext();) {
            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
            exp.addInteraction(intact);
        }
        // --------------------------------------------------------------------
    }

    // Override the super method as the current experiment is added to the
    // recent experiment list.
    public void addToRecentList(EditUserI user) {
        user.addToCurrentExperiment((Experiment) getAnnotatedObject());
    }

    // Override to remove the current experiment from the recent list.
    public void removeFromRecentList(EditUserI user) {
        user.removeFromCurrentExperiment((Experiment) getAnnotatedObject());
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.exp.layout");
    }

    // Override to provide Experiment help tag.
    public String getHelpTag() {
        return "editor.experiment";
    }

    // Override to provide set experiment from the bean.
    public void updateFromForm(DynaActionForm dynaform) {
        // Set the common values by calling super first.
        super.updateFromForm(dynaform);
        String organism = (String) dynaform.get("organism");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(organism)) {
            // Set the view bean with the new values.
            setOrganism(organism);
        }

        // These two items need to be normalized.
        String interaction = (String) dynaform.get("inter");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(interaction)) {
            setInter(interaction);
        }
        String identification = (String) dynaform.get("ident");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(identification)) {
            setIdent(identification);
        }
    }

    public void sanityCheck(EditUserI user) throws ValidationException,
            SearchException {
        // COMMENTED OUT these checks as they need to be warning!

        /// keeps track of pubmed xrefs.
//        int pmCount = 0;
//
//        // Keeps track of primary pubmed counts.
//        int pmPrimaryCount = 0;
//
//        for ( Iterator iterator = getXrefs().iterator(); iterator.hasNext(); ) {
//            Xref xref = ((XreferenceBean) iterator.next()).getXref(user);
//            if (xref.getCvDatabase().getShortLabel().equals("pubmed")) {
//                pmCount++;
//                if (xref.getCvXrefQualifier().getShortLabel().equals(
//                        "primary-reference")) {
//                    pmPrimaryCount++;
//                }
//            }
//        }
//        if (pmCount == 0) {
//            throw new ExperimentException("error.exp.sanity.pubmed");
//        }
//        if (pmPrimaryCount != 1) {
//            throw new ExperimentException("error.exp.sanity.primary.pubmed");
//        }
    }

    // Null for any of these values will throw an exception.
    public void validate(EditUserI user) throws ValidationException,
            SearchException {
//        super.validate(user);
//        if (myOrganism == null) {
//            throw new ExperimentException("error.exp.biosrc");
//        }
//        else if (myInter == null) {
//            throw new ExperimentException("error.exp.inter");
//        }
//        else if (myIdent == null) {
//            throw new ExperimentException("error.exp.ident");
//        }
    }

    // Override the super method to clear this object.
    public void clear() {
        super.clear();
        // Clear of any references to this object.
        myOrganism = null;
        myInter = null;
        myIdent = null;
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
        return getMenuFactory().getDagMenu(EditorMenuFactory.INTERACTIONS, mode);
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
        return getMenuFactory().getDagMenu(EditorMenuFactory.IDENTIFICATIONS, mode);
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
     * Adds an Interaction.
     * @param intbean the Interaction bean to add.
     *
     * <pre>
     * post: myInteractionsToAdd = myInteractionsToAdd@pre + 1
     * post: myInteractions = myInteractions@pre + 1
     * </pre>
     */
    public void addInteraction(InteractionBean intbean) {
        // Add to the view.
        myInteractions.add(intbean);
    }

    /**
     * True if given interaction exists in this object's interaction collection.
     * @param intbean the bean to compare.
     * @return true <code>intbean</code> exists in this object's interaction
     * collection. The comparision uses the equals method of
     * <code>InteractionBean</code> class.
     *
     * <pre>
     * post: return->true implies myInteractionsToAdd.exists(intbean)
     * </pre>
     */
    public boolean interactionExists(InteractionBean intbean) {
        return myInteractions.contains(intbean);
    }

    /**
     * Removes an Interaction
     * @param intbean the Interaction bean to remove.
     *
     * <pre>
     * post: myInteractionsToDel = myInteractionsToDel@pre - 1
     * post: myInteractions = myInteractions@pre - 1
     * </pre>
     */
    public void delInteraction(InteractionBean intbean) {
        // Add to the container to delete interactions.
        myInteractionsToDel.add(intbean);
        // Remove from the view as well.
        myInteractions.remove(intbean);
    }

    /**
     * Adds an Interaction bean to hold if the new interaction doesn't
     * already exists in the interaction hold collection and in the
     * current interaction collection for this interaction.
     * @param ints a collection of <code>Interaction</code> to add.
     *
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public void addInteractionToHold(Collection ints) {
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            InteractionBean expbean = new InteractionBean((Interaction) iter.next());
            // Avoid duplicates.
            if (!myInteractionsToHold.contains(expbean)
                    && !myInteractions.contains(expbean)) {
                myInteractionsToHold.add(expbean);
            }
        }
    }

    /**
     * Returns a collection of <code>InteractionBean</code>s for the current
     * experiment.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(InteractionBean))
     * </pre>
     */
    public List getInteractions() {
        return myInteractions;
    }

    /**
     * Returns a collection of <code>InteractionBean</code> objects on hold.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(InteractionBean))
     * </pre>
     */
    public List getHoldInteractions() {
        return myInteractionsToHold;
    }

    /**
     * Hides an Interaction bean from hold.
     * @param intbean an <code>InteractionBean</code> to hide.
     * <pre>
     * pre: myInteractionsToHold->includes(intbean)
     * post: myInteractionsToHold = myInteractionsToHold@pre - 1
     * </pre>
     */
    public void hideInteractionToHold(InteractionBean intbean) {
        myInteractionsToHold.remove(intbean);
    }

    /**
     * Clears all the interactions on hold.
     */
    public void clearInteractionToHold() {
        myInteractionsToHold.clear();
    }

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear interactions.
        myInteractionsToDel.clear();
        myInteractionsToHold.clear();
        clearInteractionToHold();
    }

    /**
     * Returns the number of interactions for this experiment.
     * @return the number of interactions for this experiment if it hasn't
     * already reached the max limit. When the maximum limit is reached,
     * maximum value + 1 is returned to indicate.
     */
    public int getNumberOfInteractions() {
        int maxLimit = EditorService.getInstance().getInteractionLimit();
        if (myHasLargeInts) {
            return maxLimit + 1;
        }
        // Save the current interactions.
        int currentInts = myInteractions.size();

        // Has it reached the limit?
        if (currentInts > maxLimit) {
            return maxLimit + 1;
        }
        return currentInts;
    }

    // Helper methods

    private void makeInteractionBeans(Collection ints) {
        myInteractions.clear();
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            Interaction interaction = (Interaction) iter.next();
            myInteractions.add(new InteractionBean(interaction));
        }
    }
}
