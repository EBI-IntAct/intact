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
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.persistence.dao.XrefDao;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactTestException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;

import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.io.Writer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

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

    private static final String TAB = "\n";
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

        openLogFiles();

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        for ( String smallMoleculeAc : smallMoleculeAcs ) {

            report.incrementMoleculeCount();

            // Honnor already running transaction
            final boolean wasTransactionActive = dataContext.isTransactionActive();
            if ( !wasTransactionActive ) {
                dataContext.beginTransaction();
            }

            final SmallMoleculeImpl sm = daoFactory.getInteractorDao( SmallMoleculeImpl.class ).getByAc( smallMoleculeAc );
            final Collection<InteractorXref> xrefs = XrefUtils.getIdentityXrefs( sm );

            String chebiId = null;
            InteractorXref identityXref = null;

            switch ( xrefs.size() ) {
                case 1:
                    identityXref = xrefs.iterator().next();
                    chebiId = identityXref.getPrimaryId();
                    break;

                case 0:
                    // ERROR - no identity found
                    logMultipleIdentity( sm, xrefs );
                    continue; // abort and go to next molecule

                default: // more than one
                    // ERROR - more than one identity
                    logNoIdentity( sm );
                    continue; // abort and go to next molecule
            }

            // get CHEBI molecule
            ChebiWebServiceClient chebiClient = new ChebiWebServiceClient();
            final Entity entity;
            try {
                entity = chebiClient.getCompleteEntity( chebiId );
            } catch ( ChebiWebServiceFault_Exception e ) {
                logUnknownChebiId( sm, chebiId );
                continue; // abort and go to next molecule
            }

            // start updating intact small molecule
            sm.setShortLabel( entity.getChebiAsciiName() );
            updateChebiXrefs( identityXref, sm, entity );

            if( entity.getInchi() != null ) {
                updateInchiAnnotation( sm, entity );
            }

            if ( log.isTraceEnabled() ) {
                log.trace( "Completed update of Small Molecule: " + sm );
            }

            if ( !wasTransactionActive ) {
                commitTransaction();
            }

        }

        closeLogFiles();
    }


    //////////////////////
    // Logging

    private void logUnknownChebiId( SmallMoleculeImpl sm, String chebiId ) {

        report.incrementUnknownChebiIdCount();

        final String msg = sm.getAc() + TAB + sm.getShortLabel() + chebiId + NEW_LINE;
        try {
            noIdentityWriter.write( msg );
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
        } catch ( IOException e ) {
            log.error( "Could not log multiple identity:" + NEW_LINE + msg, e );
        }
    }

    private void logNoIdentity( SmallMoleculeImpl sm ) {

        report.incrementNoIdentityCount();

        final String msg = sm.getAc() + TAB + sm.getShortLabel() + NEW_LINE;
        try {
            noIdentityWriter.write( msg );
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
            multipleIdentityWriter = new FileWriter( new File( outputDirectory, MULTIPLE_IDENTITY_LOG_FILENAME ) );
            noIdentityWriter = new FileWriter( new File( outputDirectory, NO_IDENTITY_LOG_FILENAME ) );
            unknownChebiIdWriter = new FileWriter( new File( outputDirectory, UNKNOWN_CHEBI_ID_LOG_FILENAME ) );
        } catch ( IOException e ) {
            log.error( "Could not create log files", e );
        }
    }

    private void closeLogFiles() {
        try {
            multipleIdentityWriter.flush();
            multipleIdentityWriter.close();

            noIdentityWriter.flush();
            noIdentityWriter.close();

            unknownChebiIdWriter.flush();
            unknownChebiIdWriter.close();
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
                        if ( log.isDebugEnabled() ) log.debug( "Removing outdated Inchi annotation: " + annotation.getAnnotationText() );
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

            if ( log.isDebugEnabled() ) log.debug( "Updating qualifier of outdated Chebi Xref to secondary-ac: " + identityXref.getPrimaryId() );
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

    protected void commitTransaction() throws SmallMoleculeUpdatorException {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        if ( dataContext.isTransactionActive() ) {
            try {
                dataContext.commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new IntactTestException( e );
            }
        }
    }
}