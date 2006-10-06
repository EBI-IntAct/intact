/*
 * Copyright 2006 The Apache Software Foundation.
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
package uk.ac.ebi.intact.psiupload;

import uk.ac.ebi.intact.AbstractIntactTest;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class CutreTest extends AbstractIntactTest
{

    public void testImportCVs() throws Exception
    {
        File cvTree = new File(PsiUploadTest.class.getResource("cvTree.txt").getFile());

        List<String> mis = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(cvTree));
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.trim().startsWith("MI"))
            {
                mis.add(line.trim().substring(0,7));
            }
        }

        System.out.println("MIS: "+mis.size());

        System.out.println("Not in Xrefs");

        for (String mi : mis)
        {
            CvObject cvObject = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getCvObjectDao().getByXref(mi);

            if (cvObject == null)
            {
                System.out.println("\t"+mi);
            }
            
        }

    }
}
