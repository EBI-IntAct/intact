package uk.ac.ebi.intact.protein.mapping.curation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultIdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.IdentificationStrategy;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyWithIdentifier;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyWithName;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyWithSequence;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * TODO try 20174651
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class ProteinIdentificationManager {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinIdentificationManager.class );
    private SheetContent sheetContent = new SheetContent();
    private HashMap<StrategyName, IdentificationStrategy> existingStrategyInstances = new HashMap<StrategyName, IdentificationStrategy>();
    protected boolean enableIsoformId = false;
    private IdentificationContext identificationContext;

    public ProteinIdentificationManager(String fileName){
        try {
            identificationContext = new IdentificationContext();
            InputStream proteinsToIdentify = new FileInputStream(new File(fileName));
            this.sheetContent.loadSheetContentOf(proteinsToIdentify);
            initialiseExistingStrategyInstances();

        } catch (FileNotFoundException e) {
            log.error("The file " + fileName + " doesn't exist.",e);
        }

    }

    public ProteinIdentificationManager(File file){
        try {
            identificationContext = new IdentificationContext();
            InputStream proteinsToIdentify = new FileInputStream(file);
            this.sheetContent.loadSheetContentOf(proteinsToIdentify);
            initialiseExistingStrategyInstances();

        } catch (FileNotFoundException e) {
            log.error("The file " + file.getAbsolutePath() + " doesn't exist.",e);
        }

    }

    public ProteinIdentificationManager(){
        identificationContext = new IdentificationContext();
        this.sheetContent = null;
        initialiseExistingStrategyInstances();
    }

    public void enableIsoformIds(){
        this.enableIsoformId=true;
    }

    private void initialiseExistingStrategyInstances(){
        if (!existingStrategyInstances.containsKey(StrategyName.identifier_mapping)){
            existingStrategyInstances.put(StrategyName.identifier_mapping, new StrategyWithIdentifier());
        }
        if (!existingStrategyInstances.containsKey(StrategyName.name_search)){
            existingStrategyInstances.put(StrategyName.name_search, new StrategyWithName());
        }
        if (!existingStrategyInstances.containsKey(StrategyName.sequence_blast)){
            existingStrategyInstances.put(StrategyName.sequence_blast, new StrategyWithSequence());
        }
    }

    /*private void setIdentificationStrategyForLine(int i){
        String strategyName;
        Class strategyClass;
        if (this.sheetContent.hasGivenASequenceForProtein(i)){
            strategyName = StrategyWithSequence.class.getSimpleName();
            strategyClass = StrategyWithSequence.class;
        }
        else{
            if (this.sheetContent.hasGivenAnIdentifierForProtein(i)){
                strategyName = StrategyWithIdentifier.class.getSimpleName();
                strategyClass = StrategyWithIdentifier.class;
            }
            else {
                strategyName = StrategyWithName.class.getSimpleName();
                strategyClass = StrategyWithName.class;
            }
        }

        if (existingStrategyInstances.containsKey(strategyName)){
            this.strategy = existingStrategyInstances.get(strategyName);
        }
        else {
            try {
                this.strategy = (IdentificationStrategy) strategyClass.newInstance();
            } catch (InstantiationException e) {
                log.error("We can't instantiate the identification strategy " + strategyName + ".");
            } catch (IllegalAccessException e) {
                log.error("We can't instantiate the identification strategy " + strategyName + ".");
            }
            existingStrategyInstances.put(strategyName, strategy);
        }
    }*/

    public ProteinIdentificationManager(InputStream stream){
        InputStream proteinsToIdentify = stream;
        this.sheetContent.loadSheetContentOf(proteinsToIdentify);
    }

    public SheetContent getSheetContent() {
        return sheetContent;
    }

    public void setSheetContent(InputStream proteinsToIdentify) {
        this.sheetContent.loadSheetContentOf(proteinsToIdentify);
    }

    public void setSheetContent(SheetContent content) {
        this.sheetContent = content;
    }

    private void setContext(String sequence, String identifier, BioSource organism, String geneName, String proteinName){
        this.identificationContext.setSequence(sequence);
        this.identificationContext.setIdentifier(identifier);
        this.identificationContext.setOrganism(organism);
        this.identificationContext.setGene_name(geneName);
        this.identificationContext.setProtein_name(proteinName);
    }

   /* public ArrayList<DefaultIdentificationResults> processProteinIdentification(){
        ArrayList<DefaultIdentificationResults> identificationResults = new ArrayList<DefaultIdentificationResults>();

        for (int i = 0; i < this.sheetContent.size(); i++){
            this.identificationContext.clean();
            setContext(this.sheetContent.getSequenceOfProteinAt(i), this.sheetContent.getIdentifierOfProteinAt(i), this.sheetContent.getOrganismOfProteinAt(i), this.sheetContent.getGeneNameOfProteinAt(i), this.sheetContent.getProteinNameOfProteinAt(i));
            setIdentificationStrategyForLine(i);
            this.strategy.enableIsoforms(this.enableIsoformId);

            try {
                identificationResults.add(this.strategy.identifyProtein(this.identificationContext));
            } catch (StrategyException e) {
                if (e instanceof StrategyWithSequenceException){
                    log.error("We cannot identify the sequence " + this.sheetContent.getSequenceOfProteinAt(i) + " at the line " + i + "using a blast strategy.", e);
                }
                else if (e instanceof StrategyWithIdentifierException){
                    log.error("We cannot map the identifier " + this.sheetContent.getIdentifierOfProteinAt(i) + " to an uniprot ID at the line " + i + "using an identifier mapping strategy.", e);
                }
                else if (e instanceof StrategyWithNameException){
                    log.error("We cannot map the name " + this.sheetContent.getGeneNameOfProteinAt(i) != null ? this.sheetContent.getGeneNameOfProteinAt(i) : this.sheetContent.getProteinNameOfProteinAt(i) + " to an uniprot ID at the line " + i + "using an name mapping strategy.", e);
                }
                else {
                    log.error("We cannot map the protein at the line " + i , e);
                }
            }
        }

        return identificationResults;
    } */


    public IdentificationResults processProteinIdentificationUsingIdentifier(String identifier, BioSource organism){
        this.identificationContext.clean();
        this.identificationContext.setIdentifier(identifier);
        this.identificationContext.setOrganism(organism);

        IdentificationStrategy strategy =  existingStrategyInstances.get(StrategyName.identifier_mapping);
        strategy.enableIsoforms(this.enableIsoformId);

        try {
            IdentificationResults result = strategy.identifyProtein(this.identificationContext);
        } catch (StrategyException e) {
            log.error("We cannot map the identifier " + identifier + " to an uniprot ID using an identifier mapping strategy.", e);
        }

        return null;
    }

    public DefaultIdentificationResults processProteinIdentificationUsingSequence(String sequence, BioSource organism){
        this.identificationContext.clean();
        this.identificationContext.setSequence(sequence);
        this.identificationContext.setOrganism(organism);

        IdentificationStrategy strategy = existingStrategyInstances.get(StrategyName.sequence_blast);
        strategy.enableIsoforms(this.enableIsoformId);

        try {
            IdentificationResults result = strategy.identifyProtein(this.identificationContext);
        } catch (StrategyException e) {
            log.error("We cannot map the sequence " + sequence + " to an uniprot ID using a sequence blast strategy.", e);
        }

        return null;
    }

    public IdentificationResults processProteinIdentificationUsingName(String name, BioSource organism, ColumnNames type){
        this.identificationContext.clean();

        switch (type){
            case gene_name:
                this.identificationContext.setGene_name(name);
            case protein_name:
                this.identificationContext.setProtein_name(name);
            default:
                this.identificationContext.setGlobalName(name);
        }
        this.identificationContext.setOrganism(organism);

        IdentificationStrategy strategy = existingStrategyInstances.get(StrategyWithName.class.getSimpleName());
        strategy.enableIsoforms(this.enableIsoformId);

        try {
            IdentificationResults result = strategy.identifyProtein(this.identificationContext);
        } catch (StrategyException e) {
            log.error("We cannot map the name " + name + " to an uniprot ID using a Uniprot name search strategy.", e);
        }

        return null;
    }
}
