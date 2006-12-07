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
package uk.ac.ebi.intact.site.webapp;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchBean implements Serializable
{

    private String searchQuery;

    public SearchBean()
    {

    }

    public String doSearch()
    {
        FacesContext context = FacesContext.getCurrentInstance();

        // short-circuit the cycle to redirect to a external page
        try
        {
            context.responseComplete();
            context.getExternalContext().redirect("http://www.ebi.ac.uk/intact/search/do/search?searchString="+searchQuery);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }
}
