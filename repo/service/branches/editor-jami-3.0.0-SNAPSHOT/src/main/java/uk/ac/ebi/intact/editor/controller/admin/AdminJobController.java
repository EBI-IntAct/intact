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
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.mi.jami.batch.MIBatchJobManager;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;

import javax.annotation.Resource;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "admin" )
@Lazy
public class AdminJobController extends BaseController {

    @Resource( name = "psiMIJobManager" )
    private transient MIBatchJobManager psiMIJobManager;

    public AdminJobController() {
    }

    public List<String> getJobNames() {
        return new ArrayList<String>(getPsiMIJobManager().getJobOperator().getJobNames());
    }

    public List<Long> getRunningJobExecutions( String jobName ) {
        try {
            return new ArrayList<Long>( getPsiMIJobManager().getJobOperator().getRunningExecutions(jobName) );
        } catch (NoSuchJobException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<Long> getJobInstances( String jobName ) {
        try {
            return new ArrayList<Long>(getPsiMIJobManager().getJobOperator().getJobInstances( jobName, 0, 50 ));
        } catch (NoSuchJobException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<Long> getJobExecutions( Long jobInstanceId ) {
        if ( jobInstanceId > 0 ) {
            try {
                return new ArrayList<Long>(getPsiMIJobManager().getJobOperator().getExecutions( jobInstanceId ));
            } catch (NoSuchJobInstanceException e) {
                return Collections.EMPTY_LIST;
            }
        }
        return Collections.EMPTY_LIST;
    }

    public StepExecution getLastStepExecution( JobInstance jobInstance, String stepName ) {
        return this.psiMIJobManager.getJobRepository().getLastStepExecution(jobInstance, stepName);
    }

    public void restart( ActionEvent evt ) {

        if (!evt.getComponent().getChildren().isEmpty()){
            UIParameter param = ( UIParameter ) evt.getComponent().getChildren().iterator().next();

            long executionId = ( Long ) param.getValue();

            try {
                getPsiMIJobManager().restartJob(executionId);

                addInfoMessage( "Job restarted", "Execution ID: " + executionId );
            } catch ( JobInstanceAlreadyCompleteException e ) {
                addErrorMessage( "Job is already complete", "Execution ID: " + executionId );
                e.printStackTrace();
            } catch ( NoSuchJobExecutionException e ) {
                addErrorMessage( "Job execution does not exist", "Execution ID: " + executionId );
                e.printStackTrace();
            } catch ( NoSuchJobException e ) {
                addErrorMessage( "Job does not exist", "Execution ID: " + executionId );
                e.printStackTrace();
            } catch ( JobRestartException e ) {
                addErrorMessage( "Problem restarting job", "Execution ID: " + executionId );
                e.printStackTrace();
            } catch ( JobParametersInvalidException e ) {
                addErrorMessage( "Job parameters are invalid", "Execution ID: " + executionId );
                e.printStackTrace();
            } catch (NoSuchJobInstanceException e) {
                addErrorMessage("Job instance does not exist", "Execution ID: " + executionId);
            }
        }
    }

    public void stop( ActionEvent evt ) {
        UIParameter param = ( UIParameter ) evt.getComponent().getChildren().iterator().next();
        long executionId = ( Long ) param.getValue();

        try {
            getPsiMIJobManager().getJobOperator().stop(executionId);

            addInfoMessage( "Job stopped", "Execution ID: " + executionId );
        } catch ( NoSuchJobExecutionException e ) {
            addErrorMessage( "Job does not exist", "Execution ID: " + executionId );
            e.printStackTrace();
        } catch ( JobExecutionNotRunningException e ) {
            addErrorMessage( "Job is not running anymore", "Execution ID: " + executionId );
            e.printStackTrace();
        }
    }

    public MIBatchJobManager getPsiMIJobManager() {
        if (this.psiMIJobManager == null){
            this.psiMIJobManager = ApplicationContextProvider.getBean("psiMIJobManager");
        }
        return psiMIJobManager;
    }
}