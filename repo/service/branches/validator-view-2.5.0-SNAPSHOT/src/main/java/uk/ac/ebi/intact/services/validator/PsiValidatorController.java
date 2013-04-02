/**
 * Copyright (c) 2006 The European Bioinformatics Institute, and others.
 * All rights reserved.
 */
package uk.ac.ebi.intact.services.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.PreRenderView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.DisclosureEvent;
import org.apache.myfaces.trinidad.model.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.faces.controller.BaseController;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContent;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This is the managed bean that contains the model of the information show to the user. From this bean,
 * all the information shown is handled. It creates the reports, etc.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
@Controller("psiValidatorController")
@Scope("conversation.access")
@ViewController( viewIds = {"/start.xhtml"})
public class PsiValidatorController extends BaseController {

    //static private List<String> PROGRESS_STEPS;
    private static final String ZIP_EXTENSION = ".zip";
    private static final String XML_EXTENSION = ".xml";
    private static final String TXT_EXTENSION = ".txt";
    private static final String CSV_EXTENSION = ".csv";
    private static final String TSV_EXTENSION = ".tsv";
    private static final String SAMPLE_URL="http://psimi.googlecode.com/svn/trunk/validator/psimi-schema-validator/xml_samples/10366597.xml";
    private static final String MIMIX="MIMIx";
    private static final String IMEX="IMEx";
    private static final String PSIMI="PSI-MI";


    /**
     * Logging is an essential part of an application
     */
    private static final Log log = LogFactory.getLog( PsiValidatorController.class );

    /**
     * If true, a local file is selected to be uploaded
     */
    private boolean uploadLocalFile=true;

    /**
     * The type of validation to be performed: syntax, cv, MIMIX, IMEx.
     */
    private ValidationScope validationScope = ValidationScope.MIMIX;

    /**
     * Data model to be validated. Default value is PSI-MI (Note: this should be reflected in the tabbedPanel)
     */
    private DataModel model = DataModel.PSI_MI;

    /**
     * The file to upload
     */
    private UploadedFile psiFile;

    /**
     * The URL to upload
     */
    private String psiUrl;

    /**
     * Data model of the validation progress.
     */
    //protected volatile DefaultBoundedRangeModel progressModel;

    /**
     * If we are viewing a report, this is the report viewed
     */
    private PsiReport currentPsiReport;

    /**
     * The list of items to select for rule customization grouped by scopes
     */
    private Map<ValidationScope, List<SelectItem>> itemRules;

    /**
     * The map of selected items
     */
    private Map<ValidationScope, List<Integer>> customizedRules;

    /**
     * The map of object rules
     */
    private Map<Integer, ObjectRule> mapOfRules;

    /**
     * Constructor
     */
    public PsiValidatorController() {
        //this.uploadLocalFile = true;

    }

    @PostConstruct
    public void init() {
        this.mapOfRules = new HashMap<Integer, ObjectRule>();

        ValidatorWebContext context = ValidatorWebContext.getInstance();

        ValidatorWebContent content = context.getValidatorWebContent();

        Map<ValidationScope, Set<ObjectRule>> rules = content.getPsiMiObjectRules();

        itemRules = new HashMap<ValidationScope, List<SelectItem>>();
        customizedRules = new HashMap<ValidationScope, List<Integer>>();

        initialiseCustomizedRules(rules);
    }

	private boolean isPartialRequest() {
		FacesContext context = FacesContext.getCurrentInstance();
		return RequestContext.getCurrentInstance().isPartialRequest(context);
	}

