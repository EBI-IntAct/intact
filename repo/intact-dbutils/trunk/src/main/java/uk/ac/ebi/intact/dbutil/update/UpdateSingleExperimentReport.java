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
package uk.ac.ebi.intact.dbutil.update;

import uk.ac.ebi.intact.util.cdb.UpdateExperimentAnnotationsFromPudmed;

import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UpdateSingleExperimentReport
{
    private UpdateExperimentAnnotationsFromPudmed.UpdateReport updateReport;
    private String experimentAc;
    private String experimentLabel;
    private boolean invalid;
    private String invalidMessage;
    private List<String> warningMessages;

    private UpdatedValue shortLabelValue;
    private UpdatedValue fullNameValue;

    public UpdateSingleExperimentReport(String experimentAc, String experimentLabel)
    {
        this.experimentAc = experimentAc;
        this.experimentLabel = experimentLabel;
    }

    public UpdateSingleExperimentReport(UpdateExperimentAnnotationsFromPudmed.UpdateReport updateReport)
    {
        this.updateReport = updateReport;
    }

    public UpdateExperimentAnnotationsFromPudmed.UpdateReport getUpdateReport()
    {
        return updateReport;
    }    

    public void setUpdateReport(UpdateExperimentAnnotationsFromPudmed.UpdateReport updateReport)
    {
        this.updateReport = updateReport;
    }

    public boolean isUpdated()
    {
        return isAuthorEmailUpdated()
                && isAuthorListUpdated()
                && isContactUpdated()
                && isFullNameUpdated()
                && isJournalUpdated()
                && isShortLabelUpdated()
                && isYearUpdated();
    }

    public String getExperimentAc()
    {
        return experimentAc;
    }

    public String getExperimentLabel()
    {
        return experimentLabel;
    }

    public boolean isInvalid()
    {
        return invalid;
    }

    public String getInvalidMessage()
    {
        return invalidMessage;
    }

    public void setInvalidMessage(String invalidMessage)
    {
        this.invalid = true;
        this.invalidMessage = invalidMessage;
    }

    public boolean isShortLabelUpdated()
    {
        return shortLabelValue != null;
    }

    public boolean isFullNameUpdated()
    {
        return fullNameValue != null;
    }

    public boolean isAuthorListUpdated()
    {
        return updateReport.isAuthorListUpdated();
    }

    public boolean isContactUpdated()
    {
        return updateReport.isContactUpdated();
    }

    public boolean isYearUpdated()
    {
        return updateReport.isYearUpdated();
    }

    public boolean isJournalUpdated()
    {
        return updateReport.isJournalUpdated();
    }

    public boolean isAuthorEmailUpdated()
    {
        return updateReport.isAuthorEmailUpdated();
    }

    public List<String> getWarningMessages()
    {
        return warningMessages;
    }

    public void setWarningMessages(List<String> warningMessages)
    {
        this.warningMessages = warningMessages;
    }

    public boolean addWarningMessage(String o)
    {
        return warningMessages.add(o);
    }

    public UpdatedValue getShortLabelValue()
    {
        return shortLabelValue;
    }

    public void setShortLabelValue(UpdatedValue shortLabelValue)
    {
        this.shortLabelValue = shortLabelValue;
    }

    public UpdatedValue getFullNameValue()
    {
        return fullNameValue;
    }

    public void setFullNameValue(UpdatedValue fullNameValue)
    {
        this.fullNameValue = fullNameValue;
    }

    public UpdatedValue getAuthorListValue()
    {
        return convertValue(updateReport.getAuthorListValue());
    }

    public UpdatedValue getContactListValue()
    {
        return convertValue(updateReport.getContactListValue());
    }

    public UpdatedValue getYearListValue()
    {
        return convertValue(updateReport.getYearListValue());
    }

    public UpdatedValue getJournalListValue()
    {
        return convertValue(updateReport.getJournalListValue());
    }

    public UpdatedValue getAuthorEmailValue()
    {
        return convertValue(updateReport.getAuthorEmailValue());
    }

    private UpdatedValue convertValue(UpdateExperimentAnnotationsFromPudmed.UpdatedValue ueaValue)
    {
        return new UpdatedValue(ueaValue.getOldValue(), ueaValue.getNewValue());
    }
}
