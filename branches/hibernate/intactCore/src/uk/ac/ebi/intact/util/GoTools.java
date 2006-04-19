/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.go.GoUtils;

import java.io.File;
import java.util.Iterator;

/**
 * Utilities to read and write files in GO format
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class GoTools {

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    private GoTools()
    {
        // this is a util class, so it can never be instantiated
    }

    /**
     * Load or unload Controlled Vocabularies in GO format. Usage: GoTools upload   IntAct_classname goid_db
     * Go_DefinitionFile [Go_DagFile] | GoTools download[v14] IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]
     * <p/>
     * goid_db is the shortLabel of the database which is to be used to establish object identity by mapping it to goid:
     * in the GO flat file. Example: If goid_db is psi-mi, an CvObject with an xref "psi-mi; MI:123" is considered to be
     * the same object as an object from the flat file with goid MI:123. If goid_db is '-', the shortLabel will be used
     * if present.
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {

        String usage = "Usage: GoTools upload IntAct_classname goid_db Go_DefinitionFile [Go_DagFile] " + NEW_LINE +
                       "OR" + NEW_LINE +
                       "GoTools download IntAct_classname goid_db Go_DefinitionFile [Go_DagFile]" + NEW_LINE +
                       "goid_db is the shortLabel of the database which is to be used to establish" + NEW_LINE +
                       "object identity by mapping it to goid: in the GO flat file." + NEW_LINE +
                       "Example: If goid_db is psi-mi, an CvObject with an xref psi-mi; MI:123 is" + NEW_LINE +
                       "considered to be the same object as an object from the flat file with goid MI:123." + NEW_LINE +
                       "If goid_db is '-', the short label will be used if present.";

        // Check parameters
        if ( ( args.length < 4 ) || ( args.length > 5 ) ) {
            throw new IllegalArgumentException( "Invalid number of arguments." + NEW_LINE + usage );
        }

        // retreive parameters
        String mode = args[ 0 ];
        String cvClazz = args[ 1 ];
        String id_db = args[ 2 ];
        String defFilename = args[ 3 ];

        String dagFile = null;
        if ( args.length == 5 ) {
            dagFile = args[ 4 ];
        }

        // The first argument must be either upload or download
        if ( mode.equals( "upload" ) || mode.equals( "download" ) ) {
            // The target as a Class object
            Class targetClass = Class.forName( cvClazz );

            // Create database access object
            IntactHelper helper = new IntactHelper();

            createNecessaryCvTerms( helper );

            try {
                // args[2] is the go id database.
                GoUtils goUtils = new GoUtils( helper, id_db, targetClass );

                if ( mode.equals( "upload" ) ) {
                    // Insert definitions
                    File defFile = new File( defFilename );
                    goUtils.insertGoDefinitions( defFile );

                    // Insert DAG
                    if ( args.length == 5 ) {
                        goUtils.insertGoDag( dagFile );
                    }
                } else if ( mode.equals( "download" ) ) {
                    // Write definitions
                    System.out.println( "Writing GO definitons to " + defFilename + " ..." );
                    goUtils.writeGoDefinitions( defFilename );

                    // Write go dag format
                    if ( args.length == 5 ) {
                        System.out.println( "Writing GO DAG to " + dagFile + " ..." );
                        goUtils.writeGoDag( dagFile );
                        System.out.println( "Done." );
                    }
                }
            } finally {
                helper.closeStore();
            }
        } else {
            throw new IllegalArgumentException( "Invalid argument " + mode + NEW_LINE + usage );
        }
    }

    //////////////////////////////////////////////////////
    // Constants related to the identity and psi-mi CVs

    public static final String IDENTITY_MI_REFERENCE = "MI:0356";
    public static final String IDENTITY_SHORTLABEL = "identity";

    public static final String PSI_MI_REFERENCE = "MI:0488";
    public static final String PSI_SHORTLABEL = "psi-mi";


    /**
     * Assures that necessary Controlled vocabulary terms are present prior to manipulation of other terms.
     *
     * @param helper access to the database.
     *
     * @throws IntactException
     */
    private static void createNecessaryCvTerms( IntactHelper helper ) throws IntactException {

        // 1. check that the CvXrefQualifier 'identity'/'MI:0356' is present...
        boolean identityCreated = false;
        CvXrefQualifier identity = (CvXrefQualifier) helper.getObjectByXref( CvXrefQualifier.class,
                                                                             IDENTITY_MI_REFERENCE );
        if ( identity == null ) {
            // then look for shortlabel
            identity = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, IDENTITY_SHORTLABEL );

            if ( identity == null ) {

                System.out.println( "CvXrefQualifier( " + IDENTITY_SHORTLABEL + " ) doesn't exists, create it." );

                // not there, then create it manually
                identity = new CvXrefQualifier( helper.getInstitution(), IDENTITY_SHORTLABEL );
                helper.create( identity );

                // That flag will allow the creation of the PSI MI Xref after checking that the CvDatabase exists.
                identityCreated = true;
                if (identityCreated){
                    System.out.println("identity has been created");
                }
            }
        }


        // 2. check that the CvDatabase 'psi-mi'/'MI:0356' is present...
        boolean psiAlreadyExists = false;
        CvDatabase psi = (CvDatabase) helper.getObjectByXref( CvDatabase.class,
                                                              PSI_MI_REFERENCE );
        if ( psi == null ) {
            // then look for shortlabel
            psi = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, PSI_SHORTLABEL );

            if ( psi == null ) {

                System.out.println( "CvDatabase( " + PSI_SHORTLABEL + " ) doesn't exists, create it." );

                // not there, then create it manually
                psi = new CvDatabase( helper.getInstitution(), PSI_SHORTLABEL );
                helper.create( psi );

                Xref xref = new Xref( helper.getInstitution(), psi, PSI_MI_REFERENCE, null, null, identity );
                psi.addXref( xref );

                helper.create( xref );
            } else {
                psiAlreadyExists = true;
            }
        } else {
            psiAlreadyExists = true;
        }

        if ( psiAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( psi, PSI_MI_REFERENCE, helper, psi, identity );
        }

        if ( identityCreated ) {

            // add the psi reference to the identity term.
            Xref xref = new Xref( helper.getInstitution(), psi, IDENTITY_MI_REFERENCE, null, null, identity );
            identity.addXref( xref );

            helper.create( xref );
        } else {
            // identity already existed, check that it has the right PSI Xref.
            updatePsiXref( identity, IDENTITY_MI_REFERENCE, helper, psi, identity );
        }


        // 3. check that the CvDatabase 'pubmed'/'MI:0446' is present...
        boolean pubmedAlreadyExists = false;
        CvDatabase pubmed = (CvDatabase) helper.getObjectByXref( CvDatabase.class,
                                                              CvDatabase.PUBMED_MI_REF );
        if ( pubmed == null ) {
            // then look for shortlabel
            pubmed = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, CvDatabase.PUBMED );

            if ( pubmed == null ) {

                System.out.println( "CvDatabase( " + CvDatabase.PUBMED + " ) doesn't exists, create it." );

                // not there, then create it manually
                pubmed = new CvDatabase( helper.getInstitution(), CvDatabase.PUBMED );
                helper.create( pubmed );

                Xref xref = new Xref( helper.getInstitution(), psi, CvDatabase.PUBMED_MI_REF, null, null, identity );
                pubmed.addXref( xref );

                helper.create( xref );
            } else {
                pubmedAlreadyExists = true;
            }
        } else {
            pubmedAlreadyExists = true;
        }

        if ( pubmedAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( pubmed, CvDatabase.PUBMED_MI_REF, helper, psi, identity );
        }



        // 4. check that the CvDatabase 'go-definition-ref'/'MI:0242' is present...
        boolean goDefRefAlreadyExists = false;
        CvXrefQualifier goDefRef = (CvXrefQualifier) helper.getObjectByXref( CvXrefQualifier.class,
                                                                             CvXrefQualifier.GO_DEFINITION_REF_MI_REF );
        if ( goDefRef == null ) {
            // then look for shortlabel
            goDefRef = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, CvXrefQualifier.GO_DEFINITION_REF );

            if ( goDefRef == null ) {

                System.out.println( "CvXrefQualifier( " + CvXrefQualifier.GO_DEFINITION_REF + " ) doesn't exists, create it." );

                // not there, then create it manually
                goDefRef = new CvXrefQualifier( helper.getInstitution(), CvXrefQualifier.GO_DEFINITION_REF );
                helper.create( goDefRef );

                Xref xref = new Xref( helper.getInstitution(), psi, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, null, null, identity );
                goDefRef.addXref( xref );

                helper.create( xref );
            } else {
                goDefRefAlreadyExists = true;
            }
        } else {
            goDefRefAlreadyExists = true;
        }

        if ( goDefRefAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( goDefRef, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, helper, psi, identity );
        }
        

