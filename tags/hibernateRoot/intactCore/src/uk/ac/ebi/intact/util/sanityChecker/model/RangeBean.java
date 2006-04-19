/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBean  extends AnnotatedBean {

    private String feature_ac;
    private String sequence;

    private String interaction_ac;
    private String interactor_ac;


    private int fromintervalstart;
    private int fromintervalend;
    private String fromfuzzytype_ac;
    private int tointervalstart;
    private int tointervalend;
    private String tofuzzytype_ac;
    private String component_ac;

    public String getInteractor_ac() {
        return interactor_ac;
    }

    public void setInteractor_ac(String interactor_ac) {
        this.interactor_ac = interactor_ac;
    }

    public String getInteraction_ac() {
        return interaction_ac;
    }

    public void setInteraction_ac(String interaction_ac) {
        this.interaction_ac = interaction_ac;
    }

    public int getFromintervalstart() {
        return fromintervalstart;
    }

    public void setFromintervalstart(int fromintervalstart) {
        this.fromintervalstart = fromintervalstart;
    }

    public String getComponent_ac() {
        return component_ac;
    }

    public void setComponent_ac(String component_ac) {
        this.component_ac = component_ac;
    }


    public String getFromfuzzytype_ac() {
        return fromfuzzytype_ac;
    }

    public void setFromfuzzytype_ac(String fromfuzzytypeac) {
        this.fromfuzzytype_ac = fromfuzzytypeac;
    }



    public int getFromintervalend() {
        return fromintervalend;
    }

    public void setFromIntervalEnd(int fromIntervalend) {
        this.fromintervalend = fromIntervalend;
    }

    public int getTointervalstart() {
        return tointervalstart;
    }

    public void setTointervalstart(int tointervalstart) {
        this.tointervalstart = tointervalstart;
    }

    public int getTointervalend() {
        return tointervalend;
    }

    public void setTointervalend(int tointervalend) {
        this.tointervalend = tointervalend;
    }

    public String getTofuzzytype_ac() {
        return tofuzzytype_ac;
    }

    public void setTofuzzytype_ac(String tofuzzytype_ac) {
        this.tofuzzytype_ac = tofuzzytype_ac;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getFeature_ac() {
        return feature_ac;
    }

    public void setFeature_ac(String feature_ac) {
        this.feature_ac = feature_ac;
    }

}