	@PreRenderView
	public void preRender() {

		log.debug("Pre Render phase, checking session expired");


		if (isPartialRequest()) {
			return;
		}

		FacesContext context = FacesContext.getCurrentInstance();

		String statusParam = context.getExternalContext().getRequestParameterMap().get("status");

		if (statusParam != null && "exp".equals(statusParam)) {
				FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, "Session Expired", "The session have been restored");
				context.addMessage(null, facesMessage);
		}
	}

	private void initialiseCustomizedRules(Map<ValidationScope, Set<ObjectRule>> rules){
        Integer index = 0;

        Set<ObjectRule> mimixRules = rules.get(ValidationScope.MIMIX);

        for (ObjectRule rule : mimixRules){
            ValidationScope scope;

            if (rule.getScope() == null){
                scope = null;
            }
            else if (rule.getScope().equalsIgnoreCase(MIMIX)){
                scope = ValidationScope.MIMIX;
            }
            else if (rule.getScope().equalsIgnoreCase(PSIMI)){
                scope = ValidationScope.PSI_MI;
            }
            else{
                scope = null;
            }

            if (scope != null && rule.getName() != null){
                this.mapOfRules.put(index, rule);

                if (itemRules.containsKey(scope)){
                    itemRules.get(scope).add(new SelectItem(index, rule.getName()));
                }
                else{
                    List<SelectItem> ruleItems = new ArrayList<SelectItem>();
                    ruleItems.add(new SelectItem(index, rule.getName()));

                    itemRules.put(scope, ruleItems);
                }

                if (customizedRules.containsKey(scope)){
                    customizedRules.get(scope).add(index);
                }
                else{
                    List<Integer> ruleIds = new ArrayList<Integer>();
                    ruleIds.add(index);

                    customizedRules.put(scope, ruleIds);
                }

                index++;
            }
        }

        Set<ObjectRule> imexRules = rules.get(ValidationScope.IMEX);

        for (ObjectRule rule : imexRules){
            ValidationScope scope;

            if (rule.getScope() == null){
                scope = null;
            }
            else if (rule.getScope().equalsIgnoreCase(IMEX)){
                scope = ValidationScope.IMEX;
            }
            else{
                scope = null;
            }

            if (scope != null && rule.getName() != null){
                this.mapOfRules.put(index, rule);

                if (itemRules.containsKey(scope)){
                    itemRules.get(scope).add(new SelectItem(index, rule.getName()));
                }
                else{
                    List<SelectItem> ruleItems = new ArrayList<SelectItem>();
                    ruleItems.add(new SelectItem(index, rule.getName()));

                    itemRules.put(scope, ruleItems);
                }

                index++;
            }
        }
    }

    public void validationModelChangedMI( DisclosureEvent event ) {
        if ( event.isExpanded() ) {
            model = DataModel.PSI_MI;
            validationScope = ValidationScope.MIMIX; // set to default
            if ( log.isDebugEnabled() ) log.debug( "Data model set to '" + model + "'" );
        }
    }

    public void validationModelChangedPAR( DisclosureEvent event ) {
        if ( event.isExpanded() ) {
            model = DataModel.PSI_PAR;
            validationScope = ValidationScope.CV_ONLY; // set to default
            if ( log.isDebugEnabled() ) log.debug( "Data model set to '" + model + "'" );
        }
    }

    public String loadExample(){

        this.uploadLocalFile = false;
        this.psiUrl = SAMPLE_URL;
        this.model = DataModel.PSI_MI;
        this.validationScope = ValidationScope.MIMIX;

        return "start";
    }

    /**
     * Validates the data entered by the user upon pressing the validate button.
     *
     */
    public String validate( ) {

        // initializeProgressModel();

        try {

            if (currentPsiReport != null){
                this.currentPsiReport = null;
            }
            validateInputStream();

        } catch ( Throwable t ) {
            final String msg = "Failed to upload from " + ( uploadLocalFile ? "local file" : "URL" );

            log.error( msg, t );

            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage( msg );
            context.addMessage( null, message );
        }

        return "start";
    }

    /*private void initializeProgressModel() {
        progressModel = new DefaultBoundedRangeModel(-1, 5);
        progressModel.setValue( 0 );
    }*/

    private void validateInputStream() throws IOException {

        // we execute the method of the builder that actually creates the report
        log.info( "About to start building the PSI report" );

        if ( uploadLocalFile ) {
            // we use a different upload method, depending on the user selection
            uploadFromLocalFile();
        } else {
            uploadFromUrl();
        }
    }

    /**
     * Reads the local file and validates it.
     *
     * @throws IOException if something has gone wrong with the file
     */
    private void uploadFromLocalFile() throws IOException {

        if ( psiFile == null ) {
            throw new IllegalStateException( "Failed to upload the file" );
        }

        if ( log.isInfoEnabled() ) {
            log.info( "Uploading local file: " + psiFile.getFilename() );
        }

        boolean successful;

        if (psiFile.getFilename().endsWith(ZIP_EXTENSION)){
            InputStream inputStream = psiFile.getInputStream();

            try{
                successful = unpackArchive(inputStream);
            }   finally {
                inputStream.close();
            }
        }
        else{
            // and now we can instantiate the builder to create the validation report,
            // using the name of the file and the stream
            //f = storeAsTemporaryFile( psiFile.getInputStream(), psiFile.getFilename() );

            // starts to create the validator report
            InputStream inputStream = psiFile.getInputStream();
            try{
                InputStream inputStream2 = psiFile.getInputStream();

                try{
                    setUpValidatorReport(psiFile.getFilename(), inputStream, inputStream2);

                    successful = true;
                }
                finally {
                    inputStream2.close();
                }
            }
             finally {
                inputStream.close();
            }
        }

        if (!successful){
            final String msg = "The given file ("+psiFile.getFilename()+") is not or does not contain any XML files to validate";
            log.error( msg);

            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, msg, null );
            context.addMessage( null, message );
        }
    }

    private void setUpValidatorReport(String fileName, InputStream streamToValidate, InputStream streamToView) throws IOException {

        final long start = System.currentTimeMillis();
        PsiReportBuilder builder = new PsiReportBuilder( fileName, model, validationScope );
        final long stop = System.currentTimeMillis();
        log.trace( "Time to load the validator: " + (stop - start) + "ms" );

        // we execute the method of the builder that actually creates the report
        log.info( "About to start building the PSI report" );
        try{
            if (!validationScope.equals(ValidationScope.CUSTOMIZED)){
                this.currentPsiReport = builder.createPsiReport(streamToValidate);
            }
            else{
                List<ObjectRule> customizedRules = new ArrayList<ObjectRule>();

                List<Integer> rules = this.customizedRules.get(ValidationScope.PSI_MI);

                if (rules != null && !rules.isEmpty()){
                    extractSelectedRules(customizedRules, rules);
                }

                rules = this.customizedRules.get(ValidationScope.MIMIX);

                if (rules != null && !rules.isEmpty()){
                    extractSelectedRules(customizedRules, rules);
                }

                rules = this.customizedRules.get(ValidationScope.IMEX);

                if (rules != null && !rules.isEmpty()){
                    extractSelectedRules(customizedRules, rules);
                }

                this.currentPsiReport = builder.createPsiReport(streamToValidate, customizedRules);
            }
        }
        finally {
            streamToValidate.close();
        }

        try{
            builder.createHtmlView(this.currentPsiReport, streamToView);
        }
        finally {
            streamToView.close();
        }
    }

    private void extractSelectedRules(List<ObjectRule> customizedRules, List<Integer> rules) {
        for (Integer index : rules){
            if (mapOfRules.containsKey(index)){
                customizedRules.add(mapOfRules.get(index));
            }
        }
    }

    /**
     * Reads the file from a URL, so it can read locally and remotely
     *
     * @throws IOException if something goes wrong with the file or the connection
     */
    private void uploadFromUrl() throws IOException {
        if ( psiUrl == null ) {
            throw new IllegalStateException( "Failed to read the URL" );
        }

        if ( log.isInfoEnabled() ) {
            log.info( "Uploading Url: " + psiUrl );
        }

        try {
            // we create the URL object with the string provided by the user in the form
            URL url = new URL( psiUrl );

            // we only want the name of the file, and not the whole URL.
            // Gets the last part of the URL
            String name = psiUrl.substring( psiUrl.lastIndexOf( File.separator ) + 1, psiUrl.length() );

            boolean successful;

            if (psiUrl.endsWith(ZIP_EXTENSION)){

                successful = unpackArchive(url.openStream());
            }
            else{
                // and now we can instantiate the builder to create the validation report,
                // using the name of the file and the stream

                // starts to create the validator report
                setUpValidatorReport(name, url.openStream(), url.openStream());

                successful = true;
            }

            if (!successful){
                final String msg = "The given URL ("+psiUrl+") does not point to any XML files to validate";
                log.error( msg);

                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, msg, null );
                context.addMessage( null, message );
            }
        }
        catch ( Throwable e ) {
            currentPsiReport = null;
            final String msg = "The given URL wasn't valid";
            log.error( msg, e );

            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_WARN, msg, null );
            context.addMessage( null, message );
        }
    }

    /**
     * Unpack a zip file
     *
     * @param in
     * @return the file
     * @throws IOException
     */
    private boolean unpackArchive(InputStream in) throws IOException {
        final int BUFFER = 2048;

        File tempDirectory = new File( System.getProperty( "java.io.tmpdir", "tmp" ) );
        if ( !tempDirectory.exists() ) {
            if ( !tempDirectory.mkdirs() ) {
                throw new IOException( "Cannot create temp directory: " + tempDirectory.getAbsolutePath() );
            }
        }

        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry entry;
        BufferedOutputStream dest = null;

        InputStream streamToValidate = null;
        boolean successful = false;

        try{
            while ((entry = zis.getNextEntry()) != null)
            {
                log.info("Extracting: " +entry.getName());
                int count;
                byte data[] = new byte[BUFFER];

                if (!entry.isDirectory()){
                    if (entry.getName().endsWith(XML_EXTENSION) ||
                            entry.getName().endsWith(CSV_EXTENSION) ||
                            entry.getName().endsWith(TSV_EXTENSION) ||
                            entry.getName().endsWith(TXT_EXTENSION)){
                        String finalFileName = entry.getName().substring( entry.getName().lastIndexOf( File.separator ) + 1, entry.getName().length() );

                        String name = tempDirectory.getAbsolutePath() + File.separator + finalFileName;

                        // write the files to the disk
                        FileOutputStream fos = new
                                FileOutputStream(name);
                        dest = new
                                BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER))
                                != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();

                        File createdFile = new File(name);

                        streamToValidate = new FileInputStream(createdFile);

                        // starts to create the validator report
                        setUpValidatorReport(finalFileName, streamToValidate, new FileInputStream(createdFile));

                        createdFile.delete();

                        successful = true;

                        break;
                    }
                }
            }
        }
        finally {
            try{
                zis.close();
            }
            catch (IOException e){
                log.error("Impossible to close zipInputStream", e);
            }

            if (dest != null){
                try{
                    dest.close();
                }
                catch (IOException e){
                    log.error("Impossible to close destination file", e);
                }
            }

            if (streamToValidate != null){
                try{
                    streamToValidate.close();
                }
                catch (IOException e){
                    log.error("Impossible to close input stream to validate", e);
                }
            }
        }
        return successful;
    }

    // ACCESSOR METHODS

    public boolean isUploadLocalFile() {
        return uploadLocalFile;
    }

    public void setUploadLocalFile( boolean uploadLocalFile ) {
        this.uploadLocalFile = uploadLocalFile;
    }

    public UploadedFile getPsiFile() {
        return psiFile;
    }

    public void setPsiFile( UploadedFile psiFile ) {
        this.psiFile = psiFile;
    }

    public String getPsiUrl() {
        return psiUrl;
    }

    public void setPsiUrl( String psiUrl ) {
        this.psiUrl = psiUrl;
    }

    public PsiReport getCurrentPsiReport() {
        return currentPsiReport;
    }

    public void setCurrentPsiReport( PsiReport currentPsiReport ) {
        this.currentPsiReport = currentPsiReport;
    }

    public ValidationScope getValidationScope() {
        return validationScope;
    }

    public void setValidationScope(ValidationScope validationScope) {
        this.validationScope = validationScope;
    }

    public List<Integer> getPsiMiCustomizedRules() {
        List<Integer> psimiRules = this.customizedRules.get(ValidationScope.PSI_MI);
        if (psimiRules == null){
            return Collections.EMPTY_LIST;
        }

        return psimiRules;
    }

    public void setPsiMiCustomizedRules(List<Integer> customizedRules) {
        this.customizedRules.put(ValidationScope.PSI_MI, customizedRules);
    }

    public List<Integer> getMimixCustomizedRules() {
        List<Integer> mimixRules = this.customizedRules.get(ValidationScope.MIMIX);
        if (mimixRules == null){
            return Collections.EMPTY_LIST;
        }

        return mimixRules;
    }

    public void setMimixCustomizedRules(List<Integer> customizedRules) {
        this.customizedRules.put(ValidationScope.MIMIX, customizedRules);
    }

    public List<Integer> getImexCustomizedRules() {
        List<Integer> imexRules = this.customizedRules.get(ValidationScope.IMEX);
        if (imexRules == null){
            return Collections.EMPTY_LIST;
        }

        return imexRules;
    }

    public void setImexCustomizedRules(List<Integer> customizedRules) {
        this.customizedRules.put(ValidationScope.IMEX, customizedRules);
    }

    public List<SelectItem> getPsiMiItemRules() {
        List<SelectItem> psimiRules = this.itemRules.get(ValidationScope.PSI_MI);
        if (psimiRules == null){
            return Collections.EMPTY_LIST;
        }

        return psimiRules;
    }

    public List<SelectItem> getMimixItemRules() {
        List<SelectItem> mimixRules = this.itemRules.get(ValidationScope.MIMIX);
        if (mimixRules == null){
            return Collections.EMPTY_LIST;
        }

        return mimixRules;
    }

    public List<SelectItem> getImexItemRules() {
        List<SelectItem> imexRules = this.itemRules.get(ValidationScope.IMEX);
        if (imexRules == null){
            return Collections.EMPTY_LIST;
        }

        return imexRules;
    }

    public int getNumberOfImexRules(){
        List<SelectItem> imexRules = this.itemRules.get(ValidationScope.IMEX);
        if (imexRules == null){
            return 0;
        }

        return imexRules.size();
    }

    public int getNumberOfMimixRules(){
        List<SelectItem> mimixRules = this.itemRules.get(ValidationScope.MIMIX);
        if (mimixRules == null){
            return 0;
        }

        return mimixRules.size();
    }

    public int getNumberOfPsiMiRules(){
        List<SelectItem> psimiRules = this.itemRules.get(ValidationScope.PSI_MI);
        if (psimiRules == null){
            return 0;
        }

        return psimiRules.size();
    }
}
