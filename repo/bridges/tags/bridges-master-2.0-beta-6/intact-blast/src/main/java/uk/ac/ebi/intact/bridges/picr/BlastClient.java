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

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class BlastClient {
     /*
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


    public String getUPI(String accession, PicrSearchDatabase ... databases) {
        List<UPEntry> upEntries = getAccessionMapperPort().getUPIForAccession(accession, null, databaseEnumToList(databases), null, true);

        if (upEntries.isEmpty()) {
            return null;
        }

        return upEntries.iterator().next().getUPI();
    }
    
    private List<String> databaseEnumToList(PicrSearchDatabase ... databases) {
        List<String> databaseNames = new ArrayList<String>(databases.length);

        for (PicrSearchDatabase database : databases) {
            databaseNames.add(database.toString());
        }

        return databaseNames;
    }

    public static void main(String[] args) {
        PicrClient client = new PicrClient();

        System.out.println(client.getUPI("NP_417804", PicrSearchDatabase.REFSEQ));
    }
     */
}