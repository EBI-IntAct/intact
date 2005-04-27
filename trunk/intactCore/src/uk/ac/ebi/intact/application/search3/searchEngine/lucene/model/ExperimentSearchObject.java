package uk.ac.ebi.intact.application.search3.searchEngine.lucene.model;

import java.util.Map;

/**
 * This class provides a possibility to store the attributes of an Experiment to create an index
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class ExperimentSearchObject extends SearchObject {

    // object that holds the information about the CvIdentification of one experiment
    private CvSearchObject cvIdentification;
    // object that holds the information about the CvInteraction of one experiment
    private CvSearchObject cvInteraction;

    /**
     * @param ac               accession number
     * @param shortLabel       shortlabel of the experiment
     * @param fullName         fullname/description of the experiment
     * @param objClass         name of the IntAct class
     * @param xrefs            Map holding all xrefs, the key is the name of the database and the value
     *                         is an collection of primaryIDs
     * @param annotations      Map holding all annotations, the key is the name of the topic and the value
     *                         is the annotation
     * @param cvIdentification holds the information of the participant detection method
     * @param cvInteraction    holds the information of the interaction detection method
     * @param alias            Map with alias, again the key is the description of the alias and the value the name of the alias
     */
    public ExperimentSearchObject(final String ac, final String shortLabel, final String fullName,
                                  final String objClass, final Map xrefs, final Map annotations,
                                  final CvSearchObject cvIdentification,
                                  final CvSearchObject cvInteraction,
                                  final Map alias) {

        super(ac, shortLabel, fullName, objClass, xrefs, annotations, alias);
        this.cvIdentification = cvIdentification;
        this.cvInteraction = cvInteraction;
    }

//    public ExperimentSearchObject(final String ac, final String shortLabel,
//                                  final String fullName,
//                                  final String objClass) {
//
//        super(ac, shortLabel, fullName, objClass, null, null, null);
//    }
//
//    public ExperimentSearchObject(final String ac, final String shortLabel,
//                                  final String fullName, final String objClass, final Map xrefs) {
//        super(ac, shortLabel, fullName, objClass, xrefs, null, null);
//    }
//
//    public ExperimentSearchObject(final String ac, final String shortLabel,
//                                  final String fullName, final String objClass,
//                                  final Map xrefs, final Map annotations) {
//        super(ac, shortLabel, fullName, objClass, xrefs, annotations, null);
//    }
//

    /**
     * getter for the variable cvIdentification, which holds the
     * participant detection information for a specific experiment
     *
     * @return object that stores participant detection information
     */
    public CvSearchObject getCvIdentification() {
        return cvIdentification;
    }

    /**
     * Getter for the variable cvInteraction, which holds the interaction detection information
     * for a specific experiment
     *
     * @return object that stores the interaction detection information
     */
    public CvSearchObject getCvInteraction() {
        return cvInteraction;
    }

}
