package uk.ac.ebi.intact.persistence.util;

import org.hibernate.util.DTDEntityResolver;
import org.xml.sax.InputSource;
import org.apache.commons.logging.*;

import java.io.InputStream;

/**
 * Extends Hibernate's default resolver with lookup of entities on classpath.
 * <p>
 * For example, the following can be resolved:
 * <pre>
 * <?xml version="1.0"?>
 * <!DOCTYPE hibernate-mapping SYSTEM "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd"
 * [
 * <!ENTITY usertypes SYSTEM "/org/hibernate/ce/auction/model/UserTypes.hbm.xml">
 * ]>
 *
 * <hibernate-mapping>
 *
 * &usertypes;
 * ...
 * </pre>
 * The file will be looked up in the classpath. Don't forget the leading slash!
 * Relative location is not supported.
 *
 * @author christian.bauer@jboss.com
 */
public class ImportFromClasspathEntityResolver extends DTDEntityResolver {

    private static final Log log = LogFactory.getLog(ImportFromClasspathEntityResolver.class);

    // This is the prefix of SYSTEM entity identifiers which are not URLs
    private static final String PREFIX = "file://";

    public InputSource resolveEntity(String publicId, String systemId) {
        log.debug("Trying to resolve system id: " + systemId);
        if (systemId != null && systemId.startsWith(PREFIX) ) {
            // Remove the initial slash and look it up
            String resource = systemId.substring( PREFIX.length() );
            log.debug("Looking up entity on classpath: " + resource);

            InputStream stream = getClass().getResourceAsStream( resource );
            if ( stream == null ) {
                stream = getClass().getClassLoader()
                        .getResourceAsStream( resource );
            }
            if ( stream == null ) {
                stream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream( resource );
            }
            if ( stream == null ) {
                log.error("Couldn't find entity in classpath: " + resource);
            } else {
                log.debug("Found entity on classpath: " + resource);
                InputSource source = new InputSource(stream);
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                return source;
            }
        }
        return super.resolveEntity(publicId, systemId);
    }

}

