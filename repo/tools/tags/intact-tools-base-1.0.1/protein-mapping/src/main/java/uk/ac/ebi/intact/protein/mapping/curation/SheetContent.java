package uk.ac.ebi.intact.protein.mapping.curation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class SheetContent {

    private ArrayList<String[]> sheetContent = new ArrayList<String[]>();
    private HashMap<ColumnNames, Integer> columnNames = new HashMap<ColumnNames, Integer>();
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinIdentificationManager.class );

    private ColumnNames getColumnNameFor(String name){
        if (name == null){
            log.error("A name of a column should not be null.");
            return ColumnNames.unset;
        }
        else {
            if (name.toLowerCase().equals("id") || name.toLowerCase().equals("identifier")){
                return ColumnNames.id;
            }
            else if (name.toLowerCase().equals("gene name") || name.toLowerCase().equals("gene")){
                return ColumnNames.gene_name;
            }
            else if (name.toLowerCase().equals("protein name") || name.toLowerCase().equals("protein")){
                return ColumnNames.protein_name;
            }
            else if (name.toLowerCase().equals("organism")){
                return ColumnNames.organism;
            }
            else {
                return ColumnNames.other;
            }
        }
    }

    public void loadSheetContentOf(InputStream proteinsToIdentify){
        BufferedReader reader = new BufferedReader(new InputStreamReader(proteinsToIdentify));
        this.sheetContent.clear();
        this.columnNames.clear();

        try{
            log.debug("Load protein sheet: ");
            if (InputFileUtils.checkInputProteins(proteinsToIdentify)){
                try {
                    String line = reader.readLine();
                    if (line.contains("\t")){
                        ArrayList<String> columns = InputFileUtils.split(line, "\t");

                        for (int i = 0; i < columns.size(); i++){
                            ColumnNames columnName = getColumnNameFor(columns.get(i));

                            if (!this.columnNames.containsKey(columnName)){
                                columnNames.put(columnName, i);
                            }
                            else{
                                log.warn("The column name " + columnName.toString() + " has been found twice : column " + this.columnNames.get(columnName) + " and column " + i + ". Only the first column will be taken into account.");
                            }

                        }
                    }
                    else {
                        ColumnNames columnName = getColumnNameFor(line);
                        columnNames.put(columnName, 0);
                    }

                    while((line=reader.readLine()) != null){
                        String [] protein = new String [columnNames.size()];

                        if (line.contains("\t")){
                            ArrayList<String> columns = InputFileUtils.split(line, "\t");

                            if (columns.size() > columnNames.size()){
                                throw new InputFileException("The sheet contains columns without name. We can't process a protein identification without knowing what information is stored in each column.");
                            }

                            for (int i = 0; i < columns.size(); i++){
                                protein [i] = columns.get(i);
                            }
                        }
                        else {
                            protein[0] = line;
                            sheetContent.add(protein);
                        }
                    }
                    log.debug("Protein sheet loaded.");
                } catch (IOException e) {
                    log.error("We can't read the input file.", e);
                }
            }
            else {
                log.error("We can't load the input file. Check if there is a first line for the column names and if the file is a well formed tab file.");
            }
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    private String getColumnValueOfProteinAt(int line, ColumnNames columnName){
        if (this.columnNames.get(columnName) != null){
            return this.sheetContent.get(line)[this.columnNames.get(columnName)];
        }
        return null;
    }

    public String getIdentifierOfProteinAt(int line){
        return getColumnValueOfProteinAt(line, ColumnNames.id);
    }
    public String getOrganismOfProteinAt(int line){
        return getColumnValueOfProteinAt(line, ColumnNames.organism);
    }
    public String getSequenceOfProteinAt(int line){
        return getColumnValueOfProteinAt(line, ColumnNames.sequence);
    }
    public String getGeneNameOfProteinAt(int line){
        return getColumnValueOfProteinAt(line, ColumnNames.gene_name);
    }
    public String getProteinNameOfProteinAt(int line){
        return getColumnValueOfProteinAt(line, ColumnNames.protein_name);
    }

    public boolean hasGivenAnIdentifier(){
        if (this.columnNames.containsKey(ColumnNames.id)){
            return true;
        }
        return false;
    }

    public boolean hasGivenAnIdentifierForProtein(int i){
        if (i >= this.sheetContent.size() || i < 0){
            log.error("There is no protein at the line "+i+ " in the input file.");
            return false;
        }
        else{
            if (hasGivenAnIdentifier()){
                String id = getIdentifierOfProteinAt(i);
                if (id != null){
                    if (id.length() > 0){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean hasGivenAnOrganism(){
        if (this.columnNames.containsKey(ColumnNames.organism)){
            return true;
        }
        return false;
    }

    public boolean hasGivenAnOrganismForProtein(int i){
        if (i >= this.sheetContent.size() || i < 0){
            log.error("There is no protein at the line "+i+ " in the input file.");
            return false;
        }
        else{
            if (hasGivenAnOrganism()){
                String organism = getOrganismOfProteinAt(i);
                if (organism != null){
                    if (organism.length() > 0){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean hasGivenASequence(){
        if (this.columnNames.containsKey(ColumnNames.sequence)){
            return true;
        }
        return false;
    }

    public boolean hasGivenASequenceForProtein(int i){
        if (i >= this.sheetContent.size() || i < 0){
            log.error("There is no protein at the line "+i+ " in the input file.");
            return false;
        }
        else{
            if (hasGivenASequence()){
                String sequence = getSequenceOfProteinAt(i);
                if (sequence != null){
                    if (sequence.length() > 0){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean hasGivenAGeneName(){
        if (this.columnNames.containsKey(ColumnNames.gene_name)){
            return true;
        }
        return false;
    }

    public boolean hasGivenAGeneNameForProtein(int i){
        if (i >= this.sheetContent.size() || i < 0){
            log.error("There is no protein at the line "+i+ " in the input file.");
            return false;
        }
        else{
            if (hasGivenAGeneName()){
                String gene = getGeneNameOfProteinAt(i);
                if (gene != null){
                    if (gene.length() > 0){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean hasGivenAProteinName(){
        if (this.columnNames.containsKey(ColumnNames.protein_name)){
            return true;
        }
        return false;
    }

    public boolean hasGivenAProteinNameForProtein(int i){
        if (i >= this.sheetContent.size() || i < 0){
            log.error("There is no protein at the line "+i+ " in the input file.");
            return false;
        }
        else{
            if (hasGivenAProteinName()){
                String protein = getProteinNameOfProteinAt(i);
                if (protein != null){
                    if (protein.length() > 0){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public int size(){
        return this.sheetContent.size();
    }

    public String [] getLine(int i){
        return this.sheetContent.get(i);
    }

    public HashMap<ColumnNames, Integer> getColumnNames() {
        return columnNames;
    }
}
