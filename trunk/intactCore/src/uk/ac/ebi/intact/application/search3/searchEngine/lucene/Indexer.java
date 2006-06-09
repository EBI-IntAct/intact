/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAO;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.lucene.model.SearchObject;
import uk.ac.ebi.intact.business.IntactException;

import uk.ac.ebi.intact.util.Chrono;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides methods to index the IntAct database. The indexing uses the RAM directory to store the index
 * first, which speeds the indexing up in comparison to the trivial index.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class Indexer {

    private SearchDAO dao;
    private SearchObjectIndexer soi;


    /**
     * Constructs an Indexer object.
     *
     * @param dao a SearchDAO object
     * @param soi a SearchObjectIndexer object
     */

    public Indexer( SearchDAO dao, SearchObjectIndexer soi ) {
        this.dao = dao;
        this.soi = soi;
    }

    /**
     * This method that creates a Lucene index out of the IntAct database. This methods fetchs first all objects, that
     * should be searchable, out of the database. Afterwards the index is created and while creating the index is first
     * stored into RAM and when the creation is finished the whole index is written into the given file
     *
     * @param dir file to write the index
     *
     * @throws IOException     ...
     * @throws IntactException ...
     */
    public void createIndex( File dir ) throws IOException, IntactException {
        // writer to write the index on scratch disk
        IndexWriter fsWriter = null;
        // writer to store the index in RAM
        IndexWriter ramWriter = null;
        // collection holding all IntAct objects, that should be searchable
        Collection documents = new ArrayList( 512 * 512 ); // this is over 250.000 but not reallocation in most cases.
        // directory to write the index to
        FSDirectory fsDir = null;
        fsDir = FSDirectory.getDirectory( dir, true );
        // RAM directory to hold the index temporarily
        RAMDirectory ramDir = new RAMDirectory();

        // instanciate the writer with the corresponding directory and with the IntActAnalyzer,
        // which analyzed the search objects and their features first
        fsWriter = new IndexWriter( fsDir, new IntactAnalyzer(), true );
        ramWriter = new IndexWriter( ramDir, new IntactAnalyzer(), true );

        // get all searchable IntAct objects
        documents = this.getAllDocuments();

        NumberFormat formatter = new DecimalFormat( ".00" );
        int countDocs = 0;
        // iterate through the collection of search objects and add one document per search object
        // into the index
        for ( Iterator iterator = documents.iterator(); iterator.hasNext(); ) {
            countDocs++;

            Document doc = (Document) iterator.next();
            ramWriter.addDocument( doc );

            if ( ( countDocs % 40 ) == 0 ) {
                System.out.print( "." );
                System.out.flush();

                if ( ( countDocs % 2400 ) == 0 ) {
                    // 60 dots per line
                    String percent = formatter.format( ( (float) countDocs / (float) documents.size() ) * 100 );
                    System.out.println( " " + countDocs + "(" + percent + "%)" );
                }
            }
        }

        // write the index from the RAM into the file on scratch
        fsWriter.addIndexes( new Directory[]{ ramDir } );

        fsWriter.close();
        ramWriter.close();
    }

    /**
     * This method retrieves all IntAct object that should be searchable, transform them into an Lucene Document and
     * returns a list of these documents. There is one document per search object.
     *
     * @return collection holding all documents to be inserted into the index
     *
     * @throws IntactException
     */
    public Collection getAllDocuments() throws IntactException {
        // collection of all searchable IntAct objects
        Collection searchObjects = null;
        // collections of all Lucene documents to be returned
        Collection documents = new ArrayList();

        // get all IntAct object out of the database
        try {
            searchObjects = dao.getAllSearchObjects();
        } catch ( IntactException e ) {
            throw new IntactException();
        }

        // iterate through the IntAct objects, to transform them into Lucene documents.
        for ( Iterator iterator = searchObjects.iterator(); iterator.hasNext(); ) {
            SearchObject obj = (SearchObject) iterator.next();

            // print out the heap size at this state
            long heapSize = Runtime.getRuntime().totalMemory();
//            System.out.println("heapSize in getAllDocuments: " + heapSize);
            // add the document to the collection of documents
            documents.add( soi.getDocument( obj ) );
        }
        return documents;
    }

    /**
     * The main method expects one argument, which defines the location where to store the index.
     *
     * @param args [0] the directory in which to put the lucene index.
     *
     * @throws IOException
     * @throws IntactException
     */
    public static void main( String[] args ) throws IOException, IntactException {
        try {
            String usage = "Usage: Indexer <Name of the index directory>";
            System.out.println( "Start to create the Lucene index..." );

            // first check the number of arguments
            if ( args.length != 1 ) {
                System.out.println( "Invalid numbers of argument!\n" + usage );
                System.exit( 1 );
            }
            //create a file with the name of the first argument
            File indexFile = new File( args[ 0 ] );

            Indexer test = new Indexer( new SearchDAOImpl(), new SearchObjectIndexer() );
            Chrono time = new Chrono();
            time.start();
            // create the index
            test.createIndex( indexFile );
            time.stop();
            // print the time the indexing used
            System.out.println( "\nIndex created in " + time.toString() );

        } catch ( OutOfMemoryError aome ) {

            aome.printStackTrace();

            System.err.println( "" );
            System.err.println( "Indexer ran out of memory." );
            System.err.println( "Please run it again and change the JVM configuration." );
            System.err.println( "Here are some the options: http://java.sun.com/docs/hotspot/VMOptions.html" );
            System.err.println( "Hint: You can use -Xms -Xmx to specify respectively the minimum and maximum" );
            System.err.println( "      amount of memory that the JVM is allowed to allocate." );
            System.err.println( "      eg. java -Xms128m -Xmx512m <className>" );

            System.exit( 1 );

        } catch ( IntactException e ) {
            e.printStackTrace();
            System.err.println( "" );
            System.err.println( "There went something wrong with fetching the search objects out of the database" );
        }
    }
}
