package uk.ac.ebi.intact.util.msd;

import uk.ac.ebi.intact.util.msd.model.PdbBean;

import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;
import java.math.BigDecimal;


/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 27-Mar-2006
 * Time: 12:35:59
 * To change this template use File | Settings | File Templates.
 */
public class PdbAndExpCreator {

    public ArrayList createPDBandExp (String pdbCode) throws Exception, SQLException{
        ArrayList PDBList= new ArrayList(10);
        MsdHelper helper = new MsdHelper();

        // LOAD intact_MSD_DATA View into a MsdInteraction Object an an Experiment Object
        helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCode, title,  " +
                                          "experiment_type as experimentType, res_val as resolution, "+
                                          "r_work as rWork, " +"r_free as rFree, "+
                                          "oligomeric_state as oligomericStateList, "+"pubmedid as pmid, "+
                                          "comp_list as moleculeList " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?");

        for (Iterator iterator = helper.getBeans(PdbBean.class,pdbCode).iterator(); iterator.hasNext();) {
            PdbBean pdbBean = (PdbBean) iterator.next();


             //create the MsdInteraction object
             MsdInteraction msdInteraction = createMsdInteraction(pdbBean);

            //create the Experiment Object


            //link MsdExperiment object and MsdInteraction
            MsdExperiment exp = createMsdExperiment(pdbBean, msdInteraction);

            exp.addPmid(exp.getPmid(),exp);

            msdInteraction.setExperiment(exp);

            PDBList.add(msdInteraction);
        }
        helper.close();
        return PDBList;

    }

    private MsdInteraction createMsdInteraction(PdbBean pdbBean){
        //create the MsdInteraction object
            MsdInteraction msdInteraction= new MsdInteraction();

            //set experimentType
            msdInteraction.setExperimentType(pdbBean.getExperimentType());

            //set PDBcode
            msdInteraction.setPdbCode(pdbBean.getPdbCode());

            //set Title
            String title = pdbBean.getTitle();
            if (title != null){
                msdInteraction.setTitle(title.trim());
            } else {
                msdInteraction.setResolution(null);
            }

            //set Resolution
            BigDecimal resolution = pdbBean.getResolution();
            if (resolution != null) {
                msdInteraction.setResolution(pdbBean.getResolution().toString());
            } else {
                msdInteraction.setResolution(null);
            }


            //set rWork
            BigDecimal rWork = pdbBean.getrWork();
            if (rWork != null){
                msdInteraction.setrWork(pdbBean.getrWork().toString());
            }else msdInteraction.setrWork(null);


            // set rFree
            BigDecimal rFree = pdbBean.getrFree();
            if (rFree != null){
                msdInteraction.setrFree(pdbBean.getrFree().toString());
            }else msdInteraction.setrFree(null);

            // set oligomericStateList
            msdInteraction.setOligomericStateList(pdbBean.getOligomericStateList());

            return msdInteraction;
    }

    private MsdExperiment createMsdExperiment(PdbBean pdbBean, MsdInteraction msdInteraction){

        MsdExperiment msdExp= new MsdExperiment();
        msdExp.setExperimentType(pdbBean.getExperimentType());

        if(pdbBean.getPmid() != null){
            String pmid=pdbBean.getPmid().toString();
            msdExp.setPmid(pmid);
        }
        msdExp.addPdb(msdInteraction);
        return msdExp;
    }

    public static void main(String[] args) throws Exception, SQLException {
        PdbAndExpCreator pdbAndExpcreator = new  PdbAndExpCreator();
        ArrayList PDBList=pdbAndExpcreator.createPDBandExp ("1B7R");
        MsdInteraction pdb=(MsdInteraction)PDBList.get(0);
        System.out.println("pdb:"+pdb.getPdbCode());
        System.out.println("exp:"+pdb.getExperiment());


    }
}
