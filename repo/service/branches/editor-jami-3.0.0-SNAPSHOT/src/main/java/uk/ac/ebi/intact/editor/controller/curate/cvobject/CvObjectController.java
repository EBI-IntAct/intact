package uk.ac.ebi.intact.editor.controller.curate.cvobject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.hibernate.Hibernate;
import org.primefaces.model.DualListModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class CvObjectController extends AnnotatedObjectController {

    @Resource(name = "cvObjectService")
    private CvObjectService cvObjectService;

    private String ac;
    private IntactCvTerm cvObject;
    private String cvClassName;

    private String newCvObjectType;

    private boolean isTopic;

    private DualListModel<IntactCvTerm> parents;
    private Map<String, String> classMap;

    @PostConstruct
    public void initializeClassMap(){
        classMap = new HashMap<String, String>();
        classMap.put(IntactUtils.INTERACTION_DETECTION_METHOD_OBJCLASS, "MI:0001" );
        classMap.put( IntactUtils.INTERACTION_TYPE_OBJCLASS, "MI:0190" );
        classMap.put( IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS, "MI:0002" );
        classMap.put( IntactUtils.FEATURE_METHOD_OBJCLASS, "MI:0003" );
        classMap.put( IntactUtils.FEATURE_TYPE_OBJCLASS, "MI:0116" );
        classMap.put( IntactUtils.INTERACTOR_TYPE_OBJCLASS, "MI:0313" );
        classMap.put( IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS, "MI:0346" );
        classMap.put( IntactUtils.RANGE_STATUS_OBJCLASS, "MI:0333" );
        classMap.put( IntactUtils.QUALIFIER_OBJCLASS, "MI:0353" );
        classMap.put( IntactUtils.DATABASE_OBJCLASS, "MI:0444" );
        classMap.put( IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS, "MI:0495" );
        classMap.put( IntactUtils.BIOLOGICAL_ROLE_OBJCLASS, "MI:0500" );
        classMap.put( IntactUtils.ALIAS_TYPE_OBJCLASS, "MI:0300" );
        classMap.put( IntactUtils.TOPIC_OBJCLASS, "MI:0590" );
        classMap.put( IntactUtils.PARAMETER_TYPE_OBJCLASS, "MI:0640" );
        classMap.put( IntactUtils.UNIT_OBJCLASS, "MI:0647" );
        classMap.put( IntactUtils.CONFIDENCE_TYPE_OBJCLASS, "MI:1064" );
    }

    @Override
    public AnnotatedObject getAnnotatedObject() {
        return cvObject;
    }

    @Override
    public void setAnnotatedObject(AnnotatedObject annotatedObject) {
        this.cvObject = (CvDagObject) annotatedObject;

        if (cvObject != null){
            this.ac = annotatedObject.getAc();
        }
    }

    @Override
    public IntactPrimaryObject getJamiObject() {
        return null;
    }

    @Override
    public void setJamiObject(IntactPrimaryObject annotatedObject) {
        // nothing to do
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String clone() {

        return clone(cvObject, new CvObjectIntactCloner());
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void loadData(ComponentSystemEvent evt) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (ac != null) {
                cvObject = (CvDagObject) loadByAc(getDaoFactory().getCvObjectDao(), ac);
                if (cvObject != null){
                    // initialise xrefs
                    Hibernate.initialize(cvObject.getXrefs());
                    // initialise xrefs
                    Hibernate.initialize(cvObject.getAnnotations());
                    // initialise xrefs
                    Hibernate.initialize(cvObject.getAliases());
                    // initialise xrefs
                    Hibernate.initialize(cvObject.getParents());
                }
            } else if (cvClassName != null) {
                cvObject = newInstance(cvClassName);
            }

            if (cvObject == null) {
                addErrorMessage("No CvObject with this AC", ac);
                return;
            }

            if (!Hibernate.isInitialized(cvObject.getXrefs())
                    || !Hibernate.isInitialized(cvObject.getAnnotations())
                    || !Hibernate.isInitialized(cvObject.getAliases())
                    || !Hibernate.isInitialized(cvObject.getParents())){
                cvObject = (CvDagObject) loadByAc(getDaoFactory().getCvObjectDao(), cvObject.getAc());
                // initialise xrefs
                Hibernate.initialize(cvObject.getXrefs());
                // initialise xrefs
                Hibernate.initialize(cvObject.getAnnotations());
                // initialise xrefs
                Hibernate.initialize(cvObject.getAliases());
                // initialise xrefs
                Hibernate.initialize(cvObject.getParents());
            }

            prepareView();
            refreshTabsAndFocusXref();
        }
        generalLoadChecks();
    }

    private void prepareView() {
        if (cvObject != null) {

            List<CvObject> cvObjectsByClass = new ArrayList<CvObject>(getDaoFactory().getCvObjectDao(cvObject.getClass()).getAll());
            List<CvObject> existingParents = new ArrayList<CvObject>(cvObject.getParents());

            Collections.sort( existingParents, new CvObjectService.CvObjectComparator() );
            Collections.sort( cvObjectsByClass, new CvObjectService.CvObjectComparator() );

            parents = new DualListModel<CvObject>(cvObjectsByClass, existingParents);

            if (cvObject instanceof CvTopic) {
                isTopic = true;
            }
        }
    }

    public String newCvObject() {
        if (newCvObjectType != null) {
            CvObject cvObject = newInstance(newCvObjectType);
            setCvObject(cvObject);
        }

        prepareView();

        return navigateToObject(cvObject);
    }

    private CvDagObject newInstance(String cvClassName) {
        CvDagObject obj = null;

        try {
            Class cvClass = Thread.currentThread().getContextClassLoader().loadClass(cvClassName);

            obj = (CvDagObject) cvClass.newInstance();

            if (this.classMap.containsKey(cvClass)){
                CvDagObject parent = (CvDagObject)getDaoFactory().getCvObjectDao(cvClass).getByIdentifier(this.classMap.get(cvClass));
                if (parent != null){
                    obj.getParents().add(parent);
                }
            }
        } catch (Exception e) {
            addErrorMessage("Problem creating cvObject", "Class "+cvClassName);
            e.printStackTrace();
        }

        getChangesController().markAsUnsaved(obj);

        return obj;
    }

    @Override
    public boolean doSaveDetails() {

        Collection<CvObject> parentsToRemove = CollectionUtils.subtract(cvObject.getParents(), parents.getTarget());
        Collection<CvObject> parentsToAdd = CollectionUtils.subtract(parents.getTarget(), cvObject.getParents());

        for (CvObject parent : parentsToAdd) {
            CvDagObject refreshedParent = (CvDagObject) getDaoFactory().getCvObjectDao().getByAc(parent.getAc());
            refreshedParent.addChild(cvObject);
            getDaoFactory().getCvObjectDao().update(refreshedParent);
            getDaoFactory().getCvObjectDao().update(cvObject);
        }

        for (CvObject parent : parentsToRemove) {
            CvDagObject refreshedParent = (CvDagObject) getDaoFactory().getCvObjectDao().getByAc(parent.getAc());
            refreshedParent.removeChild(cvObject);
            getDaoFactory().getCvObjectDao().update(refreshedParent);
            getDaoFactory().getCvObjectDao().update(cvObject);
        }

        cvObjectService.refresh(null);
        cvTermService.clearAll();

        return super.doSaveDetails();
    }

    @Override
    public void postRevert(){
        prepareView();
    }

    public String[] getUsedIn() {
        String usedInArr = super.findAnnotationText(CvTopic.USED_IN_CLASS);

        if (usedInArr == null) {
            return new String[0];
        }

        String[] rawClasses = usedInArr.split(",");
        String[] classes = new String[rawClasses.length];

        for (int i=0; i<rawClasses.length; i++) {
            classes[i] = rawClasses[i].trim();
        }

        return classes;
    }

    public void setUsedIn(String[] usedIn) {
        String usedInArr = StringUtils.join(usedIn, ",");
        super.updateAnnotation(CvTopic.USED_IN_CLASS, usedInArr);
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public CvObject getCvObject() {
        return cvObject;
    }

    public void setCvObject(CvObject cvObject) {
        this.cvObject = (CvDagObject) cvObject;
        this.ac = cvObject.getAc();
    }

    public String getCvClassName() {
        return cvClassName;
    }

    public void setCvClassName(String cvClassName) {
        this.cvClassName = cvClassName;
    }

    public boolean isTopic() {
        return isTopic;
    }

    public CvDagObject getParentCvObjects() {
        if (cvObject.getParents().isEmpty()) {
            return null;
        }

        return cvObject.getParents().iterator().next();
    }

    public void setParentCvObjects(CvDagObject parentCvObjects) {
        if (parentCvObjects != null) {

            // very important : DO NOT use Array.AsList because it will be a problem when persisting the data. We can only do a clear operation on lists
            // and the clear method is always called in the corePersister to refresh collections (instead of using the set )

            cvObject.getParents().clear();
            cvObject.getParents().add(parentCvObjects);
        }
    }

    public DualListModel<CvObject> getParents() {
        return parents;
    }

    public void setParents(DualListModel<CvObject> parents) {
        this.parents = parents;
    }

    public String getNewCvObjectType() {
        return newCvObjectType;
    }

    public void setNewCvObjectType(String newCvObjectType) {
        this.newCvObjectType = newCvObjectType;
    }

    @Override
    public void doSave(boolean refreshCurrentView) {
        ChangesController changesController = (ChangesController) getSpringContext().getBean("changesController");
        PersistenceController persistenceController = getPersistenceController();

        doSaveIntact(refreshCurrentView, changesController, persistenceController);
    }

    @Override
    public String doSave() {
        return super.doSave();
    }

    @Override
    public void doSaveIfNecessary(ActionEvent evt) {
        super.doSaveIfNecessary(evt);
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String getCautionMessage() {
        if (cvObject == null){
            return null;
        }
        if (!Hibernate.isInitialized(cvObject.getAnnotations())){
            return getAnnotatedObjectHelper().findAnnotationText(getDaoFactory().getCvObjectDao().getByAc(cvObject.getAc()),
                    CvTopic.CAUTION_MI_REF, getDaoFactory());
        }
        return findAnnotationText(CvTopic.CAUTION_MI_REF);
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String getInternalRemarkMessage() {
        if (cvObject == null){
            return null;
        }
        if (!Hibernate.isInitialized(cvObject.getAnnotations())){
            return getAnnotatedObjectHelper().findAnnotationText(getDaoFactory().getCvObjectDao().getByAc(cvObject.getAc()),
                    CvTopic.INTERNAL_REMARK, getDaoFactory());
        }
        return findAnnotationText(CvTopic.INTERNAL_REMARK);
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectAnnotations() {
        return super.collectAnnotations();
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectAliases() {
        return super.collectAliases();
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectXrefs() {
        return super.collectXrefs();
    }

    public void changed(ActionEvent evt) {
        setUnsavedChanges(true);
    }

}
