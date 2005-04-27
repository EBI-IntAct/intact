/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.lucene;

import org.apache.lucene.analysis.WhitespaceTokenizer;

import java.io.Reader;

/**
 * The main part of this class I copied from the MedlineTokenizer.
 * This Tokenizer defines that characters where the Analyzer should stop.
 *
 * @author Anja Friedrichsen, Mark R. (markr@ebi.ac.uk)
 * @version $id$
 */
public class IntactTokenizer extends WhitespaceTokenizer {

    public IntactTokenizer(Reader in) {
        super(in);
    }

    /**
     * this method defines the characters where the tokenizer should stop.
     *
     * @param c 
     * @return
     */
    protected boolean isTokenChar(char c) {
        boolean x = super.isTokenChar(c);
        boolean y = !(
                (c == '?') ||
                (c == '!') ||
                (c == ';') ||
                (c == '.') ||
                (c == '\'') ||
                (c == '\\') ||
                (c == '/') ||
                (c == ',') ||
                (c == '"') ||
                (c == '~') ||
                (c == '{') ||
                (c == '}') ||
                (c == '>') ||
                (c == '<') ||
                (c == '+') ||
                (c == ':') ||
                (c == '%') ||
                (c == '&') ||
                (c == '+') ||
                (c == ')') ||
                (c == '(') ||
                (c == '[') ||
                (c == ']') ||
                (c == '#') ||
                (c == '|') ||
                (c == '^') ||
                (c == '@'));

                // % & * ( ) [ ] # | ^

        return x & y;

    }

}
