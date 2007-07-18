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
package uk.ac.ebi.intact.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * DES Encryption/Decryption.
 *
 * For password encyption remember that to check if the password introduced by the user is ok, it is necessary to encrypt
 * what the user has input and compare the encrypted strings
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DesEncrypter
{
    private Cipher cypher;
    private Cipher decipher;

    public DesEncrypter(SecretKey key)
    {
        try
        {
            this.cypher = Cipher.getInstance("DES");
            this.decipher = Cipher.getInstance("DES");

            cypher.init(Cipher.ENCRYPT_MODE, key);
            decipher.init(Cipher.DECRYPT_MODE, key);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a string using the SecretKey provided in the constructor
     * @param str
     * @return
     */
    public String encrypt(String str) 
    {
        try
        {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = cypher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception while encrypting", e);
        }
    }

    /**
     * Descrypts a string using the SecretKey provided in the constructor
     * @param str
     * @return
     */
    public String decrypt(String str)
    {
        try
        {
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = decipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception while decrypting", e);
        }
    }
}
