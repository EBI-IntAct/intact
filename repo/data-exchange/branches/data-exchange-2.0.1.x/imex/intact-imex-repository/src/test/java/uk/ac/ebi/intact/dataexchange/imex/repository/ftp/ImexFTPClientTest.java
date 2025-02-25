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
package uk.ac.ebi.intact.dataexchange.imex.repository.ftp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * ImexFTPClient Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexFTPClientTest {

    private ImexFTPClient ftpClient;

    @Before
    public void before() throws Exception {
        ftpClient = ImexFTPClientFactory.createIntactClient();
        ftpClient.connect();
    }

    @After
    public void after() throws Exception {
        ftpClient.disconnect();
        ftpClient = null;
    }

    @Test
    public void listFiles() throws Exception {
        Assert.assertFalse(ftpClient.listFiles().isEmpty());
    }

    @Test
    public void listFiles_mint() throws Exception {
        final ImexFTPClient imexFTPClient = ImexFTPClientFactory.createMintClient();
        imexFTPClient.connect();

        final List<ImexFTPFile> ftpFiles = imexFTPClient.listFiles();

        System.out.println("Files: "+ftpFiles.size());

        for (ImexFTPFile file : ftpFiles) {
            System.out.println(file);
        }
        Assert.assertFalse(ftpFiles.isEmpty());

        imexFTPClient.disconnect();
    }

    @Test
    public void listFiles_intact() throws Exception {
        final ImexFTPClient imexFTPClient = ImexFTPClientFactory.createIntactClient();
        imexFTPClient.connect();

        final List<ImexFTPFile> ftpFiles = imexFTPClient.listFiles();

        System.out.println("Files: "+ftpFiles.size());

        for (ImexFTPFile file : ftpFiles) {
            System.out.println(file);
        }
        Assert.assertFalse(ftpFiles.isEmpty());

        imexFTPClient.disconnect();
    }
}