/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker;

import uk.ac.ebi.intact.util.sanityChecker.model.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.PropertyLoader;
import java.util.*;


/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class EditorUrlBuilder {
        public static final String SANITYCHECK_CONFIG_FILE = "/config/sanityCheck.properties";
        private static String EDITOR_BASIC_URL = null;
        static {
        Properties props = PropertyLoader.load( SANITYCHECK_CONFIG_FILE );
        if (props != null) {
            EDITOR_BASIC_URL = props.getProperty ("editor_basic_url");
        } else {
            System.err.println ("Unable to open the properties file: " + SANITYCHECK_CONFIG_FILE);
        }
    }
    private static final String editorUrl = EDITOR_BASIC_URL + "/editor/do/secure/edit?";

    public String getEditorUrl(IntactBean intactBean){

        String url = new String();

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
            else if(CvComponentRole.class.getName().equals(objclass)){
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

    public String getEditorUrl(String type, String ac){
        String url = editorUrl + "ac=" + ac + "&type=" + type;
        return url;
    }

}
