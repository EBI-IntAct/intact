package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.util.msd.model.PdbBean;
import uk.ac.ebi.intact.util.msd.util.CvMapper;
import uk.ac.ebi.intact.util.msd.util.MsdHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: karine Date: 18 mai 2006 Time: 10:28:20 To change this template use File | Settings |
 * File Templates.
 */
public class MsdExperimentGenerator {


    public MsdExperiment createExp( String pdbCode, Collection<MsdExperiment> expList ) throws Exception, SQLException {
        MsdExperiment msdExp = new MsdExperiment();

        MsdHelper helper = new MsdHelper();

        helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCode, title,  " +
                                          "experiment_type as experimentType, res_val as resolution, " +
                                          "r_work as rWork, r_free as rFree" +
                                          "comp_list as moleculeList, " +
                                          "pubmedid as pmid, " +
                                          "oligomeric_state as oligomericStateList, " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?" );

        List pdbBeanList = helper.getBeans( PdbBean.class, pdbCode );
        if ( pdbBeanList.isEmpty() || pdbBeanList == null ) {
            msdExp = null;
        }
        PdbBean pdbBean = filterPdbBeans( pdbBeanList );

        /**
         PdbBean :
         private String pdbCode;
         private String title;//title
         private String experimentType;// experiment_type
         private BigDecimal resolution;  // res_val
         private BigDecimal rFree; //r_free
         private BigDecimal rWork; //r_work
         private String moleculeList;//comp_list
         private BigDecimal pmid;//pubmedid
         private String oligomericStateList; //oligomeric_state
         **/

        String experimentType = pdbBean.getExperimentType();
        CvObject cv = experimentTypeConvertor( experimentType );

        if ( cv == null ) {
            msdExp = null;
        } else {
            String pmid = pmidConvertor( pdbBean.getPmid() );

            // for (MsdExperiment exp : expList {
            for ( Iterator<MsdExperiment> iterator = expList.iterator(); iterator.hasNext(); ) {
                MsdExperiment exp = iterator.next();
                if ( exp.getCvIntDetect() == cv ) {
                    // if same cv and  pmid use the same experiment
                    msdExp = exp;

                } else {

                    msdExp = new MsdExperiment();
                    msdExp.setExperimentType( experimentType );
                    msdExp.setCvIntDetect( cv );
                    msdExp.setPmid( pmid );
                    MsdInteractionGenerator msdInteractionGenerator = new MsdInteractionGenerator();
                    msdInteractionGenerator.createInt( pdbBean, msdExp );

                }
            }

        }


        return msdExp;
    }

    /**
     * The PDBbean contains a BigDecimal for pmid and need to be converted to a String to fit the Intact model
     *
     * @param pmidBG as BigDecimal
     *
     * @return pmid as a String
     */
    public String pmidConvertor( BigDecimal pmidBG ) {
        String pmid;
        pmid = pmidBG.toString();
        return pmid;
    }

    /**
     * The experimentType need to be mapped to a controlled vocabulary for experiment detection
     *
     * @param experimentType from the MSD table
     *
     * @return CvObject using the CV mapping of the CvMapper
     */
    public CvObject experimentTypeConvertor( String experimentType ) {
        CvObject cvObject = null;
        try {
            cvObject = CvMapper.getCvObjectFromMsdTerm( experimentType );
        } catch ( IntactException e ) {
            System.out.println( "Could not find in Intact any cvObject corresponding to the msdTerm " + experimentType );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return cvObject;
    }

    /**
     * Due to different assemblies which can have different oligomeric states, the intact_msd_data can contains several
     * rows for the same PdbCode. This method concatenate the oligomeric states and transform the PdbBeanlist in one
     * PdbBean
     *
     * @param pdbBeanList
     *
     * @return pdbBean
     */
    public PdbBean filterPdbBeans( List<PdbBean> pdbBeanList ) {
        PdbBean UniqPdbBean;
        if ( pdbBeanList.size() > 1 ) {
            String oligomericStates = "";


            for ( Iterator iterator = pdbBeanList.iterator(); iterator.hasNext(); ) {
                PdbBean pdbBean = (PdbBean) iterator.next();
                oligomericStates = oligomericStates + ", " + pdbBean.getOligomericStateList();
            }
            UniqPdbBean = pdbBeanList.get( 0 );
            UniqPdbBean.setOligomericStateList( oligomericStates );

        } else {
            UniqPdbBean = pdbBeanList.get( 0 );
        }
        return UniqPdbBean;
    }

    public static void main( String[] args ) throws Exception, SQLException {
        MsdExperimentGenerator msdExperimentGenerator = new MsdExperimentGenerator();
        MsdExperiment exp;
        Collection<MsdExperiment> listExp = new ArrayList();
        exp = msdExperimentGenerator.createExp( "1B7R", listExp );
        System.out.println( exp.toString() );
        for ( Iterator<MsdInteraction> iter = exp.getMsdInteractions().iterator(); iter.hasNext(); ) {
            MsdInteraction msdInteraction = iter.next();
            System.out.println( msdInteraction.toString() );
        }


    }
}








