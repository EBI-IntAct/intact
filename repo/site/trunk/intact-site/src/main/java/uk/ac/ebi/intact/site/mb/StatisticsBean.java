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
package uk.ac.ebi.intact.site.mb;

import uk.ac.ebi.intact.search.wsclient.SearchServiceClient;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class StatisticsBean implements Serializable
{
    private static final String SEARCH_WS_URL = "uk.ac.ebi.intact.SEARCH_WS_URL";

    private boolean loaded;
    private boolean failed;

    private int experimentCount;
    private int interactionCount;
    private int proteinCount;
    private int cvObjectCount;

    public StatisticsBean()
    {
    }

    public synchronized void prepare(ActionEvent evt)
    {
        String wsdl = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(SEARCH_WS_URL);

        if (!loaded && !failed)
        {
            try
            {
                SearchServiceClient client = new SearchServiceClient(wsdl);
                experimentCount = client.getSearchPort().countExperimentsUsingIntactQuery("*");
                interactionCount = client.getSearchPort().countAllBinaryInteractions();
                proteinCount = client.getSearchPort().countProteinsUsingIntactQuery("*");
                cvObjectCount = client.getSearchPort().countCvObjectsUsingIntactQuery("*");

                loaded = true;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                failed = true;
            }
        }
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }

    public boolean isFailed()
    {
        return failed;
    }

    public void setFailed(boolean failed)
    {
        this.failed = failed;
    }

    public int getExperimentCount()
    {
        return experimentCount;
    }

    public void setExperimentCount(int experimentCount)
    {
        this.experimentCount = experimentCount;
    }

    public int getInteractionCount()
    {
        return interactionCount;
    }

    public void setInteractionCount(int interactionCount)
    {
        this.interactionCount = interactionCount;
    }

    public int getProteinCount()
    {
        return proteinCount;
    }

    public void setProteinCount(int proteinCount)
    {
        this.proteinCount = proteinCount;
    }

    public int getCvObjectCount()
    {
        return cvObjectCount;
    }

    public void setCvObjectCount(int cvObjectCount)
    {
        this.cvObjectCount = cvObjectCount;
    }
}
