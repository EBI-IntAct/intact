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
package uk.ac.ebi.intact.sanity.commons.rules.report;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.sanity.commons.report.SanityReport;

import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityReportUtilsTest extends AbstractReportTestCase {

    @Test
    public void createPersonalizedReportsByUpdator() throws Exception {
        Map<String, SanityReport> personalizedReports = SanityReportUtils.createPersonalizedReportsByUpdator(getDefaultSanityReport());

        Assert.assertEquals(2, personalizedReports.size());
        Assert.assertEquals(1, personalizedReports.get("anne").getSanityResults().size());
        Assert.assertEquals(1, personalizedReports.get("peter").getSanityResults().size());

    }

}