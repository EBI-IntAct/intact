package uk.ac.ebi.intact.psimitab.rsc;

import psidev.psi.mi.tab.model.builder.Row;


import java.util.Properties;
import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.2-SNAPSHOT
 */
public interface RelevanceScoreCalculator {

    String calculateScore( Row row);
    String calculateScore( Row row,String nameA,String nameB);

    Properties getWeights();
    void setWeights(Properties properties);
    Properties readPropertiesFile( String filePath) throws IOException;
    boolean writePropertiesFile(String filePath) throws IOException;
}
