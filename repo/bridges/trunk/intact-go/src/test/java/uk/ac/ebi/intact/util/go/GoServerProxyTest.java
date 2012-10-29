/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.util.go;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class GoServerProxyTest {

    @Test
    @Ignore
    public void query_ok() throws Exception {
        GoServerProxy goServerProxy = new GoServerProxy();
        GoTerm term = goServerProxy.query("GO:0005634");

        Assert.assertEquals("nucleus", term.getName());
        Assert.assertEquals("A membrane-bounded organelle of eukaryotic cells in which chromosomes are housed and replicated. In most cells, the nucleus contains all of the cell&apos;s chromosomes except the organellar chromosomes, and is the site of RNA synthesis and processing. In some species, or in specialized cell types, RNA metabolism or DNA replication may be absent.", term.getDefinition());

        Assert.assertEquals("GO:0005575", term.getCategory().getId());
    }

    @Test (expected = GoServerProxy.GoIdNotFoundException.class)
    public void query_notFound() throws Exception {
        GoServerProxy goServerProxy = new GoServerProxy();
        GoTerm term = goServerProxy.query("PleaseDontGO");
    }

    @Test (expected = NullPointerException.class)
    public void query_isNull() throws Exception {
        GoServerProxy goServerProxy = new GoServerProxy();
        GoTerm term = goServerProxy.query(null);
    }
}
