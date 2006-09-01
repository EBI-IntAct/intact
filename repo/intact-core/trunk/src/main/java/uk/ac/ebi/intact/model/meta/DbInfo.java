/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.meta;

import uk.ac.ebi.intact.model.AbstractAuditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * Contains metadata about the schema
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Sep-2006</pre>
 */
@Entity
@Table(name = "ia_db_info")
public class DbInfo extends AbstractAuditable
{

    public static final String SCHEMA_VERSION = "schema_version"; 

    @Id
    @Column(name="dbi_key")
    private String key;

    private String value;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }


    @Override
    @Temporal
    @Column(name = "created_date")
    public Date getCreated()
    {
        return super.getCreated();
    }

    @Override
    @Temporal
    @Column(name = "created_date")
    public Date getUpdated()
    {
        return super.getUpdated();
    }

    @Override
    @Column(name = "created_user")
    public String getCreator()
    {
        return super.getCreator();
    }

    @Override
    @Column(name = "updated_user")
    public String getUpdator()
    {
        return super.getUpdator();
    }

    @Override
    public String toString()
    {
        return "DbInfo{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
