/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.confidence.dataRetriever;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.confidence.expansion.ExpansionStrategy;
import uk.ac.ebi.intact.confidence.expansion.SpokeExpansion;
import uk.ac.ebi.intact.confidence.model.IntActIdentifierImpl;
import uk.ac.ebi.intact.confidence.model.InteractionSimplified;
import uk.ac.ebi.intact.confidence.model.ProteinSimplified;
import uk.ac.ebi.intact.confidence.model.Report;
import uk.ac.ebi.intact.confidence.model.io.InteractionSimplifiedWriter;
import uk.ac.ebi.intact.confidence.model.io.ProteinSimplifiedWriter;
import uk.ac.ebi.intact.confidence.model.io.impl.CompactInteractionSWriterImpl;
import uk.ac.ebi.intact.confidence.model.io.impl.ProteinSimplifiedWriterImpl;
import uk.ac.ebi.intact.confidence.utils.IntactUtils;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

import java.io.*;
import java.util.*;

/**
 * Class to retrieve the high confidence set from IntAct.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre>
 *               16 Aug 2007
 *               </pre>
 */
public class IntactDbRetriever implements DataRetrieverStrategy {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactDbRetriever.class );
//    private List<InteractionSimplified> highConfidenceSet;

    private DaoFactory daoFactory;

    private File workDir;
    private ExpansionStrategy expansionStrategy;
    private boolean dbNrForTest = false;


    private boolean getHC = false;
    private boolean getMC = false;

    private int nrHC = 0;
    private int nrMC = 0;
    private int nrAuthorConf = 0;
    private int nrAuthorConf1 = 0;
    private int nrAuthorConf2 = 0;
    private int nrAuthorConf3 = 0;
    private int nrInVitro = 0;
    private int nrDirectInts = 0;
    private int nrDisulfideInts =0;
    private int nrCrossLink = 0;
    private int nrComplexes = 0;
    private int nrEnzyme = 0;
    private int nrFluoresc = 0;


    public IntactDbRetriever() {
        expansionStrategy = new SpokeExpansion();
        workDir = new File( System.getProperty( "java.io.tmpdir" ), "IntactDbRetriever" );
        workDir.mkdir();
    }

    public IntactDbRetriever( File workDir, ExpansionStrategy expansion ) {
        this( workDir, expansion, IntactContext.getCurrentInstance().getDataContext().getDaoFactory() );
    }

    public IntactDbRetriever( File workDir, ExpansionStrategy expansion, DaoFactory daoFactory ) {
        this.daoFactory = daoFactory;      
        if ( workDir == null ) {
            workDir = new File( System.getProperty( "java.io.tmpdir" ), "IntactDbRetriever" );
            workDir.mkdir();
        } else {
            this.workDir = workDir;
            workDir.mkdir();
        }

        this.expansionStrategy = expansion;
    }

    /////////////////////
    // Getter(s) /Setter(s).
    public void setWorkDir( File workDir ) {
        this.workDir = workDir;
    }


    // //////////////////
    // // Inherited method(s)
    /**
     * returns the list of the high confidence interactions
     *
     * @return List InteractionSimplified
     * @throws DataRetrieverException
     */
    public int retrieveHighConfidenceSet( File folder ) throws DataRetrieverException {
        try {
            File file = new File( workDir.getPath(), "mediumConfidence.txt" );
            Writer fw = new FileWriter( file );
            this.getHC = true;
            readConfidenceSets( folder );
            // readMediumConfidenceSet( null, fw );
            fw.close();
        } catch ( FileNotFoundException e ) {
            throw new DataRetrieverException( e );
        } catch ( IOException e ) {
            throw new DataRetrieverException( e );
        } catch ( Exception e ) {
            throw new DataRetrieverException( e);
        }
        return nrHC;
    }

    /**
     * returns the list of the medium confidence interactions
     *
     * @return List InteractionSimplified
     * @throws DataRetrieverException
     */
    public void retrieveMediumConfidenceSet( File folder ) throws DataRetrieverException {
        this.getMC = true;
        try {
            readConfidenceSets(folder);
        } catch ( Exception e ) {
            throw new DataRetrieverException(e);
        }
//        readMediumConfidenceSet( w, null );
    }

    public void retrieveHighAndMediumConfidenceSet( File folder ) throws DataRetrieverException {
        this.getHC = true;
        this.getMC = true;
        try {
            readConfidenceSets(folder);
        } catch ( IOException e ) {
            throw new DataRetrieverException(e);
        } catch ( Exception e ) {
            throw new DataRetrieverException( e);
        }
    }

    // //////////////////
    // // Public method(s).
    public InteractionSimplified read( IntActIdentifierImpl intactId ) {
        InteractionDao interactionDao = daoFactory.getInteractionDao();
        InteractionImpl interaction = interactionDao.getByAc( intactId.convertToString() );
        if ( isInteractionEligible( interaction ) ) {
            if ( isHighConfidence(interaction) || isCuratedComplex( interaction ) ) {
                return saveInteractionInformation( interaction );
            }

            if ( isInVitro( interaction ) ) {
                return saveInteractionInformation( interaction );
            }

            CvInteractionType cvInteraction = interaction.getCvInteractionType();
            if ( isDirectInteraction(cvInteraction)){
                return saveInteractionInformation( interaction );
            }
            if ( isDisulfideBond( cvInteraction ) ) {
                return saveInteractionInformation( interaction );
            }

            if (isCrossLinked(interaction)){
                return saveInteractionInformation( interaction);
            }

            if ( isEnzymeRole(interaction) || isFluorescenceRole( interaction ) ) {
                return saveInteractionInformation( interaction );
            }
        }
        return null;
    }

    /**
     * reads only the protein information(uniprotAc and sequence and role) out
     * of the DB
     *
     * @param uniportAcs list with uniportACs
     * @return List {@link ProteinSimplified}
     */
    public List<ProteinSimplified> readSeq( List<String> uniportAcs ) {
        List<ProteinSimplified> proteins = new ArrayList<ProteinSimplified>();
        ProteinDao proteinDao = daoFactory.getProteinDao();
        for ( String ac : uniportAcs ) {
            Protein protein = proteinDao.getByAc( ac );
            ProteinSimplified proteinS = saveProteinInformation( protein );
            proteins.add( proteinS );
        }

        return proteins;
    }

    /**
     * saves out of DB the interactionAc, and components -uniprotAc into
     * InteractionSimplified structures
     *
     * @param ebiACs
     * @return List {@link InteractionSimplified}
     */
    public List<InteractionSimplified> read( Set<String> ebiACs ) {
        List<InteractionSimplified> interactions = new ArrayList<InteractionSimplified>();
        InteractionDao interactionDao = daoFactory.getInteractionDao();
        for ( String ac : ebiACs ) {
            Interaction interaction = interactionDao.getByAc( ac );
            if ( interaction != null ) {
                InteractionSimplified intS = saveInteractionInformation( interaction );
                if (log.isTraceEnabled()){
                    log.trace( "read: " + intS.getAc() );
                }
                interactions.add( intS );
            } else
                if (log.isDebugEnabled()){
                    log.debug( "interaction accession nr not found : " + ac );
                }
        }

        return interactions;
    }

    public void readConfidences( Report report ) { //File hcFile, File mcFile) throws Exception {
        cleanIfExists( report.getHighconfFile() );
        cleanIfExists( report.getMedconfFile() );
        nrHC = 0;
        nrMC = 0;
        nrAuthorConf = 0;
        nrComplexes = 0;
        nrEnzyme = 0;
        nrFluoresc = 0;

        int totalNr = 0;
        int paceNr = 50;
        // only for test purpose

        InteractionDao interactionDao = daoFactory.getInteractionDao();
        totalNr = interactionDao.countAll();
        if ( log.isDebugEnabled() ) {
            log.debug( "\tGoing to process: " + totalNr );
        }

        int firstResult = 0;
        int maxResults = 50;

        if ( dbNrForTest ) {
            paceNr = 2;
            maxResults = 2;
        }

        boolean firstTime = true;
        List<InteractionImpl> interactions = null;
        for ( int i = 0; i < totalNr; i += paceNr ) {
            beginTransaction();
            interactions = interactionDao.getAll( firstResult, maxResults );

            process( interactions, report );//, mcFile);
            if ( firstTime ) {
                firstTime = false;
            }
            if ( log.isTraceEnabled() ) {
                int processed = firstResult + interactions.size();

                if ( firstResult != processed ) {
                    log.trace( "\t\tProcessed " + ( firstResult + interactions.size() + " out of " + totalNr ) );
                }
                log.trace("authorConf(case1, case2, case3): " + nrAuthorConf +"(" + nrAuthorConf1 + ", " + nrAuthorConf2 + ", " + nrAuthorConf3 +")"
                + " complexes: " + nrComplexes + " enzyme: " + nrEnzyme + " flurophore: " + nrFluoresc
                + " in vitro: " + nrInVitro + " direct interactions: " + nrDirectInts + " disulfid bond: " + nrDisulfideInts
                + " crosslink: " + nrCrossLink);
            }

            firstResult = firstResult + maxResults;
            endTransaction();
        }
        if ( log.isDebugEnabled() ) {
            log.debug( "Processed " + totalNr + " IntAct interactions." );
        }
    }

    // ///////////////////
    // // Private Methods
    private void readConfidenceSets( File folder ) throws Exception {
        Report report = new Report(new File(folder, "highconf_set.txt"), new File(folder, "medconf_set.txt"));
        readConfidences(report);
    }

    private void cleanIfExists( File hcFile ) {
        if ( hcFile == null ) {
            return;
        }
        existsDelete( hcFile );

        String fileName = fileName( hcFile );
        File ipFile = new File( fileName + "_ip.txt" );
        existsDelete( ipFile );

        File goFile = new File( fileName + "_go.txt" );
        existsDelete( goFile );

        File seqFile = new File( fileName + "_seq.txt" );
        existsDelete( seqFile );
    }

    private void existsDelete( File file ) {
        if ( file.exists() ) {
            file.delete();
        }
    }

    private void endTransaction() {
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            throw new IntactException( e );
        }
    }

    private void process( List<InteractionImpl> interactions, Report report ) {//File hcFile, File mcFile) throws Exception {
        for ( Iterator<InteractionImpl> iterator = interactions.iterator(); iterator.hasNext(); ) {
            InteractionImpl interaction = iterator.next();
            if ( isInteractionEligible( interaction ) ) {
                boolean highConf = isHighConfidence( interaction );
                boolean curatedComplex = isCuratedComplex (interaction);
                boolean inVitro = isInVitro( interaction );
                CvInteractionType cvInteraction = interaction.getCvInteractionType();
                boolean directInt = isDirectInteraction( cvInteraction );
                boolean disulfideBond = isDisulfideBond(cvInteraction);
                boolean isCrosslink = isCrossLinked( interaction );
                boolean enzyme = isEnzymeRole( interaction );
                boolean  fluorescence = isFluorescenceRole(interaction);

                if (highConf){nrAuthorConf++;}
                if (curatedComplex){nrComplexes++;}
                if (inVitro){nrInVitro++;}
                if (directInt){nrDirectInts++;}
                if (disulfideBond) {nrDisulfideInts++;}
                if (isCrosslink) {nrCrossLink++;}
                if (enzyme) {nrEnzyme++;}
                if (fluorescence) {nrFluoresc++;}

                if ( highConf || curatedComplex || enzyme || fluorescence || inVitro || directInt || disulfideBond || isCrosslink ) {
                    InteractionSimplified intS = saveInteractionInformation( interaction );
                    // expand intS
                    Collection<InteractionSimplified> intSsimpl = expansionStrategy.expand( intS );
                    nrHC += intSsimpl.size();
                    if ( log.isTraceEnabled() ) {
                        if ( highConf || curatedComplex) {
                            log.trace( "HighConfOrComplex: " + intS.getAc() + " (authorConf: " + nrAuthorConf + ") (curated complex: " + nrComplexes + ")  total hc ( with dupplicates): " + nrHC );
                        } else if ( enzyme || fluorescence ) {
                            log.trace( "enzymeOrFluoresc: " + intS.getAc() + " (enzymes: " + nrEnzyme + ") ( flurophore: " + nrFluoresc + ") total hc (with dupplicates): " + nrHC );
                        } else if ( directInt || disulfideBond ) {
                            log.trace( "directOrDisulfide: " + intS.getAc() + " (directInt: " + nrDirectInts + ") ( disulfide: " + nrDisulfideInts + ") total hc (with dupplicates): " + nrHC );
                        } else if ( inVitro || isCrosslink ) {
                            log.trace( "vitroOrCrosslink: " + intS.getAc() + " (vitro: " + nrInVitro + ") ( crosslink: " + nrCrossLink + ") total hc (with dupplicates): " + nrHC );
                        }
                    }

                    //outPut intS to hcFile
                    outputData( intSsimpl, report.getHighconfFile() );
                    report.setHighconfGOFile( new File( fileName( report.getHighconfFile() ), "_go.txt" ) );
                    report.setHighconfIpFile( new File( fileName( report.getHighconfFile() ), "_ip.txt" ) );
                    report.setHighconfSeqFile( new File( fileName( report.getHighconfFile() ), "_seq.txt" ) );
                } else {
                    InteractionSimplified intS = saveInteractionInformation( interaction );
                    // expand intS
                    Collection<InteractionSimplified> intSsimpl = expansionStrategy.expand( intS );
                    nrMC += intSsimpl.size();
                    if ( log.isInfoEnabled() ) {
                        // log.info ("Medium Conf : " + intS.getAc() + " total mc (with dupplicates): " + nrMC);
                    }

                    //outPut intS to mcFile
                    outputData( intSsimpl, report.getMedconfFile() );
                    report.setMedconfGOFile( new File( fileName( report.getMedconfFile() ), "_go.txt" ) );
                    report.setMedconfIpFile( new File( fileName( report.getMedconfFile() ), "_ip.txt" ) );
                    report.setMedconfSeqFile( new File( fileName( report.getMedconfFile() ), "_seq.txt" ) );

                }
            }
        }
    }

    private void outputData( Collection<InteractionSimplified> interactions, File outFile ) {
        File goFile = new File( fileName( outFile ) + "_go.txt" );
        File ipFile = new File( fileName( outFile ) + "_ip.txt" );
        File seqFile = new File( fileName( outFile ) + "_seq.txt" );

        InteractionSimplifiedWriter writer = new CompactInteractionSWriterImpl();
        ProteinSimplifiedWriter protWriter = new ProteinSimplifiedWriterImpl();
        try {
        for ( Iterator<InteractionSimplified> iterator = interactions.iterator(); iterator.hasNext(); ) {
            InteractionSimplified interactionSimplified = iterator.next();
            writer.append( interactionSimplified, outFile );
            protWriter.appendGO( interactionSimplified.getComponents(), goFile );
            protWriter.appendIp( interactionSimplified.getComponents(), ipFile );
            protWriter.appendSeq( interactionSimplified.getComponents(), seqFile );
        }
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    protected String fileName( File outFile ) {
        String fileName = outFile.getPath();
        int i = fileName.lastIndexOf( "." );
        fileName = fileName.substring( 0, i );
        return fileName;
    }

    private void beginTransaction() {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    /**
     * checks if the interaction contains at least 2 proteins from uniprot
     *
     * @param interaction
     * @return true or false
     */
    private boolean isInteractionEligible( InteractionImpl interaction ) {
        int nr = 0;

        for ( Component comp : interaction.getComponents() ) {
            Interactor interactor = comp.getInteractor();
            if ( interactor != null && Protein.class.isAssignableFrom( interactor.getClass() )
                 && ProteinUtils.isFromUniprot( ( Protein ) interactor ) && fromUniprot( interactor ) ) {
                nr += 1;
                if ( comp.getStoichiometry() > 1 ) {
                    return true;
                }
            }
            if ( nr == 2 ) {
                return true;
            }
        }

        return false;
    }

    private boolean fromUniprot( Interactor interactor ) {
        if ( interactor == null ) {
            return false;
        }
        Collection<InteractorXref> xrefs = interactor.getXrefs();
        for ( InteractorXref interactorXref : xrefs ) {
            CvDatabase db = interactorXref.getCvDatabase();
            CvObjectXref dbXref = CvObjectUtils.getPsiMiIdentityXref( db );
            if ( dbXref == null ) {
                if (log.isWarnEnabled()){
                    log.warn( "dbXref == null, db: " + db + " interactor ac: " + interactor.getAc() );
                }
                return false;
            }
            if ( CvDatabase.UNIPROT_MI_REF.equals( db.getMiIdentifier() ) ) {
                CvXrefQualifier qualifier = interactorXref.getCvXrefQualifier();
                CvObjectXref qualifierXref = CvObjectUtils.getPsiMiIdentityXref( qualifier );
                // if the uniprotAc are marked for removal
                if ( qualifierXref == null ) {
                    if ( log.isWarnEnabled() ) {
                        log.warn( "qualifierXref is null for interactor :" + interactor.getAc() + " db qualifier: " + qualifier.getAc() );
                    }
                    return false;
                }

                if ( CvXrefQualifier.IDENTITY_MI_REF.equals( qualifierXref.getPrimaryId() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if the interaction belongs to a complex curated
     *
     * @param interaction
     * @return true or false
     */
    private boolean isCuratedComplex( InteractionImpl interaction ) {
         for ( Annotation item : interaction.getAnnotations() ) {
            if ( CvTopic.CURATED_COMPLEX.equals( item.getCvTopic().getShortLabel() ) ) {
              //  nrComplexes++;
//                if ( log.isTraceEnabled() ) {
//                    log.trace( "Found 'curated-complex': " + interaction.getAc() + "( total curated-complexes: " + nrComplexes + ")"  );
//                }
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the interaction belongs to a complex curated or if the author
     * confidence is high
     *
     * @param interaction
     * @return true or false
     */
    private boolean isHighConfidenceOrComplexes( InteractionImpl interaction ) {

        if ( isHighConfidence( interaction ) ) {
            //nrAuthorConf++;
            return true;
        }

        return false;
    }

    private boolean isHighConfidence( InteractionImpl interaction ) {
        if ( isCaseTwo( interaction ) ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "CASE 2: Found interaction " + interaction.getAc() );
            }
            nrAuthorConf2++;
            return true;
        }

        if ( isCaseThree( interaction ) ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "CASE 3: Found interaction " + interaction.getAc() );
            }
            nrAuthorConf3++;
            return true;
        }

        if ( isCaseOne( interaction ) ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "CASE 1: Found interaction " + interaction.getAc() );
            }
            nrAuthorConf1++;
            return true;
        }

        return false;
    }

     private boolean isCrossLinked( InteractionImpl interaction ) {
        Collection<Experiment> experiments = interaction.getExperiments();
        for ( Iterator<Experiment> iter = experiments.iterator(); iter.hasNext(); ) {
            Experiment experiment = iter.next();
            if (CvInteraction.CROSSLINK.equalsIgnoreCase( experiment.getCvInteraction().getShortLabel())){
                nrCrossLink++;
                if (log.isTraceEnabled()){
                    log.trace("Found 'crosslink' : " +interaction.getAc() + " nrCrossLink: " + nrCrossLink);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isCaseOne( InteractionImpl interaction ) {
        Collection<Experiment> experiments = interaction.getExperiments();
        for ( Iterator<Experiment> iter = experiments.iterator(); iter.hasNext(); ) {
            Experiment experiment = iter.next();
            if ( !FilterConstants.AUTHOR_CONFIDENCE_not_exp1.contains( experiment.getAc() ) ) {
                for ( Annotation item : interaction.getAnnotations() ) {
                    if ( CvTopic.AUTHOR_CONFIDENCE_MI_REF.equals( item.getCvTopic().getMiIdentifier() ) ) {
                        if ( FilterConstants.AUTHOR_CONFIDENCE_desc1.contains( item.getAnnotationText() ) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCaseTwo( InteractionImpl interaction ) {
        Collection<Experiment> experiments = interaction.getExperiments();
        for ( Iterator<Experiment> iter = experiments.iterator(); iter.hasNext(); ) {
            Experiment experiment = iter.next();
            if ( FilterConstants.AUTHOR_CONFIDENCE_exp2.contains( experiment.getAc() ) ) {
                for ( Annotation item : interaction.getAnnotations() ) {
                    if ( CvTopic.AUTHOR_CONFIDENCE_MI_REF.equals( item.getCvTopic().getMiIdentifier() ) ) {
                        if ( FilterConstants.AUTHOR_CONFIDENCE_desc2.contains( item.getAnnotationText() ) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCaseThree( InteractionImpl interaction ) {
        String parrish1 = "EBI-1256426";
        String parrish2 = "EBI-1256430";
        Collection<Experiment> experiments = interaction.getExperiments();
        for ( Iterator<Experiment> iter = experiments.iterator(); iter.hasNext(); ) {
            Experiment experiment = iter.next();
            if ( parrish1.equalsIgnoreCase( experiment.getAc() ) || parrish2.equalsIgnoreCase( experiment.getAc() ) ) {
                for ( Annotation item : interaction.getAnnotations() ) {
                    if ( CvTopic.AUTHOR_CONFIDENCE_MI_REF.equals( item.getCvTopic().getMiIdentifier() ) ) {
                        Double d = Double.valueOf( item.getAnnotationText() );
                        if ( d > 0.5 ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isInVitro( InteractionImpl interaction ) {
        Collection<Experiment> experiments = interaction.getExperiments();
        for ( Iterator<Experiment> iter = experiments.iterator(); iter.hasNext(); ) {
            Experiment experiment = iter.next();
            if ( FilterConstants.IN_VITRO.equalsIgnoreCase( experiment.getBioSource().getShortLabel() ) ) {
                for ( Iterator<Component> iter2 = interaction.getComponents().iterator(); iter2.hasNext(); ) {
                    Component component = iter2.next();
                    BioSource bioS = component.getExpressedIn();
                    if ( bioS != null &&  CvExperimentalRole.PREY_PSI_REF.equalsIgnoreCase( component.getCvBiologicalRole().getMiIdentifier())) {
                        if ( FilterConstants.PARTICIPANTS_IN_VITRO.contains( bioS.getShortLabel() ) ) {
                            //TODO: if one of the comps is in the list above its enough for me to take, is this fine ?!
//                            nrInVitro++;
//                            if ( log.isTraceEnabled() ) {
//                                log.trace( "Found 'in vitro' : " + interaction.getAc() + " type: '" + component.getExpressedIn().getShortLabel() + "' nrInVitro : " + nrInVitro );
//                            }
                            return true;
                        }
                    } else {
                        if ( log.isWarnEnabled() ) {
                            log.warn( "BioScource was null! " + interaction.getAc() );
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isDisulfideBond( CvInteractionType cvInteraction  ) {
        if (CvInteractionType.DISULFIDE_BOND.equalsIgnoreCase( cvInteraction.getShortLabel())){
//            nrDisulfideInts++;
//             if (log.isTraceEnabled()){
//                log.trace("Found disulfide bond: " + interaction.getAc() + " nrDisulfideInts: " + nrDisulfideInts);
//            }
            return true;
        }
        return false;
    }

    private boolean isDirectInteraction( CvInteractionType cvInteraction ) {
        if (CvInteractionType.DIRECT_INTERACTION_MI_REF.equals( cvInteraction.getMiIdentifier())){
//            nrDirectInts++;
//            if (log.isTraceEnabled()){
//                log.trace("Found direct interaction : " + interaction.getAc() + " total: " + nrDirectInts);
//            }
            return true;
        }
        if (CvInteractionType.DISULFIDE_BOND.equalsIgnoreCase( cvInteraction.getShortLabel())){
//            nrDisulfideInts++;
//             if (log.isTraceEnabled()){
//                log.trace("Found disulfide bond: " + interaction.getAc() + " nrDisulfideInts: " + nrDisulfideInts);
//            }
            return true;
        }
        return false;
    }

    /**
     * checks if the interaction contains enzymes, enzymes targets
     *
     * @param interaction
     * @return true or false
     */
    private boolean isEnzymeRole( InteractionImpl interaction ) {
        int enzymeNr = 0;
        int enzymeTargetNr = 0;

        for ( Component component : interaction.getComponents() ) {
            if ( CvBiologicalRole.ENZYME_PSI_REF.equals( component.getCvBiologicalRole().getMiIdentifier() ) ) {
                enzymeNr++;
            } else
            if ( CvBiologicalRole.ENZYME_TARGET_PSI_REF.equals( component.getCvBiologicalRole().getMiIdentifier() ) ) {
                enzymeTargetNr++;
            }
        }

        // TODO: ask if it is possible to have an interaction between an enzyme
        // and another protein (but not a target enzyme), if yes, are these
        // considered as high confidence?
        if ( enzymeNr + enzymeTargetNr >= 2 ) {
            nrEnzyme++;
            return true;
        }
        return false;
    }

    /**
     * checks if the interaction contains fluorescence
     * acceptors / donors
     *
     * @param interaction
     * @return true or false
     */
    private boolean isFluorescenceRole( InteractionImpl interaction ) {
        int fluorescenceAcceptorNr = 0;
        int fluorescenceDonorNr = 0;

        for ( Component component : interaction.getComponents() ) {
            if ( CvExperimentalRole.FLUROPHORE_ACCEPTOR_MI_REF.equals( component.getCvExperimentalRole().getMiIdentifier() ) ) {
                fluorescenceAcceptorNr++;
            } else
            if ( CvExperimentalRole.FLUROPHORE_DONOR_MI_REF.equals( component.getCvExperimentalRole().getMiIdentifier() ) ) {
                fluorescenceDonorNr++;
            }
        }

        if ( fluorescenceAcceptorNr + fluorescenceDonorNr >= 2 ) {
            nrFluoresc++;
            return true;
        }

        return false;
    }

    /**
     * saves the interaction information: interactionAc, the protein, the
     * proteins role into an InteractionSimplified object
     *
     * @param interaction
     * @return InteractionSimplified
     */
    private InteractionSimplified saveInteractionInformation( Interaction interaction ) {
        InteractionSimplified interactionS = new InteractionSimplified();

        interactionS.setAc( interaction.getAc() );

        Collection<Component> components = interaction.getComponents();
        List<ProteinSimplified> proteins = new ArrayList<ProteinSimplified>();
        for ( Component comp : components ) {
            Interactor interactor = comp.getInteractor();

            String role = "-";
            CvExperimentalRole expRole = comp.getCvExperimentalRole();
            CvObjectXref psiMiXref = CvObjectUtils.getPsiMiIdentityXref( expRole );
            if ( CvExperimentalRole.BAIT_PSI_REF.equals( psiMiXref.getPrimaryId() ) ) {
                role = "bait";
            }
            if ( CvExperimentalRole.PREY_PSI_REF.equals( psiMiXref.getPrimaryId() ) ) {
                role = "prey";
            }

            // this is because an interactor could be a small molecule, you want
            // to make sure you have a protein
            if ( Protein.class.isAssignableFrom( interactor.getClass() ) && fromUniprot( interactor )
                 && ProteinUtils.isFromUniprot( ( Protein ) interactor ) ) {
                ProteinSimplified protein = saveProteinInformation( ( Protein ) interactor );
                if ( protein != null ) {
                    protein.setRole( role );
                    proteins.add( protein );
                }
            }
        }
        if ( proteins.size() == 1 ) {
            proteins.add( proteins.get( 0 ) );
        }
        interactionS.setInteractors( proteins );

        return interactionS;
    }

    /**
     * saves the uniprotAc and the sequence into the new protein object
     *
     * @param protein
     * @return ProteinSimplified object
     */
    private ProteinSimplified saveProteinInformation( Protein protein ) {
        ProteinSimplified proteinS = new ProteinSimplified();

        InteractorXref uniprotXref = ProteinUtils.getUniprotXref( protein );
        if ( uniprotXref != null ) {
            try {
                proteinS.setUniprotAc( new UniprotAc( uniprotXref.getPrimaryId() ) );
            } catch ( IllegalArgumentException e ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "UniprotAc not recognized as a valid one:>" + uniprotXref.getPrimaryId() + "<" );
                }
            }
        } else {
            proteinS.setUniprotAc( null );
        }

        proteinS.setSequence( IntactUtils.getSequence( protein ) );
        proteinS.setGoSet( IntactUtils.getGOs( daoFactory, protein ) );
        proteinS.setInterProSet( IntactUtils.getIPs( daoFactory, protein ) );
        
        return proteinS;
    }  

    /**
     * @param dbNrForTest the dbNrForTest to set
     */
    public void setDbNrForTest( boolean dbNrForTest ) {
        this.dbNrForTest = dbNrForTest;
	}
}
