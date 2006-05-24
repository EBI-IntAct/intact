package uk.ac.ebi.intact.util.msd.util;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: karine
 * Date: 23 mai 2006
 * Time: 19:56:53
 * To change this template use File | Settings | File Templates.
 */
public class CvMapper {
    //TODO : add a properties file for the CV mapping
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

        public String cvMapping (String experimentType){
        String cvExperimentType;
        if (CvHashMap.containsKey(experimentType)){
            cvExperimentType= CvHashMap.get(experimentType);
        }else return null;
        return cvExperimentType;
        }
}
