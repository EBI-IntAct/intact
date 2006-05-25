package uk.ac.ebi.intact.util.msd;

import org.apache.commons.cli.*;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdExperiment;
import uk.ac.ebi.intact.util.msd.generator.msdGenerator.MsdExperimentGenerator;
import uk.ac.ebi.intact.util.msd.model.PdbBean;
import uk.ac.ebi.intact.util.msd.util.MsdHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: karine Date: 23 mai 2006 Time: 18:52:06 To change this template use File | Settings |
 * File Templates.
 */
public class MsdIntegrator {
    private List listPmid = new ArrayList();
    private static MsdExperimentGenerator msdExperimentGenerator = new MsdExperimentGenerator();

    public void integrateMsd( Collection<String> listPmid ) throws Exception, SQLException {
        Collection<MsdExperiment> listExp = new ArrayList();

        //select PMID
        //check not in intact on experiment with primary reference
        //new PMID list

        //For each PMID

        MsdHelper helper = new MsdHelper();

        helper.addMapping( PdbBean.class, "SELECT ditinct entry_id as pdbCode " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE pubmedid =?" );
        // for (String pmid : listPmid) {
        for ( Iterator<String> iterPmid = listPmid.iterator(); iterPmid.hasNext(); ) {
            String pmid = iterPmid.next().toString();
            List ListpdbBean = helper.getBeans( PdbBean.class, pmid );
            listExp.clear();
            // for (PdbBean pdbBean : ListpdbBean) {
            for ( Iterator<PdbBean> iterPdb = ListpdbBean.iterator(); iterPdb.hasNext(); ) {
                PdbBean pdbBean = iterPdb.next();
                String pdbCode = pdbBean.getPdbCode();
                // check PdbCode not in intact on an interaction with xref primary reference
                MsdExperiment exp = msdExperimentGenerator.createExp( pdbCode, listExp );
                if ( exp != null ) {
                    addExp( listExp, exp );
                    //add in intact the experiments for this pmid
                }
            }
        }

    }


    public void addExp( Collection<MsdExperiment> listExp, MsdExperiment exp ) {
        if ( listExp == null ) {listExp = new ArrayList();}
        listExp.add( exp );
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
        boolean debugEnabled = line.hasOption( "debug" );
        //integrator.setDebugEnabled(debugEnabled);
        //integrator.integrateMsd('12345');
    }

}

