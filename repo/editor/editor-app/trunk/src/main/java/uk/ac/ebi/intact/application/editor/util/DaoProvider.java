/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.util;

import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class DaoProvider {

    public static AnnotatedObjectDao getDaoFactory(Class clazz){
        if(Protein.class.isAssignableFrom(clazz)){
            return DaoFactory.getProteinDao();
        }else if (BioSource.class.isAssignableFrom(clazz)){
            return DaoFactory.getBioSourceDao();
        }else if (Experiment.class.isAssignableFrom(clazz)){
            return DaoFactory.getExperimentDao();
        }else if (CvObject.class.isAssignableFrom(clazz) ){
            if(CvAliasType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvAliasType.class);
            }else if(CvCellType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvCellType.class);

            }else if(CvComponentRole.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvComponentRole.class);

            }else if(CvDatabase.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvDatabase.class);

            }else if(CvFeatureIdentification.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvFeatureIdentification.class);

            }else if(CvFeatureType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvFeatureType.class);

            }else if(CvFuzzyType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvFuzzyType.class);

            }else if(CvIdentification.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvIdentification.class);

            }else if(CvInteraction.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvInteraction.class);

            }else if(CvInteractionType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvInteractionType.class);

            }else if(CvInteractorType.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvInteractorType.class);

            }else if(CvTissue.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvTissue.class);

            }else if(CvTopic.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvTopic.class);

            }else if(CvXrefQualifier.class.isAssignableFrom(clazz)){
                return DaoFactory.getCvObjectDao(CvXrefQualifier.class);

            }else{
                return DaoFactory.getCvObjectDao(CvObject.class);
            }
        }else if (Interaction.class.isAssignableFrom(clazz)){
            return DaoFactory.getInteractionDao();
        }else if (NucleicAcid.class.isAssignableFrom(clazz)){
            return DaoFactory.getInteractorDao();
        }else if (Protein.class.isAssignableFrom(clazz)){
            return DaoFactory.getInteractorDao();
        }else if(SmallMolecule.class.isAssignableFrom(clazz)){
            return DaoFactory.getInteractorDao();
        }
        else throw new IntactException("Class " + clazz.getName() + " is not a searchable object");
    }




}
