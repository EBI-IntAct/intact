package uk.ac.ebi.intact.bridges.picr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.picr.jaxb.GetUPIForAccessionReturn;
import uk.ac.ebi.intact.bridges.picr.jaxb.IdenticalCrossReferences;
import uk.ac.ebi.intact.bridges.picr.resultParsing.PicrParsingException;
import uk.ac.ebi.intact.bridges.picr.resultParsing.PicrRESTResultParser;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.*;
import uk.ac.ebi.picr.accessionmappingservice.AccessionMapperInterface;
import uk.ac.ebi.picr.accessionmappingservice.AccessionMapperService;
import uk.ac.ebi.picr.model.CrossReference;
import uk.ac.ebi.picr.model.UPEntry;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The PiCR client
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Mar-2010</pre>
 */

public class PicrClient {

    private AccessionMapperService accessionMapperService;
    private PicrRESTResultParser parser = new PicrRESTResultParser();
    private static final String wsdlFile = "http://www.ebi.ac.uk/Tools/picr/service?wsdl";

    private static final String restURLForUniprotBestGuess = "http://www.ebi.ac.uk/Tools/picr/rest/getUniProtBestGuess?";
    private static final String accessionMappingURL = "http://www.ebi.ac.uk/picr/AccessionMappingService";
    private static final String accessionMappingName = "AccessionMapperService";

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(PicrClient.class);

    public PicrClient(){
        this(wsdlFile);
    }

