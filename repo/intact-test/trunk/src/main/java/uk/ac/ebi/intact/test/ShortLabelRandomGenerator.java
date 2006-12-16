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
package uk.ac.ebi.intact.test;

import java.util.Random;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ShortLabelRandomGenerator
{
    private char[] VOWELS = new char[] {'a','e','i','o','u'};
    private char[] CONSONANTS = new char[] {'q','w','r','t','y','p','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m'};

    public ShortLabelRandomGenerator()
    {
    }

    public String generateLabel(int minLength, int maxLength)
    {
        Random r = new Random();
        int length = r.nextInt(maxLength-minLength)+minLength;

        StringBuffer sb = new StringBuffer(length);

        for (int i=0; i<length; i++)
        {
            if (i % 2 == 0)
            {
                sb.append(CONSONANTS[r.nextInt(CONSONANTS.length)]);
            }
            else
            {
                sb.append(VOWELS[r.nextInt(VOWELS.length)]);
            }
        }

        return sb.toString();
    }

    public static void main(String[] args)
    {
        System.out.println(new ShortLabelRandomGenerator().generateLabel(7,9));
    }
}
