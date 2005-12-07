/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTerm;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTermAnnotation;
import uk.ac.ebi.intact.util.controlledVocab.model.CvTermXref;
import uk.ac.ebi.intact.util.controlledVocab.model.IntactOntology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Class handling the update of CvObject.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16-Oct-2005</pre>
 */
public class UpdateCVs {

    /////////////////////////////////
    // Update of the IntAct Data

    /**
     * Global Update of the IntAct CVs, based upon an Ontology object. This is an iterative process that will update
     * each supported CVs independantely.
     *
     * @param ontology Controlled vocabularies upon which we do the update.
     *
     * @throws IntactException upon data access error
     */
    public static void update( IntactOntology ontology ) throws IntactException {

        // try to update class by class
        Collection allTypes = ontology.getTypes();
        for ( Iterator iterator = allTypes.iterator(); iterator.hasNext(); ) {
            Class aClass = (Class) iterator.next();

            // single class update
            update( ontology, aClass );
        }
    }

    /**
     * Update a specific supported Controlled Vocabulary. Note: The Ontology contains a mapping Class -> CV.
     *
     * @param ontology
     * @param cvObjectClass
     *
     * @throws IntactException
     */
    public static void update( IntactOntology ontology,
                               Class cvObjectClass ) throws IntactException {

        System.out.println( "====================================================" );
        System.out.println( "Updating " + cvObjectClass.getName() );
        System.out.println( "====================================================" );

        /**
         * ALGO
         * ----
         *
         * Iterate over all new Terms (OBO)
         * for each
         *     check if we have it in IntAct (UpdateTerm consists in updating shortlabel, fullname,
         *                                                                    annotations, xrefs, aliases)
         *          Yes: updateTerm
         *          No:  create basic object, then updateTerm
         *
         * Iterate over all new Terms (OBO)
         * for each
         *     check children and apply hierarchy to IntAct
         */
        IntactHelper helper = new IntactHelper();
        Collection intactTerms = helper.search( cvObjectClass, "ac", null );
        System.out.println( "\t" + intactTerms.size() + " term(s) found in IntAct." );

        ///////////////////////////////////////////////////////
        // 1. Indexing of the IntAct terms by MI number
        Map intactIndex = new HashMap( intactTerms.size() );
        for ( Iterator iterator = intactTerms.iterator(); iterator.hasNext(); ) {
            CvObject cvObject = (CvObject) iterator.next();
            String psi = getPsiId( cvObject );

            // Bear in mind that only CV that already exists are indexed here, newly created ones will be indexed later.
            if ( psi != null ) {
                intactIndex.put( psi, cvObject );
            } else {
                System.err.println( "Could not index ( " + cvObject.getShortLabel() + " doesn't have an MI ID)." );
            }
        }

        ////////////////////////////////////////
        // 2. update of the terms' content
        Collection oboTerms = ontology.getCvTerms( cvObjectClass );
        System.out.println( "\t" + oboTerms.size() + " term(s) loaded from definition file." );
        for ( Iterator iterator = oboTerms.iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();
            CvObject cvObject = (CvObject) intactIndex.get( cvTerm.getId() );

            helper.startTransaction( BusinessConstants.JDBC_TX );

            if ( cvObject == null ) {

                // search by shortlabel
                cvObject = (CvObject) helper.getObjectByLabel( cvObjectClass, cvTerm.getShortName() );

                if ( cvObject == null ) {

                    try {
                        // create a new object using refection
                        Constructor constructor =
                                cvObjectClass.getConstructor( new Class[]{ Institution.class, String.class } );

                        cvObject = (CvObject)
                                constructor.newInstance( new Object[]{ helper.getInstitution(), cvTerm.getShortName() } );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        continue;
                    }

                    // persist it
                    helper.create( cvObject );
                    String className = cvObjectClass.getName().substring( cvObjectClass.getName().lastIndexOf( '.' ) + 1,
                                                                          cvObjectClass.getName().length() );
                    System.out.println( "\tCreating " + className + "( " + cvObject.getShortLabel() + " )" );

                    // add that new term in the index
                    intactIndex.put( cvTerm.getId(), cvObject );
                }
            }

            // update its content
            try {
                updateTerm( helper, cvObject, cvTerm );

                // end the transaction as no error occured.
                helper.finishTransaction();
            } catch ( Throwable t ) {

                if ( helper.isInTransaction() ) {
                    System.err.print( "Rolling back transaction..." );
                    helper.undoTransaction();
                    System.err.println( "done" );
                }

                t.printStackTrace();
            }
        } // end of update of the terms' content

        ///////////////////////////////////////
        // 3. Update of the hierarchy
        System.out.println( "\tUpdating Vocabulary hierarchy..." );
        boolean stopUpdate = false;
        for ( Iterator iterator = oboTerms.iterator(); iterator.hasNext() && !stopUpdate; ) {
            CvTerm cvTerm = (CvTerm) iterator.next();

            // Get the IntAct equivalent from the index (it should have been either created or updated)
            CvObject cvObject = (CvObject) intactIndex.get( cvTerm.getId() );

            if ( cvObject == null ) {
                // that should never happen !! Exception
                System.err.println( "ERROR: Could not find " + cvTerm.getId() + " - " + cvTerm.getShortName() );
                continue;
            }

            // check that the term is a DAG (ie. CvDagObject), if not, skip the hierarchy update.
            CvDagObject dagObject = null;
            if ( cvObject instanceof CvDagObject ) {
                dagObject = (CvDagObject) cvObject;
            } else {
                // we do not have heterogeneous collection here, we can stop the hierarchy update here.
                System.out.println( "\t" + cvObject.getClass().getName() + " is not a DAG, skip hierarchy update." );
                stopUpdate = true;
                continue;
            }

            // keep in that collection the term that haven't been read yet from the obo definition
            Collection allChildren = new ArrayList( dagObject.getChildren() );

            // browse all direct children of the current term
            for ( Iterator iterator2 = cvTerm.getChildren().iterator(); iterator2.hasNext(); ) {
                CvTerm child = (CvTerm) iterator2.next();

                // get corresponding IntAct child
                CvDagObject intactChild = (CvDagObject) intactIndex.get( child.getId() );

                // if the relationship doesn't exists, create it
                if ( !( dagObject.getChildren().contains( intactChild ) ) ) {
                    // add that relation
                    dagObject.addChild( intactChild );

                    System.out.println( "\t\tAdding Relationship[(" + dagObject.getAc() + ") " + dagObject.getShortLabel() +
                                        " ---child---> (" + intactChild.getAc() + ") " + intactChild.getShortLabel() + "]" );

                    // TODO may need to do at at SQL level
                    helper.update( dagObject );
                    helper.update( intactChild );
                }

                allChildren.remove( intactChild );
            }

            // delete all relationships that were not described in the OBO file.
            for ( Iterator iterator2 = allChildren.iterator(); iterator2.hasNext(); ) {
                CvDagObject child = (CvDagObject) iterator2.next();

                try {
                    Connection connection = helper.getJDBCConnection();
                    Statement statement = connection.createStatement();
                    statement.execute( "DELETE FROM ia_cv2cv " +
                                       "WHERE parent_ac = '" + dagObject.getAc() + "' AND " +
                                       "      child_ac = '" + child.getAc() + "'" );
                    statement.close();

                    // BUG: when a CvFeatureType is used in the data, and we try to delete some relationship,
                    //      OJB deletes the CvObject itself ... which causes a constraint violation on foreign key.
                    //      P6Spy allowed to debug that.
                    // WORK AROUND: do that data update at SQL level, dodgy but at least it works :(

//                    dagObject.removeChild( child );
//                    helper.update( dagObject );
//                    helper.update( child );

                    System.out.println( "\t\tRemoving Relationship[(" + dagObject.getAc() + ") " + dagObject.getShortLabel() +
                                        " ---child---> (" + child.getAc() + ") " + child.getShortLabel() + "]" );

                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        } // end of update of the hierarchy

        ///////////////////////////////////////
        // 4. Flag root term as hidden
        Collection roots = ontology.getRoots( cvObjectClass );
        CvTopic hidden = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.HIDDEN );

        if ( hidden == null ) {
            System.err.println( "WARNING: The CvTopic(" + CvTopic.HIDDEN + ") could not be found in IntAct. " +
                                "Skip flagging of the root terms." );
        } else {
            for ( Iterator iterator = roots.iterator(); iterator.hasNext(); ) {
                CvTerm rootTerm = (CvTerm) iterator.next();

                // get the intact term
                CvObject root = (CvObject) intactIndex.get( rootTerm.getId() );

                if ( root == null ) {
                    System.err.println( "ERROR: the term " + rootTerm.getId() + " should have been found in IntAct." );
                } else {

                    addUniqueAnnotation( helper, root, hidden, null );
                }
            }
        }
    }

    /**
     * Go through all obsolete OBO terms and try to update the correcponding IntAct CV terms. The corresponding IntAct
     * terms is found by PSI-MI ID.
     *
     * @param ontology the ontology from which we will get the list of obsolete terms
     *
     * @throws IntactException if something goes wrong
     */
    public static void updateObsoleteTerms( IntactOntology ontology ) throws IntactException {

        System.out.println( "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
        System.out.println( "Updating Obsolete Terms" );
        IntactHelper helper = new IntactHelper();

        // note: we don't create term that weren't existing in IntAct before if they are already obsolete in PSI
        Collection obsoleteTerms = ontology.getObsoleteTerms();
        CvTopic obsolete = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.OBSOLETE );
        boolean error = false;
        for ( Iterator iterator = obsoleteTerms.iterator(); iterator.hasNext(); ) {
            CvTerm cvTerm = (CvTerm) iterator.next();

            String id = cvTerm.getId();
            CvObject cvObject = (CvObject) helper.getObjectByPrimaryId( CvObject.class, id );
            if ( cvObject == null ) {
                System.err.println( "ERROR: the term " + id + " (" + cvTerm.getShortName() + ") should have been found in IntAct." );
                error = true;
            } else {
                addUniqueAnnotation( helper, cvObject, obsolete, cvTerm.getObsoleteMessage() );
            }
        }

        if ( error ) {
            System.out.println( "Note: There is a possibility that all terms that are not found weren't in IntAct before they became obsolete in PSI-MI." );
        }

    }

    /**
     * Add an annotation in a CvObject if it is not in there yet. The CvTopic and the text of the annotation are given
     * as parameters so the methods is flexible.
     *
     * @param helper   database access
     * @param cvObject the CvObject in which we want to add the annotation
     * @param topic    the topic of the annotation. must not be null.
     * @param text     the text of the annotation. Can be null.
     *
     * @throws IntactException if something goes wrong during the update.
     */
    private static void addUniqueAnnotation( IntactHelper helper,
                                             CvObject cvObject,
                                             CvTopic topic,
                                             String text ) throws IntactException {
        if ( topic == null ) {
            System.err.println( "WARNING: The CvTopic(" + CvTopic.HIDDEN + ") could not be found in IntAct. " +
                                "Skip flagging of the root terms." );
        } else {

            Annotation hiddenAnnot = new Annotation( helper.getInstitution(), topic );
            hiddenAnnot.setAnnotationText( text );

            if ( cvObject.getAnnotations().contains( hiddenAnnot ) ) {
                System.out.println( "'" + cvObject.getShortLabel() + "' was already flagged " + topic.getShortLabel() + "." );

            } else {

                // add the annotation.
                helper.create( hiddenAnnot );

                cvObject.addAnnotation( hiddenAnnot );
                helper.update( cvObject );
                System.out.println( "Flagged '" + cvObject.getShortLabel() + "' as " + topic.getShortLabel() + "." );
            }
        }
    }

    /**
     * Browse the CvObject's Xref and find (if possible) the primary ID of the first Xref( CvDatabase( psi-mi ) ).
     *
     * @param cvObject the Object we are introspecting.
     *
     * @return a PSI ID or null is none is found.
     */
    private static String getPsiId( CvObject cvObject ) {

        for ( Iterator iterator = cvObject.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();

            if ( CvDatabase.PSI_MI.equals( xref.getCvDatabase().getShortLabel() ) &&
                 CvXrefQualifier.IDENTITY.equals( xref.getCvXrefQualifier().getShortLabel() ) ) {

                return xref.getPrimaryId();
            }
        }

        return null;
    }

    /**
     * Get the requested CvObject from the Database, create it if it doesn't exist. It is searched by shortlabel. If not
     * found, a CvObject is create using the information given (shortlabel)
     *
     * @param helper     access to the database
     * @param clazz      the CvObject concrete type
     * @param shortlabel shortlabel of the term
     *
     * @return a CvObject persistent in the backend.
     *
     * @throws IntactException          is an error occur while writting on the database.
     * @throws IllegalArgumentException if the class given is not a concrete type of CvObject (eg. CvDatabase)
     */
    public static CvObject getCvObject( IntactHelper helper,
                                        Class clazz,
                                        String shortlabel ) throws IntactException {
        return getCvObject( helper, clazz, shortlabel, null );
    }

    /**
     * Get the requested CvObject from the Database, create it if it doesn't exist. It is first searched by Xref(psi-mi)
     * if an ID is given, then by shortlabel. If not found, a CvObject is create using the information given
     * (shortlabel, then potentially a PSI ID)
     *
     * @param helper     access to the database
     * @param clazz      the CvObject concrete type
     * @param shortlabel shortlabel of the term
     * @param mi         MI id of the term
     *
     * @return a CvObject persistent in the backend.
     *
     * @throws IntactException          is an error occur while writting on the database.
     * @throws IllegalArgumentException if the class given is not a concrete type of CvObject (eg. CvDatabase)
     */
    public static CvObject getCvObject( IntactHelper helper,
                                        Class clazz,
                                        String shortlabel,
                                        String mi ) throws IntactException {

        // Check that the given class is a CvObject or one if its sub-type.
        if ( !CvObject.class.isAssignableFrom( clazz ) ) {
            throw new IllegalArgumentException( "The given class must be a sub type of CvObject" );
        }

        // Search by MI
        CvObject cv = (CvObject) helper.getObjectByPrimaryId( clazz, mi );

        if ( cv == null ) {
            // Search by Name
            cv = (CvObject) helper.getObjectByLabel( clazz, shortlabel );
        }

        if ( cv == null ) {
            // create it
            try {

                // create a new object using refection
                Constructor constructor = clazz.getConstructor( new Class[]{ Institution.class, String.class } );
                cv = (CvObject) constructor.newInstance( new Object[]{ helper.getInstitution(), shortlabel } );

                // persist it
                helper.create( cv );

                // create MI Xref if necessary
                if ( mi != null && mi.startsWith( "MI:" ) ) {

                    CvDatabase psi = null;
                    if ( mi.equals( CvDatabase.PSI_MI_MI_REF ) ) {
                        // possible infinite loop here !!
                        psi = (CvDatabase) cv;
                    } else {
                        psi = (CvDatabase) getCvObject( helper, CvDatabase.class,
                                                        CvDatabase.PSI_MI,
                                                        CvDatabase.PSI_MI_MI_REF );
                    }

                    CvXrefQualifier identity = null;
                    if ( mi.equals( CvXrefQualifier.IDENTITY_MI_REF ) ) {
                        // possible infinite loop here !!
                        identity = (CvXrefQualifier) cv;
                    } else {
                        identity = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class,
                                                                  CvXrefQualifier.IDENTITY,
                                                                  CvXrefQualifier.IDENTITY_MI_REF );
                    }

                    Xref xref = new Xref( helper.getInstitution(), psi, mi, null, null, identity );

                    cv.addXref( xref );
                    helper.create( xref );
                    System.out.println( "Created required term: " + shortlabel + " (" + mi + ")" );
                }
            } catch ( Exception e ) {
                // that's should not happen, but just in case...
                e.printStackTrace();
            }
        }

        return cv;
    }

