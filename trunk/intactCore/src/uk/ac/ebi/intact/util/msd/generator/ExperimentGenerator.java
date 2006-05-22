package uk.ac.ebi.intact.util.msd.generator;

import uk.ac.ebi.intact.util.msd.MsdHelper;
import uk.ac.ebi.intact.util.msd.MsdExperiment;
import uk.ac.ebi.intact.util.msd.MsdInteraction;
import uk.ac.ebi.intact.util.msd.model.PdbBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: karine
 * Date: 18 mai 2006
 * Time: 10:28:20
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentGenerator {

    private static HashMap<String, String> CvHashMap= new HashMap (10);

    static {
        CvHashMap.put("electron microscopy","MI:0040");
        CvHashMap.put("electron tomography","MI:0410");
        CvHashMap.put("NMR","MI:0077");
        CvHashMap.put("Single crystal X-ray diffraction", "MI:0114");
    }
    // complexes in msd_unp_data:
    //  Electron diffraction 4 Electron microscopy 32 Electron tomography 11 NMR 158
    //  Single crystal X-ray diffraction 2889 Theoretical model 86 Unspecified 6 Null 6

    // On MSD search :
    // X-ray	Electron microscopy Theoretical model	Electron diffraction
    // N.M.R.	Electron tomography Fibre diffraction	Fluorescence transfer
    // Infrared spectroscopy	Neutron diffraction
    // Powder diffraction (X-ray)	Solid state NMR     Other method

    /**  The controlled vocabulary for participant detection is:
     * predetermined: id: MI:0396 name: predetermined participant
     *   The Biosource is:
     * in vitro : full host organisms description is recommended using tax id == -1 as convention to refer to 'in vitro' interaction
     *   The controlled vocabulary for the experiment detection depends on the MSD experimentType:
     * "electron diffraction is not included in IntAct for the moment"
     * "electron microscopy" corresponds to CV id: MI:0040 name: electron microscopy
     * "electron tomography" corresponds to CV id: MI:0410 name: electron tomography
     * "NMR" corresponds to CV id: MI:0077 name: nuclear magnetic resonance
     * "Single crystal X-ray diffraction" corresponds to CV id: MI:0114 name: x-ray crystallography
     * "theorical model" is not included in IntAct
     * "unspecified" is not included in IntAct**/



    public ArrayList <MsdExperiment> createExp (String pdbCode) throws Exception, SQLException{

        ArrayList <MsdExperiment> ExperimentList = new ArrayList <MsdExperiment>();
        ArrayList <MsdInteraction> InteractionList = new ArrayList <MsdInteraction>();

        MsdHelper helper = new MsdHelper();

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

        helper.addMapping( PdbBean.class, "SELECT entry_id as pdbCode, title,  " +
                                          "experiment_type as experimentType, res_val as resolution, "+
                                          "r_work as rWork, r_free as rFree" +
                                          "comp_list as moleculeList, " +
                                          "pubmedid as pmid, "+
                                          "oligomeric_state as oligomericStateList, "+
                                          "FROM INTACT_MSD_DATA " +
                                          "WHERE entry_id =?" );

        /**for (Iterator iterator = helper.getBeans(PdbBean.class,pdbCode).iterator(); iterator.hasNext();) {
            PdbBean pdbBean = (PdbBean) iterator.next();
            System.out.println(pdbBean);
            System.out.println(pdbBean.getClass());
            System.out.println("experimentType:"+pdbBean.getExperimentType());
            System.out.println("MsdInteraction : "+pdbBean.getPdbCode());
            System.out.println("resolution : "+pdbBean.getResolution());
            System.out.println("moleculeList : "+pdbBean.getMoleculeList());
            System.out.println("rWork : " + pdbBean.getrWork());
            System.out.println("rFree: "+ pdbBean.getrFree());
            System.out.println("pmid: "+ pdbBean.getPmid());
            System.out.println("oligomericStateList: "+ pdbBean.getOligomericStateList());
        }**/

        for (Iterator iterator = helper.getBeans(PdbBean.class,pdbCode).iterator(); iterator.hasNext();) {
            PdbBean pdbBean = (PdbBean) iterator.next();

            //create the Experiment Object
            // if same cv and  pmid use the previous one
            String experimentType = pdbBean.getExperimentType();
            MsdInteraction msdInteraction = new MsdInteraction();
            MsdExperiment msdExp = new MsdExperiment();
            msdExp.setExperimentType(experimentTypeConvertor(pdbBean.getExperimentType()));
            msdExp.setPmid(pmidConvertor(pdbBean.getPmid()));

      }

        return ExperimentList;
    }

     public String  pmidConvertor (BigDecimal pmidBG){
         String pmid;
         pmid = pmidBG.toString();
         return pmid;
     }

    public String experimentTypeConvertor (String experimentType){
        String cvExperimentType;
        if (CvHashMap.containsKey(experimentType)){
            cvExperimentType= CvHashMap.get(experimentType);
        }else return null;
        return cvExperimentType;
        }





}
