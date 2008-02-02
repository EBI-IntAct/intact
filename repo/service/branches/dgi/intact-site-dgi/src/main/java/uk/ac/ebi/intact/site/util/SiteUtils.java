/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.site.util;

import uk.ac.ebi.faces.DataLoadingException;
import uk.ac.ebi.intact.site.items.Datasets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SiteUtils
{
    
    public static final String XML_MIME_TYPE = "application/xml; charset=UTF-8";

    private SiteUtils(){}

    public static List<Datasets.Dataset> readDatasets(String datasetsXml) throws DataLoadingException
    {
        List<Datasets.Dataset> dataSets;

        Datasets datasets = null;
        try {
            URL datasetsUrl = new URL(datasetsXml);
            datasets = (Datasets) readDatasetsXml(datasetsUrl.openStream());
        } catch (Throwable e) {
            throw new DataLoadingException(e);
        }

        if (datasets != null)
        {
            dataSets = datasets.getDataset();
        }
        else
        {
            dataSets = new ArrayList<Datasets.Dataset>();
        }

        return dataSets;
    }

    private static Object readDatasetsXml(InputStream is) throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance(Datasets.class.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return  unmarshaller.unmarshal(is);
    }

}
