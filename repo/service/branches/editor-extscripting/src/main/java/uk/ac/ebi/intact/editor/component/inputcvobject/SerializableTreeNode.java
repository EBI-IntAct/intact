/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.editor.component.inputcvobject;

import org.primefaces.model.TreeNode;

import java.io.Serializable;

/**
* @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
public class SerializableTreeNode extends TreeNode implements Serializable {

    public SerializableTreeNode() {
        super(null, null);
    }


}
