package uk.ac.ebi.intact.google.spreadsheet;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Utility method on spreadsheets.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 0.1
 */
public class SpreadsheetFacade {

    private static final Log log = LogFactory.getLog( SpreadsheetFacade.class );

    /**
     * Returns the SpreadsheetEntry for the spreadsheet with the given key.
     *
     * @throws java.io.IOException If a network error occurs while trying to communicate with Spreadsheets
     * @throws com.google.gdata.util.ServiceException
     *                             If an application-level protocol error occurs while trying to communicate with Spreadsheets
     */
    public static SpreadsheetEntry getSpreadsheetWithKey( SpreadsheetService service, String key ) throws IOException, ServiceException {
        URL metafeedUrl = new URL( "http://spreadsheets.google.com/feeds/spreadsheets/private/full" );
        SpreadsheetFeed spreadsheetFeed = service.getFeed( metafeedUrl, SpreadsheetFeed.class );

        List<SpreadsheetEntry> spreadsheets = spreadsheetFeed.getEntries();
        for ( SpreadsheetEntry spreadsheet : spreadsheets ) {
            if ( spreadsheet.getKey().equals( key ) ) {
                if( log.isInfoEnabled() ) {
                    log.info( "Found spreadsheet '" + spreadsheet.getTitle() + "', Web URL: " + spreadsheet.getSpreadsheetLink().getHref() );
                }
                return spreadsheet;
            }
        }
        throw new IllegalStateException( "You don't have access to a spreadsheet with key " + key );
    }

    public static void deleteAllDataTables(SpreadsheetService service, SpreadsheetEntry spreadsheet) throws IOException, ServiceException {
        FeedURLFactory factory = FeedURLFactory.getDefault();
        URL tableFeedUrl = factory.getTableFeedUrl(spreadsheet.getKey());
        TableFeed feed = service.getFeed(tableFeedUrl, TableFeed.class);

        for (TableEntry entry : feed.getEntries()) {
            log.warn( "Deleting DataTable: " + entry.getTitle().getPlainText() );
            entry.delete();
        }
    }
}
