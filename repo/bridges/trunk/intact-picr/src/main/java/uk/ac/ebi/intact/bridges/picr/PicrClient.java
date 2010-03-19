package uk.ac.ebi.intact.bridges.picr;

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

    public PicrClient(){
        this("http://www.ebi.ac.uk/Tools/picr/service?wsdl");
    }

    public PicrClient(String wsdlUrl){
        try {
            accessionMapperService = new AccessionMapperService(new URL(wsdlUrl), new QName("http://www.ebi.ac.uk/picr/AccessionMappingService", "AccessionMapperService"));
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
     */
    public ArrayList<String> getSwissprotIdsForAccession(String accession, String taxonId) {
        ArrayList<String> swissprotIdList = getIdsForAccession(accession, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT);

        return swissprotIdList;
    }

    /**
     * Finds the list of termblIds for a provided ID and taxonId
     * @param accession the accession to look for
     * @param taxonId : the organism of the protein
     * @return the tremblId if found, empty list otherwise
     */
    public ArrayList<String> getTremblIdsForAccession(String accession, String taxonId) {
        ArrayList<String> tremblIdList = getIdsForAccession(accession, taxonId, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        return tremblIdList;
    }

    /**
     * get the list of cross references accessions for a provided Id and taxonId from a list of databases
     * @param accession the accession to look for
     * @param taxonId : the organism of the protein
     * @param databases : the databases to query
     * @return the cross reference IDs if found, empty list otherwise
     */
    private ArrayList<String> getIdsForAccession(String accession, String taxonId, PicrSearchDatabase ... databases){
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
     */
    public List<UPEntry> getUPEntriesForAccession(String accession, String taxonId, PicrSearchDatabase ... databases) {
        return getAccessionMapperPort().getUPIForAccession(accession, null, databaseEnumToList(databases), taxonId, true);
    }

    /**
     * get the cross references ids for a provided sequence and taxonId from a list of databases
     * @param sequence the sequence to look for
     * @param taxonId : the organism of the protein
     * @param databases : the databases to query
     * @return the list of cross reference IDs if found, empty list otherwise
     */
    private ArrayList<String> getIdsForSequence(String sequence, String taxonId, PicrSearchDatabase ... databases){
        UPEntry upEntry = getUPEntriesForSequence(sequence, taxonId, databases);
        ArrayList<String> idList = new ArrayList<String>();

        List<CrossReference> listOfReferences = upEntry.getIdenticalCrossReferences();
        if (!listOfReferences.isEmpty()) {
            for (CrossReference c : listOfReferences) {
                String ac = c.getAccession();
                if (ac != null){
                    idList.add(ac);
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
     */
    public ArrayList<String> getSwissprotIdsForSequence(String sequence, String taxonId) {
        ArrayList<String> swissprotIdList = getIdsForSequence(sequence, taxonId, PicrSearchDatabase.SWISSPROT_VARSPLIC, PicrSearchDatabase.SWISSPROT);

        return swissprotIdList;
    }

    /**
     * Finds the list of termblIds for a provided sequence and taxonId
     * @param sequence the sequence to look for
     * @param taxonId : the organism of the protein
     * @return the tremblId if found, empty list otherwise
     */
    public ArrayList<String> getTremblIdsForSequence(String sequence, String taxonId) {
        ArrayList<String> tremblIdList = getIdsForSequence(sequence, taxonId, PicrSearchDatabase.TREMBL_VARSPLIC, PicrSearchDatabase.TREMBL);

        return tremblIdList;
    }

    /**
     * Get the UPEntry which matches the sequence and taxonId in the given databases
     * @param sequence : sequence of the protein to retrieve
     * @param taxonId : organism of the sequence
     * @param databases : the databases to look into
     * @return an UPEntry instance matching the sequence, taxonId in the specific databases
     */
    private UPEntry getUPEntriesForSequence(String sequence, String taxonId, PicrSearchDatabase ... databases) {
        if (databases == null) databases = PicrSearchDatabase.values();

        // sequence has to be in fasta format. If not, create a definition
        if (!sequence.startsWith(">")) {
            sequence = ">mySequence"+System.getProperty("line.separator")+sequence;
        }

        return getAccessionMapperPort().getUPIForSequence(sequence, databaseEnumToList(databases),
                taxonId,
                true);
    }

    /**
     * Get the UniprotEntry with its accession number
     * @param accession : the Uniprot identifier of the protein we want to retrieve
     * @return A list of UniprotEntry instances for this identifier
     */
    public List<UniProtEntry> getUniprotEntryForAccession(String accession) {

        Query query = UniProtQueryBuilder.buildFullTextSearch( accession );
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

}
