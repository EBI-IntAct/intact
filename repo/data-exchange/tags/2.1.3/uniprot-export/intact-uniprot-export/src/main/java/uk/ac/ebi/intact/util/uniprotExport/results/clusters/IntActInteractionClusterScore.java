package uk.ac.ebi.intact.util.uniprotExport.results.clusters;

import org.apache.log4j.Logger;
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.*;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteractionForScoring;
import uk.ac.ebi.enfin.mi.cluster.MethodTypePair;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;
import uk.ac.ebi.enfin.mi.score.scores.UnNormalizedMIScore;
import uk.ac.ebi.intact.util.uniprotExport.filters.FilterUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Extension of the InteractionClusterScore : use a different format to export the scores and added utility methods.
 *
 * This class is computing the mi score of each clustered binary interaction while clustering
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16-Sep-2010</pre>
 */

public class IntActInteractionClusterScore extends InteractionClusterScore implements IntactCluster {

    private static final Logger logger = Logger.getLogger(IntActInteractionClusterScore.class);
    private String[] scoreList = null;
    private String scoreListCSV;

    private PsimiTabWriter writer;

    public IntActInteractionClusterScore(){
        super();

        setMappingIdDbNames("uniprotkb,intact");
        writer = new PsimiTabWriter();

        // we want direct interaction = 5
        setDirectInteractionWeight_5();
        // we want method weights with PCA = 1.5
        initializeMethodWeights_PCA_1_5();
        setPublicationWeight(0.0f);

        this.miscore = new UnNormalizedMIScore();
    }

