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
package uk.ac.ebi.intact.app.search.convert;

import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;

import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchableQueryConverter implements Converter
{
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException
    {
        if (!SearchableQuery.isSearchableQuery(value))
        {
            throw new ConverterException("Value with wrong format");
        }

        try
        {
            return SearchableQuery.parseSearchableQuery(value);
        }
        catch (Exception e)
        {
            throw new ConverterException("Problem converting value to SearchableQuery: "+value);
        }
        
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException
    {
        if (value == null)
        {
            return "";
        }
        
        if (!(value instanceof SearchableQuery))
        {
            throw new ConverterException("Object is not an instance of SearchableQuery: '"+value+"', "+value.getClass());
        }
        return value.toString(); 
    }
}
