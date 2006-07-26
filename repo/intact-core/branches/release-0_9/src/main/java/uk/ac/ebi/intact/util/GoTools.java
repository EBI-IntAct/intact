/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.util.go.GoUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilities to read and write files in GO format
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class GoTools {

    private static final Log log = LogFactory.getLog(GoTools.class);

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

            createNecessaryCvTerms( );

                // args[2] is the go id database.
                GoUtils goUtils = new GoUtils( id_db, targetClass );

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
     * @throws IntactException
     */
    private static void createNecessaryCvTerms() throws IntactException {

        Institution institution = DaoFactory.getInstitutionDao().getInstitution();

        CvObjectDao<CvXrefQualifier> cvXrefQualifierDao = DaoFactory.getCvObjectDao(CvXrefQualifier.class);

        // 1. check that the CvXrefQualifier 'identity'/'MI:0356' is present...
        boolean identityCreated = false;
        CvXrefQualifier identity = cvXrefQualifierDao.getByXref(IDENTITY_MI_REFERENCE );
        if ( identity == null ) {
            // then look for shortlabel
            identity = cvXrefQualifierDao.getByShortLabel(IDENTITY_SHORTLABEL);

            if ( identity == null ) {

                log.info( "CvXrefQualifier( " + IDENTITY_SHORTLABEL + " ) doesn't exists, create it." );

                // not there, then create it manually
                identity = new CvXrefQualifier( institution, IDENTITY_SHORTLABEL );
                cvXrefQualifierDao.persist( identity );

                // That flag will allow the creation of the PSI MI Xref after checking that the CvDatabase exists.
                identityCreated = true;
                if (identityCreated){
                    log.debug("identity has been created");
                }
            }
        }


        // 2. check that the CvDatabase 'psi-mi'/'MI:0356' is present...
        CvObjectDao<CvDatabase> cvDatabaseDao = DaoFactory.getCvObjectDao(CvDatabase.class);

        boolean psiAlreadyExists = false;
        CvDatabase psi = cvDatabaseDao.getByXref(PSI_MI_REFERENCE );
        if ( psi == null ) {
            // then look for shortlabel
            psi = cvDatabaseDao.getByShortLabel( PSI_SHORTLABEL );

            if ( psi == null ) {

                log.info( "CvDatabase( " + PSI_SHORTLABEL + " ) doesn't exists, create it." );

                // not there, then create it manually
                psi = new CvDatabase( institution, PSI_SHORTLABEL );
                cvDatabaseDao.persist( psi );

                Xref xref = new Xref( institution, psi, PSI_MI_REFERENCE, null, null, identity );
                psi.addXref( xref );

                DaoFactory.getXrefDao().persist( xref );
            } else {
                psiAlreadyExists = true;
            }
        } else {
            psiAlreadyExists = true;
        }

        if ( psiAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( psi, PSI_MI_REFERENCE, psi, identity );
        }

        if ( identityCreated ) {

            // add the psi reference to the identity term.
            Xref xref = new Xref( institution, psi, IDENTITY_MI_REFERENCE, null, null, identity );
            identity.addXref( xref );

            DaoFactory.getXrefDao().persist( xref );
        } else {
            // identity already existed, check that it has the right PSI Xref.
            updatePsiXref( identity, IDENTITY_MI_REFERENCE, psi, identity );
        }


        // 3. check that the CvDatabase 'pubmed'/'MI:0446' is present...
        boolean pubmedAlreadyExists = false;
        CvDatabase pubmed = cvDatabaseDao.getByXref(CvDatabase.PUBMED_MI_REF );
        if ( pubmed == null ) {
            // then look for shortlabel
            pubmed = cvDatabaseDao.getByShortLabel(CvDatabase.PUBMED );

            if ( pubmed == null ) {

                log.info( "CvDatabase( " + CvDatabase.PUBMED + " ) doesn't exists, create it." );

                // not there, then create it manually
                pubmed = new CvDatabase( institution, CvDatabase.PUBMED );
                cvDatabaseDao.persist( pubmed );

                Xref xref = new Xref( institution, psi, CvDatabase.PUBMED_MI_REF, null, null, identity );
                pubmed.addXref( xref );

                DaoFactory.getXrefDao().persist( xref );
            } else {
                pubmedAlreadyExists = true;
            }
        } else {
            pubmedAlreadyExists = true;
        }

        if ( pubmedAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( pubmed, CvDatabase.PUBMED_MI_REF, psi, identity );
        }



        // 4. check that the CvDatabase 'go-definition-ref'/'MI:0242' is present...
        boolean goDefRefAlreadyExists = false;
        CvXrefQualifier goDefRef = cvXrefQualifierDao.getByXref(CvXrefQualifier.GO_DEFINITION_REF_MI_REF );
        if ( goDefRef == null ) {
            // then look for shortlabel
            goDefRef = cvXrefQualifierDao.getByShortLabel( CvXrefQualifier.GO_DEFINITION_REF );

            if ( goDefRef == null ) {

                log.info( "CvXrefQualifier( " + CvXrefQualifier.GO_DEFINITION_REF + " ) doesn't exists, create it." );

                // not there, then create it manually
                goDefRef = new CvXrefQualifier( institution, CvXrefQualifier.GO_DEFINITION_REF );
                cvXrefQualifierDao.persist( goDefRef );

                Xref xref = new Xref( institution, psi, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, null, null, identity );
                goDefRef.addXref( xref );

                DaoFactory.getXrefDao().persist( xref );
            } else {
                goDefRefAlreadyExists = true;
            }
        } else {
            goDefRefAlreadyExists = true;
        }

        if ( goDefRefAlreadyExists ) {
            // check that it has the right MI reference.
            updatePsiXref( goDefRef, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, psi, identity );
        }
        

//        if ( pubmedCreated ) {
//
//            // add the psi reference to the identity term.
//            Xref xref = new Xref( institution, psi, CvDatabase.PUBMED_MI_REF, null, null, identity );
//            pubmed.addXref( xref );
//
//            helper.create( xref );
//        } else {
//            // identity already existed, check that it has the right PSI Xref.
//            updatePsiXref( pubmed, CvDatabase.PUBMED_MI_REF, psi, identity );
//        }
//
//        if ( goDefRefCreated ) {
//
//            // add the psi reference to the identity term.
//            Xref xref = new Xref( institution, psi, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, null, null, identity );
//            goDefRef.addXref( xref );
//
//            helper.create( xref );
//        } else {
//            // identity already existed, check that it has the right PSI Xref.
//            updatePsiXref( goDefRef, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, psi, identity );
//        }

    }

    /**
     * Update the given CvObject with the given PSI MI reference. update existing Xref if necessary, or create a new one
     * if none exists.
     *
     * @param cv       the Controlled Vocabulary term to update.
     * @param mi       the MI reference to be found.
     * @param psi      the CvDatabase( psi-mi )
     * @param identity the CvXrefQualifier( identity )
     *
     * @throws IntactException
     */
    private static void updatePsiXref( CvObject cv, String mi,
                                       CvDatabase psi,
                                       CvXrefQualifier identity ) throws IntactException {

        // Note: we assume that there is at most one psi ref per term.
        boolean psiRefFound = false;

        for ( Iterator iterator = cv.getXrefs().iterator(); iterator.hasNext() && false == psiRefFound; ) {
            Xref xref = (Xref) iterator.next();

            if ( psi.equals( xref.getCvDatabase() ) ) {
                if ( false == mi.equals( xref.getPrimaryId() ) ) {

                    log.debug( "Updating " + cv.getShortLabel() + "'s MI reference (" +
                                        xref.getPrimaryId() + " becomes " + mi + ")" );
                    xref.setPrimaryId( mi );
                    DaoFactory.getXrefDao().update( xref );
                }

                if ( false == identity.equals( xref.getCvXrefQualifier() ) ) {
                    log.debug( "Updating " + cv.getShortLabel() + "'s CvXrefQualifier to identity." );

                    xref.setCvXrefQualifier( identity );
                    DaoFactory.getXrefDao().update( xref );
                }

                psiRefFound = true;
            }
        } // for

        if ( !psiRefFound ) {
            // then create the PSI ref
            Xref xref = new Xref( DaoFactory.getInstitutionDao().getInstitution(), psi, mi, null, null, identity );
            psi.addXref( xref );

            DaoFactory.getXrefDao().persist( xref );
        }
    }
}

