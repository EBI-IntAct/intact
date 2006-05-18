package uk.ac.ebi.intact.util.msd;

import uk.ac.ebi.intact.util.msd.model.PdbBean;

import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;


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

        //TEST
        helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCode, "+ //title as structureTitle,  " +
                                          "experiment_type as experimentType, res_val as resolution, "+
                                          "r_work as rWork, " +
                                          "comp_list as moleculeList " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?" );

        for (Iterator iterator = helper.getBeans(PdbBean.class,pdbCode).iterator(); iterator.hasNext();) {
            PdbBean pdbBean = (PdbBean) iterator.next();
            System.out.println(pdbBean);
            System.out.println(pdbBean.getClass());
            System.out.println("experiment:"+pdbBean.getExperimentType());
            System.out.println("Pdb : "+pdbBean.getPdbCode());
            System.out.println("resolution : "+pdbBean.getResolution());
            System.out.println("moleculeList : "+pdbBean.getMoleculeList());
            System.out.println("RFactor : " + pdbBean.getrWork());
        }

        // LOAD intact_MSD_DATA View into a Pdb Object an an Experiment Object
                helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCode, title,  " +
                                          "experiment_type as experimentType, res_val as resolution, "+
                                          "r_work as rWork, " +"r_free as rFree, "+
                                          "oligomeric_state as oligomericStateList, "+"pubmedid as pmid, "+
                                          "comp_list as moleculeList " +
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?");

        for (Iterator iterator = helper.getBeans(PdbBean.class,"1B7R").iterator(); iterator.hasNext();) {
            PdbBean pdbBean = (PdbBean) iterator.next();
            System.out.println(pdbBean);
            System.out.println(pdbBean.getClass());
            System.out.println("experiment:"+pdbBean.getExperimentType());
            System.out.println("Pdb : "+pdbBean.getPdbCode());
            System.out.println("resolution : "+pdbBean.getResolution());
            System.out.println("moleculeList : "+pdbBean.getMoleculeList());
            System.out.println("RFactor : " + pdbBean.getrWork());

            //create the Pdb object
            Pdb pdb= new Pdb();
            PDBList.add(pdb);

            //set experimentType
            pdb.setExperimentType(pdbBean.getExperimentType());
            System.out.println(pdb.getExperimentType());

            //set PDBcode
            pdb.setPdbCode(pdbBean.getPdbCode());

            //set Title
            if (pdbBean.getResolution()!=null){
                pdb.setTitle(pdbBean.getTitle().trim());
            }else pdb.setResolution(null);

            //set Resolution
            if (pdbBean.getResolution()!=null){
                pdb.setResolution(pdbBean.getResolution().toString());
            }else pdb.setResolution(null);


            //set rWork
            if (pdbBean.getrWork()!=null){
            pdb.setrWork(pdbBean.getrWork().toString());
            }else pdb.setrWork(null);


            // set rFree
            if (pdbBean.getrFree()!=null){
            pdb.setrFree(pdbBean.getrFree().toString());
            }else pdb.setrFree(null);

            // set oligomericStateList
            pdb.setOligomericStateList(pdbBean.getOligomericStateList());

            //Print test
            System.out.println("Pdb resolution "+ pdb.getResolution());
            System.out.println("Pdb rWork "+ pdb.getrWork());
            System.out.println("Pdb Title "+ pdb.getTitle());
            System.out.println("Pdb rFree "+ pdb.getrFree());

            //create the Experiment Object
            Experiment exp= new Experiment();
            exp.setExperimentType(pdbBean.getExperimentType());
            String pmid=pdbBean.getPmid().toString();
            exp.setPmid(pmid);
            System.out.println("EXP pmid "+exp.getPmid());

            //link Experiment object and Pdb
            pdb.setExperiment(exp);
            exp.AddPDB(pdb);
            exp.Addpmid(exp.getPmid(),exp);

        }
        helper.close();
        return PDBList;

    }

    public static void main(String[] args) throws Exception, SQLException {
        PdbAndExpCreator pdbAndExpcreator = new  PdbAndExpCreator();
        ArrayList PDBList=pdbAndExpcreator.createPDBandExp ("1B7R");
        Pdb pdb=(Pdb)PDBList.get(0);
        System.out.println("pdb:"+pdb.getPdbCode());
        System.out.println("exp:"+pdb.getExperiment());


    }
}
