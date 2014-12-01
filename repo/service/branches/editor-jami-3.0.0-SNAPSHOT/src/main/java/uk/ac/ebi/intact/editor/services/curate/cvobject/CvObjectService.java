/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.services.curate.cvobject;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.OntologyTerm;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Service
@Lazy
public class CvObjectService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( CvObjectService.class );

    public static final String NO_CLASS = "no_class";

    private Map<CvKey, IntactCvTerm> allCvObjectMap;
    private Map<String, IntactCvTerm> acCvObjectMap;

    private List<SelectItem> publicationTopicSelectItems;
    private List<SelectItem> experimentTopicSelectItems;
    private List<SelectItem> interactionTopicSelectItems;
    private List<SelectItem> interactorTopicSelectItems;
    private List<SelectItem> participantTopicSelectItems;
    private List<SelectItem> featureTopicSelectItems;
    private List<SelectItem> complexTopicSelectItems;
    private List<SelectItem> cvObjectTopicSelectItems;
    private List<SelectItem> noClassSelectItems;

    private List<SelectItem> databaseSelectItems;

    private List<SelectItem> qualifierSelectItems;

    private List<SelectItem> aliasTypeSelectItems;

    private List<SelectItem> interactionDetectionMethodSelectItems;

    private List<SelectItem> participantDetectionMethodSelectItems;

    private List<SelectItem> participantExperimentalPreparationsSelectItems;

    private List<SelectItem> interactionTypeSelectItems;

    private List<SelectItem> interactorTypeSelectItems;

    private List<SelectItem> experimentalRoleSelectItems;

    private List<SelectItem> biologicalRoleSelectItems;

    private List<SelectItem> featureDetectionMethodSelectItems;

    private List<SelectItem> featureTypeSelectItems;

    private List<SelectItem> fuzzyTypeSelectItems;

    private List<SelectItem> cellTypeSelectItems;

    private List<SelectItem> tissueSelectItems;

    private List<SelectItem> parameterTypeSelectItems;

    private List<SelectItem> parameterUnitSelectItems;

    private List<SelectItem> confidenceTypeSelectItems;

    private List<SelectItem> featureRoleSelectItems;

    private List<SelectItem> complexTypeSelectItems;

    private List<SelectItem> evidenceTypeSelectItems;

    private IntactCvTerm defaultExperimentalRole;
    private IntactCvTerm defaultBiologicalRole;

    public static final String USED_IN_CLASS = "used-in-class";
    public static final String OBSOLETE = "obsolete";
    public static final String OBSOLETE_MI_REF = "MI:0431";

    private boolean isInitialised = false;

    public CvObjectService() {
    }

    public synchronized void clearAll(){
        this.allCvObjectMap.clear();
        this.acCvObjectMap.clear();
        this.publicationTopicSelectItems=null;
        this.experimentTopicSelectItems=null;
        this.interactionTopicSelectItems=null;
        this.interactorTopicSelectItems=null;
        this.participantTopicSelectItems=null;
        this.featureTopicSelectItems=null;
        this.complexTopicSelectItems=null;
        this.cvObjectTopicSelectItems=null;
        this.noClassSelectItems=null;
        this.databaseSelectItems=null;
        this.qualifierSelectItems=null;
        this.aliasTypeSelectItems=null;
        this.interactionDetectionMethodSelectItems=null;
        this.participantDetectionMethodSelectItems=null;
        this.participantExperimentalPreparationsSelectItems=null;
        this.interactionTypeSelectItems=null;
        this.interactorTypeSelectItems=null;
        this.experimentalRoleSelectItems=null;
        this.biologicalRoleSelectItems=null;
        this.featureDetectionMethodSelectItems=null;
        this.featureTypeSelectItems=null;
        this.fuzzyTypeSelectItems=null;
        this.cellTypeSelectItems=null;
        this.tissueSelectItems=null;
        this.parameterTypeSelectItems=null;
        this.parameterUnitSelectItems=null;
        this.confidenceTypeSelectItems=null;
        this.featureRoleSelectItems=null;
        this.complexTypeSelectItems=null;
        this.evidenceTypeSelectItems=null;
        this.defaultExperimentalRole=null;
        this.defaultBiologicalRole=null;
        isInitialised=false;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public synchronized void loadData( ) {
        if ( log.isDebugEnabled() ) log.debug( "Loading Controlled Vocabularies" );

        publicationTopicSelectItems = new ArrayList<SelectItem>();

        String cvQuery = "select c from IntactCvTerm c " +
                "where c.ac not in (" +
                " select c2.ac from IntactCvTerm c2 join c2.dbAnnotations as a join a.topic as t " +
                "where t.shortName = :hidden)";
        Query query = getIntactDao().getEntityManager().createQuery(cvQuery);
        query.setParameter("hidden","hidden");

        final List<IntactCvTerm> allCvObjects = query.getResultList();

        allCvObjectMap = new HashMap<CvKey, IntactCvTerm>( allCvObjects.size() * 2 );
        acCvObjectMap = new HashMap<String, IntactCvTerm>( allCvObjects.size() );

        HashMultimap<String, IntactCvTerm> cvObjectsByUsedInClass = HashMultimap.create();
        HashMultimap<String, IntactCvTerm> cvObjectsByClass = HashMultimap.create();

        for ( IntactCvTerm cvObject : allCvObjects ) {
            acCvObjectMap.put( cvObject.getAc(), cvObject );
            cvObjectsByClass.put(cvObject.getObjClass(), cvObject);

            CvKey keyId = null;
            if ( cvObject.getMIIdentifier() != null ) {
                keyId = new CvKey( cvObject.getMIIdentifier(), cvObject.getObjClass() );
            }
            else if(cvObject.getMODIdentifier() != null){
                keyId = new CvKey( cvObject.getMIIdentifier(), cvObject.getObjClass() );
            }
            else if (!cvObject.getIdentifiers().isEmpty()){
                keyId = new CvKey(cvObject.getIdentifiers().iterator().next().getId(), cvObject.getObjClass());
            }
            else {
                keyId = new CvKey(cvObject.getAc(), cvObject.getObjClass());
            }
            CvKey keyLabel = new CvKey( cvObject.getShortName(), cvObject.getObjClass() );
            allCvObjectMap.put( keyId, cvObject );
            allCvObjectMap.put( keyLabel, cvObject );

            if (IntactUtils.TOPIC_OBJCLASS.equals(cvObject.getObjClass())) {
                String[] usedInClasses = findUsedInClass( cvObject );

                for ( String usedInClass : usedInClasses ) {
                    cvObjectsByUsedInClass.put( usedInClass, cvObject );
                }

                if ( usedInClasses.length == 0 ) {
                    cvObjectsByUsedInClass.put(NO_CLASS, cvObject );
                }
            }
        }

        // topics
        final List<IntactCvTerm>publicationTopics = getSortedTopicList("uk.ac.ebi.intact.model.Publication", cvObjectsByUsedInClass);
        final List<IntactCvTerm>experimentTopics = getSortedTopicList("uk.ac.ebi.intact.model.Experiment", cvObjectsByUsedInClass);
        final List<IntactCvTerm>interactionTopics = getSortedTopicList( "uk.ac.ebi.intact.model.Interaction", cvObjectsByUsedInClass);
        final List<IntactCvTerm>interactorTopics = getSortedTopicList( "uk.ac.ebi.intact.model.Interactor", cvObjectsByUsedInClass);
        interactorTopics.addAll(getSortedTopicList("uk.ac.ebi.intact.model.NulceicAcid", cvObjectsByUsedInClass));
        interactorTopics.addAll(getSortedTopicList("uk.ac.ebi.intact.model.SmallMolecule", cvObjectsByUsedInClass));
        interactorTopics.addAll(getSortedTopicList("uk.ac.ebi.intact.model.Protein", cvObjectsByUsedInClass));
        final List<IntactCvTerm>participantTopics = getSortedTopicList( "uk.ac.ebi.intact.model.Component", cvObjectsByUsedInClass);
        final List<IntactCvTerm>featureTopics = getSortedTopicList( "uk.ac.ebi.intact.model.Feature", cvObjectsByUsedInClass);
        final List<IntactCvTerm>cvObjectTopics = getSortedTopicList( "uk.ac.ebi.intact.model.CvObject", cvObjectsByUsedInClass);
        final List<IntactCvTerm>complexTopics = getSortedTopicList(IntactComplex.class.getCanonicalName(), cvObjectsByClass);
        complexTopics.addAll(interactionTopics);
        final List<IntactCvTerm>noClassTopics = getSortedTopicList( NO_CLASS, cvObjectsByUsedInClass);

        // select items
        noClassSelectItems = createSelectItems(noClassTopics, null);

        SelectItemGroup noClassSelectItemGroup = new SelectItemGroup("Not classified");
        noClassSelectItemGroup.setSelectItems(noClassSelectItems.toArray(new SelectItem[noClassSelectItems.size()]));

        SelectItemGroup pubSelectItemGroup = new SelectItemGroup("Publication");
        List<SelectItem> pubTopicSelectItems = createSelectItems(publicationTopics, null);
        pubSelectItemGroup.setSelectItems(pubTopicSelectItems.toArray(new SelectItem[pubTopicSelectItems.size()]));

        SelectItemGroup expSelectItemGroup = new SelectItemGroup("Experiment");
        List<SelectItem> expTopicSelectItems = createSelectItems(experimentTopics, null);
        expSelectItemGroup.setSelectItems(expTopicSelectItems.toArray(new SelectItem[expTopicSelectItems.size()]));

        publicationTopicSelectItems = new ArrayList<SelectItem>();
        publicationTopicSelectItems.add( new SelectItem( null, "-- Select topic --", "-- Select topic --", false, false, true ) );
        publicationTopicSelectItems.add(pubSelectItemGroup);
        publicationTopicSelectItems.add(expSelectItemGroup);
        publicationTopicSelectItems.add(noClassSelectItemGroup);

        experimentTopicSelectItems = createSelectItems( experimentTopics, "-- Select topic --" );
        experimentTopicSelectItems.add(noClassSelectItemGroup);

        interactionTopicSelectItems = createSelectItems( interactionTopics, "-- Select topic --" );
        interactionTopicSelectItems.add(noClassSelectItemGroup);

        SelectItemGroup interactorSelectItemGroup = new SelectItemGroup("Interactor");
        interactorTopicSelectItems = createSelectItems(interactorTopics, null);
        interactorSelectItemGroup.setSelectItems(interactorTopicSelectItems.toArray(new SelectItem[interactorTopicSelectItems.size()]));

        interactorTopicSelectItems = new ArrayList<SelectItem>();
        interactorTopicSelectItems.add( new SelectItem( null, "-- Select topic --", "-- Select topic --", false, false, true ) );
        interactorTopicSelectItems.add(interactorSelectItemGroup);
        interactorTopicSelectItems.add(noClassSelectItemGroup);

        participantTopicSelectItems = createSelectItems( participantTopics, "-- Select topic --" );
        participantTopicSelectItems.add(noClassSelectItemGroup);

        featureTopicSelectItems = createSelectItems( featureTopics, "-- Select topic --" );
        featureTopicSelectItems.add(noClassSelectItemGroup);

        cvObjectTopicSelectItems = createSelectItems( cvObjectTopics, "-- Select topic --" );
        cvObjectTopicSelectItems.add(noClassSelectItemGroup);

        final List<IntactCvTerm> databases = getSortedList( IntactUtils.DATABASE_OBJCLASS, cvObjectsByClass);
        databaseSelectItems = createSelectItems(databases, "-- Select database --", "ECO:");

        final List<IntactCvTerm> qualifiers = getSortedList( IntactUtils.QUALIFIER_OBJCLASS, cvObjectsByClass);
        qualifierSelectItems = createSelectItems( qualifiers, "-- Select qualifier --" );

        final List<IntactCvTerm> aliasTypes = getSortedList( IntactUtils.ALIAS_TYPE_OBJCLASS, cvObjectsByClass);
        aliasTypeSelectItems = createSelectItems( aliasTypes, "-- Select type --" );

        final List<IntactCvTerm> interactionDetectionMethods = getSortedList( IntactUtils.INTERACTION_DETECTION_METHOD_OBJCLASS, cvObjectsByClass);
        interactionDetectionMethodSelectItems = createSelectItems( interactionDetectionMethods, "-- Select method --" );

        final List<IntactCvTerm> participantDetectionMethods = getSortedList( IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS, cvObjectsByClass);
        participantDetectionMethodSelectItems = createSelectItems( participantDetectionMethods, "-- Select method --" );

        final List<IntactCvTerm> participantExperimentalPreparations = getSortedList( IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS, cvObjectsByClass);
        participantExperimentalPreparationsSelectItems = createSelectItems( participantExperimentalPreparations, "-- Select experimental preparation --" );

        final List<IntactCvTerm> interactionTypes = getSortedList( IntactUtils.INTERACTION_TYPE_OBJCLASS, cvObjectsByClass);
        interactionTypeSelectItems = createSelectItems( interactionTypes, "-- Select type --" );

        final List<IntactCvTerm> interactorTypes = getSortedList( IntactUtils.INTERACTOR_TYPE_OBJCLASS, cvObjectsByClass);
        interactorTypeSelectItems = createSelectItems( interactorTypes, "-- Select type --" );

        final List<IntactCvTerm> experimentalRoles = getSortedList( IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS, cvObjectsByClass);
        // must have one experimental role
        experimentalRoleSelectItems = createExperimentalRoleSelectItems( experimentalRoles, null );

        final List<IntactCvTerm> biologicalRoles = getSortedList( IntactUtils.BIOLOGICAL_ROLE_OBJCLASS, cvObjectsByClass);
        // must have one biological role
        biologicalRoleSelectItems = createBiologicalRoleSelectItems( biologicalRoles, null );

        final List<IntactCvTerm> featureDetectionMethods = getSortedList( IntactUtils.FEATURE_METHOD_OBJCLASS, cvObjectsByClass);
        featureDetectionMethodSelectItems = createSelectItems( featureDetectionMethods, "-- Select method --" );

        final List<IntactCvTerm> featureTypes = getSortedList( IntactUtils.FEATURE_TYPE_OBJCLASS, cvObjectsByClass);
        featureTypeSelectItems = createSelectItems( featureTypes, "-- Select type --" );

        final List<IntactCvTerm> fuzzyTypes = getSortedList( IntactUtils.RANGE_STATUS_OBJCLASS, cvObjectsByClass);
        fuzzyTypeSelectItems = createSelectItems( fuzzyTypes, "-- Select type --" );

        final List<IntactCvTerm> cellTypes = getSortedList( IntactUtils.CELL_TYPE_OBJCLASS, cvObjectsByClass);
        cellTypeSelectItems = createSelectItems( cellTypes, "-- Select cell type --" );

        final List<IntactCvTerm> tissues = getSortedList( IntactUtils.TISSUE_OBJCLASS, cvObjectsByClass);
        tissueSelectItems = createSelectItems( tissues, "-- Select tissue --" );

        final List<IntactCvTerm> parameterTypes = getSortedList( IntactUtils.PARAMETER_TYPE_OBJCLASS, cvObjectsByClass);
        parameterTypeSelectItems = createSelectItems( parameterTypes, "-- Select type --" );

        final List<IntactCvTerm> parameterUnits = getSortedList( IntactUtils.UNIT_OBJCLASS, cvObjectsByClass);
        parameterUnitSelectItems = createSelectItems( parameterUnits, "-- Select unit --" );

        final List<IntactCvTerm> confidenceTypes = getSortedList( IntactUtils.CONFIDENCE_TYPE_OBJCLASS, cvObjectsByClass);
        confidenceTypeSelectItems = createSelectItems( confidenceTypes, "-- Select type --" );

        // evidence type
        IntactCvTerm evidenceTypeParent = getIntactDao().getCvTermDao().getByMIIdentifier("MI:1331", IntactUtils.DATABASE_OBJCLASS);
        if (evidenceTypeParent != null){
            loadChildren(evidenceTypeParent, evidenceTypeSelectItems, false, new HashSet<String>());
        }

        // complex type
        IntactCvTerm complexTypeParent = getIntactDao().getCvTermDao().getByMIIdentifier("MI:0314", IntactUtils.INTERACTOR_TYPE_OBJCLASS);
        SelectItem item = complexTypeParent != null ? createSelectItem(complexTypeParent, true):null;
        if (item != null){
            complexTypeSelectItems.add(item);
        }
        if (complexTypeParent != null){
            loadChildren(complexTypeParent, complexTypeSelectItems, false, new HashSet<String>());
        }

        // feature role
        IntactCvTerm roleParent = getIntactDao().getCvTermDao().getByMIIdentifier("MI:0925", IntactUtils.TOPIC_OBJCLASS);
        SelectItem item2 = roleParent != null ? createSelectItem(roleParent, false):null;
        if (item2 != null){
            featureRoleSelectItems.add(item2);
        }
        if (roleParent != null){
            loadChildren(roleParent, featureRoleSelectItems, false, new HashSet<String>());
        }

        // add all obsoletes and hidden now to the map of class objects
        List<IntactCvTerm> allCvs = getIntactDao().getCvTermDao().getAll();
        for ( IntactCvTerm cvObject : allCvs ) {
            if (!cvObjectsByClass.containsKey(cvObject.getObjClass())){
                cvObjectsByClass.put(cvObject.getObjClass(), cvObject);
            }
        }

        isInitialised=true;
    }

    public List<IntactCvTerm> getSortedTopicList( String key, Multimap<String, IntactCvTerm> topicMultimap ) {
        if ( topicMultimap.containsKey( key ) ) {
            List<IntactCvTerm> list = new ArrayList<IntactCvTerm>( topicMultimap.get( key ) );

            Collections.sort( list, new CvObjectComparator() );
            return list;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<IntactCvTerm> getSortedList( String key, Multimap<String, IntactCvTerm> classMultimap ) {
        if ( classMultimap.containsKey( key ) ) {
            List<IntactCvTerm> list = new ArrayList<IntactCvTerm>( classMultimap.get( key ) );
            Collections.sort( list, new CvObjectComparator() );
            return list;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private String[] findUsedInClass( IntactCvTerm cvObject ) {
        final Annotation annotation = AnnotationUtils.collectFirstAnnotationWithTopic(cvObject.getAnnotations(), null, USED_IN_CLASS);

        if ( annotation != null && annotation.getValue() != null) {
            String annotText = annotation.getValue();
            annotText = annotText.replaceAll( " ", "" );
            return annotText.split( "," );
        } else {
            return new String[0];
        }
    }

    public List<SelectItem> createSelectItems( Collection<IntactCvTerm> cvObjects, String noSelectionText ) {
        List<SelectItem> selectItems = new CopyOnWriteArrayList<SelectItem>();

        if ( noSelectionText != null ) {
            selectItems.add( new SelectItem( null, noSelectionText, noSelectionText, false, false, true ) );
        }

        for ( IntactCvTerm cvObject : cvObjects ) {
            selectItems.add( createSelectItem( cvObject ) );
        }

        return selectItems;
    }

    public List<SelectItem> createSelectItems(Collection<IntactCvTerm> cvObjects, String noSelectionText, String idPrefixToIgnore) {
        List<SelectItem> selectItems = new CopyOnWriteArrayList<SelectItem>();

        if ( noSelectionText != null ) {
            selectItems.add( new SelectItem( null, noSelectionText, noSelectionText, false, false, true ) );
        }

        for ( IntactCvTerm cvObject : cvObjects ) {
            boolean ignore = false;
            if (!cvObject.getIdentifiers().isEmpty()){
                for (Xref ref : cvObject.getIdentifiers()){
                    if (ref.getId().startsWith(idPrefixToIgnore)){
                        ignore = true;
                    }
                }
            }
            if (!ignore){
                selectItems.add( createSelectItem( cvObject ) );

            }
        }

        return selectItems;
    }

    public List<SelectItem> createExperimentalRoleSelectItems( Collection<IntactCvTerm> cvObjects, String noSelectionText ) {
        List<SelectItem> selectItems = new CopyOnWriteArrayList<SelectItem>();

        if ( noSelectionText != null ) {
            selectItems.add( new SelectItem( null, noSelectionText, noSelectionText, false, false, true ) );
        }

        for ( IntactCvTerm cvObject : cvObjects ) {
            selectItems.add( createSelectItem( cvObject ) );

            if (CvTermUtils.isCvTerm(cvObject, Participant.UNSPECIFIED_ROLE_MI, Participant.UNSPECIFIED_ROLE)){
                defaultExperimentalRole = cvObject;
            }
        }

        return selectItems;
    }

    public List<SelectItem> createBiologicalRoleSelectItems( Collection<IntactCvTerm> cvObjects, String noSelectionText ) {
        List<SelectItem> selectItems = new CopyOnWriteArrayList<SelectItem>();

        if ( noSelectionText != null ) {
            selectItems.add( new SelectItem( null, noSelectionText, noSelectionText, false, false, true ) );
        }

        for ( IntactCvTerm cvObject : cvObjects ) {
            selectItems.add( createSelectItem( cvObject ) );

            if (CvTermUtils.isCvTerm(cvObject, Participant.UNSPECIFIED_ROLE_MI, Participant.UNSPECIFIED_ROLE)){
                defaultBiologicalRole = cvObject;
            }
        }

        return selectItems;
    }

    private SelectItem createSelectItem( IntactCvTerm cv ) {

        boolean obsolete = !AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getAnnotations(), OBSOLETE_MI_REF, OBSOLETE).isEmpty();
        return new SelectItem( cv, cv.getShortName()+((obsolete? " (obsolete)" : "")), cv.getFullName());
    }

    public IntactCvTerm findCvObjectByAc( String ac ) {
        return acCvObjectMap.get( ac );
    }

    public IntactCvTerm findCvObjectByIdentifier( String objClass, String identifier ) {
        return allCvObjectMap.get( new CvKey(identifier, objClass) );
    }

    public IntactCvTerm findCvObject( String clazz, String idOrLabel ) {
        CvKey keyId = new CvKey( idOrLabel, clazz );
        CvKey keyLabel = new CvKey( idOrLabel, clazz );

        if ( allCvObjectMap.containsKey( keyId ) ) {
            return allCvObjectMap.get( keyId );
        } else if ( allCvObjectMap.containsKey( keyLabel ) ) {
            return allCvObjectMap.get( keyLabel );
        }

        return null;
    }

    public class CvKey {
        private String id;
        private String className;

        private CvKey( String id, String clazz ) {
            this.id = id;
            this.className = clazz;
        }

        public String getId() {
            return id;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            CvKey cvKey = ( CvKey ) o;

            if ( className != null ? !className.equals( cvKey.className ) : cvKey.className != null ) return false;
            if ( id != null ? !id.equals( cvKey.id ) : cvKey.id != null ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + ( className != null ? className.hashCode() : 0 );
            return result;
        }
    }

    public List<SelectItem> getPublicationTopicSelectItems() {
        return publicationTopicSelectItems;
    }

    public List<SelectItem> getExperimentTopicSelectItems() {
        return experimentTopicSelectItems;
    }

    public List<SelectItem> getInteractionTopicSelectItems() {
        return interactionTopicSelectItems;
    }

    public List<SelectItem> getInteractorTopicSelectItems() {
        return interactorTopicSelectItems;
    }

    public List<SelectItem> getParticipantTopicSelectItems() {
        return participantTopicSelectItems;
    }

    public List<SelectItem> getFeatureTopicSelectItems() {
        return featureTopicSelectItems;
    }

    public List<SelectItem> getCvObjectTopicSelectItems() {
        return cvObjectTopicSelectItems;
    }

    public List<SelectItem> getComplexTopicSelectItems() {
        return complexTopicSelectItems;
    }

    public List<SelectItem> getFeatureRoleSelectItems() {
        return featureRoleSelectItems;
    }

    public List<SelectItem> getComplexTypeSelectItems() {
        return complexTypeSelectItems;
    }

    public List<SelectItem> getEvidenceTypeSelectItems() {
        return evidenceTypeSelectItems;
    }

    public List<SelectItem> getDatabaseSelectItems() {
        return databaseSelectItems;
    }

    public List<SelectItem> getQualifierSelectItems() {
        return qualifierSelectItems;
    }

    public List<SelectItem> getAliasTypeSelectItems() {
        return aliasTypeSelectItems;
    }

    public List<SelectItem> getInteractionDetectionMethodSelectItems() {
        return interactionDetectionMethodSelectItems;
    }

    public List<SelectItem> getParticipantDetectionMethodSelectItems() {
        return participantDetectionMethodSelectItems;
    }

    public List<SelectItem> getParticipantExperimentalPreparationsSelectItems() {
        return participantExperimentalPreparationsSelectItems;
    }

    public List<SelectItem> getInteractionTypeSelectItems() {
        return interactionTypeSelectItems;
    }

    public List<SelectItem> getInteractorTypeSelectItems() {
        return interactorTypeSelectItems;
    }

    public List<SelectItem> getFeatureDetectionMethodSelectItems() {
        return featureDetectionMethodSelectItems;
    }

    public List<SelectItem> getFeatureTypeSelectItems() {
        return featureTypeSelectItems;
    }

    public List<SelectItem> getBiologicalRoleSelectItems() {
        return biologicalRoleSelectItems;
    }

    public List<SelectItem> getExperimentalRoleSelectItems() {
        return experimentalRoleSelectItems;
    }

    public List<SelectItem> getFuzzyTypeSelectItems() {
        return fuzzyTypeSelectItems;
    }

    public List<SelectItem> getTissueSelectItems() {
        return tissueSelectItems;
    }

    public List<SelectItem> getCellTypeSelectItems() {
        return cellTypeSelectItems;
    }

    public List<SelectItem> getParameterTypeSelectItems() {
        return parameterTypeSelectItems;
    }

    public List<SelectItem> getParameterUnitSelectItems() {
        return parameterUnitSelectItems;
    }

    public List<SelectItem> getConfidenceTypeSelectItems() {
        return confidenceTypeSelectItems;
    }

    public List<SelectItem> getNoClassSelectItems() {
        return noClassSelectItems;
    }

    public IntactCvTerm getDefaultExperimentalRole() {
        return defaultExperimentalRole;
    }

    public IntactCvTerm getDefaultBiologicalRole() {
        return defaultBiologicalRole;
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public static class CvObjectComparator implements Comparator<IntactCvTerm> {
        @Override
        public int compare( IntactCvTerm o1, IntactCvTerm o2 ) {
            if ( o1 == null ) {
                return 1;
            }

            if ( o2 == null ) {
                return -1;
            }

            return o1.getShortName().compareTo(o2.getShortName());
        }
    }

    private SelectItem createSelectItem( IntactCvTerm cv, boolean ignoreHidden ) {
        if (!ignoreHidden && AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getAnnotations(), null, "hidden").isEmpty()){
            acCvObjectMap.put(cv.getAc(), cv);
            boolean obsolete = !AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getAnnotations(), OBSOLETE_MI_REF, OBSOLETE).isEmpty();
            return new SelectItem( cv, cv.getShortName()+((obsolete? " (obsolete)" : "")),
                    cv.getFullName() + (cv.getMIIdentifier() != null ? "("+cv.getMIIdentifier()+")":""));
        }
        else if (ignoreHidden){
            acCvObjectMap.put(cv.getAc(), cv);
            boolean obsolete = !AnnotationUtils.collectAllAnnotationsHavingTopic(cv.getAnnotations(), OBSOLETE_MI_REF, OBSOLETE).isEmpty();
            return new SelectItem( cv, cv.getShortName()+((obsolete? " (obsolete)" : "")),
                    cv.getFullName()+ (cv.getMIIdentifier() != null ? "("+cv.getMIIdentifier()+")":""));
        }
        return null;
    }

    private List<String> loadChildren(IntactCvTerm parent, List<SelectItem> selectItems, boolean ignoreHidden, Set<String> processedAcs){
        List<String> list = new ArrayList<String>(parent.getChildren().size());
        for (OntologyTerm child : parent.getChildren()){
            IntactCvTerm cv = (IntactCvTerm)child;
            if (!processedAcs.contains(cv.getAc())){
                processedAcs.add(cv.getAc());
                SelectItem item = createSelectItem(cv, ignoreHidden);
                if (item != null){
                    list.add(cv.getAc());
                    selectItems.add(item);
                }
            }

            if (!cv.getChildren().isEmpty()){
                list.addAll(loadChildren(cv, selectItems, ignoreHidden, processedAcs));
            }
        }
        return list;
    }
}
