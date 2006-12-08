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
package uk.ac.ebi.intact.taglib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSiteFunctions
{
    private IntactSiteFunctions()
    {
    }

    public static Date convertToDate(Object date, String sourceFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(sourceFormat);
        try
        {
            return formatter.parse(date.toString());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
