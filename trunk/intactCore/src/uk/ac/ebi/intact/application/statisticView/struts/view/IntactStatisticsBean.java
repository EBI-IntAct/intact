/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.struts.view;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Michael Kleen mkleen@ebi.ac.uk
 * Date: Mar 17, 2005
 * Time: 5:16:48 PM
 */
public class IntactStatisticsBean {

    private String contextPath;
    private String experimentChartName;
    private String interactionChartName;
    private String proteinChartName;
    private String binaryChartName;
    private String cvTermChartName;
    private String bioSourceChartName;
    private String evidenceChartName;
    private String detectionChartName;
    private int experimentCount;
    private int interactionCount;
    private int proteinCount;
    private int cvTermCount;
    private int binaryInteractionCount;
    private final static String SERVLET_PATH = "/servlet/DisplayChart?filename=";


    public IntactStatisticsBean(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setInteractionChartName(String interactionChartName) {
        this.interactionChartName = interactionChartName;
    }

    public void setExperimentChartName(String experimentChartName) {
        this.experimentChartName = experimentChartName;
    }

    public void setProteinChartName(String proteinChartName) {
        this.proteinChartName = proteinChartName;
    }

    public void setCvTermChartName(String cvTermChartName) {
        this.cvTermChartName = cvTermChartName;
    }

    public void setBinaryChartName(String binaryChartName) {
        this.binaryChartName = binaryChartName;
    }

    public void setExperimentCount(int experimentCount) {
        this.experimentCount = experimentCount;
    }

    public void setBioSourceChartName(String bioSourceChartName) {
        this.bioSourceChartName = bioSourceChartName;
    }

    public void setEvidenceChartName(String evidenceChartName) {
        this.evidenceChartName = evidenceChartName;
    }

    public void setDetectionChartName(String detectionChartName) {
        this.detectionChartName = detectionChartName;
    }

    public int getInteractionCount() {
        return interactionCount;
    }

    public void setInteractionCount(int interactionCount) {
        this.interactionCount = interactionCount;
    }

    public int getProteinCount() {
        return proteinCount;
    }

    public void setProteinCount(int proteinCount) {
        this.proteinCount = proteinCount;
    }

    public int getCvTermCount() {
        return cvTermCount;
    }

    public void setCvTermCount(int cvTermCount) {
        this.cvTermCount = cvTermCount;
    }

    public int getBinaryInteractionCount() {
        return binaryInteractionCount;
    }

    public void setBinaryInteractionCount(int binaryInteractionCount) {
        this.binaryInteractionCount = binaryInteractionCount;
    }

    public String getExperimentChartUrl() {
        return contextPath + SERVLET_PATH + experimentChartName;
    }

    public String getInteractionChartUrl() {
        return contextPath + SERVLET_PATH + interactionChartName;
    }

    public String getProteinChartUrl() {
        return contextPath + SERVLET_PATH + proteinChartName;
    }

    public String getBinaryChartUrl() {
        return contextPath + SERVLET_PATH + binaryChartName;
    }

    public String getCvTermChartUrl() {
        return contextPath + SERVLET_PATH + cvTermChartName;
    }

    public String getBioSourceChartUrl() {
        return contextPath + SERVLET_PATH + bioSourceChartName;
    }

    public String getEvidenceChartUrl() {
        return contextPath + SERVLET_PATH + evidenceChartName;
    }

    public String getDetectionChartUrl() {
        return contextPath + SERVLET_PATH + detectionChartName;
    }

    public List getDisplayBeans() {
        List result = new ArrayList();

        result.add(new DisplayStatisticsBean("Proteins",
                                             proteinCount,
                                             "Number of proteins in the database"));

        result.add(new DisplayStatisticsBean("Interactions",
                                             interactionCount,
                                             "Number of prototype interactions and complexes"));

        result.add(new DisplayStatisticsBean("Binary interactions",
                                             binaryInteractionCount,
                                             "Number of prototype interactions, n-ary interactions expanded according to the \"spoke\" model"));

        result.add(new DisplayStatisticsBean("Experiments",
                                             experimentCount,
                                             "Distinct experiments"));

        result.add(new DisplayStatisticsBean("Terms",
                                             cvTermCount,
                                             "Controlled vocabulary terms"));

        return result;
    }
}