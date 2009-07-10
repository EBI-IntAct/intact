/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.bridges.blast;

import java.io.File;

/**
 * Wrapper for the configuration data of the blast service.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.2-SNAPSHOT
 *        <pre>
 *               30-Nov-2007
 *               </pre>
 */
public class BlastConfig {
    private File databaseDir;
    private String tableName = "job";
    private File blastArchiveDir;
    private String email;
    private int nrPerSubmission = 20;

    public BlastConfig( String email ) {
        this.email = email;
        databaseDir = new File(System.getProperty("java.io.tmpdir"));
        blastArchiveDir = new File(System.getProperty("java.io.tmpdir"));
    }

    public File getDatabaseDir() {
        return databaseDir;
    }

    public void setDatabaseDir(File dbDir){
        if(dbDir != null){
            this.databaseDir = dbDir;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public File getBlastArchiveDir() {      
        return blastArchiveDir;
    }

     public void setBlastArchiveDir(File archiveDir){
        if(archiveDir != null){
            this.blastArchiveDir = archiveDir;
        }
    }

    public String getEmail() {
        return email;
    }

    public int getNrPerSubmission() {
        return nrPerSubmission;
    }

    public void setNrPerSubmission( int nrPerSubmission ) {
        this.nrPerSubmission = nrPerSubmission;
    }
}
