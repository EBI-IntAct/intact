/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.XrefDao;
import uk.ac.ebi.intact.core.unit.IntactTestException;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;

import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.io.*;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.transaction.TransactionStatus;

/**
 * Implementation class for SmallMoleculeProcessor.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class ChebiProcessor implements SmallMoleculeProcessor {

    private static final Log log = LogFactory.getLog( ChebiProcessor.class );
    public static final String MULTIPLE_IDENTITY_LOG_FILENAME = "multiple-identity.tsv";
    public static final String NO_IDENTITY_LOG_FILENAME = "no-identity.tsv";
    public static final String UNKNOWN_CHEBI_ID_LOG_FILENAME = "unknown-chebi-id.tsv";

    private static final String TAB = "\t";
    private static final String NEW_LINE = System.getProperty( "line.separator" );

    private File outputDirectory;

    private Writer multipleIdentityWriter;
    private Writer noIdentityWriter;
    private Writer unknownChebiIdWriter;

    private SmallMoleculeUpdateReport report;

    public ChebiProcessor() {
        report = new SmallMoleculeUpdateReport();
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory( File outputDirectory ) {
        this.outputDirectory = outputDirectory;
    }

    public SmallMoleculeUpdateReport getReport() {
        return report;
    }

    public void setReport( SmallMoleculeUpdateReport report ) {
        this.report = report;
    }

    ////////////////////////////
    // SmallMoleculeProcessor

    /**
     * Interates through all the Acs and get the smallmolecule from the database and calls the enricher to enrich the
     * small molecule.
     *
     * @param smallMoleculeAcs list of smallmolecule acs
     * @throws SmallMoleculeUpdatorException
     */
    public void updateByAcs( List<String> smallMoleculeAcs ) throws SmallMoleculeUpdatorException {

        try {

            openLogFiles();


            final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
            final DaoFactory daoFactory = dataContext.getDaoFactory();

            for ( String smallMoleculeAc : smallMoleculeAcs ) {

                report.incrementMoleculeCount();

                final TransactionStatus transactionStatus = dataContext.beginTransaction();

                final SmallMoleculeImpl sm = daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByAc( smallMoleculeAc );
                Collection<InteractorXref> xrefs = XrefUtils.getIdentityXrefs( sm );

                String chebiId = null;
                InteractorXref identityXref = null;

                boolean singleIndentityAvailable = false;

                switch ( xrefs.size() ) {
                    case 1:
                        identityXref = xrefs.iterator().next();
                        chebiId = identityXref.getPrimaryId();
                        singleIndentityAvailable = true;
                        break;

                    case 0:
                        // ERROR - no identity found
                        if ( !autoFixNoIdentity( sm ) ) {
                            log.warn( "Auto fix (" + sm.getAc() + ") of chebi molecule without checbi identity failed." );
                            logNoIdentity( sm );
                        } else {

                            // fetch the chebi id
                            xrefs = XrefUtils.getIdentityXrefs( sm );
                            if( xrefs.size() == 1 ) {
                                identityXref = xrefs.iterator().next();
                                chebiId = identityXref.getPrimaryId();

                                singleIndentityAvailable = true;
                            } else {
                                log.error( "FAILURE - autofixed failed to update xref to 'identity'" );
                            }
                        }
                        break;

                    default: // more than one
                        // ERROR - more than one identity
                        logMultipleIdentity( sm, xrefs );
                }

                if( singleIndentityAvailable ) {
                    // get CHEBI molecule
                    ChebiWebServiceClient chebiClient = new ChebiWebServiceClient();
                    Entity entity = null;
                    try {
                        entity = chebiClient.getCompleteEntity( chebiId );
                    } catch ( ChebiWebServiceFault_Exception e ) {
                        logUnknownChebiId( sm, chebiId );
                    }

                    if ( entity != null ) {
                        // start updating intact small molecule
                        sm.setShortLabel( entity.getChebiAsciiName() );
                        updateChebiXrefs( identityXref, sm, entity );

                        if ( entity.getInchi() != null ) {
                            updateInchiAnnotation( sm, entity );
                        }

                        if ( log.isTraceEnabled() ) {
                            log.trace( "Completed update of Small Molecule: " + sm );
                        }
                    } else {
                        log.error( "FAILURE - could not retreive compound for id: " + chebiId +
                                   " when processing small molecule: " + sm.getAc() + " " + sm.getShortLabel() );
                    }
                } else {
                    log.error( "There was an issue with the Xref Small Molecule:  " + smallMoleculeAc + " - aborting update." );
                }

                if ( log.isTraceEnabled() ) {
                    log.trace( "-------------------------------------------" );
                }

                dataContext.commitTransaction(transactionStatus);
            } // for
        } finally {
            closeLogFiles();
        }
    }

    /**
     * If we have a single chebi Xref with a qualifier that is not identify, we log it and update to identity.
     *
     * @param sm
     * @return
     */
    private boolean autoFixNoIdentity( SmallMoleculeImpl sm ) {

        boolean autoFixSuccess = false;

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final CvDatabase chebi = daoFactory.getCvObjectDao( CvDatabase.class ).getByPsiMiRef( CvDatabase.CHEBI_MI_REF );
        final Collection<Xref> xrefs = AnnotatedObjectUtils.searchXrefs( sm, chebi );

        if ( xrefs.size() == 1 ) {
            final InteractorXref x = ( InteractorXref ) xrefs.iterator().next();
            // autofix it
            final CvXrefQualifier identity =
                    daoFactory.getCvObjectDao( CvXrefQualifier.class ).getByPsiMiRef( CvXrefQualifier.IDENTITY_MI_REF );

            log.warn( "AUTOFIX - " + sm.getAc() + " - found a small molecule that has a single chebi xref but of which the qualifier was '" +
                      x.getCvXrefQualifier().getShortLabel() + "', updating it to 'identity'..." );

            x.setCvXrefQualifier( identity );
            daoFactory.getXrefDao( InteractorXref.class ).update( x );
            daoFactory.getInteractorDao( SmallMoleculeImpl.class ).update( sm );
            autoFixSuccess = true;
        }

        return autoFixSuccess;
    }


    //////////////////////
    // Logging

    private void logUnknownChebiId( SmallMoleculeImpl sm, String chebiId ) {

        report.incrementUnknownChebiIdCount();

        final String msg = sm.getAc() + TAB + sm.getShortLabel() + TAB + chebiId + NEW_LINE;
        try {
            unknownChebiIdWriter.write( msg );
            unknownChebiIdWriter.flush();
        } catch ( IOException e ) {
            log.error( "Could not log no unknown chebi id:" + NEW_LINE + msg, e );
        }
    }

    private void logMultipleIdentity( SmallMoleculeImpl sm, Collection<InteractorXref> xrefs ) {

        report.incrementMultipleIdentyCount();

        StringBuilder sb = new StringBuilder( 256 );
        for ( Iterator<InteractorXref> iterator = xrefs.iterator(); iterator.hasNext(); ) {
            InteractorXref xref = iterator.next();
            sb.append( xref.getPrimaryId() );
            if ( iterator.hasNext() ) sb.append( TAB );
        }
        final String msg = sm.getAc() + TAB + sm.getShortLabel() + TAB + sb.toString() + NEW_LINE;
        try {
            multipleIdentityWriter.write( msg );
            multipleIdentityWriter.flush();
        } catch ( IOException e ) {
            log.error( "Could not log multiple identity:" + NEW_LINE + msg, e );
        }
    }

    private void logNoIdentity( SmallMoleculeImpl sm ) {

        report.incrementNoIdentityCount();

        final String msg = sm.getAc() + TAB + sm.getShortLabel() + NEW_LINE;
        try {
            noIdentityWriter.write( msg );
            noIdentityWriter.flush();
        } catch ( IOException e ) {
            log.error( "Could not log no identity:" + NEW_LINE + msg, e );
        }
    }

    private void openLogFiles() {

        if ( outputDirectory == null ) {
            outputDirectory = new File( "" );
            if ( log.isInfoEnabled() ) {
                log.info( "No output directory set for small molecule update log file, setting default:" + outputDirectory.getAbsolutePath() );
            }
        }

        try {
            multipleIdentityWriter = new BufferedWriter( new FileWriter( new File( outputDirectory, MULTIPLE_IDENTITY_LOG_FILENAME ) ) );
            noIdentityWriter = new BufferedWriter( new FileWriter( new File( outputDirectory, NO_IDENTITY_LOG_FILENAME ) ) );
            unknownChebiIdWriter = new BufferedWriter( new FileWriter( new File( outputDirectory, UNKNOWN_CHEBI_ID_LOG_FILENAME ) ) );
        } catch ( IOException e ) {
            log.error( "Could not create log files", e );
        }
    }

    private void closeLogFiles() {
        try {
            if( multipleIdentityWriter != null ) {
                multipleIdentityWriter.flush();
                multipleIdentityWriter.close();
            }

            if( noIdentityWriter != null ) {
                noIdentityWriter.flush();
                noIdentityWriter.close();
            }

            if( unknownChebiIdWriter != null ) {
                unknownChebiIdWriter.flush();
                unknownChebiIdWriter.close();
            }
        } catch ( IOException e ) {
            log.error( "Could not close log files", e );
        }
    }

    ////////////////////
    // Data update

    private void updateInchiAnnotation( SmallMoleculeImpl sm, Entity e ) {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        final CvObjectDao<CvTopic> tdao = daoFactory.getCvObjectDao( CvTopic.class );
        final CvTopic inchi = tdao.getByPsiMiRef( CvTopic.INCHI_ID_MI_REF );
        final Collection<Annotation> annotations =
                AnnotatedObjectUtils.findAnnotationsByCvTopic( sm, Arrays.asList( inchi ) );

        Annotation a;
        switch ( annotations.size() ) {
            case 0:
                // create new
                if ( log.isDebugEnabled() ) log.debug( "Adding missing Inchi annotation: " + e.getInchi() );
                a = new Annotation( sm.getOwner(), inchi, e.getInchi() );
                daoFactory.getAnnotationDao().persist( a );
                sm.addAnnotation( a );
                daoFactory.getInteractorDao( SmallMoleculeImpl.class ).update( sm );
                break;

            case 1:
                a = annotations.iterator().next();
                final String currentInchi = a.getAnnotationText();
                if ( currentInchi != null && !currentInchi.equals( e.getInchi() ) ) {
                    // update inchi
                    if ( log.isDebugEnabled() ) log.debug( "Updating outdated Inchi annotation: " + e.getInchi() );
                    a.setAnnotationText( e.getInchi() );
                    daoFactory.getAnnotationDao().update( a );
                }
                break;

            default: // more than one, we make sure we keep only an up-to-date one
                a = null;
                boolean needUpdating = false;
                for ( Annotation annotation : annotations ) {
                    if ( annotation.getAnnotationText().equals( e.getInchi() ) ) {
                        if ( a != null ) {
                            // in case we have more than one, we keep a single annotation
                            sm.removeAnnotation( annotation );
                            needUpdating = true;
                        }
                        a = annotation;
                    } else {
                        // delete it as this is not in ChEBI
                        if ( log.isDebugEnabled() )
                            log.debug( "Removing outdated Inchi annotation: " + annotation.getAnnotationText() );
                        sm.removeAnnotation( annotation );
                        needUpdating = true;
                    }
                }
                if ( needUpdating ) {
                    daoFactory.getInteractorDao( SmallMoleculeImpl.class ).update( sm );
                }

                if ( a == null ) {
                    // create it
                    if ( log.isDebugEnabled() ) log.debug( "Adding new Inchi annotation: " + e.getInchi() );
                    a = new Annotation( sm.getOwner(), inchi, e.getInchi() );
                    daoFactory.getAnnotationDao().persist( a );
                    sm.addAnnotation( a );
                    daoFactory.getInteractorDao( SmallMoleculeImpl.class ).update( sm );
                }
        } // switch
    }

    private void updateChebiXrefs( InteractorXref identityXref, SmallMoleculeImpl sm, Entity e ) {

        if ( !e.getChebiId().equals( identityXref.getPrimaryId() ) ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Outdated chebi cross reference '" + identityXref.getPrimaryId() +
                           "', the new one is '" + e.getChebiId() + "'" );
            }

            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            final CvObjectDao<CvXrefQualifier> qdao = daoFactory.getCvObjectDao( CvXrefQualifier.class );
            final CvXrefQualifier secondary = qdao.getByPsiMiRef( CvXrefQualifier.SECONDARY_AC_MI_REF );
            final CvXrefQualifier identity = qdao.getByPsiMiRef( CvXrefQualifier.IDENTITY_MI_REF );

            if ( log.isDebugEnabled() )
                log.debug( "Updating qualifier of outdated Chebi Xref to secondary-ac: " + identityXref.getPrimaryId() );
            identityXref.setCvXrefQualifier( secondary );
            final XrefDao<InteractorXref> xrefDao = daoFactory.getXrefDao( InteractorXref.class );
            xrefDao.update( identityXref );

            // create a new identity Xref
            InteractorXref newIdentity = new InteractorXref( identityXref.getOwner(),
                                                             identityXref.getCvDatabase(),
                                                             e.getChebiId(),
                                                             null, null,
                                                             identity );

            if ( log.isDebugEnabled() ) log.debug( "Adding new identity Chebi Xref: " + e.getChebiId() );
            xrefDao.persist( newIdentity );
            sm.addXref( newIdentity );
            daoFactory.getInteractorDao( SmallMoleculeImpl.class ).update( sm );
        }
    }
}