/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class MemoryMonitor {

    private Runtime r = Runtime.getRuntime();

    public MemoryMonitor() {
        hook();
    }

    public void hook(){
        r.addShutdownHook(
            new Thread(){
                public void run(){buildMemoryReport();
                }
            }
        );
    }

    public void buildMemoryReport(){

        float freeMemory = (float) r.freeMemory()/1024;
        Float freeMemoryF = new Float(freeMemory);

        float totalMemory = (float) r.totalMemory()/1024;
        Float totalMemoryF = new Float(totalMemory);

        float maxMemory = (float) r.maxMemory()/1024;
        Float maxMemoryF= new Float(maxMemory);


        int usedStr = ((int) (totalMemoryF.intValue() - freeMemoryF.intValue()));

        int maxFreeMemory = (int) ((maxMemory - usedStr));

        int memUsage = (int) ((freeMemory / totalMemory) * 100);
        float alarm = ((maxMemory - usedStr)/maxMemory) * 100;

        String report = "Free Memory : " + freeMemoryF.intValue() + " K \n" +
                        "TotalMemory : " + totalMemoryF.intValue() + " K \n" +
                        "MaxUsage : " + maxMemoryF.intValue() + " K \n" +
                        "Memory used : " + usedStr + " K \n" +
                        "MemUsage : " + memUsage + "%\n" +
                        "MaxFreeMemory : " + maxFreeMemory + "K\n" +
                        "Percentage of not used memory over the maxMemorySize " + alarm + "%\n";

        Float al = new Float(alarm);

        if ( al.intValue() < 10 ){
            report = report +
                     "WARNING : This process used more then 90% of the total memory. Don't forget to increase the memory " +
                     "allocated to the jvm";
        }

        System.out.println( report + "\n\n");
    }

}
