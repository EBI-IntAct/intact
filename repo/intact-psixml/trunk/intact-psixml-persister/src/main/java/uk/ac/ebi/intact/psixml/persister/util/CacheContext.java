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
package uk.ac.ebi.intact.psixml.persister.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utils to access the cache
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CacheContext implements Serializable {

    private static final String EHCACHE_CONFIG_FILE = "/ehcache-config.xml";

    private static final String ATT_NAME = CacheContext.class.getName();

    private CacheManager cacheManager;

    private Map<Class, Cache> cacheMap;

    private CacheContext() {
        URL url = CacheContext.class.getResource(EHCACHE_CONFIG_FILE);

        if (url == null) {
            throw new RuntimeException("ehcache.xml file not found: " + EHCACHE_CONFIG_FILE);
        }

        this.cacheManager = CacheManager.create(url);

        this.cacheMap = new HashMap<Class, Cache>();
    }

    public static CacheContext getInstance(IntactContext context) {
        CacheContext cacheContext = (CacheContext) context.getSession().getAttribute(ATT_NAME);

        if (cacheContext == null) {
            cacheContext = new CacheContext();
            context.getSession().setAttribute(ATT_NAME, cacheContext);
        }

        return cacheContext;
    }

    public Cache cacheFor(Class<?> aoClass) {
        if (cacheMap.containsKey(aoClass)) {
            return cacheMap.get(aoClass);
        }

        Cache cache = cacheManager.getCache(aoClass.getName());

        if (cache == null) {
            throw new RuntimeException("Cache not found: " + aoClass.getName());
        }

        cacheMap.put(aoClass, cache);

        return cache;
    }

}