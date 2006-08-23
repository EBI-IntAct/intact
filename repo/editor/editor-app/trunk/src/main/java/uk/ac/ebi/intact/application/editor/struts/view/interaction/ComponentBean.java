/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Query;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.OJBQueryFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.util.IntactHelperUtil;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.BioSourceDao;
import uk.ac.ebi.intact.persistence.dao.ComponentDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Bean to hold data for a component in an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentBean extends AbstractEditKeyBean {


    static final String PROTEIN = "Protein";
    static final String NUCLEIC_ACID = "Nucleic Acid";
    static final String SMALL_MOLECULE = "Small Molecule";

    // Class Data

    /**
     * The saving new state; this is different to saving state as this state
     * indicates saving a new item. This state is only used by proteins.jsp
     * to save new componets as as result of a search.
     */
    static final String SAVE_NEW = "saveNew";

    // Instance Data
    private String myType;
    /**
     * The interaction this protein belongs to.
     */
    private Interaction myInteraction;

    /**
     * The interactor of the bean.
     */
    private Interactor myInteractor;

    /**
     * The object this instance is created with.
     */
    private Component myComponent;

    /**
     * Swiss-Prot AC.
     */
    private String mySPAc;

    /**
     * The role of this Protein.
     */
    private String myRole;

    /**
     * The stoichiometry.
     */
    private float myStoichiometry = 1.0f;

    /**
     * The organism.
     */
    private String myOrganism;

    /**
     * Expressed in as a biosource short label.
     */
    private String myExpressedIn;

    /**
     * Contains features associated with this component.
     */
    private List<FeatureBean> myFeatures = new ArrayList<FeatureBean>();

    /**
     * The gene names for display
     */
    private String myGeneNames;

    /**
     * A list of features to add.
     */
    private transient List<FeatureBean> myFeaturesToAdd = new ArrayList<FeatureBean>();

    /**
     * A list of features to delete.
     */
    private transient List<FeatureBean> myFeaturesToDel = new ArrayList<FeatureBean>();

    /**
     * Default constructor. Need this construtor to set from a cloned
     * object. Visibility is set to package.
     */
    ComponentBean() {}

    /**
     * Instantiate an object of this class from a Protein instance.
     * @param protein the <code>Protein</code> object.
     */
    public ComponentBean(Protein protein) {
        myInteractor = IntactHelper.getRealIntactObject(protein);
        mySPAc = getSPAc();
        setOrganism();
        setEditState(SAVE_NEW);
        setType(PROTEIN);
        try {
            setGeneName();
        }
        catch (IntactException ex) {
            // Error in setting the Gene name for display
            Logger.getLogger(EditorConstants.LOGGER).error("Gene Name", ex);
        }
    }

    public ComponentBean(NucleicAcid nucleicAcid){
        myInteractor = IntactHelper.getRealIntactObject(nucleicAcid);
        setOrganism();
        setEditState(SAVE_NEW);
        setType(NUCLEIC_ACID);

        try{
            setGeneName();
        }
        catch (IntactException ex) {
            // Error in setting the Gene name for display
            Logger.getLogger(EditorConstants.LOGGER).error("Gene Name", ex);
        }
    }

    public ComponentBean(SmallMolecule smallMolecule){
        myInteractor = IntactHelper.getRealIntactObject(smallMolecule);
//        setOrganism();
        setEditState(SAVE_NEW);
        setType(SMALL_MOLECULE);
    }

    /**
     * Instantiate an object of this class from a Component instance.
     * @param component the <code>Component</code> object.
     */
    public ComponentBean(Component component) {
        initialize(component);
        if(component.getInteractor() instanceof Protein){
            setType(PROTEIN);
        }
        else if (component.getInteractor() instanceof NucleicAcid ){
            setType(NUCLEIC_ACID);
        } else if (component.getInteractor() instanceof SmallMolecule){
            setType(SMALL_MOLECULE);
        }

        // Set the feature for this bean.
        for (Feature feature : myComponent.getBindingDomains())
        {
            myFeatures.add(new FeatureBean(feature));
        }
    }



    // Read only properties.

    /**
     * This methods returns the component this bean is based on. This is a convenient
     * method to call after updating the bean. Ideally this method should be called
     * after calling to {@link #getComponent(true)}
     * method
     * @return could be null if this method was called after constructing with a
     * Protein but without calling getComponent(IntactHelper) method first.
     */
    public Component getComponent() {
        if(myComponent != null && myComponent.getAc() != null){
            ComponentDao componentDao = DaoFactory.getComponentDao();
            myComponent = componentDao.getByAc(myComponent.getAc());

        }
        return myComponent;
    }

    public Component getComponent(boolean create) throws IntactException {
        CvComponentRole newrole = getCvRole();


        // Must have a non null role and interaction for a valid component
        if ((newrole == null) || (myInteraction == null)) {
            return null;
        }
        // Component is null if this bean constructed from a Protein.
        if (myComponent == null) {
            myComponent = new Component(getService().getOwner(), myInteraction,
                    myInteractor, newrole);
        }else if (myComponent.getAc() != null){
            ComponentDao componentDao = DaoFactory.getComponentDao();
            myComponent = componentDao.getByAc(myComponent.getAc());
        }

        else {
            myComponent.setCvComponentRole(newrole);
        }
        myComponent.setStoichiometry(getStoichiometry());

        // The expressed in to set in the component.
        BioSource expressedIn = null;
        if (myExpressedIn != null) {
            BioSourceDao bioSourceDao = DaoFactory.getBioSourceDao();
            expressedIn = bioSourceDao.getByShortLabel(myExpressedIn);
        }
        myComponent.setExpressedIn(expressedIn);

        return myComponent;
    }

    public String getType(){
        return myType;
    }

    public void setType(String type){
        myType=type;
    }

    public String getAc() {
        return myComponent == null ? null : myComponent.getAc();
    }

    public String getInteractorAc() {
        return myInteractor.getAc();
    }

    public String getShortLabel() {
        return myInteractor.getShortLabel();
    }

    public String getShortLabelLink() {
        return getLink(EditorService.getTopic(Protein.class), getShortLabel());
    }

    public String getSpAc() {
        return mySPAc;
    }

    public String getFullName() {
        return myInteractor.getFullName();
    }

    // Read/Write properties.

    public String getRole() {
        return myRole;
    }

    public void setRole(String role) {
        myRole = EditorMenuFactory.normalizeMenuItem(role);
    }

    public float getStoichiometry() {
        return myStoichiometry;
    }

    public void setStoichiometry(float stoichiometry) {
        myStoichiometry = stoichiometry;
    }

    public String getExpressedIn() {
        return myExpressedIn;
    }

    public void setExpressedIn(String expressedIn) {
        myExpressedIn = EditorMenuFactory.normalizeMenuItem(expressedIn);
    }

    public String getOrganism() {
        return myOrganism;
    }

    public String getGeneName() {
        return myGeneNames;
    }

//    public void setSelect(String value) {
//        System.out.println("Value: " + value);
//        mySelected = true;
//    }
//
//    public void unselect() {
//        mySelected = false;
//    }

    /**
     * Sets the interaction for this bean. This is necessary for a newly created
     * Interaction as it doesn't exist until it is ready to persist.
     * @param interaction the interaction to set.
     */
    public void setInteraction(Interaction interaction) {
        myInteraction = interaction;
    }

    /**
     * List of features if the component is set. An empty list is returned if
     * this bean is constructed without a component.
     * @return a list of feature beans.
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public List<FeatureBean> getFeatures() {
        return myFeatures;
    }

    /**
     * Removes the feature from view and add it to the collection of features
     * to delete
     * @param bean the Feature bean to delete.
     */
    public void delFeature(FeatureBean bean) {
        // Remove it from the view (it was added as a bean).
        myFeatures.remove(bean);
        
        // Add the feature to delete.
        myFeaturesToDel.add(bean);
    }

    /**
     * Returns a collection of Feature beans to delete
     * @return a collection of Feature beans to delete
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public Collection<FeatureBean> getFeaturesToDelete() {
        return myFeaturesToDel;
    }

    /**
     * Returns a collection of Feature beans added.
     * @return a collection of Feature beans added.
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public Collection<FeatureBean> getFeaturesAdded() {
        return myFeaturesToAdd;
    }

    /**
     * Returns a collection of Feature to add
     * @return a collection of Feature to add
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public Collection<FeatureBean> getFeaturesToAdd() {
        // Features common to both add and delete.
        Collection<FeatureBean> common = CollectionUtils.intersection(myFeaturesToAdd, myFeaturesToDel);
        // All the features only found in 'features to add' collection.
        return CollectionUtils.subtract(myFeaturesToAdd, common);
    }

    /**
     * Saves the given bean. This method takes care of saving the bean to the
     * correct collection. For example, if this is a new bean, then it will be
     * added to the 'FeaturesToAdd' collection.
     * @param fb the Feature bean to save.
     */
    public void saveFeature(FeatureBean fb) {
        // Find the feature bean within the component bean.
        if (myFeatures.contains(fb)) {
            // Update an existing feature; remove it and add the new bean.
            int idx = myFeatures.indexOf(fb);
            myFeatures.remove(idx);
            myFeatures.add(idx, fb);
        }
        else {
            // New feature; just add it.
            myFeatures.add(fb);
            myFeaturesToAdd.add(fb);
        }
    }

    public void clearTransactions() {
        myFeaturesToAdd.clear();
        myFeaturesToDel.clear();
    }

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    void setFromClonedObject(Component copy) {
        initialize(copy);
        clearTransactions();

        // Clear existing features in the view.
        myFeatures.clear();

        // All the Features need to be added.
        for (Feature feature : myComponent.getBindingDomains())
        {
            FeatureBean fb = new FeatureBean(feature,
                                             stripCloneSuffix(feature.getShortLabel()));
            // Add to the view.
            myFeatures.add(fb);
            // Features need to be added to the component.
            myFeaturesToAdd.add(fb);
        }
    }

    // Helper methods

    /**
     * Intialize the member variables using the given Component object.
     * @param component <code>Component</code> object to populate this bean.
     */
    private void initialize(Component component) {
        myComponent = component;
        myInteraction = IntactHelper.getRealIntactObject(
                component.getInteraction());
        myInteractor = IntactHelper.getRealIntactObject(
                component.getInteractor());
        mySPAc = getSPAc();
        myRole = component.getCvComponentRole().getShortLabel();
        myStoichiometry = component.getStoichiometry();
        setOrganism();
        setExpressedIn();
        try {
            setGeneName();
        }
        catch (IntactException ex) {
            // Error in setting the Gene name for display
            Logger.getLogger(EditorConstants.LOGGER).error("Gene Name", ex);
        }
    }

    private void setGeneName() throws IntactException {
        // The helper to run the query.

        // The query factory to get a query.
        OJBQueryFactory qf = OJBQueryFactory.getInstance();

        // The string buffer to construct the gene name.
        StringBuffer sb = new StringBuffer();

        // The alias AC.
        CvObjectDao<CvAliasType> cvObjectDao = DaoFactory.getCvObjectDao(CvAliasType.class);
        String ac = cvObjectDao.getByXref(CvAliasType.GENE_NAME_MI_REF).getAc();
        /*Query query*/Collection<Alias> aliases = qf.getGeneNameQuery(ac, myInteractor.getAc());

        // The flag to say that we are processing the first gene name.
        boolean first = true;

        // Run through the query result set.
//        for (Iterator iter = aliases.iterator()/*helper.getIteratorByReportQuery(query)*/;
//             iter.hasNext(); ) {
         for( Alias alias : aliases ){

            String name = alias.getName();

            // Though this returns an array we are interested only in the first item.
//            Object name = ((Object[])iter.next())[0];
            if (first) {
                sb.append(name);
                first = false;
            }
            else {
                sb.append(", ").append(name);
            }
        }
        myGeneNames = sb.toString();
    }

    private void setOrganism() {
        BioSource biosource = myInteractor.getBioSource();
        if (biosource != null) {
            myOrganism = biosource.getShortLabel();
        }
    }

    private void setExpressedIn() {
        BioSource bs = myComponent.getExpressedIn();
        if (bs != null) {
            myExpressedIn = bs.getShortLabel();
        }
    }

    private String getSPAc() {
        for (Xref xref : myInteractor.getXrefs())
        {
            // Only consider SwissProt database entries.
            if (xref.getCvDatabase().getShortLabel().equals(CvDatabase.UNIPROT))
            {
                return xref.getPrimaryId();
            }
        }
        return "";
    }

    private CvComponentRole getCvRole() throws IntactException  {
        CvObjectDao<CvComponentRole> cvObjectDao = DaoFactory.getCvObjectDao(CvComponentRole.class);
        if (myRole != null) {
            return cvObjectDao.getByShortLabel(myRole);
        }
        return null;
    }

    private String stripCloneSuffix(String label) {
        int idx = label.indexOf("-x");
        // suffix is always present, so we can safely assume that idx is never -1
        return label.substring(0, idx);
    }
}