    /**
     * Update an IntAct CV term based on the definition read externally, and contained in a CvTerm.
     *
     * @param helper   data access.
     * @param cvObject the IntAct CV to update.
     * @param cvTerm   the new definition of that CV.
     *
     * @throws IntactException if error occur.
     */
    private static void updateTerm( IntactHelper helper,
                                    CvObject cvObject,
                                    CvTerm cvTerm ) throws IntactException {

        // we know they have the same ID

        System.out.println( "\t Updating CV: " + cvTerm.getShortName() );

        boolean needsUpdate = false;

        // names
        if ( !cvObject.getShortLabel().equals( cvTerm.getShortName() ) ) {
            cvObject.setShortLabel( cvTerm.getShortName() );
            needsUpdate = true;
            System.out.println( "\t\t Updated shortlabel (" + cvTerm.getShortName() + ")" );
        }


        if ( cvObject.getFullName() != null ) {
            if ( !cvObject.getFullName().equals( cvTerm.getFullName() ) ) {
                cvObject.setFullName( cvTerm.getFullName() );
                System.out.println( "\t\t Updated fullname (" + cvTerm.getShortName() + ")" );
                needsUpdate = true;
            }
        } else {
            // cvObject.getFullName() == null
            if ( cvTerm.getFullName() != null ) {
                cvObject.setFullName( cvTerm.getFullName() );
                needsUpdate = true;
                System.out.println( "\t\t Updated fullname (" + cvTerm.getShortName() + ")" );
            }
        }

        // Xref psi-mi/identity
        String id = getPsiId( cvObject );
        if ( id == null ) {
            // add missing Xref
            CvDatabase psi = (CvDatabase) getCvObject( helper, CvDatabase.class, CvDatabase.PSI_MI, CvDatabase.PSI_MI_MI_REF );
            CvXrefQualifier identity = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class,
                                                                      CvXrefQualifier.IDENTITY,
                                                                      CvXrefQualifier.IDENTITY_MI_REF );
            Xref xref = new Xref( helper.getInstitution(), psi, cvTerm.getId(), null, null, identity );
            cvObject.addXref( xref );
            helper.create( xref );
            System.out.println( "\t\t Added PSI Xref (" + cvTerm.getId() + ")" );
        }

