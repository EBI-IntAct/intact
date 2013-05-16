/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.irefindex.seguid;

/**
 * Custom Exception class to handle exceptions from this module
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SeguidException extends Exception {

    public SeguidException() {
        super();
    }

    public SeguidException( String message ) {
        super(message);
    }

    public SeguidException( String message, Throwable cause ) {
        super( message,cause );

    }

    public SeguidException(Throwable cause){
        super(cause);
    }

}