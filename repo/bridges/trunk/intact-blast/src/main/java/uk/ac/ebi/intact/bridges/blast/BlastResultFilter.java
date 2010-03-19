package uk.ac.ebi.intact.bridges.blast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.blast.model.BlastProtein;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingException;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.TAlignment;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.THit;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to read the blast results as xml file and filter with the identity percent and/or the organism.
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
     * The blast reader
     */
    private BlastMappingReader bmr;

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
            throw new BlastResultFilterException(" Problem reading the String containing the blast results",e);
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
            throw new BlastResultFilterException(" Problem reading the InputStream containing the blast results",e);
        }
        this.results = appResult;
    }

    /**
     *
     * @param results : the blast results we want to filter
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
    public ArrayList<BlastProtein> getMatchingEntries() {
        return matchingEntries;
    }

    /**
     * Create a BlastProtein instance from a THit object
     * @param hit : the hit in the blast results
     * @return a BlastProtein instance for this THit instance
     */
    private BlastProtein createBlastProteinFrom(THit hit){
        BlastProtein entry = new BlastProtein();
        entry.setAccession(hit.getAc());
        entry.setDescription(hit.getDescription());

        TAlignment alignment = hit.getAlignments().getAlignment().get(0); //always one alignment?
        entry.setIdentity(alignment.getIdentity());
        entry.setScore(alignment.getScore());
        entry.setSequence(alignment.getMatchSeq().getValue());
        entry.setStart(alignment.getMatchSeq().getStart());
        entry.setEnd(alignment.getMatchSeq().getEnd());

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
     * Create a BlastProtein instance for each hit in the results and add them to the list of BlastProtein we want to keep
     */
    public void readResultsWithoutFiltering() {

        List<THit> xmlHits = collectResults();

        if (xmlHits != null){
            for ( THit hit : xmlHits ) {
                BlastProtein blastEntry = createBlastProteinFrom(hit);
                this.matchingEntries.add(blastEntry);
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
                BlastProtein blastEntry = createBlastProteinFrom(hit);
                this.matchingEntries.add(blastEntry);
            }
        }
    }

    /**
     * Extract the organism name from a description of a hit in the results
     * @param description : the description of a hit as it appears in the blast output
     * @return the scientific name of the organism
     */
    private String extractOrganismNameFromDescription(String description){
        String organismName = null;

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
    }

    /**
     * Extract the organism name from an Uniprot entry
     * @param accession : the uniprot accession
     * @return the scientific name of the organism for this protein
     */
    private String importOrganismNameFromUniprot(String accession){
        String organismName = null;

        if (accession != null){
            Query query = UniProtQueryBuilder.buildFullTextSearch( accession );
            UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();

            EntryIterator<UniProtEntry> protEntryIterator = uniProtQueryService.getEntryIterator(query);

            UniProtEntry uniprotEntry = protEntryIterator.next();

            if (uniprotEntry != null){
                organismName = uniprotEntry.getOrganism().getScientificName().getValue();
            }
            else {
                log.error("There isn't any Uniprot entries with this accession number : "+accession);                
            }

        }
        else {
            log.error("To find the organism name of an uniprot entry, we need to have an Uniprot accession which is not null");
        }

        return organismName;
    }

    /**
     * Create a BlastProtein instance for each hit in the results which has a specific organism and add them to the list of BlastProtein we want to keep
     * @param organismName : the scientific name of an organism
     */
    public void filterResultsWithOrganism(String organismName){
        List<THit> xmlHits = collectResults();

        for ( THit hit : xmlHits ) {
            String organism = extractOrganismNameFromDescription(hit.getDescription());
            if (organism == null){
                organism = importOrganismNameFromUniprot(hit.getAc());
            }

            if (organism != null){
                if (organism.equals(organismName)){
                    BlastProtein blastEntry = createBlastProteinFrom(hit);
                    this.matchingEntries.add(blastEntry);
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
     * @param organismName : the scientific name of the organism
     */
    public void filterResultsWithIdentityAndOrganism(float identity, String organismName){
        List<THit> xmlHits = collectResults();

        for ( THit hit : xmlHits ) {
            String organism = extractOrganismNameFromDescription(hit.getDescription());

            if (organism == null){
                organism = importOrganismNameFromUniprot(hit.getAc());
            }

            TAlignment alignment = hit.getAlignments().getAlignment().get(0);

            if (alignment.getIdentity() >= identity){
                if (organism != null){
                    if (organism.equals(organismName)){
                        BlastProtein blastEntry = createBlastProteinFrom(hit);
                        this.matchingEntries.add(blastEntry);
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
     * @param organismName : the scientific name of an organism
     * @return a list of BlastProteins which have been filtered
     */
    public ArrayList<BlastProtein> filterMappingEntriesWithOrganism(String organismName){
        ArrayList<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries  ) {
            String organism = extractOrganismNameFromDescription(protein.getDescription());
            if (organism == null){
                organism = importOrganismNameFromUniprot(protein.getAccession());
            }
            if (organism != null){
                if (organism.equals(organismName)){
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
     * @param organismName : the scientific name of an organism
     * @return : a list of BlastProteins which have been filtered
     */
    public ArrayList<BlastProtein> filterMappingEntriesWithIdentityAndOrganism(float identity, String organismName){
        ArrayList<BlastProtein> filteredProtein = new ArrayList<BlastProtein>();

        for ( BlastProtein protein : this.matchingEntries ) {
            String organism = extractOrganismNameFromDescription(protein.getDescription());
            if (organism == null){
                organism = importOrganismNameFromUniprot(protein.getAccession());
            }

            if (protein.getIdentity() >= identity){
                if (organism != null){
                    if (organism.equals(organismName)){
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
            throw new BlastResultFilterException(" Problem reading the InputStream containing the blast results",e);
        }
        this.results = appResult;
    }
}
