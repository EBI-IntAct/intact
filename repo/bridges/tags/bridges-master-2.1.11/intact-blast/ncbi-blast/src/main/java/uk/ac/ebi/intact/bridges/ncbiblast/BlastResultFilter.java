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
import java.util.regex.Pattern;

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
    private List<BlastProtein> matchingEntries = new ArrayList<BlastProtein>();

    /**
     * The log of this class
     */
    public static final Log log = LogFactory.getLog( BlastResultFilter.class );

    /**
     * The intact uniprot service
     */
    private static UniprotRemoteService uniprotService = new UniprotRemoteService();

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
     * Get the hits that have been successfully filtered
     * @return a list of BlastProtein objects
     */
    public List<BlastProtein> getMatchingEntries() {
        return matchingEntries;
    }

    /**
     * If we don't want to keep the local alignments, will check the end and start of the matching sequence to know if it is a local alignment
     * @param hit
     * @param protein
     */
    private void processHitResult(THit hit, UniprotProtein protein){
        BlastProtein blastEntry = createBlastProteinFrom(hit);
        blastEntry.setUniprotProtein(protein);
        this.matchingEntries.add(blastEntry);
    }

    /**
     * Create a BlastProtein instance from a THit object
     * @param hit : the hit in the wswublast results
     * @return a BlastProtein instance for this THit instance
     */
    private BlastProtein createBlastProteinFrom(THit hit){
        BlastProtein entry = new BlastProtein();

        if (Pattern.matches(hit.getAc()+"-[1-9]",hit.getId())){
            entry.setAccession(hit.getId());
        }
        else {
            entry.setAccession(hit.getAc());
        }
        entry.setDescription(hit.getDescription());
        entry.setDatabase(hit.getDatabase());

        TAlignment alignment = hit.getAlignments().getAlignment().get(0); //always one alignment?
        entry.setIdentity(alignment.getIdentity());
        entry.setSequence(alignment.getMatchSeq().getValue());
        entry.setStartMatch(alignment.getMatchSeq().getStart());
        entry.setEndMatch(alignment.getMatchSeq().getEnd());
        entry.setStartQuery(alignment.getQuerySeq().getStart());
        entry.setEndQuery(alignment.getQuerySeq().getEnd());

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
        List<THit> empty = new ArrayList<THit>();
        return empty;
    }

    /**
     * Checks if the blastProtein is a total alignment
     * @param prot
     * @return
     */
    private static boolean isATotalAlignment(BlastProtein prot, int sequenceLength){
        int matchStart = prot.getStartMatch();
        int matchEnd = prot.getEndMatch();
        int queryStart = prot.getStartQuery();
        int queryEnd = prot.getEndQuery();

        if (matchStart == queryStart && matchStart == 1 && matchEnd == queryEnd && matchEnd == sequenceLength){
            return true;
        }
        return false;
    }

    /**
     * Create a BlastProtein instance for each hit in the results and add them to the list of BlastProtein we want to keep
     */
    public void readResultsWithoutFiltering() {

        List<THit> xmlHits = collectResults();

        if (xmlHits != null){
            for ( THit hit : xmlHits ) {
                processHitResult(hit, null);
            }
        }
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has an identity percent superior or equal to 'identity' and add them to the list of BlastProtein we want to keep
     * @param identity : the threshold identity
     */
    public void filterResultsWithIdentity(float identity){
        List<THit> xmlHits = collectResults();

        for ( THit hit : xmlHits ) {
            TAlignment alignment = hit.getAlignments().getAlignment().get(0);
            if (alignment.getIdentity() >= identity){
                processHitResult(hit, null);
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
     * Extract the uniprot entry
     * @param accession : the uniprot accession
     * @return the uniprotProtein with the matching organism
     */
    private UniprotProtein importProteinFromUniprot(String accession){
        UniprotProtein prot = null;

        try {
            if (accession != null){
                Collection<UniprotProtein> entries = uniprotService.retrieve(accession);

                if (!entries.isEmpty()){
                    UniprotProtein protein = entries.iterator().next();

                    if (entries.size() != 1){
                        log.error("The uniprot accession " + accession + " is matching several UniprotEntry instances. We will only take into account the first one : " + protein.getPrimaryAc());
                    }

                    if (protein != null){
                        if (protein.getOrganism() != null){
                            prot = protein;
                        }
                    }
                    else {
                        log.error("There isn't any Uniprot entries with this accession number : "+accession);
                    }
                }
                else {
                    throw new IllegalStateException("The uniprot protein : "+accession + " cannot be found in uniprot and one hit referred to this uniprot ac.");                    
                }
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Problem importing protein from Uniprot: "+accession, e);
        }

        return prot;
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has a specific organism and add them to the list of BlastProtein we want to keep
     * @param taxId : the scientific name of an organism
     */
    public void filterResultsWithOrganism(String taxId){
        List<THit> xmlHits = collectResults();

        for ( THit hit : xmlHits ) {
            UniprotProtein protFromUniprot = importProteinFromUniprot(hit.getAc());

            String organism = Integer.toString(protFromUniprot.getOrganism().getTaxid());

            if (organism != null){
                if (organism.equals(taxId)){
                    processHitResult(hit, protFromUniprot);
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

        for ( THit hit : xmlHits ) {
            TAlignment alignment = hit.getAlignments().getAlignment().get(0);

            if (alignment.getIdentity() >= identity){
                UniprotProtein protFromUniprot = importProteinFromUniprot(hit.getAc());

                String organism = Integer.toString(protFromUniprot.getOrganism().getTaxid());

                if (organism != null){
                    if (organism.equals(taxId)){
                        processHitResult(hit, protFromUniprot);
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
    public List<BlastProtein> filterMappingEntriesWithIdentity(float identity){
        List<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();
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
    public List<BlastProtein> filterMappingEntriesWithOrganism(String taxId){
        List<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries  ) {
            String organism = null;
            if (protein.getUniprotProtein() == null){
                protein.setUniprotProtein(importProteinFromUniprot(protein.getAccession()));
            }

            if (protein.getUniprotProtein() != null){
                organism = Integer.toString(protein.getUniprotProtein().getOrganism().getTaxid());
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
    public List<BlastProtein> filterMappingEntriesWithIdentityAndOrganism(float identity, String taxId){
        List<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries ) {
            String organism = null;

            if (protein.getUniprotProtein() == null){
                protein.setUniprotProtein(importProteinFromUniprot(protein.getAccession()));
            }

            if (protein.getUniprotProtein() != null){
                organism = Integer.toString(protein.getUniprotProtein().getOrganism().getTaxid());
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

    /**
     * Collect all the BlastProtein in the list which have a total alignment with the query sequence
     * @param proteinToFilter
     * @param sequenceLength
     * @return
     */
    public static List<BlastProtein> collectMappingEntriesWithTotalAlignment(List<BlastProtein> proteinToFilter, int sequenceLength){
        List<BlastProtein> proteins = new ArrayList<BlastProtein>();

        for (BlastProtein p : proteinToFilter){
            if (isATotalAlignment(p, sequenceLength)){
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

        if (results != null){
            try {
                appResult = bmr.read( results );
            } catch (BlastMappingException e) {
                throw new BlastResultFilterException(" Problem reading the InputStream containing the wswublast results",e);
            }
            this.results = appResult;
            clearMatchingEntries();
        }
    }
}