        // definition -- stored as Annotation( CvTopic( definition ) ) in IntAct
        String definition = cvTerm.getDefinition();
        CvTopic definitionTopic = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.DEFINITION );
        Annotation definitionAnnot = getUniqueAnnotation( helper, cvObject, definitionTopic );
        if ( definitionAnnot == null ) {

            definitionAnnot = new Annotation( helper.getInstitution(), definitionTopic, definition );
            helper.create( definitionAnnot );

            cvObject.addAnnotation( definitionAnnot );
            helper.update( cvObject );

            System.out.println( "\t\t Creating Annotation( definition, '" + definitionAnnot.getAnnotationText() + "' )" );

        } else {

            // TODO why grna's definition keeps getting updated ???

            if ( definitionAnnot.getAnnotationText() == null ) {
                if ( definition != null ) {
                    definitionAnnot.setAnnotationText( definition );
                    helper.update( definitionAnnot );
                    System.out.println( "definitionAnnot.getAnnotationText() = '" + definitionAnnot.getAnnotationText() + "'" );
                    System.out.println( "definition = '" + definition + "'" );
                    System.out.println( "\t\t Updating Annotation 1 ( definition ) (" + cvTerm.getShortName() + ")" );
                }
            } else {
                if ( !definitionAnnot.getAnnotationText().equals( definition ) ) {
                    definitionAnnot.setAnnotationText( definition );
                    helper.update( definitionAnnot );
                    System.out.println( "definitionAnnot.getAnnotationText() = '" + definitionAnnot.getAnnotationText() + "'" );
                    System.out.println( "definition = '" + definition + "'" );
                    System.out.println( "\t\t Updating Annotation 2 ( definition ) (" + cvTerm.getShortName() + ")" );
                }
            }
        }

        // Annotations
        Collection annotations = cvTerm.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            CvTermAnnotation annotationBean = (CvTermAnnotation) iterator.next();

            String annot = annotationBean.getAnnotation();
            if ( annot != null && annot.length() > 0 ) {
                // we have a comment to add
                CvTopic comment = (CvTopic) getCvObject( helper, CvTopic.class, "comment" );
                Annotation annotation = new Annotation( helper.getInstitution(), comment, annot );
                if ( ! cvObject.getAnnotations().contains( annotation ) ) {
                    helper.create( annotation );
                    cvObject.addAnnotation( annotation );
                    helper.update( cvObject );
                    System.out.println( "\t\t Created Annotation( " + comment.getShortLabel() + ", '" + annot + "' )" );
                }
            }
        }

        // Obsolete term
        CvTopic obsoleteTopic = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.OBSOLETE );
        Annotation obsoleteAnnotation = getUniqueAnnotation( helper, cvObject, obsoleteTopic );
        if ( cvTerm.isObsolete() ) {
            if ( obsoleteAnnotation != null ) {
                if ( obsoleteAnnotation.getAnnotationText() != null ) {
                    if ( !obsoleteAnnotation.getAnnotationText().equals( cvTerm.getObsoleteMessage() ) ) {
                        obsoleteAnnotation.setAnnotationText( cvTerm.getObsoleteMessage() );
                        helper.update( obsoleteAnnotation );
                        System.out.println( "Updated Obsolete flag text on " + cvObject.getShortLabel() );
                    }
                } else {
                    if ( cvTerm.getObsoleteMessage() != null ) {
                        obsoleteAnnotation.setAnnotationText( cvTerm.getObsoleteMessage() );
                        helper.update( obsoleteAnnotation );
                        System.out.println( "Updated Obsolete flag text on " + cvObject.getShortLabel() );
                    }
                }
            }
        } else {
            // not obsolete
            if ( obsoleteAnnotation != null ) {
                cvObject.removeAnnotation( obsoleteAnnotation );
                helper.update( cvObject );
                helper.delete( obsoleteAnnotation );
                System.out.println( "Delete Obsolete flag on " + cvObject.getShortLabel() );
            }
        }

        // annotations -- we don't delete any Annotations, just adding missing ones
        // TODO identify Annotation that should not be duplicated, and do an update of these.
