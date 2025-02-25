/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.it;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 *
 *
 */
@ContextConfiguration(locations = {
        "classpath*:/META-INF/intact.spring.xml",
        "classpath*:/META-INF/intact-batch.spring.xml",
        "classpath*:/META-INF/editor-test.spring.xml",
        "classpath*:/META-INF/editor.jpa-test.spring.xml"}, inheritLocations = false)
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIT extends IntactBasicTestCase {


}