    @Override
    protected void processMethodAndType(EncoreInteractionForScoring encoreInteraction, EncoreInteractionForScoring mappingEncoreInteraction) {
        Map<MethodTypePair, List<String>> existingMethodTypeToPubmed = mappingEncoreInteraction.getMethodTypePairListMap();

        for (Map.Entry<MethodTypePair, List<String>> entry : encoreInteraction.getMethodTypePairListMap().entrySet()){
            if (existingMethodTypeToPubmed.containsKey(entry.getKey())){
                List<String> existingPubmeds = existingMethodTypeToPubmed.get(entry.getKey());
                List<String> newPubmeds = encoreInteraction.getMethodTypePairListMap().get(entry.getKey());

                for (String pub : newPubmeds){
                    existingPubmeds.add(pub);
                }
            }
            else{
                existingMethodTypeToPubmed.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Initialises the weight of each method and decrease the weight of imaging techniques
     */
    private void initializeMethodWeights(){

        HashMap<String,Float> customOntologyMethodScores = new HashMap<String,Float>();
        customOntologyMethodScores.put("MI:0013", 3f); // cv1 // biophysical
        customOntologyMethodScores.put("MI:0090", 2f); // cv2 // protein complementation assay
        customOntologyMethodScores.put("MI:0254", 0.3f); // cv3 // genetic interference
        customOntologyMethodScores.put("MI:0255", 0.3f); // cv4 // post transcriptional interference
        customOntologyMethodScores.put("MI:0401", 3f); // cv5 // biochemical
        customOntologyMethodScores.put("MI:0428", 0.6f); // cv6 // imagining technique
        customOntologyMethodScores.put("unknown", 0.1f); // cv7 // unknown
        super.setCustomOntologyMethodScores(customOntologyMethodScores);
    }

    /**
     * Initialises the weight of each method and decrease the weight of imaging techniques.
     * PCA has a weight of 1.5 instead of 2
     */
    private void initializeMethodWeights_PCA_1_5(){

        HashMap<String,Float> customOntologyMethodScores = new HashMap<String,Float>();
        customOntologyMethodScores.put("MI:0013", 3f); // cv1 // biophysical
        customOntologyMethodScores.put("MI:0090", 1.5f); // cv2 // protein complementation assay
        customOntologyMethodScores.put("MI:0254", 0.3f); // cv3 // genetic interference
        customOntologyMethodScores.put("MI:0255", 0.3f); // cv4 // post transcriptional interference
        customOntologyMethodScores.put("MI:0401", 3f); // cv5 // biochemical
        customOntologyMethodScores.put("MI:0428", 0.6f); // cv6 // imagining technique
        customOntologyMethodScores.put("unknown", 0.1f); // cv7 // unknown
        super.setCustomOntologyMethodScores(customOntologyMethodScores);
    }

    /**
     * The best weight for the interactions types is direct interaction : 5.
     */
    public void setDirectInteractionWeight_5(){
        Map<String,Float> customOntologyTypeScores = new HashMap<String,Float>();
        customOntologyTypeScores.put("MI:0208", 0.25f);
        customOntologyTypeScores.put("MI:0403", 0.2f); // colocalization
        customOntologyTypeScores.put("MI:0914", 1f);
        customOntologyTypeScores.put("MI:0915", 2f);
        customOntologyTypeScores.put("MI:0407", 5f);
        customOntologyTypeScores.put("unknown", 0.1f);
        super.setCustomOntologyTypeScores(customOntologyTypeScores);
    }

    /**
     * The best weight for the interactions types is direct interaction : 3.
     */
    public void setDirectInteractionWeight_3(){
        Map<String,Float> customOntologyTypeScores = new HashMap<String,Float>();
        customOntologyTypeScores.put("MI:0208", 0.08f); // genetic interaction
        customOntologyTypeScores.put("MI:0403", 0.05f); // colocalization
        customOntologyTypeScores.put("MI:0914", 0.33f); // association
        customOntologyTypeScores.put("MI:0915", 0.67f); // physical association
        customOntologyTypeScores.put("MI:0407", 1.00f); // direct interaction
        customOntologyTypeScores.put("unknown", 0.03f); // unknown
        super.setCustomOntologyTypeScores(customOntologyTypeScores);
    }

    /**
     *
     * @return a list of formatted scores for each interaction
     */
    public String[] getScoresPerInteraction(){
        if(this.getInteractionMapping() == null){
            runService();
        }
        if(scoreList == null){
            int scoreListSize = this.getInteractionMapping().size();
            scoreList = new String[scoreListSize];
            scoreListCSV = "";
            String delimiter = "\n";
            int i = 0;
            for(EncoreInteractionForScoring eI:this.getInteractionMapping().values()){
                List<Confidence> confidenceValues = eI.getConfidenceValues();
                Double score = null;
                for(Confidence confidenceValue:confidenceValues){
                    if(confidenceValue.getType().equalsIgnoreCase("intactPsiscore")){
                        score = Double.parseDouble(confidenceValue.getValue());
                    }
                }
                if(score == null){
                    logger.error("No score for this interaction: " + eI.getId());
                }
                scoreList[i] = eI.getId() + "-" +eI.getInteractorA() + "-" + eI.getInteractorB() + ":" + score;
                scoreListCSV = scoreListCSV + scoreList[i];
                i++;
                if(scoreListSize > i){
                    scoreListCSV = scoreListCSV + delimiter;
                }
            }
        }
        return scoreList;
    }

    @Override
    public void saveScores(String fileName){
        /* Retrieve results */

        try {
            logger.info("Saving MITAB ... " + fileName);
            super.saveScoreInMitab(fileName);

        } catch (Exception e) {
            logger.error("It is not possible to write the results in the mitab file " + fileName);
            e.printStackTrace();
        }

        if(scoreList == null){
            getScoresPerInteraction();
        }
        try{
            // Create file
            logger.info("Saving scores on ... " + fileName + "_log.txt");
            FileWriter fstream = new FileWriter(fileName + "_log.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(scoreListCSV);
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            logger.error("Error: " + e.getMessage());
        }
    }

    @Override
    public void saveCluster(String fileName){
        saveScores(fileName);
    }

    /**
     * Save the scores of the specific interaction ids
     * @param fileName
     * @param interactionIds
     */
    public void saveClusteredInteractions(String fileName, Set<Integer> interactionIds){

        /* Retrieve results */

        Map<Integer, EncoreInteractionForScoring> interactionMapping = getInteractionMapping();
        Encore2Binary iConverter = new Encore2Binary(getMappingIdDbNames());
        logger.info("Saving scores...");

        try {
            File file = new File(fileName);

            FileWriter fstream = new FileWriter(fileName + ".txt");

            for(Integer mappingId:interactionIds){
                EncoreInteractionForScoring eI = interactionMapping.get(mappingId);

                // convert and write in mitab
                if (eI != null){
                    double score = FilterUtils.getMiClusterScoreFor(eI);

                    // write score in a text file
                    fstream.write(Integer.toString(eI.getId()));
                    fstream.write("-");
                    fstream.write(eI.getInteractorA());
                    fstream.write("-");
                    fstream.write(eI.getInteractorB());
                    fstream.write(":" + score);
                    fstream.write("\n");
                    fstream.flush();

                    BinaryInteraction bI = iConverter.getBinaryInteractionForScoring(eI);
                    writer.writeOrAppend(bI, file, false);
                }
            }

            //Close the output stream
            fstream.close();

        } catch (Exception e) {
            logger.error("It is not possible to write the results in the mitab file " + fileName + " or in the text file " + fileName + ".txt");
            e.printStackTrace();
        }
    }

    public void clear(){
        this.getInteractionMapping().clear();
        this.getInteractorMapping().clear();
    }

    @Override
    public Map<Integer, EncoreInteractionForScoring> getEncoreInteractionCluster() {
        return getInteractionMapping();
    }

    @Override
    public Map<Integer, BinaryInteraction<Interactor>> getBinaryInteractionCluster() {
        Encore2Binary iConverter = new Encore2Binary(getMappingIdDbNames());

        Map<Integer, BinaryInteraction<Interactor>> binaryInteractionCluster = new HashMap<Integer, BinaryInteraction<Interactor>>();

        for(Integer mappingId:getInteractionMapping().keySet()){
            EncoreInteractionForScoring eI = getInteractionMapping().get(mappingId);
            if (eI != null){
                BinaryInteraction bI = iConverter.getBinaryInteractionForScoring(eI);
                binaryInteractionCluster.put(mappingId, bI);
            }
        }

        return binaryInteractionCluster;
    }

    @Override
    public Map<String, List<Integer>> getInteractorCluster() {
        return getInteractorMapping();
    }

    @Override
    public Set<Integer> getAllInteractionIds() {
        return getInteractionMapping().keySet();
    }
}
