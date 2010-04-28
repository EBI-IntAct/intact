package uk.ac.ebi.intact.bridges.ncbiblast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingException;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.TAlignment;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.THit;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class allows to read the wswublast results as xml file and filter with the identity percent and/or the organism.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Mar-2010</pre>
 */

public class BlastResultFilter {

    /**
     * The EBIApplicationResult containing the results
     */
    private EBIApplicationResult results;
    /**
     * The list of hits (BlastProtein objects) we want to keep
     */
    private ArrayList<BlastProtein> matchingEntries = new ArrayList<BlastProtein>();

    /**
     * The log of this class
     */
    public static final Log log = LogFactory.getLog( BlastResultFilter.class );

    /**
     * The intact uniprot service
     */
    private static UniprotRemoteService uniprotService = new UniprotRemoteService();

    /**
     * To know if we keep the local alignements in the results
     */
    private boolean wantLocalAlignment = false;

    /**
     * The wswublast reader
     */
    private BlastMappingReader bmr;

    public BlastResultFilter(){
        this.results = null;
        this.bmr = new BlastMappingReader();
    }

    /**
     *
     * @param keepLocalAlignment
     */
    public BlastResultFilter(boolean keepLocalAlignment){
        this.results = null;
        this.bmr = new BlastMappingReader();
        this.wantLocalAlignment = keepLocalAlignment;
    }

    /**
     *
     * @param results : the results to filter
     */
    public BlastResultFilter(String results){
        if ( log.isTraceEnabled() ) {
            log.trace( "reading results : ");
        }
        this.bmr = new BlastMappingReader();

        EBIApplicationResult appResult = null;
        try {
            appResult = bmr.read( results );
        } catch (BlastMappingException e) {
            throw new BlastResultFilterException(" Problem reading the String containing the wswublast results",e);
        }
        this.results = appResult;
    }

    /**
     *
     * @param results : the results to filter
     */
    public BlastResultFilter(InputStream results){
        if ( log.isTraceEnabled() ) {
            log.trace( "reading results : ");
        }
        this.bmr = new BlastMappingReader();

        EBIApplicationResult appResult = null;
        try {
            appResult = bmr.read( results );
        } catch (BlastMappingException e) {
            throw new BlastResultFilterException(" Problem reading the InputStream containing the wswublast results",e);
        }
        this.results = appResult;
    }

    /**
     *
     * @param results : the wswublast results we want to filter
     */
    public BlastResultFilter(EBIApplicationResult results){
        if ( log.isTraceEnabled() ) {
            log.trace( "reading results : ");
        }
        this.bmr = new BlastMappingReader();
        this.results = results;
    }

    /**
     *
     * @param wantLocalAlignment
     */
    public void setWantLocalAlignment(boolean wantLocalAlignment) {
        this.wantLocalAlignment = wantLocalAlignment;
    }

    /**
     * Get the hits that have been successfully filtered
     * @return a list of BlastProtein objects
     */
    public ArrayList<BlastProtein> getMatchingEntries() {
        return matchingEntries;
    }

    /**
     * If we don't want to keep the local alignments, will check the end and start of the matching sequence to know if it is a local alignment
     * @param hit
     */
    private void processHitResult(THit hit, String organism, int sequenceLength){

        if (!wantLocalAlignment){
            if (!isALocalAlignment(hit, sequenceLength)){
                BlastProtein blastEntry = createBlastProteinFrom(hit);
                blastEntry.setTaxId(organism);
                this.matchingEntries.add(blastEntry);
            }
        }
        else {
            BlastProtein blastEntry = createBlastProteinFrom(hit);
            this.matchingEntries.add(blastEntry);
            blastEntry.setTaxId(organism);
        }

    }

    /**
     * Create a BlastProtein instance from a THit object
     * @param hit : the hit in the wswublast results
     * @return a BlastProtein instance for this THit instance
     */
    private BlastProtein createBlastProteinFrom(THit hit){
        BlastProtein entry = new BlastProtein();
        entry.setAccession(hit.getAc());
        entry.setDescription(hit.getDescription());
        entry.setDatabase(hit.getDatabase());

        TAlignment alignment = hit.getAlignments().getAlignment().get(0); //always one alignment?
        entry.setIdentity(alignment.getIdentity());
        entry.setSequence(alignment.getMatchSeq().getValue());
        entry.setStart(alignment.getMatchSeq().getStart());
        entry.setEnd(alignment.getMatchSeq().getEnd());

        entry.setAlignment(alignment.getPattern());

        return entry;
    }

    /**
     * get the list of THit instances from the EBIApplicationResult
     * @return the list of THits instance that contains the EBIApplicationResult
     */
    private List<THit> collectResults() {
        if ( this.results != null ) {
            List<THit> xmlHits = this.results.getSequenceSimilaritySearchResult().getHits().getHit();

            return xmlHits;
        }
        return null;
    }

