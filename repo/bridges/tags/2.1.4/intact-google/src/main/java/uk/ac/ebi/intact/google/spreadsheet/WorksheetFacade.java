package uk.ac.ebi.intact.google.spreadsheet;

import com.google.common.collect.Maps;
import com.google.gdata.client.batch.BatchInterruptedException;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/****************************************************************************
 * HELPER CLASSES
 *
 * These classes are slightly smarter versions of more standard classes,
 * equipped with little bits of extra functionality that are useful for our
 * purposes.
 ****************************************************************************/

/**
 * Wrapper around Spreadsheets WorksheetFacade entries that adds some utility methods useful for our purposes.
 * <p/>
 * Class borrowed from: http://ga-api-java-samples.googlecode.com/svn/trunk/src/v1/SpreadsheetExporter.java
 */
public class WorksheetFacade {

    private static final Log log = LogFactory.getLog( WorksheetFacade.class );

    private static final int BATCH_CHUNK_SIZE = 1000;
    
    private final SpreadsheetService spreadsheetService;
    private SpreadsheetEntry spreadsheetEntry;
    private WorksheetEntry worksheetEntry;
    private String worksheetName;
    private CellFeed cellFeed;
    private int rows;
    private int columns;
    private CellEntry[][] cellEntries;

    public WorksheetFacade( SpreadsheetEntry spreadsheetEntry,
                            WorksheetEntry backingEntry,
                            SpreadsheetService spreadsheetService ) throws IOException, ServiceException {
        this.spreadsheetEntry = spreadsheetEntry;
        this.worksheetEntry = backingEntry;
        this.worksheetName = backingEntry.getTitle().getPlainText();
        this.spreadsheetService = spreadsheetService;
        this.rows = backingEntry.getRowCount();
        this.columns = backingEntry.getColCount();
        refreshCachedData();
    }

    /**
     * Builder method that fetches a WorksheetEntry given a spreadsheet and a worksheet name.
     *
     * @param spreadsheetService
     * @param spreadsheetEntry
     * @param sheetName
     * @return return a worksheet wrapper if the sheet is found by name, null otherwise.
     * @throws IOException
     * @throws ServiceException
     */
    public static WorksheetFacade getWorksheetByName( SpreadsheetService spreadsheetService,
                                                      SpreadsheetEntry spreadsheetEntry,
                                                      String sheetName ) throws IOException, ServiceException {

        if ( spreadsheetService == null ) {
            throw new IllegalArgumentException( "You must give a non null spreadsheetService" );
        }
        if ( spreadsheetEntry == null ) {
            throw new IllegalArgumentException( "You must give a non null spreadsheetEntry" );
        }
        if ( sheetName == null ) {
            throw new IllegalArgumentException( "You must give a non null sheetName" );
        }

        final URL worksheetFeedUrl = spreadsheetEntry.getWorksheetFeedUrl();
        final WorksheetFeed wsf = spreadsheetService.getFeed( worksheetFeedUrl, WorksheetFeed.class );

        for ( WorksheetEntry worksheetEntry : wsf.getEntries() ) {
            final String name = worksheetEntry.getTitle().getPlainText();
            if ( name.equals( sheetName ) ) {
                return new WorksheetFacade( spreadsheetEntry, worksheetEntry, spreadsheetService );
            }
        }
        return null;
    }

