/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.psicquic.ws.util;

import au.com.bytecode.opencsv.CSVWriter;
import uk.ac.ebi.intact.psicquic.ws.aop.StatsUnit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.hupo.psi.mi.psicquic.QueryResponse;
import org.hupo.psi.mi.psicquic.RequestInfo;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class StatsCsvWriter {

    public void appendToFile(File file, StatsUnit statsUnit) throws IOException {
        boolean append = true;

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            append = false;
        }

        Writer writer = new FileWriter(file, append);

        QueryResponse queryResponse = statsUnit.getQueryResponse();
        RequestInfo requestInfo = statsUnit.getRequestInfo();

        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(new String[] {
                statsUnit.getTimestamp().toString(),
                statsUnit.getService(),
                statsUnit.getMethodName(),
                String.valueOf(statsUnit.getExecutionTime().toDuration().getMillis()),
                statsUnit.getQuery(),
                statsUnit.getOperand() == null? "" : statsUnit.getOperand(),
                requestInfo == null? "" : requestInfo.getResultType(),
                String.valueOf(queryResponse == null? "" : queryResponse.getResultInfo().getTotalResults()),
                String.valueOf(requestInfo == null? "" : requestInfo.getFirstResult()),
                String.valueOf(requestInfo == null? "" : requestInfo.getBlockSize()),
                String.valueOf(queryResponse == null? "" : queryResponse.getResultInfo().getBlockSize()),
                statsUnit.getRemoteAddress() == null? "" : statsUnit.getRemoteAddress(),
                statsUnit.getUserAgent() == null? "" : statsUnit.getUserAgent()
        } );

        writer.flush();
    }
}