    public PicrClient(String wsdlUrl){
        try {
            accessionMapperService = new AccessionMapperService(new URL(wsdlUrl), new QName(accessionMappingURL, accessionMappingName));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public AccessionMapperInterface getAccessionMapperPort() {
        return accessionMapperService.getAccessionMapperPort();
    }

    /**
     * Finds the list of swissProtIds for a provided ID and taxonId
     * @param accession the accession to look for
     * @param taxonId : the organism of the protein
     * @return the swissprotIds if found, empty list otherwise
     * @throws PicrClientException : an exception if the given accession is null
     */
    public ArrayList<String> getSwissprotIdsForAccession(String accession, String taxonId) throws PicrClientException{
        ArrayList<String> swissprotIdList = getIdsForAccession(accession, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT);

        return swissprotIdList;
    }

    /**
     * Finds the list of termblIds for a provided ID and taxonId
     * @param accession the accession to look for
     * @param taxonId : the organism of the protein
     * @return the tremblId if found, empty list otherwise
     * @throws PicrClientException : an exception if the given accession is null
     */
    public ArrayList<String> getTremblIdsForAccession(String accession, String taxonId) throws PicrClientException{
        ArrayList<String> tremblIdList = getIdsForAccession(accession, taxonId, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        return tremblIdList;
    }

    /**
     * Gets the list of uniparcId matching this accession number
     * @param accession
     * @param taxonId
     * @return the list of uniparc Id or empty list if the accession doesn't match any Uniparc sequence
     * @throws PicrClientException : an exception if the given accession is null
     */
    public List<UPEntry> getUniparcEntries(String accession, String taxonId) throws PicrClientException{
        List<UPEntry> upEntries = getUPEntriesForAccession(accession, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        return upEntries;
    }

    /**
     * get the list of cross references accessions for a provided Id and taxonId from a list of databases
     * @param accession the accession to look for
     * @param taxonId : the organism of the protein
     * @param databases : the databases to query
     * @return the cross reference IDs if found, empty list otherwise
     * @throws PicrClientException : an exception if the given accession is null
     */
    private ArrayList<String> getIdsForAccession(String accession, String taxonId, PicrSearchDatabase ... databases) throws PicrClientException{
        List<UPEntry> upEntries = getUPEntriesForAccession(accession, taxonId, databases);
        ArrayList<String> idList = new ArrayList<String>();
        for (UPEntry entry : upEntries){
            List<CrossReference> listOfReferences = entry.getIdenticalCrossReferences();
            if (!listOfReferences.isEmpty()) {
                for (CrossReference c : listOfReferences) {
                    String ac = c.getAccession();
                    if (ac != null){
                        idList.add(ac);
                    }
                }
            }
        }
        return idList;
    }

    /**
     * Converts a list of PicrSearchDatabase into String
     * @param databases : the databases to query
     * @return the list of databases
     */
    private List<String> databaseEnumToList(PicrSearchDatabase ... databases) {
        List<String> databaseNames = new ArrayList<String>(databases.length);

        for (PicrSearchDatabase database : databases) {
            databaseNames.add(database.toString());
        }

        return databaseNames;
    }

    /**
     * Finds the list of UPEntries for a provided ID and organism from the provided list of databases
     * @param accession the accession to look for
     * @param taxonId the organism of the protein
     * @param databases the databases to query
     * @return the uniprot ID if found, null otherwise
     * @throws PicrClientException : an exception if the given accession is null
     */
    public List<UPEntry> getUPEntriesForAccession(String accession, String taxonId, PicrSearchDatabase ... databases) throws PicrClientException{
        if (accession == null){
            throw new PicrClientException("The identifier must not be null.");
        }
        if (databases == null) databases = PicrSearchDatabase.values();
        List<UPEntry> entries = Collections.EMPTY_LIST;
        try{
            entries = getAccessionMapperPort().getUPIForAccession(accession, null, databaseEnumToList(databases), taxonId, true);
        }
        catch (Exception e){
            log.error("PICR could not work properly", e);
        }
        return entries;
    }

    /**
     * get the cross references ids for a provided sequence and taxonId from a list of databases
     * @param sequence the sequence to look for
     * @param taxonId : the organism of the protein
     * @param databases : the databases to query
     * @return the list of cross reference IDs if found, empty list otherwise
     * @throws PicrClientException : an exception if the given sequence is null
     */
    private ArrayList<String> getIdsForSequence(String sequence, String taxonId, PicrSearchDatabase ... databases) throws PicrClientException{
        UPEntry upEntry = getUPEntriesForSequence(sequence, taxonId, databases);
        ArrayList<String> idList = new ArrayList<String>();

        if (upEntry != null){
            List<CrossReference> listOfReferences = upEntry.getIdenticalCrossReferences();
            if (!listOfReferences.isEmpty()) {
                for (CrossReference c : listOfReferences) {
                    String ac = c.getAccession();
                    if (ac != null){
                        idList.add(ac);
                    }
                }
            }
        }
        return idList;
    }

    /**
     * Finds the list of swissProtIds for a provided sequence and taxonId
     * @param sequence the sequence to look for
     * @param taxonId : the organism of the protein
     * @return the swissprotIds if found, empty list otherwise
     * @throws PicrClientException : an exception if the given sequence is null
     */
    public ArrayList<String> getSwissprotIdsForSequence(String sequence, String taxonId) throws PicrClientException{
        ArrayList<String> swissprotIdList = getIdsForSequence(sequence, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT);

        return swissprotIdList;
    }

    /**
     * Finds the list of termblIds for a provided sequence and taxonId
     * @param sequence the sequence to look for
     * @param taxonId : the organism of the protein
     * @return the tremblId if found, empty list otherwise
     * @throws PicrClientException : an exception if the given sequence is null
     */
    public ArrayList<String> getTremblIdsForSequence(String sequence, String taxonId) throws PicrClientException{
        ArrayList<String> tremblIdList = getIdsForSequence(sequence, taxonId, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        return tremblIdList;
    }

    /**
     * Gets the uniparcId matching this sequence
     * @param sequence
     * @param taxonId
     * @return the uniparc Id or null if the sequence doesn't match any Uniparc sequence
     */
    public String getUniparcIdFromSequence(String sequence, String taxonId) throws PicrClientException{
        UPEntry upEntry = getUPEntriesForSequence(sequence, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        if (upEntry == null){
            return null;
        }

        return upEntry.getUPI();
    }

    /**
     * Get the UPEntry which matches the sequence and taxonId in the given databases
     * @param sequence : sequence of the protein to retrieve
     * @param taxonId : organism of the sequence
     * @param databases : the databases to look into
     * @return an UPEntry instance matching the sequence, taxonId in the specific databases
     * @throws PicrClientException : an exception if the given sequence is null
     */
    public UPEntry getUPEntriesForSequence(String sequence, String taxonId, PicrSearchDatabase ... databases) throws PicrClientException{
        if (databases == null) databases = PicrSearchDatabase.values();

        if (sequence == null){
            throw  new PicrClientException("The sequence must not be null.");
        }

        // sequence has to be in fasta format. If not, create a definition
        if (!sequence.startsWith(">")) {
            sequence = ">mySequence"+System.getProperty("line.separator")+sequence;
        }

        UPEntry entry = null;

        try{
            entry = getAccessionMapperPort().getUPIForSequence(sequence, databaseEnumToList(databases),
                    taxonId,
                    true);
        }
        catch (Exception e){
            log.error("PICR could not work properly", e);
        }

        return entry;
    }

    /**
     * Get the UniprotEntry with its accession number
     * @param accession : the Uniprot identifier of the protein we want to retrieve
     * @return A list of UniprotEntry instances for this identifier
     */
    public List<UniProtEntry> getUniprotEntryForAccession(String accession) {

        Query query = UniProtQueryBuilder.buildExactMatchIdentifierQuery( accession );
        UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();

        List<UniProtEntry> uniProtEntries = new ArrayList<UniProtEntry>();

        EntryIterator<UniProtEntry> protEntryIterator = uniProtQueryService.getEntryIterator(query);

        for (UniProtEntry uniProtEntry : protEntryIterator) {
            uniProtEntries.add(uniProtEntry);
        }
        return uniProtEntries;
    }

    /**
     *  Get the UniparcEntry with an accession number
     * @param accession : the identifier of the protein we want to retrieve
     * @return a list of UniparcEntry instances for this accession
     */
    public List<UniParcEntry> getUniparcEntryForAccession(String accession) {

        Query query = UniParcQueryBuilder.buildFullTextSearch( accession );
        UniParcQueryService uniParcQueryService = UniProtJAPI.factory.getUniParcQueryService();

        List<UniParcEntry> uniParcEntries = new ArrayList<UniParcEntry>();

        EntryIterator<UniParcEntry> protEntryIterator = uniParcQueryService.getEntryIterator(query);

        for (UniParcEntry uniParcEntry : protEntryIterator) {
            uniParcEntries.add(uniParcEntry);
        }
        return uniParcEntries;
    }

    /**
     * Get an Unique uniprot Id for this accession
     * @param accession : the accession to look at
     * @param taxonId : the organism
     * @return an Unique uniprot Id
     * @throws PicrClientException
     */
    public String [] getUniprotBestGuessFor(String accession, String taxonId) throws PicrClientException{

        if (accession == null){
            throw new PicrClientException("The identifier must not be null.");
        }

        String query = restURLForUniprotBestGuess + "accession=" + accession;

        if (taxonId != null){
            query += "&taxid=" + taxonId;
        }
        URL url = null;
        try {
            url = new URL(query);

            uk.ac.ebi.intact.bridges.picr.jaxb.GetUPIForAccessionResponse upiResponse = this.parser.read(url);

            if (upiResponse == null){
                return null;
            }
            else {
                GetUPIForAccessionReturn upiResponeReturn = upiResponse.getGetUPIForAccessionReturn();
                if (upiResponeReturn == null){
                    return null;
                }
                else {
                    IdenticalCrossReferences crossRef = upiResponeReturn.getIdenticalCrossReferences();

                    if (crossRef == null){
                        return null;
                    }
                    else {
                        String [] result = new String [2];

                        result[0] = crossRef.getDatabaseName();
                        result[1] = crossRef.getAccession();
                        return result;
                    }
                }
            }
        } catch (MalformedURLException e) {
            throw new PicrClientException("The URL " + query + " is malformed.");
        } catch (PicrParsingException e) {
            throw new PicrClientException("Problems while trying to parse the Picr REST xml results at " + query);
        }
    }

}
