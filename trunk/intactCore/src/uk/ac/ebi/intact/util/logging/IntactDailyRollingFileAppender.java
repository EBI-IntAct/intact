package uk.ac.ebi.intact.util.logging;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Properties;

public class IntactDailyRollingFileAppender extends org.apache.log4j.DailyRollingFileAppender {

    // ----------------------------------------------------------- static content

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


    // ----------------------------------------------------------- instance variable
    private boolean fileNameCustomized = false;


    // ----------------------------------------------------------- public method

    /**
     * Apply a set of customisation on the filename. That modification is done only once.
     *
     * @param filename the filename to customise
     * @return the customised filename.
     */
    private String customizeFilename (String filename) {
        // we customize the filename only once
        if (fileNameCustomized == false) {
            fileNameCustomized = true;

            // look for the $hostname flag and replace it by the proper value if it exists
            int indexOfFlag = fileName.indexOf(HOSTNAME_FLAG);
            if (indexOfFlag > -1) {
                filename = filename.substring(0, indexOfFlag) +
                        hostname +
                        filename.substring(indexOfFlag + HOSTNAME_FLAG.length(), filename.length());
            }

            /* Add at the end the username.
             * In case Tomcat run an intact application, all log files are created.
             * if several instance of Tomcat runs on the same computer, you can get file
             * right access conflicts ...adding the username should prevent troubles.
             */
            filename += "_" + username;
        }

        return filename;
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
    public void setFile (String filename, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        filename = customizeFilename (filename);
        super.setFile (filename, append, bufferedIO, bufferSize);
    }
}