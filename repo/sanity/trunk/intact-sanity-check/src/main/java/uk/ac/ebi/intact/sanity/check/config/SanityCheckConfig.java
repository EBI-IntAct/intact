package uk.ac.ebi.intact.sanity.check.config;

import uk.ac.ebi.intact.util.MailSender;

import java.util.*;

/**
 * Contains the configuration for the Sanity checks
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckConfig {
    /**
     * Prefix of the curator key from the properties file.
     */
    private static final String CURATOR = "curator.";

    /**
     * Prefix of the admin key from the properties file.
     */
    private static final String ADMIN = "admin.";

    private static final String NUMBER_CURATOR_PROPERTY = "super.curator.number";
    private static final String NAME_PROPERTY_DESCRIPTION = "super.curator.name";
    private static final String PERCENTAGE_PROPERTY_DESCRIPTION = "super.curator.percentage";

    private Collection<? extends Curator> allCurators;
    private List<Curator> curators;
    private List<SuperCurator> superCurators;
    private String editorUrl;

    private Properties mailerProperties;
    
    private boolean enableUserMails;
    private boolean enableAdminMails;

    private String emailSubjectPrefix;

    public SanityCheckConfig(Collection<? extends Curator> allCurators) {
        this.allCurators = allCurators;

        curators = new ArrayList<Curator>();
        superCurators = new ArrayList<SuperCurator>();

        for (Curator curator : allCurators) {
            if (curator instanceof SuperCurator) {
                superCurators.add((SuperCurator) curator);
            }
            else {
                curators.add(curator);
            }
        }

        this.mailerProperties = MailSender.EBI_SETTINGS;

        checkSuperCurators();
    }

    public static SanityCheckConfig loadFromProperties(Properties sanityCheckProperties, Properties correctionAssignerProps) throws Exception {
        List<Curator> allCurators = new ArrayList<Curator>();

        // load the correction assigner properties;
        if (correctionAssignerProps != null) {

            /*
            Get the number of super curators from the property file.
            */
            int superCuratorsNumber;
            String number = correctionAssignerProps.getProperty(NUMBER_CURATOR_PROPERTY);
            if (number != null) {
                superCuratorsNumber = Integer.parseInt(number);
            }
            else {
                throw new Exception("The number of curators hadn't been set properly in the properties");
            }

            /*
            Instanciate superCurators using as initial capacity the number of superCurators.
            */
            List<SuperCurator> superCurators = new ArrayList<SuperCurator>(superCuratorsNumber);

            /*
            Loading each curator from the config file
            */
            for (int i = 1; i <= superCuratorsNumber; i++) {

                /*
                Getting the name of curator i. If not found, throw an exception.
                */
                String name = correctionAssignerProps.getProperty(NAME_PROPERTY_DESCRIPTION + i);
                if (name == null) {
                    throw new Exception("Name property is not properly assign for super curator " + i);
                }

                /*
                Getting the percentage of pubmed the super curator will have to review out of the total amount of
                pubmed to correct. If not found, throw an exception.
                */
                int percentage;
                try {
                    percentage = Integer.parseInt(correctionAssignerProps.getProperty(PERCENTAGE_PROPERTY_DESCRIPTION + i));
                }
                catch (NumberFormatException nfe) {
                    throw new Exception("Name property is not properly assign for super curator " + i);
                }

                /*
                Creating the superCurator object.
                */
                SuperCurator superCurator = new SuperCurator(percentage, name);

                /*
                Adding the superCurator object to the superCurators collection.
                */
                superCurators.add(superCurator);


            }

            allCurators.addAll(superCurators);

        }

        // load the sanity check properties
        if (sanityCheckProperties != null) {
            int index;
            for (Iterator iterator = sanityCheckProperties.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();

                index = key.indexOf(CURATOR);
                if (index != -1) {
                    String userstamp = key.substring(index + CURATOR.length());
                    String curatorMail = (String) sanityCheckProperties.get(key);

                    Curator curator = getCuratorFromList(userstamp, allCurators);

                    if (curator == null) {
                        curator = new Curator(userstamp);
                    }

                    curator.setEmail(curatorMail);

                    allCurators.add(curator);

                }
                else {
                    // is it an admin then ?
                    index = key.indexOf(ADMIN);
                    if (index != -1) {
                        // store it
                        String userstamp = key.substring(index + ADMIN.length());
                        String adminMail = (String) sanityCheckProperties.get(key);

                        Curator curator = getCuratorFromList(userstamp, allCurators);

                        if (curator == null) {
                            curator = new Curator(userstamp);
                        }

                        curator.setAdmin(true);
                        curator.setEmail(adminMail);

                        allCurators.add(curator);
                    }
                }
            } // keys
        }

        SanityCheckConfig config = new SanityCheckConfig(allCurators);

        String editorBasicUrl = sanityCheckProperties.getProperty ("editor_basic_url");
        config.setEditorUrl(editorBasicUrl);

        return config;
    }

    private static Curator getCuratorFromList(String name, Collection<? extends Curator> curators) {
        for (Curator curator : curators) {
            if (curator.getName().equalsIgnoreCase(name)) {
                return curator;
            }
        }

        return null;
    }

    public void checkSuperCurators() {
        int percentageTotal = 0;

        for (SuperCurator curator : getSuperCurators()) {
            percentageTotal += curator.getPercentage();
        }

        if (percentageTotal != 100) {
            throw new SanityConfigurationException("Total percentage is different to 100%: " + percentageTotal + " (" + superCurators + ")");
        }
    }

    public Curator getCurator(String name) {
        Curator curator = getCuratorFromList(name, allCurators);

        if (curator != null) {
            return curator;
        }

        return null;
    }

    public SuperCurator getSuperCurator(String name) {
        for (SuperCurator superCurator : superCurators) {
            if (superCurator.getName().equalsIgnoreCase(name)) {
                return superCurator;
            }
        }

        throw new SanityConfigurationException("No super-curator found with name: " + name);
    }

    public Collection<? extends Curator> getAllCurators() {
        return allCurators;
    }

    public String getEditorUrl() {
        return editorUrl;
    }

    public void setEditorUrl(String editorUrl) {
        this.editorUrl = editorUrl;
    }

    public List<SuperCurator> getSuperCurators() {
        return superCurators;
    }

    public List<Curator> getCurators() {
        return curators;
    }

    public boolean isEnableAdminMails() {
        return enableAdminMails;
    }

    public void setEnableAdminMails(boolean enableAdminMails) {
        this.enableAdminMails = enableAdminMails;
    }

    public boolean isEnableUserMails() {
        return enableUserMails;
    }

    public void setEnableUserMails(boolean enableUserMails) {
        this.enableUserMails = enableUserMails;
    }

    public String getEmailSubjectPrefix() {
        return emailSubjectPrefix;
    }

    public void setEmailSubjectPrefix(String emailSubjectPrefix) {
        this.emailSubjectPrefix = emailSubjectPrefix;
    }

    public Properties getMailerProperties() {
        return mailerProperties;
    }

    public void setMailerProperties(Properties mailerProperties) {
        this.mailerProperties = mailerProperties;
    }
}
