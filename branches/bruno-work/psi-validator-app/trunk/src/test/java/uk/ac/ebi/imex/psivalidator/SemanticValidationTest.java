/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator;

import junit.framework.TestCase;
import psidev.psi.mi.validator.extensions.mi25.Mi25Validator;
import psidev.psi.mi.validator.framework.Validator;
import psidev.psi.mi.validator.framework.ValidatorException;
import psidev.psi.mi.validator.framework.ValidatorMessage;
import psidev.psi.mi.validator.util.UserPreferences;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jun-2006</pre>
 */
public class SemanticValidationTest extends TestCase
{


    public void testSemanticValidationGoodFile() throws ValidatorException, TransformerException, IOException
    {
        InputStream configFile = SemanticValidationTest.class.getResourceAsStream("resource/config-mi-validator-test.xml");
        InputStream psiFile = SemanticValidationTest.class.getResourceAsStream("resource/10381623.xml");

        String expandedStr = TransformationUtil.transformToExpanded(psiFile).toString();
        InputStream expandedFile = new ByteArrayInputStream(expandedStr.getBytes());
        //InputStream expandedFile = psiFile;

        // set work directory
        UserPreferences preferences = new UserPreferences();
        preferences.setKeepDownloadedOntologiesOnDisk( true );
        preferences.setWorkDirectory(new File(System.getProperty("java.io.tmpdir")));
        preferences.setSaxValidationEnabled( false );

        Validator validator = new Mi25Validator( configFile, preferences );

        Collection<ValidatorMessage> messages = validator.validate( expandedFile );

        assertEquals(0, messages.size());
    }

    public void testSemanticValidationWithErrors() throws ValidatorException, TransformerException, IOException
    {
        InputStream configFile = SemanticValidationTest.class.getResourceAsStream("resource/config-mi-validator-test.xml");
        InputStream psiFile = SemanticValidationTest.class.getResourceAsStream("resource/1000867_small.xml");

        String expandedStr = TransformationUtil.transformToExpanded(psiFile).toString();
        InputStream expandedFile = new ByteArrayInputStream(expandedStr.getBytes());
        //InputStream expandedFile = psiFile;

        // set work directory
        UserPreferences preferences = new UserPreferences();
        preferences.setKeepDownloadedOntologiesOnDisk( true );
        preferences.setWorkDirectory(new File(System.getProperty("java.io.tmpdir")));
        preferences.setSaxValidationEnabled( false );

        Validator validator = new Mi25Validator( configFile, preferences );

        Collection<ValidatorMessage> messages = validator.validate( expandedFile );

        assertEquals(5, messages.size());
    }
}
