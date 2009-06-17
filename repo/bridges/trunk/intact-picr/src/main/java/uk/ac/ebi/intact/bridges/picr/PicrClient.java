/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.bridges.picr;

import uk.ac.ebi.picr.accessionmappingservice.AccessionMapperInterface;
import uk.ac.ebi.picr.accessionmappingservice.AccessionMapperService;
import uk.ac.ebi.picr.model.UPEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.*;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
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
     * Finds the uniprot ID for a provided ID from the provided list of datbases
     * @param accession the accession to look for
     * @return the uniprot ID if found, null otherwise
     */
    public String getUPIForAccession(String accession, PicrSearchDatabase ... databases) {
        List<UPEntry> upEntries = getUPEntriesForAccession(accession, databases);

        if (upEntries.isEmpty()) {
            return null;
        }

        return upEntries.iterator().next().getUPI();
    }

    /**
     * Finds the uniprot ID for a provided ID from the provided list of datbases
     * @param accession the accession to look for
     * @return the uniprot ID if found, null otherwise
     */
    public List<UPEntry> getUPEntriesForAccession(String accession, PicrSearchDatabase ... databases) {
        return getAccessionMapperPort().getUPIForAccession(accession, null, databaseEnumToList(databases), null, true);
    }

    public List<UniParcEntry> getUniprotEntriesForAccession(String accession, PicrSearchDatabase ... databases) {
        String upAc = getUPIForAccession(accession, databases);

        UniProtRemoteServiceFactory factory = new UniProtRemoteServiceFactory();
        Query query = UniParcQueryBuilder.buildFullTextSearch( upAc );
        UniParcQueryService uniParcQueryService = factory.getUniParcQueryService();

        List<UniParcEntry> uniParcEntries = new ArrayList<UniParcEntry>();

        EntryIterator<UniParcEntry> protEntryIterator = uniParcQueryService.getEntryIterator(query);

        for (UniParcEntry uniParcEntry : protEntryIterator) {
            uniParcEntries.add(uniParcEntry);
        }

        return uniParcEntries;
    }

    public String getUPIForSequence(String sequence, Integer taxonId, PicrSearchDatabase ... databases) {
        UPEntry upEntry = getUPEntriesForSequence(sequence, taxonId, databases);
        return (upEntry != null)? upEntry.getUPI() : null; 
    }

    public UPEntry getUPEntriesForSequence(String sequence, Integer taxonId, PicrSearchDatabase ... databases) {
        if (databases == null) databases = PicrSearchDatabase.values();
        
        // sequence has to be in fasta format. If not, create a definition
        if (!sequence.startsWith(">")) {
            sequence = ">mySequence"+System.getProperty("line.separator")+sequence;
        }

        return getAccessionMapperPort().getUPIForSequence(sequence, databaseEnumToList(databases),
                                                          (taxonId != null)? String.valueOf(taxonId) : null, 
                                                          true);
    }

    private List<String> databaseEnumToList(PicrSearchDatabase ... databases) {
        List<String> databaseNames = new ArrayList<String>(databases.length);

        for (PicrSearchDatabase database : databases) {
            databaseNames.add(database.toString());
        }

        return databaseNames;
    }

}