//        for ( Iterator iterator = cvTerm.getAnnotations().iterator(); iterator.hasNext(); ) {
//            CvTermAnnotation cvTermAnnotation = (CvTermAnnotation) iterator.next();
//
//            CvTopic topic = (CvTopic) helper.getObjectByLabel( CvTopic.class, cvTermAnnotation.getTopic() );
//            if ( topic == null ) {
//                System.err.println( "Could not find the CvTopic( " + cvTermAnnotation.getTopic() + " )." );
//                continue;
//            }
//
//            Annotation annot = new Annotation( helper.getInstitution(), topic, cvTermAnnotation.getAnnotation() );
//
//            if ( !cvObject.getAnnotations().contains( annot ) ) {
//                cvObject.addAnnotation( annot );
//                helper.create( annot );
//                System.out.println( "\t\t Creating Annotation( " + topic.getShortLabel() + " ) (" + cvTerm.getShortName() + ")" );
//            }
//        }

        // Database Mapping PSI to IntAct
        Map dbMapping = new HashMap();
        dbMapping.put( "PMID", CvDatabase.PUBMED );
        dbMapping.put( "RESID", CvDatabase.RESID );
        dbMapping.put( "SO", CvDatabase.SO );
        dbMapping.put( "GO", CvDatabase.GO );
        dbMapping.put( "PMID for application instance", CvDatabase.PUBMED ); // need to put a specific CvXrefQualifier

        // xrefs -- we don't delete any Xrefs, just adding missing ones
        CvXrefQualifier goDefinitionRef = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class,
                                                                         CvXrefQualifier.GO_DEFINITION_REF,
                                                                         CvXrefQualifier.GO_DEFINITION_REF_MI_REF );

        if ( goDefinitionRef == null ) {
            System.err.println( "Could not find CvXrefQualifier( go-definition-ref ). skip Xref update." );
        } else {
            boolean seenPubmed = false;
            for ( Iterator iterator = cvTerm.getXrefs().iterator(); iterator.hasNext(); ) {
                CvTermXref cvTermXref = (CvTermXref) iterator.next();

                String db = cvTermXref.getDatabase();
                if ( dbMapping.containsKey( cvTermXref.getDatabase() ) ) {
                    db = (String) dbMapping.get( cvTermXref.getDatabase() );
                }

                CvDatabase database = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, db );
                if ( database == null ) {
                    System.err.println( "Could not find the CvDatabase( '" + cvTermXref.getDatabase() + "' )." );
                    continue;
                }

                CvXrefQualifier qualifier = goDefinitionRef;

                String primaryId = cvTermXref.getId();
                if ( CvDatabase.GO.equals( db ) ) {
                    primaryId = "GO:" + cvTermXref.getId();
                } else if ( CvDatabase.SO.equals( db ) ) {
                    primaryId = "SO:" + cvTermXref.getId();
                } else if ( CvDatabase.PUBMED.equals( db ) ) {
                    primaryId = cvTermXref.getId();
                    // we set the qualifier to primary-refrence to the first pubmed id we encounter.
                    if ( !seenPubmed ) {
                        CvXrefQualifier goDefRef = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class,
                                                                                    CvXrefQualifier.GO_DEFINITION_REF,
                                                                                    CvXrefQualifier.GO_DEFINITION_REF_MI_REF );
                        if ( goDefRef != null ) {
                            qualifier = goDefRef;
                        }

                        seenPubmed = true;
                    }
                } else if ( CvDatabase.RESID.equals( db ) ) {
                    primaryId = cvTermXref.getId();
                } else {
                    System.err.println( "Cannot identity '" + cvTermXref.getDatabase() + "' ... please update your mapping !!" );
                }

                Xref xref = new Xref( helper.getInstitution(), database, primaryId, null, null, qualifier );

                if ( !cvObject.getXrefs().contains( xref ) ) {
                    // TODO check if there is not already an Xref with the same primaryId, if so update qualifier.
                    cvObject.addXref( xref );
                    helper.create( xref );
                    String q = "-";
                    if ( qualifier != null ) {
                        q = qualifier.getShortLabel();
                    }
                    System.out.println( "\t\t Created Xref( " + primaryId + ", " + q + " ) (" + cvTerm.getShortName() + ")" );
                }
            }
        }

        // synonyms
        // Note:
        CvAliasType aliasType = (CvAliasType) getCvObject( helper, CvAliasType.class,
                                                           CvAliasType.GO_SYNONYM,
                                                           CvAliasType.GO_SYNONYM_MI_REF );
        if ( aliasType == null ) {
            System.err.println( "Could not find or create CvAliasType( go synonym ). skip Alias update." );
        } else {
            for ( Iterator iterator = cvTerm.getSynonyms().iterator(); iterator.hasNext(); ) {
                String synonym = (String) iterator.next();

                Alias alias = new Alias( helper.getInstitution(), cvObject, aliasType, synonym );

                if ( ! alias.getName().equals( synonym ) ) {
                    // the synonym was truncated, we don't import these.
                    System.out.println( "\t\t Skipped Alias( " + aliasType.getShortLabel() + ", '" + synonym + "' ) ... the content is truncated." );
                    continue;
                }

                if ( !cvObject.getAliases().contains( alias ) ) {
                    cvObject.addAlias( alias );
                    helper.create( alias );
                    System.out.println( "\t\t Created Alias( " + aliasType.getShortLabel() + ", '" + synonym + "' )" );
                }
            }
        }


        if ( needsUpdate ) {
            helper.update( cvObject );
        }
    }

    /**
     * search for the first Annotations having the given CvTopic and deletes all others.
     *
     * @param helper   database access
     * @param cvObject the object on which we search for an annotation
     *
     * @return a unique annotation or null if none is found.
     *
     * @throws IntactException
     */
    private static Annotation getUniqueAnnotation( IntactHelper helper,
                                                   CvObject cvObject,
                                                   CvTopic topicFilter ) throws IntactException {
        if ( topicFilter == null ) {
            throw new NullPointerException();
        }

        Annotation myAnnotation = null;

        for ( Iterator iterator = cvObject.getAnnotations().iterator(); iterator.hasNext(); ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( topicFilter.equals( annotation.getCvTopic() ) ) {

                if ( myAnnotation == null ) {
                    myAnnotation = annotation; // we keep the first one and delete all others
                } else {
                    iterator.remove();
                    helper.delete( annotation );
                }
            }
        } // for all annotations

        return myAnnotation;
    }

    /**
     * Assures that necessary Controlled vocabulary terms are present prior to manipulation of other terms.
     *
     * @param helper access to the database.
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    public static void createNecessaryCvTerms( IntactHelper helper ) throws IntactException {

        // Note, these object are being created is they don't exist yet. They are part
        // of psi-mi so they will be updated later.

        // 1. create CvXrefQualifier( identity )
        getCvObject( helper, CvXrefQualifier.class, CvXrefQualifier.IDENTITY, CvXrefQualifier.IDENTITY_MI_REF );

        // 2. create CvDatabase( psi-mi )
        getCvObject( helper, CvDatabase.class, CvDatabase.PSI_MI, CvDatabase.PSI_MI_MI_REF );

        // 3. create CvDatabase( pubmed )
        getCvObject( helper, CvDatabase.class, CvDatabase.PUBMED, CvDatabase.PUBMED_MI_REF );

        // 4. create CvDatabase( go )
        getCvObject( helper, CvDatabase.class, CvDatabase.GO, CvDatabase.GO_MI_REF );

        // 5. create CvDatabase( so )
        getCvObject( helper, CvDatabase.class, CvDatabase.SO, CvDatabase.SO_MI_REF );

        // 6. create CvDatabase( resid )
        getCvObject( helper, CvDatabase.class, CvDatabase.RESID, CvDatabase.RESID_MI_REF );

        // 7. CvXrefQualifier( go-definition-ref )
        getCvObject( helper, CvXrefQualifier.class, CvXrefQualifier.GO_DEFINITION_REF, CvXrefQualifier.GO_DEFINITION_REF_MI_REF );

        // 8. CvAliasType( go synonym )
        getCvObject( helper, CvAliasType.class, CvAliasType.GO_SYNONYM, CvAliasType.GO_SYNONYM_MI_REF );
    }

    ////////////////////////////////
    // M A I N

    private static void updateAnnotations( File file ) throws IntactException, IOException {

        if ( file == null ) {
            throw new NullPointerException( "You must give a non NULL file" );
        }

        if ( ! file.exists() ) {
            throw new IllegalArgumentException( "The given file doesn't exist. Abort." );
        }

        if ( ! file.canRead() ) {
            throw new IllegalArgumentException( "The given file could not be read. Abort." );
        }

        System.out.println( "Updating CVs' annotations using: " + file.getAbsolutePath() );

        IntactHelper helper = new IntactHelper();

        BufferedReader in = new BufferedReader( new FileReader( file ) );
        String line;
        int lineCount = 0;
        while ( ( line = in.readLine() ) != null ) {

            lineCount++;
            line = line.trim();

            // skip comments
            if ( line.startsWith( "#" ) ) {
                continue;
            }

            // skip empty lines
            if ( line.length() == 0 ) {
                continue;
            }

            // process line
            StringTokenizer stringTokenizer = new StringTokenizer( line, "\t" );

            final String shorltabel = stringTokenizer.nextToken();           // 1. shortlabel
            final String fullname = stringTokenizer.nextToken();             // 2. fullname
            final String type = stringTokenizer.nextToken();                 // 3. type
            final String mi = stringTokenizer.nextToken();                   // 4. mi
            final String topic = stringTokenizer.nextToken();                // 5. topic
            final String reason = stringTokenizer.nextToken();               // 6. exclusion reason
            final String applyToChildrenValue = stringTokenizer.nextToken(); // 7. apply to children

            try {
                final Class clazz = Class.forName( type );

                boolean applyToChildren = false;
                if ( "true".equals( applyToChildrenValue.trim().toLowerCase() ) ) {
                    applyToChildren = true;
                }

                // find the CvObject
                CvObject cv = null;
                if ( mi != null && mi.startsWith( "MI:" ) ) {
                    cv = (CvObject) helper.getObjectByPrimaryId( clazz, mi );
                    if ( cv == null ) {

                    }
                }

                if ( cv == null ) {
                    // wasn't found using MI reference, then try shortlabel
                    if ( shorltabel != null && shorltabel.trim().length() > 0 ) {
                        cv = (CvObject) helper.getObjectByLabel( clazz, shorltabel );
                    } else {
                        throw new Exception( "Line " + lineCount + ": Neither a valid shortlabel (" + shorltabel + ") " +
                                             "nor MI ref (" + mi + ") were given, could not find the corresponding " +
                                             "CvObject. Skip line." );
                    }
                }

                if ( cv != null ) {

                    System.out.println( "Checking on " + cv.getShortLabel() + "..." );

                    // if childrenToApply is true and the term is not a CvDagObject, skip and report error
                    if ( applyToChildren ) {
                        if ( ! CvDagObject.class.isAssignableFrom( cv.getClass() ) ) {
                            // error, CvObject that is not CvDagObject doesn't have children terms.
                            throw new Exception( "Line " + lineCount + ": The specified type (" + cv.getClass() + ") is " +
                                                 "not hierarchical, though you have requested an updated on children " +
                                                 "term. Skip line." );
                        }
                    }

                    // we have the object, now build the annotation
                    CvTopic cvTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, topic );
                    if ( cvTopic == null ) {
                        throw new Exception( "Line " + lineCount + ": Could not find CvTopic( " + topic + " ). Skip line." );
                    }

                    Collection termsToUpdate = new ArrayList();

                    if ( applyToChildren ) {
                        // traverse the sub DAG and fill up the collection
                        collectAllChildren( (CvDagObject) cv, termsToUpdate );
                    } else {
                        termsToUpdate.add( cv );
                    }

                    // start the update here
                    for ( Iterator iterator = termsToUpdate.iterator(); iterator.hasNext(); ) {
                        CvObject aTermToUpdate = (CvObject) iterator.next();

                        // now update that single term
                        Annotation annot = getUniqueAnnotation( helper, aTermToUpdate, cvTopic );

//                        String myReason = reason;
//                        if ( reason != null ) {
//                            myReason = reason.trim();
//                        }

                        Annotation newAnnotation = new Annotation( helper.getInstitution(), cvTopic, reason );
                        if ( annot == null ) {

                            // then add the new one
                            helper.create( newAnnotation );
                            cv.addAnnotation( newAnnotation );
                            helper.update( cv );
                            String myClassName = type.substring( type.lastIndexOf( "." ) + 1, type.length() );
                            System.out.println( "\tCREATED new Annotation( " + cvTopic.getShortLabel() + ", '" + reason + "' )" +
                                                " on " + myClassName + "( " + shorltabel + " / " + mi + " )." );

                        } else {

                            // then try to update it
                            if ( ! newAnnotation.equals( annot ) ) {


                                System.out.println( "\tOLD: " + annot );

                                System.out.println( "\tNEW: " + newAnnotation );

                                // do the update.
                                annot.setAnnotationText( reason );
                                helper.update( annot );
                                String myClassName = type.substring( type.lastIndexOf( "." ) + 1, type.length() );
                                System.out.println( "\tUPDATED Annotation( " + cvTopic.getShortLabel() + ", '" + reason + "' )" +
                                                    " on " + myClassName + "( " + shorltabel + " / " + mi + " )." );
                            }
                        }
                    } // update terms
                } // if cv found

            } catch ( ClassNotFoundException e ) {

                System.err.println( "Line " + lineCount + ": Object Type not supported: " + type );

            } catch ( Exception e ) {

                System.err.println( e.getMessage() );
            }

        } // while - reading line by line
        in.close();

        helper.closeStore();
    }

    private static void collectAllChildren( CvDagObject cv, Collection termsToUpdate ) {

        // note: if not children in the collection, then there is no recursive call ;)
        for ( Iterator iterator = termsToUpdate.iterator(); iterator.hasNext(); ) {
            CvDagObject child = (CvDagObject) iterator.next();
            termsToUpdate.add( child );
            collectAllChildren( child, termsToUpdate );
        }
    }


    public static void main( String[] args ) throws IntactException,
                                                    SQLException,
                                                    LookupException {

        if ( args.length != 1 && args.length != 2 ) {
            System.err.println( "Usage: UpdateCVs <obo file> [<annotation update file>]" );
        }

        String oboFilename = args[ 0 ];
        String annotFilename = null;
        if ( args.length == 2 ) {
            annotFilename = args[ 1 ];
        }

        /////////////////
        // 1. Parsing
        PSILoader psi = new PSILoader();
        IntactOntology ontology = psi.parseOboFile( new File( oboFilename ) );

        System.out.println( "====================================================================" );

        ////////////////////
        // 2. Updating

        // 2.1 Connect to the database.
        IntactHelper helper = new IntactHelper();
        String instanceName = helper.getDbName();
        System.out.println( "Database: " + instanceName );
        System.out.println( "User: " + helper.getDbUserName() );

        // 2.2 Check that we don't touch a production instance.
//        if ( instanceName.equalsIgnoreCase( "ZPRO" ) || instanceName.equalsIgnoreCase( "IWEB" ) ) {
//            helper.closeStore();
//            System.err.println( "This is an alpha version, you cannot edit " + instanceName + ". abort." );
//            System.exit( 1 );
//        }

        // 2.3 Create required vocabulary terms
        createNecessaryCvTerms( helper );
        helper.closeStore();

        // 2.4 update the CVs
        update( ontology );
        updateObsoleteTerms( ontology );

        if ( annotFilename != null ) {
            try {
                updateAnnotations( new File( annotFilename ) );
            } catch ( IOException e ) {
                System.err.println( "Could not Update CVs' annotations." );
                e.printStackTrace();
            }
        }
    }
}