/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.unitdataset.plugin;

import java.net.URL;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CvConfiguration {

    /**
     * OBO File with CVs
     *
     * @parameter
     * @required
     */
    private URL oboUrl;

    /**
     * Additional CVs in CSV format
     *
     * @parameter
     * @required
     */
    private URL additionalUrl;

    /**
     * File with additional annotations
     *
     * @parameter
     * @required
     */
    private URL additionalAnnotationsUrl;

    public CvConfiguration() {
    }

    public URL getAdditionalAnnotationsUrl() {
        return additionalAnnotationsUrl;
    }

    public void setAdditionalAnnotationsUrl(URL additionalAnnotationsUrl) {
        this.additionalAnnotationsUrl = additionalAnnotationsUrl;
    }

    public URL getAdditionalUrl() {
        return additionalUrl;
    }

    public void setAdditionalUrl(URL additionalUrl) {
        this.additionalUrl = additionalUrl;
    }

    public URL getOboUrl() {
        return oboUrl;
    }

    public void setOboUrl(URL oboUrl) {
        this.oboUrl = oboUrl;
    }
}