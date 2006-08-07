/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Allow to iterate over the object stored in the database withough loading them all at once.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Jul-2006</pre>
 */
public class IntactObjectIterator<T extends IntactObject> implements Iterator {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactObjectIterator.class );

    /**
     * Maximum size of a chunk of data.
     */
    public static final int DEFAULT_CHUNK_SIZE = 1;

    public static final int NOT_INITIALISED = -1;

    ////////////////////////
    // Instance variables

    // Data access.
    private IntactObjectDao<T> dao;

    // chunk of data.
    private List<T> chunk;

    // iterator on the current chunk.
    private Iterator<T> chunkIterator;

    // current count of object read
    private int index = 0;

    // count of object to be read.
    private int objectCount = NOT_INITIALISED;

    //
    private int batchSize = DEFAULT_CHUNK_SIZE;

    private Class intactObjectClass;

    //////////////////////////
    // Constructor

    private IntactObjectDao<T> buildDao() {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getIntactObjectDao( intactObjectClass );
    }

    public IntactObjectIterator( Class<T> intactObjectClass ) {

        if ( intactObjectClass == null ) {
            throw new IllegalArgumentException( "You must give a non Class that extends IntactObject." );
        }

        this.intactObjectClass = intactObjectClass;

        this.dao = buildDao();

        // initialisation
        objectCount = dao.countAll();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

                dao = null;

        log.debug( objectCount + " object to be read from the iterator." );
    }

    public IntactObjectIterator( Class intactObjectClass, int batchSize ) {

        this( intactObjectClass );

        if ( batchSize < 1 ) {
            throw new IllegalArgumentException( "Batch size must be greater or equal to 1." );
        }
        this.batchSize = batchSize;
    }

    ////////////////////////////
    // implements Iterator

    public boolean hasNext() {
        if ( objectCount == NOT_INITIALISED ) {
            throw new IllegalStateException( "" );
        }

        return index < objectCount;
    }

    public T next() {

        T object = null;

        if ( ! hasNext() ) {
            throw new NoSuchElementException();
        }

        if ( chunk == null || chunkIterator == null ) {

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();

            log.debug( "Retreiving " + batchSize + " objects." );
            chunk = dao.getAll( index, batchSize );
            log.debug( "Retreived " + chunk.size() + " object(s)." );

            chunkIterator = chunk.iterator();
        }

        if ( chunkIterator != null ) {

            object = chunkIterator.next();
            index++;

            if ( ! chunkIterator.hasNext() ) {
                dao = null;
                chunkIterator = null;
                chunk = null;
                log.info( "Commiting transaction" );
            }
        }

        return object;
    }

    public void remove() {
    }
}