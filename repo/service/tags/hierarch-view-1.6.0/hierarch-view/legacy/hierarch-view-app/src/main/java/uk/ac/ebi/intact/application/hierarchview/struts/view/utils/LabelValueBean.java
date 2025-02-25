/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchview.struts.view.utils;

/*
* Provided by the struts-example application.
*
* ====================================================================
*
* The Apache Software License, Version 1.1
*
* Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Struts", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*
*/


/**
 * Simple JavaBean to represent label-value pairs for use in collections
 * that are utilized by the <code>&lt;form:options&gt;</code> tag.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class LabelValueBean {


    // ----------------------------------------------------------- Instance variables
    /**
     * The label to be displayed to the user.
     */
    protected String label = null;

    /**
     * The value to be returned to the server.
     */
    protected String value = null;

    /**
     * The description to be returned to the server.
     */
    protected String description = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new LabelValueBean with the specified values.
     *
     * @param label       The label to be displayed to the user
     * @param value       The value to be returned to the server
     * @param description The description to be returned to the server
     */
    public LabelValueBean( String label, String value, String description ) {
        this.label = label;
        this.value = value;
        this.description = description;
    }

    // ------------------------------------------------------------- Properties


    public String getLabel() {
        return ( this.label );
    }

    public String getValue() {
        return ( this.value );
    }

    public String getDescription() {
        return ( this.description );
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Return a string representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer( "LabelValueBean[label=" );
        sb.append( this.label );
        sb.append( ", value=" );
        sb.append( this.value );
        sb.append( ", description=" );
        sb.append( this.description );
        sb.append( "]" );
        return ( sb.toString() );
    }
}


