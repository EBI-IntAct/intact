/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.editor.controller.admin;

import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.mi.jami.batch.MIBatchJobManager;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "admin" )
public class CompatibilityController extends BaseController {

    @Resource( name = "psiMIJobManager" )
    private transient MIBatchJobManager psiMIJobManager;

    public CompatibilityController() {
    }

    private void launchJob(String name, String jobId) {
        Map<String, JobParameter> jobParameterMap = new HashMap<String, JobParameter>();
        jobParameterMap.put( "jobId", new JobParameter( jobId ) );

        try {
            getPsiMIJobManager().startJobWithParameters(name, jobId != null ? "jobId=" + jobId : null);

            addInfoMessage( "Job started", "Job ID: " + jobId );
        } catch ( JobParametersInvalidException e ) {
            addErrorMessage( "Invalid job parameters", "Job ID: " + jobId );
            e.printStackTrace();
        } catch (JobInstanceAlreadyExistsException e) {
            addErrorMessage( "Job already running", "Job ID: " + jobId );
            e.printStackTrace();
        } catch (NoSuchJobException e) {
            addErrorMessage( "No such job exist", "Job ID: " + jobId );
            e.printStackTrace();
        }
    }

    private Job getJob( String jobName ) {
        return ( Job ) getSpringContext().getBean( jobName );
    }

    public MIBatchJobManager getPsiMIJobManager() {
        if (this.psiMIJobManager == null){
            this.psiMIJobManager = ApplicationContextProvider.getBean("psiMIJobManager");
        }
        return psiMIJobManager;
    }
}
