/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.test.mocks;

import junit.framework.AssertionFailedError;

import java.io.InputStream;
import java.io.IOException;

/**
 * Allow to test defined inputs.
 * <br>
 * Code found in the book <b>JUnit in action</b> by <i>Vincent Massol</i>.
 *
 * @author Vincent Massol, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class MockInputStream extends InputStream {

    private String buffer;
    private int    position   = 0;
    private int    closeCount = 0;

    public void setBuffer( String buffer ) {
         this.buffer = buffer;
    }

    public int read() throws IOException {
        if( position == this.buffer.length() ) {
            return -1;
        }

        return this.buffer.charAt( this.position++ );
    }

    public void close() throws IOException {
        closeCount++;
        super.close();
    }

    public void verify() throws AssertionFailedError {
        if( closeCount != 1 ) {
            throw new AssertionFailedError( "close() has been called "+ closeCount +" time"+
                    (closeCount>1?"s":"")+" but should have been called once and once only." );
        }
    }
}
