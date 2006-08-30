/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.util.ElapsedTime;
import uk.ac.ebi.intact.util.uniprotExport.event.StatisticsCcLineEventListener;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Aug-2006</pre>
 */
public class CcLineExportProgressThread extends Thread
{

    private static final Log log = LogFactory.getLog(CcLineExportProgressThread.class);

    private final StatisticsCcLineEventListener listener;

    public CcLineExportProgressThread(CCLineExport ccLineExport, int totalDrLinesCount)
    {
        this.listener = new StatisticsCcLineEventListener(totalDrLinesCount);
        ccLineExport.addCcLineExportListener(listener);
    }

    @Override
    public void run()
    {
        log.info("Starting ccLineExport progress listener thread");

        double seconds = 0;

        while (!listener.isFinished()) {

            try
            {
                int millisecondsLapse = 1000;
                Thread.sleep(millisecondsLapse);

                seconds = seconds + (millisecondsLapse/1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            int drProcessed = listener.getDrProcessedCount();

            double speed = (double)drProcessed / seconds;

            int drRemaining = listener.getDrLinesRemaining();
            int secsRemaining = Double.valueOf((double)drRemaining * speed).intValue();

            ElapsedTime elapsedTime = new ElapsedTime(secsRemaining);

            log.debug(listener.toString() );
            log.info("Speed (DR Line / sec): "+speed+" ;  ETA: "+elapsedTime.toString());

        }


    }
}
