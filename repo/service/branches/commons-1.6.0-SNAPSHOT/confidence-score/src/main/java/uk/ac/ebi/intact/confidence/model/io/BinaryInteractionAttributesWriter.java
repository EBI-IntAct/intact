/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.confidence.model.io;

import uk.ac.ebi.intact.confidence.model.BinaryInteractionAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Writer strategy for BinaryInteractionAttributes objects.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.0
 *        <pre>
 *        10-Dec-2007
 *        </pre>
 */
public interface BinaryInteractionAttributesWriter {

    void append ( BinaryInteractionAttributes binaryInteractionAttributes, File outFile ) throws IOException;

    void append(List<BinaryInteractionAttributes> binrayInteractions, File outFile) throws IOException;

    void write ( List<BinaryInteractionAttributes> binaryInteractionsWithAttributes, File outFile ) throws IOException;
}
