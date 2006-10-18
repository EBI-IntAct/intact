package uk.ac.ebi.intact.util.msd;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.msd.generator.intactGenerator.ExperimentGenerator;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdExperiment;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdExperimentGenerator;
import uk.ac.ebi.intact.util.msd.model.PdbBean;
import uk.ac.ebi.intact.util.msd.util.MsdHelper;
import uk.ac.ebi.intact.util.msd.util.MsdToolBox;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.context.IntactContext;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: karine Date: 23 mai 2006 Time: 18:52:06 To change this template use File | Settings |
 * File Templates.
 */
public class MsdIntegrator {

    private static final Log log = LogFactory.getLog(MsdIntegrator.class);

    private Map<String, Collection<String>> pmidMap = new HashMap<String, Collection<String>> ();
    private static MsdExperimentGenerator msdExperimentGenerator = new MsdExperimentGenerator();

    public void fillPmidMap() throws Exception, SQLException {
        //select PMID
        //check not in intact on experiment with primary reference
        //new PMID list
        MsdHelper helper = new MsdHelper();

        helper.addMapping( PdbBean.class, "select entry_id as pdbCode, distinct pubmedid as pmid " +
                                          "from  intact_msd_data " +

                                          "where pubmedid is not null " +

                                          "where entry_id not in " +
                                          "(select  entry_id from  intact_msd_chain_data " +
                                          "where type = 'Nucleic_Acid')" +

                                          "and entry_id in " +
                                          "(select entry_id  from intact_msd_unp_data " +
                                          "group by entry_id having count (distinct sptr_ac) >1)" +

                                          "and entry_id not in " +
                                          "(select entry_id from  intact_msd_chain_data where system_tax_id is null)" );

        for ( Iterator iterator = helper.getBeans( PdbBean.class, "" ).iterator(); iterator.hasNext(); ) {
            PdbBean pdbBean = (PdbBean) iterator.next();
            String pdbCode = pdbBean.getPdbCode();
            String pmid = pdbBean.getPmid().toString();
            if ( !pmidMap.containsKey( pmid ) ) {
                Collection<String> pdbCodeList = new ArrayList();
                pdbCodeList.add( pdbCode );
                pmidMap.put( pmid, pdbCodeList );
            } else {
                if ( !pmidMap.get( pmid ).contains( pdbCode ) ) {
                    pmidMap.get( pmid ).add( pdbCode );
                }
            }

        }
        helper.close();
    }

    public void fillPmidMap( Collection<String> pmids ) throws Exception, SQLException {

        MsdHelper helper = new MsdHelper();

        helper.addMapping( PdbBean.class, "SELECT distinct entry_id as pdbCode " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE pubmedid =?" );
        for ( String pmid : pmids ) {
            List<PdbBean> pdbBeans = helper.getBeans( PdbBean.class, pmid );
            for ( PdbBean pdbBean : pdbBeans ) {
                String pdbCode = pdbBean.getPdbCode();

                if ( !pmidMap.containsKey( pmid ) ) {
                    Collection<String> pdbCodeList = new ArrayList();
                    pdbCodeList.add( pdbCode );
                    pmidMap.put( pmid, pdbCodeList );
                } else {
                    if ( !pmidMap.get( pmid ).contains( pdbCode ) ) {
                        pmidMap.get( pmid ).add( pdbCode );
                    }
                }
            }

        }

    }

    public void filterAlreadyCuratedPdbCode() throws IntactException {
        CvDatabase cvPubMed = MsdToolBox.getPubmed();
        CvDatabase cvPdb = MsdToolBox.getPdb();
        CvXrefQualifier cvPrimaryRef = MsdToolBox.getPrimaryRef();
        CvXrefQualifier cvIdentity = MsdToolBox.getIdentity();
        for ( String pmid : pmidMap.keySet() ) {

            if ( !(IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().getByXrefLike(cvPubMed, cvPrimaryRef, pmid).isEmpty() ) ) {
                if (log.isDebugEnabled()) {
                log.debug( "Experiment(s) with the PMID " + pmid + " already in IntAct" );
                }
                for ( String pdbCode : pmidMap.get( pmid ) ) {
                    if ( !( IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao().getByXrefLike(cvPdb, cvIdentity, pmid).isEmpty() ) ) {
                        if (log.isDebugEnabled()) {
                            log.debug( "Interaction with the PDB " +pdbCode + " the PMID "+pmid+" are already in IntAct" );
                            }
                        pmidMap.get( pmid ).remove( pdbCode );
                        if (pmidMap.get( pmid ).isEmpty()){
                            pmidMap.remove( pmid );
                        }
                        if (log.isDebugEnabled()) {
                            log.debug( "Interaction with the PDB "+pdbCode+"  associated to the PMID "+pmid+" removed from TODO list " );
                            }
                    }else{if (log.isDebugEnabled()) {
                            log.debug( "Experiment(s) associated to the PMID "+pmid+" already exist in IntAct but not the interaction for the PDB "+pdbCode );
                            }
                    }
                }

            }else{
                for ( String pdbCode : pmidMap.get( pmid ) ) {
                    if ( !(IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao().getByXrefLike(cvPdb, cvIdentity, pmid).isEmpty() ) ) {
                        if (log.isDebugEnabled()) {
                            log.warn( "Interaction with the PDB " +pdbCode + " already in IntAct but not experiment(s) associated to the PMID "+pmid+"!" );
                            // Check manualy this entry
                            pmidMap.get( pmid ).remove( pdbCode );
                            }
                    }
                }
            }
        }

    }

    public void integrateMsd() throws Exception, SQLException {
        ExperimentGenerator experimentGenerator = new ExperimentGenerator();
        //For each PMID
        for ( String pmid : pmidMap.keySet() ) {
            // For each pdbCode
            Collection<MsdExperiment> msdExperiments = new ArrayList<MsdExperiment>();
            for ( String pdbCode : pmidMap.get( pmid ) ) {
                MsdExperiment exp = msdExperimentGenerator.createExp( pdbCode, msdExperiments );
                if ( exp != null ) {
                    msdExperiments.add( exp );

                }
            }
            for ( MsdExperiment msdExperiment : msdExperiments ) {
                experimentGenerator.createExperiment( msdExperiment );
                //persit in intact the experiments for this pmid
            }
        }

    }
    public void addExp( Collection<MsdExperiment> msdExperiments, MsdExperiment exp ) {
        if ( msdExperiments == null ) {msdExperiments = new ArrayList<MsdExperiment>();}
        msdExperiments.add( exp );
    }

    public static void main( String[] args ) {
        MsdIntegrator integrator = new MsdIntegrator();
        Option debugOpt = OptionBuilder.withDescription( "Shows verbose output." ).create( "debug" );
        Options options = new Options();
        options.addOption( debugOpt );
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        try {
            line = parser.parse( options, args, true );
        } catch ( ParseException exp ) {
            //displayUsage( options );
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit( 1 );
        }
        // Choose a test collection of pmid
        // Collection <String> pmids =
        // integrator.fillPmidMap( pmids);
        // integrator.filterAlreadyCuratedPdbCode( );
        // integrator.integrateMsd( );

    }

}

