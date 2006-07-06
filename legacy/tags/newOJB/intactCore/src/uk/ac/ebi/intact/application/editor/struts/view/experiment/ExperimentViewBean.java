/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.business.IntactHelper;
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
     * Holds the number of interactions. This value is only set if
     * {@link #myHasLargeInts} is true.
     */
    private int myLargeInts;

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

        // The number of interactions for the current experiment.
        int intsSize = exp.getInteractions().size();

        if (intsSize > maxLimit) {
            // Reached the maximum limit.
            myHasLargeInts = true;
            // Save it as we need to returns this value for
            // getNumberOfInteractions method.
            myLargeInts = intsSize;
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

        // There is no need to touch interactions for a large interaction.
        if (!myHasLargeInts) {
            // Delete interactions from the experiment. Do this block of code before
            // clearing interactions or else 'this' experiment wouldn't be removed
            // from interactions.
            for (Iterator iter = myInteractionsToDel.iterator(); iter.hasNext();) {
                Interaction intact = (Interaction) iter.next();
                exp.removeInteraction((Interaction) IntactHelper.getRealIntactObject(intact));
            }

            // --------------------------------------------------------------------
            // Need this fix to get around the proxies.
            // 1. Clear all the interaction proxies first.
            exp.getInteractions().clear();

            // 2. Now add the interaction as real objects.
            for (Iterator iter = myInteractions.iterator(); iter.hasNext();) {
                Interaction intact = (Interaction) iter.next();
                exp.addInteraction((Interaction) IntactHelper.getRealIntactObject(intact));
            }
            // --------------------------------------------------------------------
        }
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

    // Override to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.exp.layout");
    }

    // Override to provide Experiment help tag.
    public String getHelpTag() {
        return "editor.experiment";
    }

    // Override to provide set experiment from the form.
    public void copyPropertiesFrom(EditorActionForm editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the experiment form to get experiment data.
        ExperimentActionForm expform = (ExperimentActionForm) editorForm;

        setOrganism(expform.getOrganism());
        setInter(expform.getInter());
        setIdent(expform.getIdent());
    }

    // Override to copy Experiment data.
    public void copyPropertiesTo(EditorActionForm form) {
        super.copyPropertiesTo(form);

        // Cast to the experiment form to copy experiment data.
        ExperimentActionForm expform = (ExperimentActionForm) form;

        expform.setOrganism(getOrganism());
        expform.setInter(getInter());
        expform.setIdent(getIdent());
    }

    // Override to check for a large experiment.
    public Boolean getReadOnly() {
        return myHasLargeInts ? Boolean.TRUE : Boolean.FALSE;
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
        myOrganism = EditorMenuFactory.normalizeMenuItem(organism);
    }

    // Getter/Setter methods for Interaction.
    public String getInter() {
        return myInter;
    }

    public void setInter(String interaction) {
        myInter = EditorMenuFactory.normalizeMenuItem(interaction);
    }

    // Getter/Setter methods for Identification.
    public String getIdent() {
        return myIdent;
    }

    public void setIdent(String identification) {
        myIdent = EditorMenuFactory.normalizeMenuItem(identification);
    }

    /**
     * Adds an Interaction.
     * @param inter the Interaction to add.
     *
     * <pre>
     * post: myInteractionsToAdd = myInteractionsToAdd@pre + 1
     * post: myInteractions = myInteractions@pre + 1
     * </pre>
     */
    public void addInteraction(Interaction inter) {
        // Add to the view.
        myInteractions.add(inter);
    }

    /**
     * Removes an Interaction
     * @param inter the Interaction to remove.
     *
     * <pre>
     * post: myInteractionsToDel = myInteractionsToDel@pre - 1
     * post: myInteractions = myInteractions@pre - 1
     * </pre>
     */
    public void delInteraction(Interaction inter) {
        // Add to the container to delete interactions.
        myInteractionsToDel.add(inter);
        // Remove from the view as well.
        myInteractions.remove(inter);
    }

    /**
     * Adds an Interaction to hold if the new interaction doesn't
     * already exists in the interaction hold collection and in the
     * current interaction collection for this experiment.
     * @param ints a collection of <code>Interaction</code> to add.
     *
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public void addInteractionToHold(Collection ints) {
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            Interaction inter = (Interaction) iter.next();
            // Avoid duplicates.
            if (!myInteractionsToHold.contains(inter)
                    && !myInteractions.contains(inter)) {
                myInteractionsToHold.add(inter);
            }
        }
    }

    /**
     * Returns a collection of <code>Interaction</code>s for the current
     * experiment.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public List getInteractions() {
        return myInteractions;
    }

    /**
     * Returns a collection of <code>Interaction</code> objects on hold.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public List getHoldInteractions() {
        return myInteractionsToHold;
    }

    /**
     * Returns the number of interactions in the hold interaction collection.
     * @return the number of interactions in the hold interaction collection.
     */
    public int getHoldInteractionCount() {
        return myInteractionsToHold.size();
    }

    /**
     * Hides an Interaction bean from hold.
     * @param inter an <code>Interaction</code> to hide.
     * <pre>
     * pre: myInteractionsToHold->includes(intbean)
     * post: myInteractionsToHold = myInteractionsToHold@pre - 1
     * </pre>
     */
    public void hideInteractionToHold(Interaction inter) {
        myInteractionsToHold.remove(inter);
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
     * @return the number of interactions for this experiment.
     */
    public int getNumberOfInteractions() {
        return myHasLargeInts ? myLargeInts : myInteractions.size();
    }

    // Helper methods

    private void makeInteractionBeans(Collection ints) {
        myInteractions.clear();
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            Interaction interaction = (Interaction) iter.next();
            myInteractions.add(interaction);
        }
    }
}