    /**
     * Presents the given cell feed as a map from row, column pair to CellEntry.
     */
    private void refreshCachedData() throws IOException, ServiceException {

        CellQuery cellQuery = new CellQuery( worksheetEntry.getCellFeedUrl() );
        cellQuery.setReturnEmpty( true );
        this.cellFeed = spreadsheetService.getFeed( cellQuery, CellFeed.class );

        // A subtlety: Spreadsheets row,col numbers are 1-based whereas the
        // cellEntries array is 0-based. Rather than wasting an extra row and
        // column worth of cells in memory, we adjust accesses by subtracting
        // 1 from each row or column number.
        cellEntries = new CellEntry[rows][columns];
        for ( CellEntry cellEntry : cellFeed.getEntries() ) {
            Cell cell = cellEntry.getCell();
            cellEntries[cell.getRow() - 1][cell.getCol() - 1] = cellEntry;
        }
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    /**
     * Gets the cell entry corresponding to the given row and column.
     */
    public CellEntry getCell( int row, int column ) {
        return cellEntries[row - 1][column - 1];
    }

    /**
     * Returns this worksheet's column count.
     */
    public int getColCount() {
        return columns;
    }

    /**
     * Returns this worksheet's row count.
     */
    public int getRowCount() {
        return rows;
    }

    /**
     * Sets this worksheets's row count.
     */
    public void setRowCount( int newRowCount ) throws IOException, ServiceException {
        rows = newRowCount;
        worksheetEntry.setRowCount( newRowCount );
        worksheetEntry = worksheetEntry.update();
        refreshCachedData();
    }

    public int getColUsedInRow( int row ) {
        int lastNonEmptyCell = 1;
        for ( int i = 1; i < cellEntries[row].length; i++ ) {
            if ( ! isEmptyCell( row, i ) ) {
                lastNonEmptyCell = i;
            }
        }
        return lastNonEmptyCell;
    }

    public int getRowUsedInCol( int col ) {
        int lastNonEmptyCell = 1;
        for ( int i = 1; i < cellEntries.length; i++ ) {
            if ( ! isEmptyCell( i, col ) ) {
                lastNonEmptyCell = i;
            }
        }
        return lastNonEmptyCell;
    }

    public boolean isRowEmpty( int row ) {
        for ( int col = 1; col < cellEntries[row].length; col++ ) {
            if ( !isEmptyCell( row, col ) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isColumnEmpty( int column ) {
        for ( int row = 1; row < cellEntries.length; row++ ) {
            if ( !isEmptyCell( row, column ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the last empty row int he spreadsheet, if none found, returns 0;
     *
     * @return a value > 0 if an emtpy row is found, 0 otherwise.
     */
    public int getNextEmptyRow() {
        for ( int row = 1; row < cellEntries.length; row++ ) {
            if ( isRowEmpty( row ) ) {
                return row;
            }
        }
        return 0;
    }

    /**
     * Return the last empty row int he spreadsheet, if none found, returns 0;
     *
     * @return a value > 0 if an emtpy row is found, 0 otherwise.
     */
    public int getNextEmptyColumn() {
        for ( int column = 1; column < cellEntries.length; column++ ) {
            if ( isColumnEmpty( column ) ) {
                return column;
            }
        }
        return 0;
    }

    public boolean isEmptyCell( int row, int column ) {
        CellEntry cellEntry = getCell( row, column );
        final String content = cellEntry.getPlainTextContent();
        return ( content == null || content.length() == 0 );
    }

    public boolean hasDataTable() throws IOException, ServiceException {
        return getDataTable() != null;
    }

    public TableEntry getDataTable() throws IOException, ServiceException {
        FeedURLFactory factory = FeedURLFactory.getDefault();
        URL tableFeedUrl = factory.getTableFeedUrl( spreadsheetEntry.getKey() );
        final TableFeed tableFeed = spreadsheetService.getFeed( tableFeedUrl, TableFeed.class );
        for ( TableEntry tableEntry : tableFeed.getEntries() ) {
            if ( tableEntry.getTitle().getPlainText().equals( worksheetName ) ) {
                return tableEntry;
            }
        }
        return null;
    }

    public TableEntry createBasicDataTable() throws IOException, ServiceException {
        TableEntry tableEntry = new TableEntry();

        FeedURLFactory factory = FeedURLFactory.getDefault();
        URL tableFeedUrl = factory.getTableFeedUrl( spreadsheetEntry.getKey() );

        // Specify a basic table:
        tableEntry.setTitle( new PlainTextConstruct( worksheetName ) );
        tableEntry.setWorksheet( new Worksheet( worksheetName ) );
        tableEntry.setHeader( new Header( 1 ) );

        // Specify columns in the table, start row, number of rows.
        Data tableData = new Data();
        tableData.setNumberOfRows( getRowCount() );

        // Start row index cannot overlap with header row.
        tableData.setStartIndex( 2 );

        tableEntry.setData( tableData );
        spreadsheetService.insert( tableFeedUrl, tableEntry );

        return tableEntry;
    }

    /**
     * Add a new column to the right of existing ones.
     *
     * @param columnName name of the column to add.
     * @throws IOException
     * @throws ServiceException
     */
    public void addNewColumn( final String columnName ) throws IOException, ServiceException {
        TableEntry table = null;

        // get DataTable
        if ( !hasDataTable() ) {
            table = createBasicDataTable();
        } else {
            table = getDataTable();
        }

        if ( table == null ) {
            throw new IllegalArgumentException( "Could not find or create DataTable for sheet '" + worksheetName + "'" );
        }

        final int columnIdx = getNextEmptyColumn();
        final Data data = table.getData();
        final String columnRef = translateIndexToLetter( columnIdx );
        data.addColumn( new Column( columnRef, columnName ) );
    }


    /**
     * Translates a numerical index (1..n) into a char index (A..Z).
     *
     * @param idx numerical index (must be > 0).
     * @return a String representation of the char index.
     */
    public String translateIndexToLetter( int idx ) {
        if ( idx < 1 ) {
            throw new IllegalArgumentException( "Worksheet index has to be greater than 0: " + idx );
        }
        return String.valueOf( ( char ) ( 'A' + ( idx - 1 ) ) );
    }

    /**
     * Send the updates in chunks of 1000 to ensure we don't send too much data in one batch
     *
     * @param updatedCells
     * @throws IOException
     * @throws ServiceException
     */
    public void batchUpdate( List<CellEntry> updatedCells ) throws IOException, ServiceException {
        List<List<CellEntry>> batches = chunkList( updatedCells, BATCH_CHUNK_SIZE );
        for ( List<CellEntry> batch : batches ) {
            CellFeed batchFeed = new CellFeed();
            for ( CellEntry cellEntry : batch ) {
                Cell cell = cellEntry.getCell();
                BatchUtils.setBatchId( cellEntry, "R" + cell.getRow() + "C" + cell.getCol() );
                BatchUtils.setBatchOperationType( cellEntry, BatchOperationType.UPDATE );
                batchFeed.getEntries().add( cellEntry );
            }

            Link batchLink = getBatchUpdateLink();
            CellFeed batchResultFeed = spreadsheetService.batch( new URL( batchLink.getHref() ), batchFeed );
            // Make sure all the operations were successful.
            for ( CellEntry entry : batchResultFeed.getEntries() ) {
                if ( !BatchUtils.isSuccess( entry ) ) {
                    String batchId = BatchUtils.getBatchId( entry );
                    BatchStatus status = BatchUtils.getBatchStatus( entry );
                    log.error( "Failed entry" );
                    log.error( "\t" + batchId + " failed (" + status.getReason() + ") " );
                    return;
                }
            }
        }
    }

    /**
     * Chunks a list of items into sublists where each sublist contains at most the specified maximum number of items.
     *
     * @param ts        The list of elements to chunk
     * @param chunkSize The maximum number of elements per sublist
     * @return A list of sublists, where each sublist has chunkSize or fewer elements
     *         and all elements from ts are present, in order, in some sublist
     */
    private static <T> List<List<T>> chunkList( List<? extends T> ts, int chunkSize ) {
        Iterator<? extends T> iterator = ts.iterator();
        List<List<T>> returnList = new LinkedList<List<T>>();
        while ( iterator.hasNext() ) {
            List<T> sublist = new LinkedList<T>();
            for ( int i = 0; i < chunkSize && iterator.hasNext(); i++ ) {
                sublist.add( iterator.next() );
            }
            returnList.add( sublist );
        }
        return returnList;
    }

    /**
     * Gets a link to the batch update URL for this worksheet.
     */
    public Link getBatchUpdateLink() {
        return cellFeed.getLink( Link.Rel.FEED_BATCH, Link.Type.ATOM );
    }

    public Map<String, Integer> getColumnNameIndex() {
        Map<String, Integer> map = Maps.newHashMap();
        int maxCol = getColUsedInRow( 1 );
        for ( int i = 1; i <= maxCol; i++ ) {
            String name = getCell( 1, i ).getCell().getValue();
            map.put( name, i );
        }
        return map;
    }
}