//        if ( pubmedCreated ) {
//
//            // add the psi reference to the identity term.
//            Xref xref = new Xref( helper.getInstitution(), psi, CvDatabase.PUBMED_MI_REF, null, null, identity );
//            pubmed.addXref( xref );
//
//            helper.create( xref );
//        } else {
//            // identity already existed, check that it has the right PSI Xref.
//            updatePsiXref( pubmed, CvDatabase.PUBMED_MI_REF, helper, psi, identity );
//        }
//
//        if ( goDefRefCreated ) {
//
//            // add the psi reference to the identity term.
//            Xref xref = new Xref( helper.getInstitution(), psi, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, null, null, identity );
//            goDefRef.addXref( xref );
//
//            helper.create( xref );
//        } else {
//            // identity already existed, check that it has the right PSI Xref.
//            updatePsiXref( goDefRef, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, helper, psi, identity );
//        }

    }

    /**
     * Update the given CvObject with the given PSI MI reference. update existing Xref if necessary, or create a new one
     * if none exists.
     *
     * @param cv       the Controlled Vocabulary term to update.
     * @param mi       the MI reference to be found.
     * @param helper   Database access
     * @param psi      the CvDatabase( psi-mi )
     * @param identity the CvXrefQualifier( identity )
     *
     * @throws IntactException
     */
    private static void updatePsiXref( CvObject cv, String mi,
                                       IntactHelper helper,
                                       CvDatabase psi,
                                       CvXrefQualifier identity ) throws IntactException {

        // Note: we assume that there is at most one psi ref per term.
        boolean psiRefFound = false;

        for ( Iterator iterator = cv.getXrefs().iterator(); iterator.hasNext() && false == psiRefFound; ) {
            Xref xref = (Xref) iterator.next();

            if ( psi.equals( xref.getCvDatabase() ) ) {
                if ( false == mi.equals( xref.getPrimaryId() ) ) {

                    System.out.println( "Updating " + cv.getShortLabel() + "'s MI reference (" +
                                        xref.getPrimaryId() + " becomes " + mi + ")" );
                    xref.setPrimaryId( mi );
                    helper.update( xref );
                }

                if ( false == identity.equals( xref.getCvXrefQualifier() ) ) {
                    System.out.println( "Updating " + cv.getShortLabel() + "'s CvXrefQualifier to identity." );

                    xref.setCvXrefQualifier( identity );
                    helper.update( xref );
                }

                psiRefFound = true;
            }
        } // for

        if ( !psiRefFound ) {
            // then create the PSI ref
            Xref xref = new Xref( helper.getInstitution(), psi, mi, null, null, identity );
            psi.addXref( xref );

            helper.create( xref );
        }
    }
}

