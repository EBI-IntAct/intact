package uk.ac.ebi.intact.util.logging;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Properties;

public class IntactDailyRollingFileAppender extends org.apache.log4j.DailyRollingFileAppender {

    /**
     * The option the user can use to add the hostname in the file path
     */
    private static final String HOSTNAME_FLAG = "$hostname";

    private static String hostname;
    private static String username;

    static {

        // try to get the hostname
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname="noHostnameFound";
        }

        // get the username
        Properties props = System.getProperties();
        username = props.getProperty("user.name");
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
    public void setFile (String fileName,
                         boolean append,
                         boolean bufferedIO,
                         int bufferSize)
            throws IOException {

        // look for the $hostname flag and replace it by the proper value if it exists
        int indexOfFlag = fileName.indexOf(HOSTNAME_FLAG);
        if (indexOfFlag > -1) {
            fileName = fileName.substring(0, indexOfFlag) +
                       hostname +
                       fileName.substring(indexOfFlag + HOSTNAME_FLAG.length(), fileName.length());
        }

        // Add at the end the date of creation
        // in case Tomcat run an intact part, all log files are created.
        // if several instance of Tomcat runs ... you get file right access conflict ...
        // adding that it should prevent troubles.
        fileName += "_" + username;

        super.setFile (fileName,
                append,
                bufferedIO,
                bufferSize);
    } // setFile

} // IntactDailyRollingFileAppender