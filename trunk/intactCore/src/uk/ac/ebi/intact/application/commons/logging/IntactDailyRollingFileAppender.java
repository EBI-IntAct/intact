package uk.ac.ebi.intact.application.commons.logging;

import java.util.Properties;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;

public class IntactDailyRollingFileAppender extends org.apache.log4j.DailyRollingFileAppender {

    /**
     * The option the user can use to add the hostname in the file path
     */
    private static final String HOSTNAME_FLAG = "$hostname";

    private static String hostname;

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname="noHostnameFound";
        }
    }

    /**
     * Over definition of that method inherited from FileAppender.
     * It allows to specify in the file path the hostname by using $hostname
     *
     * e.g. if myAppender.File=/tmp/$hostname_myfile.log and your host is mycomputer.ebi.ac.uk,
     * it will become /tmp/mycomputer.ebi.ac.uk_myfile.log,
     *
     * @throws IOException
     */
    public void setFile(String fileName,
                        boolean append,
                        boolean bufferedIO,
                        int bufferSize)
            throws IOException {

        int indexOfFlag = fileName.indexOf(HOSTNAME_FLAG);
        if (indexOfFlag >= -1) {
            fileName = fileName.substring(0, indexOfFlag) +
                       hostname +
                       fileName.substring(indexOfFlag + HOSTNAME_FLAG.length(), fileName.length());
        }

//       // add the hostname if doesn't exists
//        if (fileName.indexOf(hostname) == -1) {
//            Properties systemProperties = System.getProperties();
//            String fileSeparator = systemProperties.getProperty ("file.separator");
//            int index = fileName.lastIndexOf (fileSeparator);
//            fileName = fileName.substring(0, index) +
//                    fileSeparator + hostname + "_" +
//                    fileName.substring(index + 1, fileName.length());
//        }

        super.setFile (fileName,
                append,
                bufferedIO,
                bufferSize);

    }


    public static void main (String args[]) {

        System.out.println (IntactDailyRollingFileAppender.hostname);

        Properties p = System.getProperties();
        p.list( System.out);

    }

}