    /**
     * Checks if the hit is a local alignment
     * @param hit
     * @return
     */
    private boolean isALocalAlignment(THit hit, int sequenceLength){
        TAlignment al = hit.getAlignments().getAlignment().iterator().next();
        int matchStart = al.getMatchSeq().getStart();
        int matchEnd = al.getMatchSeq().getEnd();
        int queryStart = al.getQuerySeq().getStart();
        int queryEnd = al.getQuerySeq().getEnd();

        if (matchStart == queryStart && matchStart == 1 && matchEnd == queryEnd && matchEnd == sequenceLength){
            return false;
        }
        return true;
    }

    /**
     * Checks if the blastProtein is a global alignment
     * @param prot
     * @return
     */
    private static boolean isAGlobalAlignment(BlastProtein prot, int sequenceLength){
        int matchStart = prot.getStart();
        int matchEnd = prot.getEnd();
        int queryStart = 0;
        int queryEnd = sequenceLength;

        if (matchStart == queryStart && matchEnd == queryEnd){
            return true;
        }
        return false;
    }

    /**
     * Create a BlastProtein instance for each hit in the results and add them to the list of BlastProtein we want to keep
     */
    public void readResultsWithoutFiltering() {

        List<THit> xmlHits = collectResults();
        int sequenceLength = this.results.getHeader().getParameters().getSequences().getSequence().iterator().next().getLength();

        if (xmlHits != null){
            for ( THit hit : xmlHits ) {
                processHitResult(hit, null, sequenceLength);
            }
        }
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has an identity percent superior or equal to 'identity' and add them to the list of BlastProtein we want to keep
     * @param identity : the threshold identity
     */
    public void filterResultsWithIdentity(float identity){
        List<THit> xmlHits = collectResults();
        int sequenceLength = this.results.getHeader().getParameters().getSequences().getSequence().iterator().next().getLength();

        for ( THit hit : xmlHits ) {
            TAlignment alignment = hit.getAlignments().getAlignment().get(0);
            if (alignment.getIdentity() >= identity){
                processHitResult(hit, null, sequenceLength);
            }
        }
    }

    /**
     * Extract the organism name from a description of a hit in the results
     * @param description : the description of a hit as it appears in the wswublast output
     * @return the scientific name of the organism
     */
    /*
    private String extractOrganismNameFromDescription(String description, String database){
        String organismName = null;

        if (database != null && database.equals("INTACT")){
            return extractOrganismNameFromIntActDescription(description);
        }

        if (description != null){
            if (description.contains("OS=")){
                int index = description.indexOf("OS=");
                String temporary = description.substring(index+3);

                int index2 = temporary.indexOf("=");
                if (index2 != -1 && index2 >= 3){
                    organismName = temporary.substring(0, index2 - 3);
                }
                else if (index2 == -1){
                    organismName = temporary;
                }
            }
        }

        return organismName;
    }*/

    /**
     * Extract the organism name from a description of a hit in the results
     * @param description : the description of a hit as it appears in the wswublast output
     * @return the scientific name of the organism
     */
    /*
    private String extractOrganismNameFromIntActDescription(String description){
        String organismName = null;

        if (description != null){
            String shortLabel = description;

            if (description.contains(" ")){
                String [] des = description.split(" ");

                if (des.length == 3 || des.length == 2){
                    shortLabel = des[1];
                }
            }

            if (shortLabel.contains("_")){
                String [] org = shortLabel.split("_");

                return org [1];
            }
        }

        return organismName;
    }*/

    /**
     * Extract the organism name from an Uniprot entry
     * @param accession : the uniprot accession
     * @return the scientific name of the organism for this protein
     */
    private String importOrganismTaxIdFromUniprot(String accession){
        String taxId = null;

        if (accession != null){
            Collection<UniprotProtein> entries = uniprotService.retrieve(accession);
            UniprotProtein protein = entries.iterator().next();

            if (entries.size() != 1){
                log.error("The uniprot accession " + accession + " is matching several UniprotEntry instances. We will only take into account the first one : " + protein.getPrimaryAc());
            }

            if (protein != null){
                if (protein.getOrganism() != null){
                    taxId = Integer.toString(protein.getOrganism().getTaxid());
                }
            }
            else {
                log.error("There isn't any Uniprot entries with this accession number : "+accession);
            }

        }

        return taxId;
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has a specific organism and add them to the list of BlastProtein we want to keep
     * @param taxId : the scientific name of an organism
     */
    public void filterResultsWithOrganism(String taxId){
        List<THit> xmlHits = collectResults();
        int sequenceLength = this.results.getHeader().getParameters().getSequences().getSequence().iterator().next().getLength();

        for ( THit hit : xmlHits ) {

            String organism = importOrganismTaxIdFromUniprot(hit.getAc());

            if (organism != null){
                if (organism.equals(taxId)){
                    processHitResult(hit, organism, sequenceLength);
                }
            }
            else {
                throw new BlastResultFilterException("This hit doesn't have a scientific organism name and can be filtered : "+hit.toString());
            }

        }
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has an identity percent superior or equal to 'identity' and a specific organism and add them to the list of BlastProtein we want to keep
     * @param identity : the threshold identity
     * @param taxId : the scientific name of the organism
     */
    public void filterResultsWithIdentityAndOrganism(float identity, String taxId){
        List<THit> xmlHits = collectResults();
        int sequenceLength = this.results.getHeader().getParameters().getSequences().getSequence().iterator().next().getLength();

        for ( THit hit : xmlHits ) {
            String organism = importOrganismTaxIdFromUniprot(hit.getAc());

            TAlignment alignment = hit.getAlignments().getAlignment().get(0);

            if (alignment.getIdentity() >= identity){
                if (organism != null){
                    if (organism.equals(taxId)){
                        processHitResult(hit, organism, sequenceLength);
                    }
                }
                else {
                    throw new BlastResultFilterException("This hit doesn't have a scientific organism name and can be filtered : "+hit.toString());
                }
            }
        }
    }

    /**
     * Get a list of BlastProtein instances which have an identity percent superior or equal to 'identity'
     * @param identity : the threshold identity
     * @return a list of BlastProteins which have been filtered
     */
    public ArrayList<BlastProtein> filterMappingEntriesWithIdentity(float identity){
        ArrayList<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();
        for ( BlastProtein protein : this.matchingEntries ) {
            if (protein.getIdentity() >= identity){
                filteredProtein.add(protein);
            }
        }

        return filteredProtein;
    }

    /**
     * Get a list of BlastProtein instances which have a specific organism
     * @param taxId : the scientific name of an organism
     * @return a list of BlastProteins which have been filtered
     */
    public ArrayList<BlastProtein> filterMappingEntriesWithOrganism(String taxId){
        ArrayList<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries  ) {
            String organism = protein.getTaxId();

            if (organism == null){
                organism = importOrganismTaxIdFromUniprot(protein.getAccession());
                protein.setTaxId(organism);
            }
            if (organism != null){
                if (organism.equals(taxId)){
                    filteredProtein.add(protein);
                }
            }
            else {
                throw new BlastResultFilterException("This hit doesn't have a scientific organism name and can be filtered : "+protein.getAccession());
            }

        }
        return filteredProtein;
    }

    /**
     * Get a list of BlastProtein instances which have an identity percent superior or equal to 'identity'
     * @param identity : the threshold identity
     * @param taxId : the scientific name of an organism
     * @return : a list of BlastProteins which have been filtered
     */
    public ArrayList<BlastProtein> filterMappingEntriesWithIdentityAndOrganism(float identity, String taxId){
        ArrayList<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries ) {
            String organism = protein.getTaxId();

            if (organism == null){
                organism = importOrganismTaxIdFromUniprot(protein.getAccession());
                protein.setTaxId(organism);
            }

            if (protein.getIdentity() >= identity){
                if (organism != null){
                    if (organism.equals(taxId)){
                        filteredProtein.add(protein);
                    }
                }
                else {
                    throw new BlastResultFilterException("This hit doesn't have a scientific organism name and can be filtered : "+protein.getAccession());
                }
            }
        }return filteredProtein;

    }

    public static ArrayList<BlastProtein> collectMappingEntriesWithGlobalAlignment(ArrayList<BlastProtein> proteinToFilter, int sequenceLength){
         ArrayList<BlastProtein> proteins = new ArrayList<BlastProtein>();

        for (BlastProtein p : proteinToFilter){
            if (isAGlobalAlignment(p, sequenceLength)){
                proteins.add(p);
            }
        }

        return proteins;
    }

    /**
     * Clear the matching entries
     */
    public void clearMatchingEntries(){
        this.getMatchingEntries().clear();
    }

    /**
     * Set the results with an other EBIApplicationResult
     * @param results
     */
    public void setResults(EBIApplicationResult results) {
        this.results = results;
        clearMatchingEntries();
    }

    /**
     * Set the results with an other InputStream containing the results
     * @param results
     */
    public void setResults(InputStream results) {
        if ( log.isTraceEnabled() ) {
            log.trace( "reading results : ");
        }

        EBIApplicationResult appResult = null;
        try {
            appResult = bmr.read( results );
        } catch (BlastMappingException e) {
            throw new BlastResultFilterException(" Problem reading the InputStream containing the wswublast results",e);
        }
        this.results = appResult;
    }
}
