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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores a cache of elements with id
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ConversionCache {

    private static final Log log = LogFactory.getLog(ConversionCache.class);

    private Map<Object, Object> idMap;

    public ConversionCache() {
        this.idMap = new HashMap<Object, Object>();
    }

    public static ThreadLocal<ConversionCache> instance = new ThreadLocal<ConversionCache>() {
        @Override
        protected ConversionCache initialValue() {
            return new ConversionCache();
        }
    };

    public static Object getElement(Object id) {
        if (id == null) return null;

        return instance.get().idMap.get(id);
    }

    public static void putElement(Object key, Object element) {
        if (key != null) {
            instance.get().idMap.put(key, element);
        }
    }

    public static void clear() {
        if (log.isInfoEnabled()) log.info("Clearing Conversion cache");
        
        instance.get().idMap.clear();
    }
}