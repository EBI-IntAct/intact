/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.task.mitab;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactPsimiTabWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MitabItemWriter implements BinaryInteractionItemWriter, ItemStream {

    private Writer outputWriter;
    private boolean header = true;
    private boolean firstAccess = true;

    public void write(List<? extends BinaryInteraction> items) throws Exception {
        PsimiTabWriter writer;

        if (items.isEmpty()) {
            return;
        }

        BinaryInteraction firstInteraction = items.iterator().next();

        if (firstInteraction instanceof IntactBinaryInteraction) {
            writer = new IntactPsimiTabWriter();
        } else {
            writer = new PsimiTabWriter();
        }

        writer.setHeaderEnabled(false);

        if (header && firstAccess) {
            writer.setHeaderEnabled(true);
            firstAccess = false;
        }

        writer.write(new ArrayList<BinaryInteraction>(items), outputWriter);
        outputWriter.flush();
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {

    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    public void close() throws ItemStreamException {
        try {
            outputWriter.close();
        } catch (IOException e) {
            throw new ItemStreamException("Problem closing writer", e);
        }
    }

    public void setOutputWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public void setOutputFile(File file) {
        try {
            file.getParentFile().mkdirs();
            setOutputWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem creating writer with file: "+file, e);
        }
    }
}
