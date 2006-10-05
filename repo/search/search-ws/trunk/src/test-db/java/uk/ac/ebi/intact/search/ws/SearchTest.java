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
package uk.ac.ebi.intact.search.ws;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-Oct-2006</pre>
 */
public class SearchTest extends TestCase
{

    private static final Log log = LogFactory.getLog(SearchTest.class);


    public SearchTest()
    {
        super();
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testSearchDefault()
    {
        Search search = new Search();
        PartnerResult[] results = search.findPartnersUsingUniprotIds(new String[] {"P38398"});

        for (PartnerResult result : results)
        {
            System.out.println(result.getUniprotAc());

            for (String partner : result.getPartnerUniprotAcs())
            {
                System.out.println("\t"+partner);
            }
        }
    }
}
