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
package uk.ac.ebi.intact.bridges.citexplore;

import uk.ac.ebi.cdb.webservice.*;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CitexploreClient {

    private WSCitationImplService service;

    public CitexploreClient(){
        service = new WSCitationImplService();
    }

    public CitexploreClient(String wsdlUrl){
        try {
            service = new WSCitationImplService(new URL(wsdlUrl), new QName("http://webservice.cdb.ebi.ac.uk/", "WSCitationImplPort"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public WSCitationImpl getPort() {
        return service.getWSCitationImplPort();
    }

    public Citation getCitationById(String id) {
        List<ResultBean> results = searchCitationsById(id).getResultBeanCollection();

        if (!results.isEmpty()) {
            return results.iterator().next().getCitation();
        }

        return null;
    }

    private ResultListBean searchCitationsById(String id) {
        try {
            return getPort().searchCitations("id:"+id, "all", 0, null);
        } catch (QueryException_Exception e) {
            throw new CitexploreClientException(e);
        }
    }

    public static void main(String[] args) throws Exception{
        CitexploreClient client = new CitexploreClient();

        System.out.println(client.getCitationById("1234567").getTitle());
    }

}