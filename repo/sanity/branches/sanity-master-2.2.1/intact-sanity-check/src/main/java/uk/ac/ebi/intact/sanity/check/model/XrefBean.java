/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: XrefBean.java,v 1.1 2005/07/28 16:13:29 catherineleroy Exp $
 */
public class XrefBean extends IntactBean{
    private String parent_ac;
    private String primaryid;
    private String database_ac;
    private String qualifier_ac;


    public XrefBean() {
    }

        public String getParent_ac() {
        return parent_ac;
    }

    public void setParent_ac(String parent_ac) {
        this.parent_ac = parent_ac;
    }

    public String getPrimaryid() {
        return primaryid;
    }

    public void setPrimaryid(String primaryid) {
        this.primaryid = primaryid;
    }

    public String getDatabase_ac() {
        return database_ac;
    }

    public void setDatabase_ac(String database_ac) {
        this.database_ac = database_ac;
    }

        public String getQualifier_ac() {
            return qualifier_ac;
        }

        public void setQualifier_ac(String qualifier_ac) {
            this.qualifier_ac = qualifier_ac;
        }

}
