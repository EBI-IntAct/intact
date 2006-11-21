/*
 * Copyright 2006 The European Bioinformatics Institute.
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
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * Util methods for interactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class AnnotatedObjectUtils
{

    private static final Log log = LogFactory.getLog(AnnotatedObjectUtils.class);

    private AnnotatedObjectUtils(){}

    /**
     * Trims a shortlabel if it is too long to be inserted in the database
     * @return true if the label has been modified
     */
    public static String prepareShortLabel(String shortLabel)
    {
        boolean modified = false;

        if (shortLabel == null)
        {

            throw new NullPointerException("Must define a non null short label");

        }
        else
        {
            // delete leading and trailing spaces.
            shortLabel = shortLabel.trim();

            if ("".equals(shortLabel))
            {
                throw new IllegalArgumentException(
                        "Must define a non empty short label");
            }

            if (shortLabel.length() >= AnnotatedObject.MAX_SHORT_LABEL_LEN)
            {
                shortLabel = shortLabel.substring(0, AnnotatedObject.MAX_SHORT_LABEL_LEN);
                modified = true;
            }
        }

        return shortLabel;
    }
}
