package uk.ac.ebi.intact.application.search3.searchEngine.lucene.model;

import java.util.Map;

/**
 * This class provides a possibility to store the attributes of an Interaction to create an index
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class InteractionSearchObject extends SearchObject {

    // object that stores the information about the interaction type
    CvSearchObject cvInteractionsType;

    /**
     * @param ac                 accession number
     * @param shortLabel         shortlabel of the interaction
     * @param fullName           fullname/description of the interaction
     * @param objClass           name of the IntAct class
     * @param xrefs              Map holding all xrefs, the key is the name of the database and the value
     *                           is an collection of primaryIDs
     * @param annotations        Map holding all annotations, the key is the name of the topic and the value
     *                           is the annotation
     * @param cvInteractionsType object that stores information about the interaction type
     * @param alias              Map with alias, again the key is the description of the alias and the value the name of the alias
     */
    public InteractionSearchObject(final String ac, final String shortLabel,
                                   final String fullName, final String objClass,
                                   final Map xrefs, final Map annotations,
                                   final CvSearchObject cvInteractionsType,
                                   final Map alias) {

        super(ac, shortLabel, fullName, objClass, xrefs, annotations, alias);
        this.cvInteractionsType = cvInteractionsType;
    }

//    public InteractionSearchObject(final String ac, final String shortLabel,
//                                   final String fullName, final String objClass) {
//
//        super(ac, shortLabel, fullName, objClass, null, null, null);
//    }
//
//    public InteractionSearchObject(final String ac, final String shortLabel,
//                                   final String fullName, final String objClass, final Map xrefs) {
//
//        super(ac, shortLabel, fullName, objClass, xrefs, null, null);
//    }
//
//    public InteractionSearchObject(final String ac, final String shortLabel,
//                                   final String fullName, final String objClass,
//                                   final Map xrefs, final Map annotations) {
//
//        super(ac, shortLabel, fullName, objClass, xrefs, annotations, null);
//    }


    /**
     * Getter for cvInteractionType, which holds information about the interaction type
     *
     * @return object storing interaction type information
     */
    public CvSearchObject getCvInteractionsType() {
        return cvInteractionsType;
    }
}
