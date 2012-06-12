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
package uk.ac.ebi.intact.bridges.ontologies.iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.exception.ExceptionUtils;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Ontology iterator that gets the documents from lines;
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class LineOntologyIterator implements OntologyIterator{

    private LineIterator lineIterator;
    private String currentLine;
    private Reader reader;

    public LineOntologyIterator(URL url) throws IOException {
        this(url.openStream());
    }

    public LineOntologyIterator(InputStream is) {
        this(new InputStreamReader(is));
    }

    public LineOntologyIterator(Reader reader) {
        this.reader = reader;
        lineIterator = IOUtils.lineIterator(reader);
    }

    public boolean hasNext() {
        while (lineIterator.hasNext()) {
            currentLine = (String) lineIterator.next();

            if (!skipLine(currentLine)) {
                return true;
            }
        }

        if (reader != null){
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Impossible to close the reader " + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return false;
    }

    public OntologyDocument next() {
        try {
            return processLine(currentLine);
        } catch (Throwable e) {
            throw new RuntimeException("Problem processing line: "+ currentLine, e);
        }
    }

    public void remove() {
       throw new UnsupportedOperationException("Cannot be removed");
    }

    protected abstract OntologyDocument processLine(String line);

    public boolean skipLine(String line) {
        line = line.trim();

        if (line.length() == 0) {
            return true;
        }

        return false;
    }
}
