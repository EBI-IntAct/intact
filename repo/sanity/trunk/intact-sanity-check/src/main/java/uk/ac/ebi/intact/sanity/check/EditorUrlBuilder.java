/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.model.*;
import uk.ac.ebi.intact.sanity.commons.report.InsaneObject;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;


/**
 * Editor Url Builder.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: EditorUrlBuilder.java,v 1.5 2006/05/18 08:34:28 catherineleroy Exp $
 */
public class EditorUrlBuilder {
    private String editorBasicUrl = null;

    private String editorUrl;

    public EditorUrlBuilder(SanityCheckConfig sanityConfig)
    {
        this.editorBasicUrl = sanityConfig.getEditorUrl();

        if (editorBasicUrl != null) {
            this.editorUrl = editorBasicUrl + "/do/secure/edit?";
        }
    }

    @Deprecated
    public String getEditorUrl(IntactBean intactBean){

        String url = "";

        if(intactBean instanceof ExperimentBean){
            url = editorUrl+"ac="+intactBean.getAc()+"&type=Experiment";
        }
        else if(intactBean instanceof InteractorBean){
            InteractorBean interactorBean = (InteractorBean) intactBean;
            String objclass = interactorBean.getObjclass();

            if(ProteinImpl.class.getName().equals(objclass)){
                url = editorUrl+"ac="+intactBean.getAc()+"&type=Protein";
            }else if ( InteractionImpl.class.getName().equals(objclass)){
                url = editorUrl+"ac="+intactBean.getAc()+"&type=Interaction";
            }else if ( NucleicAcidImpl.class.getName().equals(objclass)){
                url = editorUrl+"ac="+intactBean.getAc()+"&type=NucleicAcid";
            }
        }
        else if ( intactBean instanceof BioSourceBean ){
            url = editorUrl + "ac="+intactBean.getAc()+"&type=BioSource";
        }
        else if ( intactBean instanceof ControlledvocabBean ){

            ControlledvocabBean cvBean = (ControlledvocabBean) intactBean;

            String objclass = cvBean.getObjclass();

            if(CvTopic.class.getName().equals(objclass)){
                url = editorUrl + "ac=" + intactBean.getAc() + "&type=CvTopic";
            }
            else if(CvAliasType.class.getName().equals(objclass)){
                url = editorUrl +  "ac=" + intactBean.getAc()+"&type=CvAliasType";
            }
            else if(CvCellType.class.getName().equals(objclass)){
                url = editorUrl +  "ac=" + intactBean.getAc() + "&type=CvCellType";
            }
            else if(CvExperimentalRole.class.getName().equals(objclass)){
                url = editorUrl + "ac=" + intactBean.getAc() + "&type=CvComponentRole";
            }
            else if(CvDatabase.class.getName().equals(objclass)){
                url = editorUrl + "ac=" + intactBean.getAc() + "&type=CvDatabase";
            }
            else if(CvFuzzyType.class.getName().equals(objclass)){
                url = editorUrl  + "ac=" + intactBean.getAc() + "&type=CvFuzzyType";
            }
            else if(CvTissue.class.getName().equals(objclass)){
                url = editorUrl + "ac=" + intactBean.getAc() + "&type=CvTissue";
            }
            else if(CvXrefQualifier.class.getName().equals(objclass)){
                url = editorUrl + "ac=" + intactBean.getAc() + "&type=CvXrefQualifier";
            }
        }

        return url;
    }

    @Deprecated
    public String getEditorUrl(String type, String ac){
        return getEditorUrl( type, ac, null );
    }

    public String getEditorUrl(String type, String ac, String ruleKey){
        type = type.replaceAll("Impl", "");
        String url = editorUrl + "ac=" + ac + "&type=" + type;
        if( ruleKey != null ) {
            url = url + "&rule=" + ruleKey;
        }
        return url;
    }

    @Deprecated
    public void addEditorUrl(InsaneObject insaneObject) {
        addEditorUrl( insaneObject, null );
    }

    public void addEditorUrl(InsaneObject insaneObject, String ruleKey ) {
        String url = getEditorUrl(insaneObject.getObjclass(), insaneObject.getAc(), ruleKey );
        insaneObject.setUrl(url);
    }
}
